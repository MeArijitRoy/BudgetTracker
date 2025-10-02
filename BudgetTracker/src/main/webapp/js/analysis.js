document.addEventListener('DOMContentLoaded', function() {
	const analysisForm = document.getElementById('analysisForm');
	const graphTypeSelect = document.getElementById('graphType');
	const dateRangeSelect = document.getElementById('dateRange');
	const chartCanvas = document.getElementById('analysisChart');
	const tableContainer = document.getElementById('transactionTableContainer');
	const spinner = document.getElementById('chartSpinner');

	let currentChart = null;

	// 1. Handle the main form submission to generate a report
	analysisForm.addEventListener('submit', function(event) {
		event.preventDefault(); // Prevent the default form submission
		fetchReportData();
	});

	// 2. Dynamically update date range options when the graph type changes
	graphTypeSelect.addEventListener('change', updateDateRangeOptions);


	/**
	 * Fetches the report data from the AnalysisServlet via AJAX.
	 */
	function fetchReportData() {
		showSpinner(true);
		const formData = new FormData(analysisForm);
		const params = new URLSearchParams();

		// FormData doesn't handle multi-select well, so we get it manually
		document.querySelectorAll('input[name="accounts"]:checked').forEach(checkbox => {
			params.append('accounts[]', checkbox.value);
		});

		// Append other form fields
		params.append('graphType', formData.get('graphType'));
		params.append('dateRange', formData.get('dateRange'));
		params.append('currency', formData.get('currency'));

		// CORRECTED: Build a full, absolute URL using the global contextPath variable.
		// This ensures the URL is always correct (e.g., /BudgetTracker/AnalysisServlet).
		fetch(`${contextPath}/AnalysisServlet?${params.toString()}`)
			.then(response => {
				if (!response.ok) {
					throw new Error(`HTTP error! Status: ${response.status}`);
				}
				return response.json();
			})
			.then(data => {
				renderReport(formData.get('graphType'), data);
				showSpinner(false);
			})
			.catch(error => {
				console.error('Error fetching analysis data:', error);
				renderError('Failed to load report data. Please try again.');
				showSpinner(false);
			});
	}

	/**
	 * Renders the correct report (chart or table) based on the graph type.
	 */
	function renderReport(graphType, data) {
		if (currentChart) {
			currentChart.destroy(); // Destroy the old chart before drawing a new one
		}

		// Hide/show the correct container
		if (graphType === 'transactionList') {
			chartCanvas.style.display = 'none';
			tableContainer.style.display = 'block';
			drawTransactionTable(data);
		} else {
			tableContainer.style.display = 'none';
			chartCanvas.style.display = 'block';

			switch (graphType) {
				case 'spendingBreakdown':
					drawDonutChart(data);
					break;
				case 'cashFlowTrend':
					drawBarChart(data);
					break;
				case 'balanceTrend':
					drawLineChart(data);
					break;
			}
		}
	}

	/**
	 * Draws the Spending Breakdown Donut Chart.
	 */
	function drawDonutChart(data) {
		const labels = data.map(item => item.categoryName);
		const amounts = data.map(item => item.totalAmount);

		currentChart = new Chart(chartCanvas, {
			type: 'doughnut',
			data: {
				labels: labels,
				datasets: [{
					label: 'Spending',
					data: amounts,
					backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40']
				}]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				plugins: {
					title: { display: true, text: 'Spending Breakdown' },
					legend: { position: 'bottom' }
				}
			}
		});
	}

	/**
	 * Draws the Cash Flow Trend Bar Chart.
	 */
	function drawBarChart(data) {
		const labels = data.map(item => item.month);
		const incomes = data.map(item => item.totalIncome);
		const expenses = data.map(item => item.totalExpense);

		currentChart = new Chart(chartCanvas, {
			type: 'bar',
			data: {
				labels: labels,
				datasets: [
					{
						label: 'Income',
						data: incomes,
						backgroundColor: 'rgba(75, 192, 192, 0.6)'
					},
					{
						label: 'Expense',
						data: expenses,
						backgroundColor: 'rgba(255, 99, 132, 0.6)'
					}
				]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				plugins: {
					title: { display: true, text: 'Cash Flow Trend' }
				},
				scales: {
					x: { stacked: false },
					y: { stacked: false }
				}
			}
		});
	}

	/**
	 * Draws the Balance Over Time Line Chart.
	 */
	function drawLineChart(data) {
		const labels = data.map(item => new Date(item.date).toLocaleDateString());
		const balances = data.map(item => item.balance);

		currentChart = new Chart(chartCanvas, {
			type: 'line',
			data: {
				labels: labels,
				datasets: [{
					label: 'Balance',
					data: balances,
					borderColor: 'rgba(54, 162, 235, 1)',
					backgroundColor: 'rgba(54, 162, 235, 0.2)',
					fill: true,
					tension: 0.1
				}]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				plugins: {
					title: { display: true, text: 'Balance Over Time' }
				}
			}
		});
	}

	/**
	 * Builds and displays the transaction list table.
	 */
	function drawTransactionTable(data) {
		let tableHTML = '<table class="transaction-table"><thead><tr>' +
			'<th>Date</th><th>Type</th><th>Amount</th><th>Category</th><th>Account</th><th>Note</th>' +
			'</tr></thead><tbody>';

		if (data.length === 0) {
			tableHTML += '<tr><td colspan="6" style="text-align:center;">No transactions found for these filters.</td></tr>';
		} else {
			data.forEach(tx => {
				const date = new Date(tx.transactionDate).toLocaleDateString();
				const amountClass = tx.transactionType === 'Expense' ? 'amount-expense' : 'amount-income';
				tableHTML += `<tr>
                                <td>${date}</td>
                                <td>${tx.transactionType}</td>
                                <td class="${amountClass}">${tx.amount.toFixed(2)}</td>
                                <td>${tx.category ? tx.category.name : 'N/A'}</td>
                                <td>${tx.account.name}</td>
                                <td>${tx.note || ''}</td>
                              </tr>`;
			});
		}
		tableHTML += '</tbody></table>';
		tableContainer.innerHTML = tableHTML;
	}

	/**
	 * Displays an error message in the chart area.
	 */
	function renderError(message) {
		if (currentChart) currentChart.destroy();
		chartCanvas.style.display = 'none';
		tableContainer.style.display = 'block';
		tableContainer.innerHTML = `<p style="text-align:center; color:red;">${message}</p>`;
	}

	/**
	 * Shows or hides the loading spinner.
	 */
	function showSpinner(show) {
		spinner.style.display = show ? 'flex' : 'none';
	}

	/**
	 * Updates the date range options based on the selected graph type.
	 */
	function updateDateRangeOptions() {
		const selectedGraph = graphTypeSelect.value;
		let options = '';

		if (selectedGraph === 'spendingBreakdown') {
			options = `<option value="last30days">Last 30 Days</option>
                       <option value="last3months">Last 3 Months</option>
                       <option value="last6months">Last 6 Months</option>
                       <option value="last12months">Last 12 Months</option>`;
		} else {
			options = `<option value="last30days">Last 30 Days (Daily)</option>
                       <option value="last6months">Last 6 Months (Monthly)</option>
                       <option value="last12months">Last 12 Months (Monthly)</option>`;
		}
		dateRangeSelect.innerHTML = options;
	}

	// --- INITIALIZATION ---
	updateDateRangeOptions();
	fetchReportData();
});
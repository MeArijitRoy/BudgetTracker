function createSpendingChart(canvasId, chartData) {

    const labels = chartData.map(item => item.categoryName);
    const data = chartData.map(item => item.totalAmount);

    const ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.error("Canvas element with ID not found:", canvasId);
        return;
    }

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                label: 'Spending',
                data: data,
                backgroundColor: [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'
                ],
                borderColor: '#fff',
                borderWidth: 2,
                cutout: '45%' 
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true, 
                    position: 'bottom',
                    align: 'center',
                    labels: {
                        boxWidth: 12,
                        padding: 15,
                        font: {
                            size: 11
                        }
                    }
                },
                title: {
                    display: true,
                    text: 'Top Spending This Month',
                    font: {
                        size: 14
                    },
                    padding: {
                        top: 5,
                        bottom: 10
                    }
                }
            }
        }
    });
}


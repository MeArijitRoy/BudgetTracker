<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<title>Records</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/header.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/records.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/modal.css">
	<link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%232D9A7A' rx='15'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' fill='white' font-size='70' font-family='Arial,sans-serif' font-weight='bold'%3EBT%3C/text%3E%3C/svg%3E">
</head>

<body>

	<jsp:include page="header.jsp">
		<jsp:param name="activePage" value="records" />
	</jsp:include>

	<main class="page-container">

		<aside class="filter-panel">
			<h3>Filters</h3>
			<form action="<c:url value='/RecordsServlet' />" method="get">
				<div class="filter-group">
					<label for="filterDate">Date</label> <input type="date"
						id="filterDate" name="filterDate" value="${param.filterDate}">
				</div>

				<div class="filter-group">
					<label for="filterType">Payment Type</label> <select
						id="filterType" name="filterType">
						<option value="" ${empty param.filterType ? 'selected' : ''}>All
							Types</option>
						<option value="Expense"
							${param.filterType == 'Expense' ? 'selected' : ''}>Expense</option>
						<option value="Income"
							${param.filterType == 'Income' ? 'selected' : ''}>Income</option>
						<option value="Transfer"
							${param.filterType == 'Transfer' ? 'selected' : ''}>Transfer</option>
					</select>
				</div>
                
				<div class="filter-group">
					<label for="filterAccount">Account</label> <select
						id="filterAccount" name="filterAccount">
						<option value="">All Accounts</option>
						<c:forEach var="acc" items="${accounts}">
							<option value="${acc.id}"
								${param.filterAccount == acc.id ? 'selected' : ''}>${acc.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="filter-group">
					<label for="filterCategory">Category</label> <select
						id="filterCategory" name="filterCategory">
						<option value="">All Categories</option>
						<c:forEach var="cat" items="${categories}">
							<option value="${cat.id}"
								${param.filterCategory == cat.id ? 'selected' : ''}>${cat.name}</option>
						</c:forEach>
					</select>
				</div>

				<button type="submit" class="filter-btn">Apply Filters</button>
			</form>
		</aside>

		<section class="records-container">
			<div class="records-header">
				<button id="openModalBtn" class="add-record-btn">+ Add
					Record</button>
			</div>
			<div
				style="background-color: white; padding: 10px; border: none; border-radius: 5px;">
				<div style="max-height: 70vh; overflow-y: auto;">
					<div class="records-list">
						<c:if test="${empty transactions}">
							<div class="empty-state">
								<p>No records found. Click "+ Add Record" to get started!</p>
							</div>
						</c:if>

						<c:forEach var="tx" items="${transactions}">
							<div class="transaction-card ${tx.transactionType.toLowerCase()}">
								<div class="date">
									<fmt:formatDate value="${tx.transactionDate}" pattern="dd"
										var="day" />
									<fmt:formatDate value="${tx.transactionDate}" pattern="MMM"
										var="month" />
									<span class="day">${day}</span> <span class="month">${month}</span>
								</div>

								<div class="details">
									<span class="type">${tx.transactionType}</span>
									<div class="note">${tx.note}</div>
								</div>
								<div class="meta">
									<div class="amount">
										₹
										<fmt:formatNumber value="${tx.amount}" type="number"
											minFractionDigits="2" maxFractionDigits="2" />
									</div>
									<div class="category">${tx.category.name}</div>
									<div class="category">${tx.account.name}</div>
								</div>
								
								<%-- CHANGE: Added the three-dots menu for Edit/Delete options --%>
                                <div class="card-actions">
                                    <button class="action-btn">⋮</button>
                                    <div class="action-menu">
                                        <%--<a href="#">Edit</a>--%>
                                        <a href="${pageContext.request.contextPath}/DeleteTransactionServlet?id=${tx.id}"
                                           onclick="return confirm('Are you sure you want to delete this transaction? This action cannot be undone.');"
                                           class="delete-link">Delete</a>
                                    </div>
                                </div>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>

		</section>
	</main>
	<jsp:include page="add_record_modal.jsp" />
	<script src="${pageContext.request.contextPath}/js/records.js"></script>
	<script src="${pageContext.request.contextPath}/js/header.js"></script>

</body>
</html>
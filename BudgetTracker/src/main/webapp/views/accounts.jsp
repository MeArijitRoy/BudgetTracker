<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Accounts</title>
    <%-- Link to the required stylesheets --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/accounts.css">
</head>
<body>

    <%-- Include the reusable header, marking "accounts" as the active page --%>
    <jsp:include page="header.jsp">
        <jsp:param name="activePage" value="accounts"/>
    </jsp:include>

    <main class="page-container">

        <%-- Left Panel: Add New Account Form --%>
        <aside class="add-account-panel">
            <h3>Add New Account</h3>
            <form action="${pageContext.request.contextPath}/AddAccountServlet" method="post">
                
                <c:if test="${not empty formError}">
                    <div class="form-error">${formError}</div>
                </c:if>

                <div class="form-group">
                    <label for="accountName">Account Name</label>
                    <input type="text" id="accountName" name="accountName" required>
                </div>
                <div class="form-group">
                    <label for="accountType">Account Type</label>
                    <select id="accountType" name="accountType" required>
                        <option value="Cash">Cash</option>
                        <option value="Bank Account">Bank Account</option>
                        <option value="Credit Card">Credit Card</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="initialBalance">Initial Balance</label>
                    <input type="number" id="initialBalance" name="initialBalance" step="0.01" value="0.00" required>
                </div>
                <div class="form-group">
                    <label for="currency">Currency</label>
                    <select id="currency" name="currency" required>
                        <option value="INR">INR (Indian Rupee)</option>
                        <option value="USD">USD (US Dollar)</option>
                        <option value="EUR">EUR (Euro)</option>
                    </select>
                </div>
                <div class="form-group color-picker-group">
                    <label for="color">Display Color</label>
                    <input type="color" id="color" name="color" value="#4CAF50">
                </div>
                <button type="submit" class="add-account-btn">Create Account</button>
            </form>
        </aside>

        <%-- Right Panel: List of Account Cards (Now with Dynamic Currency) --%>
        <section class="accounts-container">
            <div class="accounts-list">
                
                <c:if test="${empty accountSummaries}">
                    <div class="empty-state">
                        <p>No accounts found. Add your first account to get started!</p>
                    </div>
                </c:if>

                <c:forEach var="acc" items="${accountSummaries}">
                    <div class="account-card" style="border-left-color: ${acc.color};">
                        <div class="account-card-header">
                            <h4 class="account-name">${acc.name}</h4>
                        </div>
                        <div class="account-card-body">
                            <div class="current-balance">
                                <%-- DYNAMIC CURRENCY SYMBOL --%>
                                <c:choose>
                                    <c:when test="${acc.currency == 'INR'}">₹</c:when>
                                    <c:when test="${acc.currency == 'USD'}">$</c:when>
                                    <c:when test="${acc.currency == 'EUR'}">€</c:when>
                                    <c:otherwise>${acc.currency}</c:otherwise>
                                </c:choose>
                                <fmt:formatNumber value="${acc.currentBalance}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </div>
                            <div class="account-card-stats">
                                <span class="stat-income">
                                    <c:choose>
                                        <c:when test="${acc.currency == 'INR'}">₹</c:when>
                                        <c:when test="${acc.currency == 'USD'}">$</c:when>
                                        <c:when test="${acc.currency == 'EUR'}">€</c:when>
                                    </c:choose>
                                    <fmt:formatNumber value="${acc.totalIncome}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                </span>
                                <span class="stat-expense">
                                    <c:choose>
                                        <c:when test="${acc.currency == 'INR'}">₹</c:when>
                                        <c:when test="${acc.currency == 'USD'}">$</c:when>
                                        <c:when test="${acc.currency == 'EUR'}">€</c:when>
                                    </c:choose>
                                    <fmt:formatNumber value="${acc.totalExpense}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                </span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                
            </div>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/js/header.js"></script>
</body>
</html>


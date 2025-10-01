<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.budgetbakers.entities.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="com.budgetbakers.entities.CategorySpending" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    Gson gson = new Gson();
%>

<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body>

    <jsp:include page="header.jsp">
        <jsp:param name="activePage" value="dashboard"/>
    </jsp:include>

    <main class="dashboard-container">
        
        <section class="kpi-container">
            <div class="kpi-card">
                <div class="kpi-title">Total Balance</div>
                <div class="kpi-value">
                    <%-- DYNAMIC CURRENCY for KPIs --%>
                    <c:choose>
                        <c:when test="${primaryCurrency == 'INR'}">₹</c:when>
                        <c:when test="${primaryCurrency == 'USD'}">$</c:when>
                        <c:when test="${primaryCurrency == 'EUR'}">€</c:when>
                        <c:otherwise>${primaryCurrency}</c:otherwise>
                    </c:choose>
                    <fmt:formatNumber value="${kpis.totalBalance}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-title">Monthly Cash Flow</div>
                <div class="kpi-value" style="color: ${kpis.monthlyCashFlow >= 0 ? '#4CAF50' : '#ef5350'};">
                    <c:choose>
                        <c:when test="${primaryCurrency == 'INR'}">₹</c:when>
                        <c:when test="${primaryCurrency == 'USD'}">$</c:when>
                        <c:when test="${primaryCurrency == 'EUR'}">€</c:when>
                        <c:otherwise>${primaryCurrency}</c:otherwise>
                    </c:choose>
                    <fmt:formatNumber value="${kpis.monthlyCashFlow}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-title">Monthly Spending</div>
                <div class="kpi-value">
                    <c:choose>
                        <c:when test="${primaryCurrency == 'INR'}">₹</c:when>
                        <c:when test="${primaryCurrency == 'USD'}">$</c:when>
                        <c:when test="${primaryCurrency == 'EUR'}">€</c:when>
                        <c:otherwise>${primaryCurrency}</c:otherwise>
                    </c:choose>
                    <fmt:formatNumber value="${kpis.monthlySpending}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                </div>
            </div>
        </section>

        <section class="account-widgets-container">
            <c:if test="${empty accountSummaries}">
                <p>No accounts found. Go to the Accounts page to add your first one!</p>
            </c:if>

            <c:forEach var="account" items="${accountSummaries}" varStatus="loop">
                <div class="account-widget">
                    <div class="widget-header">
                        <h3>${account.name}</h3>
                        <div class="widget-balance">
                           <%-- DYNAMIC CURRENCY for each account widget --%>
                           <c:choose>
                                <c:when test="${account.currency == 'INR'}">₹</c:when>
                                <c:when test="${account.currency == 'USD'}">$</c:when>
                                <c:when test="${account.currency == 'EUR'}">€</c:when>
                                <c:otherwise>${account.currency}</c:otherwise>
                           </c:choose>
                           <fmt:formatNumber value="${account.currentBalance}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                        </div>
                    </div>
                    <div class="widget-chart-container">
                        <% 
                            String spendingJson = "[]";
                            Account currentAccount = (Account) pageContext.getAttribute("account");
                            if (currentAccount != null) {
                                List<CategorySpending> spendingList = currentAccount.getTopSpendingCategories();
                                if (spendingList != null) {
                                    spendingJson = gson.toJson(spendingList);
                                }
                            }
                        %>
                        <canvas class="spending-chart-canvas" 
                                id="spendingChart-${loop.index}" 
                                data-spending='<%= spendingJson %>'></canvas>
                    </div>
                </div>
            </c:forEach>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/js/chart.umd.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const chartCanvases = document.querySelectorAll('.spending-chart-canvas');
            
            chartCanvases.forEach(canvas => {
                const spendingDataJson = canvas.dataset.spending;
                
                if (spendingDataJson) {
                    try {
                        const spendingData = JSON.parse(spendingDataJson);
                        if (spendingData && spendingData.length > 0) {
                            createSpendingChart(canvas.id, spendingData);
                        } else {
                            const ctx = canvas.getContext('2d');
                            ctx.textAlign = 'center';
                            ctx.textBaseline = 'middle';
                            ctx.font = '14px Arial';
                            ctx.fillText('No spending data for this month.', canvas.width/2 , canvas.height/5);
                        }
                    } catch (e) {
                        console.error("Failed to parse spending data:", spendingDataJson, e);
                        const ctx = canvas.getContext('2d');
                        ctx.fillText('Error loading chart data.', canvas.width / 2, canvas.height / 2);
                    }
                }
            });
        });
    </script>
</body>
</html>


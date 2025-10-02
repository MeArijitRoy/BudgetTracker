<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Analysis</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/analysis.css">
    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>
</head>
<body>

    <jsp:include page="header.jsp">
        <jsp:param name="activePage" value="analysis"/>
    </jsp:include>

    <main class="page-container">
        
        <aside class="analysis-controls">
            <h3>Analysis Filters</h3>
            <form id="analysisForm">
                
                <div class="control-group">
                    <label for="graphType">Report Type</label>
                    <select id="graphType" name="graphType">
                        <option value="spendingBreakdown">Spending Breakdown</option>
                        <option value="cashFlowTrend">Cash Flow Trend</option>
                        <option value="balanceTrend">Balance Over Time</option>
                        <option value="transactionList">Transaction List</option>
                    </select>
                </div>

                <div class="control-group">
                    <label for="dateRange">Date Range</label>
                    <select id="dateRange" name="dateRange">
                        <%-- Options will be dynamically populated by JavaScript --%>
                    </select>
                </div>

                <div class="control-group">
                    <label for="currency">Currency</label>
                    <select id="currency" name="currency" required>
                        <c:if test="${empty currencies}">
                            <option value="">No currencies found</option>
                        </c:if>
                        <c:forEach var="cur" items="${currencies}">
                            <option value="${cur}">${cur}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="control-group">
                    <label>Accounts</label>
                    <div class="multi-select-container">
                        <c:forEach var="acc" items="${accounts}">
                            <label class="checkbox-label">
                                <input type="checkbox" name="accounts" value="${acc.id}" checked> ${acc.name}
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <button type="submit" class="apply-btn">Generate Report</button>
            </form>
        </aside>

        <section class="chart-container">
            <div id="chartSpinner" class="spinner-container" style="display: none;">
                <div class="spinner"></div>
                <p>Loading Report...</p>
            </div>
            <canvas id="analysisChart"></canvas>
            <div id="transactionTableContainer" style="display: none;">
                <%-- The transaction data table will be built here by JavaScript --%>
            </div>
        </section>

    </main>

    <script src="${pageContext.request.contextPath}/js/chart.umd.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/analysis.js"></script>
    <script src="${pageContext.request.contextPath}/js/header.js"></script>

</body>
</html>


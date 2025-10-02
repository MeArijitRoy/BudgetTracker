<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- This file contains the top navigation bar with a dynamic profile dropdown. --%>
<header class="header">
    <div class="logo">BudgetTracker</div>
    <nav>
        <ul>
            <%-- The 'active' class is applied based on the 'activePage' parameter passed from the including page --%>
            <li><a href="${pageContext.request.contextPath}/DashboardServlet" 
                   class="${param.activePage == 'dashboard' ? 'active' : ''}">Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/AccountsServlet" 
                   class="${param.activePage == 'accounts' ? 'active' : ''}">Accounts</a></li>
            <li><a href="${pageContext.request.contextPath}/RecordsServlet" 
                   class="${param.activePage == 'records' ? 'active' : ''}">Records</a></li>
            <li><a href="${pageContext.request.contextPath}/AnalysisPageServlet" 
                   class="${param.activePage == 'analysis' ? 'active' : ''}">Analysis</a></li>
            <li><a href="#" 
                   class="${param.activePage == 'investments' ? 'active' : ''}">Investments</a></li>
            <li><a href="#" 
                   class="${param.activePage == 'imports' ? 'active' : ''}">Imports</a></li>
        </ul>
    </nav>
    
    <div class="profile-container">
        <div class="profile-icon" id="profileIcon"></div>
        <div class="profile-dropdown" id="profileDropdown">
            <a href="#">Settings</a>
            <a href="<c:url value='/LogoutServlet'/>">Logout</a>
        </div>
    </div>
</header>
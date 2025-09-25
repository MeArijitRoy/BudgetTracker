<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- This file contains the top navigation bar with dynamic active links. --%>
<header class="header">
    <div class="logo">BudgetTracker</div>
    <nav>
        <ul>
            <%-- The 'active' class is applied based on the 'activePage' parameter passed from the including page --%>
            <li><a href="${pageContext.request.contextPath}/views/home.jsp" 
                   class="${param.activePage == 'dashboard' ? 'active' : ''}">Dashboard</a></li>
            <li><a href="#" 
                   class="${param.activePage == 'accounts' ? 'active' : ''}">Accounts</a></li>
            <li><a href="${pageContext.request.contextPath}/RecordsServlet" 
                   class="${param.activePage == 'records' ? 'active' : ''}">Records</a></li>
            <li><a href="#" 
                   class="${param.activePage == 'analysis' ? 'active' : ''}">Analysis</a></li>
            <li><a href="#" 
                   class="${param.activePage == 'investments' ? 'active' : ''}">Investments</a></li>
            <li><a href="#" 
                   class="${param.activePage == 'imports' ? 'active' : ''}">Imports</a></li>
        </ul>
    </nav>
    <div class="profile-icon"></div>
</header>


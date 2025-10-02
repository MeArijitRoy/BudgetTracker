<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Set New Password</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
	<link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%232D9A7A' rx='15'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' fill='white' font-size='70' font-family='Arial,sans-serif' font-weight='bold'%3EBT%3C/text%3E%3C/svg%3E">
</head>
<body>
    <div class="left-panel">
        <h1>Your Finances in One Place</h1>
        <p>Dive into reports, build budgets, sync with your banks and enjoy automatic categorization.</p>
    </div>
    <div class="right-panel">
        <div class="form-container">
            <h2>Set Your New Password</h2>
            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>
            <form action="<c:url value='/SetPasswordServlet'/>" >
                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm New Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
                <button type="submit" class="submit-btn">Set Password</button>
            </form>
        </div>
    </div>
</body>
</html>

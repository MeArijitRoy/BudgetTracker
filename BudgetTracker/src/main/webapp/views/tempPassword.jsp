<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Enter Temporary Password</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <style>
        .info-message {
            text-align: center;
            color: #333;
            background-color: #e2f2ff;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #b8dcf2;
        }
    </style>
</head>
<body>
    <div class="left-panel">
        <h1>Your Finances in One Place</h1>
        <p>Dive into reports, build budgets, sync with your banks and enjoy automatic categorization.</p>
    </div>
    <div class="right-panel">
        <div class="form-container">
            <h2>Verification Required</h2>
            <p style="text-align:center; color: #555; margin-bottom: 20px;">Enter the temporary password sent to your email.</p>
            <form action="<c:url value='/TempPasswordServlet'/>">
                <div class="form-group">
                    <label for="tempPassword">Temporary Password</label>
                    <input type="text" id="tempPassword" name="tempPassword" required>
                </div>
                <button type="submit" class="submit-btn">Verify</button>
            </form>
        </div>
    </div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>An Error Occurred</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f7fafc;
            text-align: center;
        }
        .container {
            background-color: white;
            padding: 50px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        h1 {
            color: #e53e3e; /* Red for error */
            font-size: 2.5rem;
            margin-bottom: 20px;
        }
        p {
            color: #555;
            font-size: 1.2rem;
            margin-bottom: 30px;
        }
        a {
            display: inline-block;
            padding: 12px 25px;
            background-color: #2D9A7A;
            color: white;
            border-radius: 8px;
            font-size: 1rem;
            text-decoration: none;
            transition: background-color 0.3s;
        }
        a:hover {
            background-color: #267D65;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Oops! Something Went Wrong</h1>
        <p>We're sorry, but an unexpected error occurred. Please try again later.</p>
        <p>
            <c:if test="${not empty errorMessage}">
                <em><c:out value="${errorMessage}" /></em>
            </c:if>
        </p>
        <a href="${pageContext.request.contextPath}/views/home.jsp">Return to Dashboard</a>
    </div>
</body>
</html>

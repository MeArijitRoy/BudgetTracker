<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/records.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <style>
        /* Styles to recreate the original simple layout */
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f4f7f6;
        }
        
        /* This main-content area will now be the centered part */
        .main-content {
            display: flex;
            justify-content: center;
            align-items: center;
            /* Calculate height minus the approximate header height */
            height: calc(100vh - 70px); 
        }

        .container {
            text-align: center;
            background-color: white;
            padding: 50px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .container h1 {
            color: #2D9A7A;
            font-size: 3rem;
            margin-bottom: 20px;
        }

        .container p {
            color: #555;
            font-size: 1.2rem;
            margin-bottom: 30px;
        }

        .logout-btn {
            display: inline-block;
            padding: 12px 25px;
            background-color: #e53e3e;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.3s;
        }

        .logout-btn:hover {
            background-color: #c53030;
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp">
        <jsp:param name="activePage" value="dashboard"/>
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <h1>Welcome to Your Dashboard!</h1>
            <p>You have successfully logged in.</p>
            
            <form action="<c:url value='/LogoutServlet' />">
                <button type="submit" class="logout-btn">Logout</button>
            </form>
        </div>
    </main>

</body>
<script src="${pageContext.request.contextPath}/js/header.js"></script>
</html>


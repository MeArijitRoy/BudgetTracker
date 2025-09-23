<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Home</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Roboto', sans-serif; margin: 0; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f7fafc; }
        .container { text-align: center; background-color: white; padding: 50px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h1 { color: #2D9A7A; font-size: 3rem; margin-bottom: 20px; }
        p { color: #555; font-size: 1.2rem; margin-bottom: 30px; }
        .logout-btn { display: inline-block; padding: 12px 25px; background-color: #e53e3e; color: white; border: none; border-radius: 8px; font-size: 1rem; cursor: pointer; text-decoration: none; transition: background-color 0.3s; }
        .logout-btn:hover { background-color: #c53030; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to Your Dashboard!</h1>
        <p>You have successfully logged in.</p>
        
        <%-- Using c:url makes the link robust and always correct --%>
        <form action="<c:url value='/LogoutServlet' />">
            <button type="submit" class="logout-btn">Logout</button>
        </form>
    </div>
</body>
</html>


<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Log In</title>
<link
	href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/login.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/gbutton.css">
<style>
.divider {
	text-align: center;
	color: #aaa;
	margin: 25px 0;
	position: relative;
}

.divider:before, .divider:after {
	content: '';
	position: absolute;
	top: 50%;
	width: 40%;
	height: 1px;
	background: #ddd;
}

.divider:before {
	left: 0;
}

.divider:after {
	right: 0;
}

.google-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	text-decoration: none;
	width: 100%;
	padding: 12px;
	background-color: #fff;
	color: #555;
	border: 1px solid #ccc;
	border-radius: 8px;
	font-size: 1rem;
	cursor: pointer;
	transition: background-color 0.3s, box-shadow 0.3s;
}

.google-btn:hover {
	background-color: #f8f8f8;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.google-btn svg {
	margin-right: 12px;
	width: 20px;
	height: 20px;
}
</style>
</head>
<body>
	<div class="left-panel">
		<h1>Your Finances in One Place</h1>
		<p>Dive into reports, build budgets, sync with your banks and
			enjoy automatic categorization.</p>
	</div>
	<div class="right-panel">
		<div class="form-container">
			<h2>Log In</h2>
			<c:if test="${not empty message}">
				<p class="message">${message}</p>
			</c:if>
			<form action="LoginServlet">
				<div class="form-group">
					<label for="email">E-mail</label> <input type="email" id="email"
						name="email" placeholder="john.doe@example.com" required>
				</div>
				<button type="submit" class="submit-btn">Log In with E-mail</button>
			</form>

			<div class="divider">or</div>
			<a href="https://accounts.google.com/o/oauth2/v2/auth?response_type=code&scope=openid%20email%20profile&client_id=934914497238-c03grsrq4cv6ihgj69vbflvqrfm15j2e.apps.googleusercontent.com&redirect_uri=http://localhost:8080/BudgetTracker/GoogleCallbackServlet"
				class="google-btn"> 
				<img src="https://developers.google.com/identity/images/g-logo.png"
				alt="Google logo" class="google-icon"> 
				<span>Sign in with Google</span>
			</a>
		</div>
	</div>
</body>
</html>


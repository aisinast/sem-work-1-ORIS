<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title>Откройте для себя удивительные места по всему миру — Spotty</title>
        <link rel="stylesheet" href="styles/welcome-page-style.css">
    </head>

    <body>
        <div class="main-container">
            <img src="images/logo.PNG" alt="Spotty">
            <h3>Откройте для себя удивительные места по всему миру</h3>

            <div id="horizontal-container">
                <a href="${pageContext.request.contextPath}/main-page">Продолжить без регистрации</a>
                <a href="${pageContext.request.contextPath}/registration">Зарегистрироваться / войти</a>
            </div>
        </div>
    </body>
</html>

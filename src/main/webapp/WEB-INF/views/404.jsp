<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title>Страница не найдена :(</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/404-style.css">

        <style>
            body{
                min-height:100vh;
                display:flex; align-items:center; justify-content:center;
                background-image: url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size: cover;
                color: var(--modal-text-color);
                font-family: "SF Pro", -apple-system, BlinkMacSystemFont, sans-serif;
            }
        </style>
    </head>

    <body>
        <div class="main-container">
            <div class="left-container">
                <h1 class="error-title">404</h1>

                <p class="subtitle">
                    Похоже, вы заблудились. Такой страницы здесь нет, но можно
                    <a class="link" href="${pageContext.request.contextPath}/main-page">вернуться</a>
                    и продолжить путешествие
                </p>
            </div>

            <div class="right-container">
                <img class="sad-frog-image" src="${pageContext.request.contextPath}/images/frog.png" alt="sad frog">
            </div>
        </div>
    </body>
</html>

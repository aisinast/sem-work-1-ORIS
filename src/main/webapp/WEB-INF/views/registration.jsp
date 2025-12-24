<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>

<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Регистрация — Spotty</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/modal-styles.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

        <style>
            body{
                min-height:100vh;
                display:flex; align-items:center; justify-content:center;
                background-image: url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size: cover;
                color: var(--modal-text-color);
            }
        </style>

    </head>
    <body>
        <main class="modal-card" role="main">
            <div class="modal-topbar">
                <a class="modal-close-btn" href="${pageContext.request.contextPath}/main-page" title="Закрыть">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </div>

            <h1 class="modal-title">Регистрация</h1>
            <p class="modal-subtitle">Создайте аккаунт, чтобы сохранять места и делиться впечатлениями</p>

            <c:if test="${not empty error}">
                <div class="modal-alert modal-alert--error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/registration" method="post" class="modal-form">
                <div class="modal-form-field">
                    <label for="email">Электронная почта</label>
                    <input type="email" id="email" name="email"
                           class="modal-input" placeholder="you@example.com"
                           value="${param.email}" required>
                </div>

                <div class="modal-form-field">
                    <label for="username">Логин</label>
                    <input type="text" id="username" name="username"
                           class="modal-input" placeholder="Ваш логин"
                           value="${param.username}" required>
                </div>

                <div class="modal-form-field">
                    <label for="password">Пароль</label>
                    <input type="password" id="password" name="password"
                           class="modal-input" placeholder="Придумайте пароль"
                           required>
                </div>

                <div class="modal-form-field">
                    <label for="confirm_password">Повторите пароль</label>
                    <input type="password" id="confirm_password" name="confirm_password"
                           class="modal-input" placeholder="Введите пароль ещё раз"
                           required>
                </div>

                <button type="submit" class="modal-btn modal-btn--primary modal-btn--full">Зарегистрироваться</button>
            </form>

            <div class="modal-redirect">
                <span>Уже есть аккаунт?</span>
                <a class="modal-link" href="${pageContext.request.contextPath}/login">Войти</a>
            </div>
        </main>
    </body>

    <script src="${pageContext.request.contextPath}/js/preview-image.js"></script>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>Добавить место — Spotty</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/create-post-styles.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <style>
            body{
                min-height:100vh;
                margin:0;
                display:flex;
                align-items:flex-start;
                justify-content:center;
                background-image:url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size:cover;
                background-position:center;
                padding:32px 16px;
                color:var(--text);
                font-family:"SF Pro",-apple-system,BlinkMacSystemFont,sans-serif;
            }
        </style>
    </head>
    <body>
        <main class="card" role="main">
            <h1 class="title">Добавить место</h1>
            <p class="desc">Откройте миру новую локацию</p>

            <c:if test="${not empty error}">
                <div class="modal-alert modal-alert--error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/create-place" method="post" id="postForm">
                <div class="grid">
                    <div class="field full">
                        <label class="label">Название локации</label>
                        <input type="text" id="place_name" name="place_name"
                            maxlength="120" placeholder="Введите название локации" required>

                        <p class="desc">Введите адрес</p>
                        <label class="label">Страна</label>
                        <input type="text" id="country" name="country"
                               maxlength="60" placeholder="Введите страну" required>

                        <label class="label">Город</label>
                        <input type="text" id="city" name="city"
                               maxlength="60" placeholder="Введите город" required>

                        <label class="label">Улица</label>
                        <input type="text" id="street" name="street"
                               maxlength="30" placeholder="Введите название улицы (или название географического объекта)" required>

                        <label class="label">Дом</label>
                        <input type="text" id="house_number" name="house_number"
                               maxlength="10" placeholder="Введите номер дома (если нет, ставьте '-')" required>
                    </div>

                    <div class="footer">
                        <a class="btn" href="${pageContext.request.contextPath}/choose-place">Отмена</a>
                        <button class="btn btn-primary" type="submit">Создать</button>
                    </div>
                </div>
            </form>
        </main>
    </body>
</html>

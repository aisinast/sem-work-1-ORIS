<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <meta charset="UTF-8">
        <title>Выбор места — Spotty</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/сhoose-place-styles.css">
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
        <main class="search-modal-card" role="main">
            <div class="topbar">
                <a class="profile-close-btn" href="${pageContext.request.contextPath}/posts/create" title="Закрыть">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </div>

            <h1 class="header">Поиск</h1>

            <form action="${pageContext.request.contextPath}/choose-place" method="post" class="choose-form" style="margin-bottom:18px;">
                <div class="search-form-field">
                    <input type="search" id="search_query" name="query"
                           class="modal_input" placeholder="Напишите, что хотите найти"
                           value="${param.query}">
                    <button class="search-button" type="submit">
                        <i class="bi bi-search-heart"></i>
                    </button>
                </div>
            </form>

            <form action="${pageContext.request.contextPath}/posts/create" method="get">
                <div class="results">
                    <c:forEach var="place" items="${places}">
                        <label class="place-radio-option">
                            <input type="radio" name="placeId" value="${place.placeId}" required>
                            <my:place-card place="${place}"></my:place-card>
                        </label>
                    </c:forEach>
                    <c:if test="${empty places}">
                        <div class="muted">Места не найдены</div>
                    </c:if>
                </div>

                <button type="submit" class="btn-primary" style="margin-top:18px;">Сохранить</button>
            </form>

            <div class="redirect">
                <span>Не нашли место?</span>
                <a class="link" href="${pageContext.request.contextPath}/create-place">Создать</a>
            </div>
        </main>
    </body>
</html>
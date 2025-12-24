<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
           
<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>${place.placeName} — место Spotty</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/place-page.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/post-card-styles.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

        <script>
            const contextPath = '${pageContext.request.contextPath}';
        </script>

        <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU"></script>
        <style>
            body{
                min-height:100vh;
                display:flex;
                align-items:center;
                justify-content:center;
                background-image: url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size: cover;
                font-family:"SF Pro",-apple-system,BlinkMacSystemFont,sans-serif;
            }
        </style>
    </head>
    <body>
        <div class="container">

            <div class="topbar">
                <a class="close-btn" href="${pageContext.request.contextPath}/main-page" title="Закрыть">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </div>

            <div class="place-title">${place.placeName}</div>

            <div class="address">${place.fullAddress}</div>

            <div id="map"></div>
            <div id="map-data"
                 data-lat="${place.latitude}"
                 data-lng="${place.longitude}"
                 data-name="${place.placeName}"
                 data-address="${place.fullAddress}">
            </div>

            <h3>Посты</h3>

            <ul class="posts-list">
                <c:forEach var="post" items="${posts}">
                    <my:post-card post="${post}"></my:post-card>
                </c:forEach>
                <c:if test="${empty posts}">
                    <li style="color:#bbb;">Постов пока нет.</li>
                </c:if>
            </ul>
        </div>
    </body>

    <script src="${pageContext.request.contextPath}/js/toggle-like.js"></script>
    <script src="${pageContext.request.contextPath}/js/ymaps-map.js"></script>

</html>

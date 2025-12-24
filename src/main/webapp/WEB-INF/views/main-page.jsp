<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta charset="UTF-8" />
        <title>Spotty — главная</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/post-card-styles.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main-page-styles.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/pagination-styles.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

        <script>
            const contextPath = '${pageContext.request.contextPath}';
            console.log('Context Path initialized:', contextPath);
        </script>
    </head>

    <body>
        <header class="topbar">
            <a href="${pageContext.request.contextPath}/" class="brand">
                <span class="dot"></span> Spotty
            </a>
            <div class="actions">
                <a class="icon-btn" href="${pageContext.request.contextPath}/search" title="Поиск"><i class="bi bi-search-heart-fill"></i></a>
                <a class="icon-btn" href="${pageContext.request.contextPath}/posts/create" title="Создать пост"><i class="bi bi-plus-square-fill"></i></a>
                <a class="icon-btn" href="${pageContext.request.contextPath}/profile" title="Профиль"><i class="bi bi-person-circle"></i></a>
            </div>
        </header>

        <main class="container">
            <c:choose>
                <c:when test="${empty posts}">
                    <div class="empty">
                        Пусто... Создайте первый пост или воспользуйтесь поиском выше.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="feed">
                        <c:forEach items="${posts}" var="p">
                            <my:post-card post="${p}"></my:post-card>
                        </c:forEach>
                    </div>
                </c:otherwise>

            </c:choose>
        </main>

        <my:pagination currentPage="${postPage.currentPage}" totalPages="${postPage.totalPages}"></my:pagination>
    </body>

    <script src="${pageContext.request.contextPath}/js/toggle-like.js"></script>
</html>

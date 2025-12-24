<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <meta charset="UTF-8">
        <title>Поиск — Spotty</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/search-styles.css">
        <style>
            body{
                min-height:100vh; margin:0; display:flex; align-items:flex-start; justify-content:center;
                background-image:url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size:cover; background-position:center; padding:32px 16px; color:var(--text);
                font-family:"SF Pro",-apple-system,BlinkMacSystemFont,sans-serif;
            }
        </style>
    </head>
    <body>
        <main class="search-modal-card" role="main">
            <div class="topbar">
                <a class="profile-close-btn" href="${pageContext.request.contextPath}/main-page" title="Закрыть">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </div>

            <h1 class="header">Поиск</h1>

            <div class="search-container">
                <form action="${pageContext.request.contextPath}/search" method="post" class="search-form">
                    <div class="search-form-field">
                        <input type="search" id="search_query" name="query"
                               class="modal_input" placeholder="Напишите, что хотите найти"
                               value="${param.query}" required>
                        <input type="hidden" name="tab" value="${empty param.tab ? 'accounts' : param.tab}"/>
                        <button class="search-button" type="submit">
                            <i class="bi bi-search-heart"></i>
                        </button>
                    </div>

                    <section class="tabs">
                        <nav class="tab-bar">
                            <a class="tab ${empty param.tab || param.tab eq 'accounts' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/search?query=${fn:escapeXml(param.query)}&tab=accounts">Аккаунты</a>
                            <a class="tab ${param.tab eq 'places' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/search?query=${fn:escapeXml(param.query)}&tab=places">Места</a>
                        </nav>
                    </section>
                </form>

                <c:choose>
                    <c:when test="${empty param.tab || param.tab eq 'accounts'}">
                        <div class="results">
                            <c:forEach var="user" items="${users}">
                                <my:profile-card user="${user}"></my:profile-card>
                            </c:forEach>
                            <c:if test="${empty users}">
                                <div class="muted">Никого не нашли</div>
                            </c:if>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="results">
                            <c:forEach var="place" items="${places}">
                               <my:place-card place="${place}"></my:place-card>
                            </c:forEach>
                            <c:if test="${empty places}">
                                <div class="muted">Места не найдены</div>
                            </c:if>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </body>
</html>

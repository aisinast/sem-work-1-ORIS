<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Профиль — Spotty</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/post-card-styles.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/profile-styles.css">

        <script>
            const contextPath = '${pageContext.request.contextPath}';
        </script>

        <style>
            body{
                min-height:100vh;
                display:flex;
                justify-content:center;
                background-image: url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size: cover;
                font-family: "SF Pro", -apple-system, BlinkMacSystemFont, sans-serif;
                overflow-y: auto;
                padding: 20px 0;
            }
        </style>
    </head>

    <body>
        <main class="profile-modal-card" role="main">
            <div class="profile-topbar">
                <a class="profile-close-btn" href="${pageContext.request.contextPath}/main-page" title="Закрыть">
                    <i class="bi bi-x-circle-fill"></i>
                </a>


                <c:if test="${isOwnProfile}">
                    <div class="u-meta">
                        <a class="btn" href="${pageContext.request.contextPath}/profile/edit">
                            Редактировать профиль
                        </a>

                        <a class="btn" id="logoutBtn" data-context-path="${pageContext.request.contextPath}">
                            Выйти из аккаунта
                        </a>
                    </div>
                </c:if>

                <c:if test="${not isOwnProfile and principal != null}">
                    <form id="followForm" method="post" action="${pageContext.request.contextPath}/profile/follow" >
                        <input type="hidden" name="userId" value="${user.userId}"/>
                        <button type="submit"
                                class="btn ${isFollowing ? '' : 'btn-primary'}"
                                id="followBtn">
                            <span>${isFollowing ? 'Отписаться' : 'Подписаться'}</span>
                        </button>
                    </form>
                </c:if>
            </div>

            <section class="profile-card">
                <img class="avatar"
                     src="${user.avatarUrl != null ? pageContext.request.contextPath.concat(user.avatarUrl) :
                                pageContext.request.contextPath.concat('/images/default-avatar.png')}"
                     alt="${user.username}">
                <div class="u-meta">
                    <div class="modal-title">${user.username}</div>
                    <p class="modal-subtitle"><c:out value="${empty user.bio ? 'Пока без описания' : user.bio}"/></p>
                </div>
            </section>

            <div class="horizontal-line"></div>

            <div class="stats">
                <div class="stats-item"><b>${postsCount}</b> <span>постов</span></div>
                <div class="stats-item"><b>${followersCount}</b> <span>подписчиков</span></div>
                <div class="stats-item"><b>${followingCount}</b> <span>подписок</span></div>
            </div>

            <section class="tabs">
                <nav class="tab-bar">
                    <c:choose>
                        <c:when test="${isOwnProfile}">
                            <a class="tab ${param.tab eq 'liked' ? '' : 'active'}"
                               href="${pageContext.request.contextPath}/profile?tab=my">Мои посты</a>
                            <a class="tab ${param.tab eq 'liked' ? 'active' : ''}"
                               href="${pageContext.request.contextPath}/profile?tab=liked">Понравившиеся</a>
                        </c:when>
                        <c:otherwise>
                            <a class="tab active" href="#">Посты пользователя</a>
                        </c:otherwise>
                    </c:choose>
                </nav>

                <c:choose>
                    <c:when test="${param.tab eq 'liked' && isOwnProfile}">
                        <c:if test="${empty liked}">
                            <div class="empty">Нет сохранённых постов</div>
                        </c:if>
                        <div class="feed">
                            <c:forEach var="post" items="${liked}">
                                <my:post-card post="${post}"/>
                            </c:forEach>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <c:if test="${empty myPosts}">
                            <div class="empty">
                                <c:choose>
                                    <c:when test="${isOwnProfile}">
                                        У вас ещё нет постов
                                    </c:when>
                                    <c:otherwise>
                                        У пользователя пока нет постов
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                        <div class="feed">
                            <c:forEach var="post" items="${myPosts}">
                                <my:post-card post="${post}"/>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>
    </body>

    <script src="${pageContext.request.contextPath}/js/logout.js"></script>
    <script src="${pageContext.request.contextPath}/js/toggle-like.js"></script>
</html>
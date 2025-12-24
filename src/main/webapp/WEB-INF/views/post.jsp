<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>${post.title}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/post-view.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

        <script>
            const contextPath = '${pageContext.request.contextPath}';
        </script>

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
        <div class="post-card">
            <img class="post-img" src="${pageContext.request.contextPath.concat(post.imageUrl)}" alt="Обложка поста">
            <div class="post-main">
                <div class="post-head">
                    <h1 class="post-title">${post.title}</h1>

                    <div class="post-actions-right">
                        <a class="btn-back" href="${pageContext.request.contextPath}/main-page">
                            <i class="bi bi-arrow-left"></i>
                            <span>Назад</span>
                        </a>

                        <c:if test="${currentUserId == post.authorId}">
                            <a class="post-edit" href="${pageContext.request.contextPath}/edit-post?post_id=${post.postId}">
                                <i class="bi bi-pencil"></i> Изменить
                            </a>

                            <button
                                    type="button"
                                    class="btn-back"
                                    onclick="deletePost('${pageContext.request.contextPath}', '${post.postId}')">
                                Удалить
                            </button>
                        </c:if>
                    </div>
                </div>
                <div class="post-meta">
                    <a href="${pageContext.request.contextPath}/profile/${post.authorId}" class="meta-link">
                        <i class="bi bi-person"></i> ${post.authorUsername}
                    </a>
                    <span class="meta-place">
                        <i class="bi bi-geo-alt"></i>
                        <a href="${pageContext.request.contextPath}/places/${post.placeId}" class="meta-link">${post.placeName}</a>
                    </span>
                    <span class="meta-date">
                        <i class="bi bi-calendar-event"></i>
                        <span><c:out value="${post.formattedCreatedAt}" /></span>
                    </span>
                </div>

                <c:if test="${not empty tags}">
                    <div class="post-tags">
                        <div class="tags-strip">
                            <c:forEach var="t" items="${tags}">
                                <span class="tag" data-tag-id="${t.tagId}" data-cat="${t.category}">
                                    <i class="bi bi-hash"></i>
                                    <c:out value="${t.tagName}" />
                                </span>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

                <div class="post-content">
                    ${post.content}
                </div>
                <div class="post-actions">
                    <button
                            class="like-btn ${post.currentUserLiked ? 'liked' : ''}"
                            data-post-id="${post.postId}"
                            data-liked="${post.currentUserLiked}"
                            onclick="toggleLike('${post.postId}', ${post.currentUserLiked})">
                        <i class="bi bi-hand-thumbs-up${post.currentUserLiked ? '-fill' : ''}"></i>
                        <span class="likes-count">${post.likes}</span>
                    </button>
                </div>
            </div>
        </div>
    </body>

    <script src="${pageContext.request.contextPath}/js/delete-post.js"></script>
    <script src="${pageContext.request.contextPath}/js/toggle-like.js"></script>
</html>

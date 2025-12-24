<%@ tag description="Post tag" pageEncoding="UTF-8" %>
<%@ attribute name="post" required="true" type="ru.itis.spotty.models.Post" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<article class="post">
    <img class="post-cover" src="${pageContext.request.contextPath.concat(post.imageUrl)}">

    <div class="post-body">
        <a class="post-title" href="${pageContext.request.contextPath}/posts/${post.postId}">${post.title}</a>

        <div class="post-meta">
            <span>
                <div class="user-info">
                    <i class="bi bi-person"></i>
                    <a href="${pageContext.request.contextPath}/profile/${post.authorId}" class="meta-info">${post.authorUsername}</a>
                </div>

                <div class="place-info">
                    <a href="${pageContext.request.contextPath}/places/${post.placeId}" class="meta-info">${post.placeName}</a>
                </div>

                <div class="created-at-info">
                    <i class="bi bi-calendar-event"></i>
                    <p class="created-at-info">${post.formattedCreatedAt}</p>
                </div>
            </span>
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
</article>
<%@ tag description="Profile card" pageEncoding="UTF-8"%>
<%@ attribute name="user" required="true" type="ru.itis.spotty.models.User" %>

<article class="result-card">
    <a class="user-row" href="${pageContext.request.contextPath}/profile/${user.userId}">
        <img class="avatar"
             src="${user.avatarUrl != null ? pageContext.request.contextPath.concat(user.avatarUrl) :
                        pageContext.request.contextPath.concat('/images/default-avatar.png')}"
             alt="${user.username}">
        <div class="user-info">
            <div class="muted">${user.username}</div>
            <div class="biography">${user.bio}</div>
        </div>
    </a>
</article>

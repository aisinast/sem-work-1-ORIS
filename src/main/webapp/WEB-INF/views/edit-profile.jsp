<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title>Редактировать профиль — Spotty</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/modal-styles.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/profile-styles.css">

        <style>
            body{
                min-height:100vh;
                display:flex; align-items:center; justify-content:center;
                background-image: url("${pageContext.request.contextPath}/images/world-map.PNG");
                background-size: cover;
                color: var(--modal-text-color);
            }

            .avatar-preview {
                width: 100px;
                height: 100px;
                border-radius: 50%;
                object-fit: cover;
                margin: 0 auto 10px;
                display: block;
                border: 2px solid #ddd;
            }

            .avatar-wrap .avatar-preview {
                width: 100px !important;
                height: 100px !important;
            }
        </style>
    </head>

    <body>
        <main class="modal-card" role="main">
            <div class="modal-topbar">
                <a class="modal-close-btn" href="${pageContext.request.contextPath}/profile" title="Закрыть">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </div>

            <h1 class="modal-title">Редактировать профиль</h1>
            <p class="modal-subtitle">Обновите имя, био и изображение профиля</p>

            <c:if test="${not empty error}">
                <div class="modal-alert modal-alert--error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/profile/edit" method="post"
                  class="modal-form" enctype="multipart/form-data">

                <div class="avatar-wrap" style="text-align:center; margin: 6px 0 14px;">
                    <img class="avatar-preview" id="avatarPreview"
                         src="${user.avatarUrl != null ? pageContext.request.contextPath.concat(user.avatarUrl) : pageContext.request.contextPath.concat('/images/default-avatar.png')}"
                         alt="${user.username}"
                         onerror="this.src='${pageContext.request.contextPath}/images/default-avatar.png'">
                </div>

                <div class="modal-form-field">
                    <label for="username">Логин</label>
                    <input type="text" id="username" name="username"
                           class="modal-input" placeholder="${user.username}"
                           value="${user.username}" required>
                </div>

                <div class="modal-form-field">
                    <label for="email">Почта</label>
                    <input type="email" id="email" name="email"
                           class="modal-input" placeholder="${user.email}"
                           value="${user.email}" required>
                </div>

                <div class="modal-form-field">
                    <label for="bio">Описание</label>
                    <input type="text" id="bio" name="bio"
                           class="modal-input" placeholder="${user.bio != null ? user.bio : 'Пока без описания'}"
                           value="${user.bio}">
                </div>

                <div class="modal-form-field">
                    <label for="avatar">Аватар</label>
                    <input type="file" id="avatar" name="avatar"
                           class="modal-input" accept="image/jpeg,image/png,image/jpg,image/gif,image/webp"/>
                    <small style="color: #666; font-size: 12px;">
                        Поддерживаемые форматы: JPEG, PNG, GIF, WebP. Максимальный размер: 5MB
                    </small>
                </div>

                <button type="submit" class="modal-btn modal-btn--full modal-btn--primary">Сохранить изменения</button>
            </form>
        </main>
    </body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta charset="UTF-8">

        <title>Создать пост — Spotty</title>

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/create-post-styles.css">
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
        <main class="card" role="main">
            <h1 class="title">Создать пост</h1>
            <p class="desc">Поделитесь местом и впечатлениями: добавьте заголовок, фото, выберите место и отметьте теги.</p>

            <c:if test="${not empty error}">
                <div class="modal-alert modal-alert--error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/posts/create" method="post" enctype="multipart/form-data" id="postForm">

                <c:if test="${not empty place}">
                    <input type="hidden" name="placeId" value="${place.placeId}">
                </c:if>

                <div class="grid">

                    <div class="field full">
                        <label class="label" for="title">Заголовок</label>
                        <input type="text" id="title" name="title"
                               maxlength="120" placeholder="Например: Лучшее кафе на набережной" required>$
                    </div>


                    <a class="field place-picker"
                       style="position:relative;max-width:400px"
                       href="${pageContext.request.contextPath}/choose-place${not empty place ? '?currentPlaceId=' += place.placeId : ''}">
                        <c:choose>
                            <c:when test="${not empty place}">
                                <div>
                                    <div style="font-weight:700">${place.placeName}</div>
                                    <div class="muted">${place.fullAddress}</div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                Выберите место
                            </c:otherwise>
                        </c:choose>
                    </a>

                    <div class="field full">
                        <label class="label" for="content">Текст поста</label>
                        <textarea id="content" name="content" placeholder="Опишите атмосферу, блюда, впечатления..." required></textarea>
                    </div>

                    <div class="field">
                        <label class="label">Фотография</label>
                        <div class="upload">
                            <label class="btn" for="photo"><i class="bi bi-paperclip"></i> Прикрепить</label>
                            <input type="file" id="photo" name="photo" accept="image/*">
                            <span class="hint" id="photoName">Файл не выбран</span>
                        </div>
                    </div>

                    <div class="field full">
                        <label class="label">Теги</label>
                        <div class="tags-strip" id="tagsStrip">
                            <c:forEach var="t" items="${tags}">
                                        <span class="tag"
                                              data-tag-id="${t.tagId}"
                                              data-cat="${t.category}">
                                                ${t.tagName}
                                        </span>
                            </c:forEach>
                        </div>
                        <input type="hidden" name="tagIds" id="tagIds" value="">
                        <span class="hint">Нажмите на тег, чтобы прикрепить; повторно — чтобы открепить.</span>
                    </div>

                </div>

                <div class="footer">
                    <a class="btn" href="${pageContext.request.contextPath}/main-page">Отмена</a>
                    <button class="btn btn-primary" type="submit">Опубликовать</button>
                </div>
            </form>
        </main>

        <script src="${pageContext.request.contextPath}/js/create-post.js"></script>
    </body>
</html>
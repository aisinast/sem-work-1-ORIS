<%@ tag description="Place card" pageEncoding="UTF-8" %>
<%@ attribute name="place" required="true" type="ru.itis.spotty.models.Place" %>

<article class="result-card">
    <a class="title" href="${pageContext.request.contextPath}/places/${place.placeId}">
        ${place.placeName}
    </a>
    <div class="muted">${place.fullAddress}</div>
</article>
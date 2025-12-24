<%@ tag description="Pagination" pageEncoding="UTF-8" %>
<%@ attribute name="currentPage" required="true" %>
<%@ attribute name="totalPages" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="size" value="${not empty param.size ? param.size : 6}" />
<c:set var="orderBy" value="${not empty param.orderBy ? param.orderBy : 'newest'}" />
<c:set var="window" value="2" />

<div class="pagination">
    <c:choose>
        <c:when test="${currentPage == 1}">
            <span class="page-btn disabled">‹</span>
        </c:when>
        <c:otherwise>
            <a class="page-btn" href="?page=${currentPage - 1}&size=${size}&orderBy=${orderBy}">‹</a>
        </c:otherwise>
    </c:choose>

    <c:if test="${totalPages >= 1}">
        <c:if test="${currentPage - window > 1}">
            <a class="page-link" href="?page=1&size=${size}&orderBy=${orderBy}">1</a>
            <span class="dots">…</span>
        </c:if>

        <c:set var="start" value="${currentPage - window < 1 ? 1 : currentPage - window}" />
        <c:set var="end" value="${currentPage + window > totalPages ? totalPages : currentPage + window}" />

        <c:forEach var="i" begin="${start}" end="${end}">
            <c:choose>
                <c:when test="${i == currentPage}">
                    <span class="page-link active">${i}</span>
                </c:when>
                <c:otherwise>
                    <a class="page-link" href="?page=${i}&size=${size}&orderBy=${orderBy}">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${end < totalPages}">
            <c:if test="${end < totalPages - 1}">
                <span class="dots">…</span>
            </c:if>
            <a class="page-link" href="?page=${totalPages}&size=${size}&orderBy=${orderBy}">${totalPages}</a>
        </c:if>
    </c:if>

    <c:choose>
        <c:when test="${currentPage == totalPages}">
            <span class="page-btn disabled">›</span>
        </c:when>
        <c:otherwise>
            <a class="page-btn" href="?page=${currentPage + 1}&size=${size}&orderBy=${orderBy}">›</a>
        </c:otherwise>
    </c:choose>
</div>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Publications</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
<header class="hero">
    <div class="topbar">
        <a class="brand" href="<c:url value='/'/>"><span class="logo">P2</span>Scholar Explorer</a>
        <nav>
            <a href="<c:url value='/'/>">Home</a>
            <a href="<c:url value='/titles'/>" class="active">Publications</a>
            <a href="<c:url value='/researcher'/>">Researchers</a>
        </nav>
    </div>
    <div class="hero-text">
        <h1>Publications</h1>
        <p>${totalCount} publications from ${publishers.size()} publishers.</p>
    </div>
</header>

<main>
    <div class="card">
        <%-- Publisher buttons (they keep the chosen years) + how many are shown --%>
        <div class="section-head">
            <div class="filters">
                <c:url var="allLink" value="/titles">
                    <c:if test="${not empty from}"><c:param name="from" value="${from}"/></c:if>
                    <c:if test="${not empty to}"><c:param name="to" value="${to}"/></c:if>
                </c:url>
                <a href="${allLink}" class="${empty publisher ? 'active' : ''}">All</a>
                <c:forEach var="pb" items="${publishers}">
                    <c:url var="pbLink" value="/titles">
                        <c:param name="publisher" value="${pb}"/>
                        <c:if test="${not empty from}"><c:param name="from" value="${from}"/></c:if>
                        <c:if test="${not empty to}"><c:param name="to" value="${to}"/></c:if>
                    </c:url>
                    <a href="${pbLink}" class="${pb == publisher ? 'active' : ''}"><c:out value="${pb}"/></a>
                </c:forEach>
            </div>
            <span class="count">${publications.size()} publications</span>
        </div>

        <%-- Year range (keeps the chosen publisher) --%>
        <form class="year-filter" action="<c:url value='/titles'/>" method="get">
            <c:if test="${not empty publisher}">
                <input type="hidden" name="publisher" value="<c:out value='${publisher}'/>">
            </c:if>
            <span>Years</span>
            <label>from <input type="number" name="from" min="${minYear}" max="${maxYear}" placeholder="${minYear}" value="${from}"></label>
            <label>to <input type="number" name="to" min="${minYear}" max="${maxYear}" placeholder="${maxYear}" value="${to}"></label>
            <button type="submit">Apply</button>
            <c:if test="${not empty from or not empty to}">
                <c:url var="clearYears" value="/titles">
                    <c:if test="${not empty publisher}"><c:param name="publisher" value="${publisher}"/></c:if>
                </c:url>
                <a href="${clearYears}">Clear years</a>
            </c:if>
        </form>

        <c:if test="${not empty yearError}">
            <p class="error"><c:out value="${yearError}"/></p>
        </c:if>

        <c:choose>
            <c:when test="${empty publications}">
                <p class="empty">No publications found for this filter.</p>
            </c:when>
            <c:otherwise>
                <div class="list-head"><span>Publication</span><span>Citations</span></div>
                <ul class="pubs">
                    <c:forEach var="p" items="${publications}">
                        <li class="pub">
                            <div>
                                <c:choose>
                                    <c:when test="${not empty p.doi}">
                                        <a class="pub-title" href="https://doi.org/<c:out value='${p.doi}'/>" target="_blank" rel="noopener"><c:out value="${p.title}"/></a>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="pub-title"><c:out value="${p.title}"/></div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="pub-meta">
                                    <%-- The publisher badge only when no publisher filter is active (otherwise it repeats) --%>
                                    <c:if test="${empty publisher}">
                                        <c:url var="pubLink" value="/titles"><c:param name="publisher" value="${p.publisher}"/></c:url>
                                        <a class="badge pub-${p.publisher}" href="${pubLink}"><c:out value="${p.publisher}"/></a>
                                    </c:if>
                                    <span>${p.year}</span>
                                </div>
                                <div class="pub-authors">
                                    <c:forEach var="a" items="${p.authors}" varStatus="s">
                                        <c:url var="link" value="/researcher"><c:param name="name" value="${a}"/></c:url>
                                        <a href="${link}"><c:out value="${a}"/></a><c:if test="${!s.last}">, </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="cites">${p.citations}</div>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<footer>Enterprise Application Integration · Project 2 · Ana Malva · Carolina Picado</footer>
</body>
</html>

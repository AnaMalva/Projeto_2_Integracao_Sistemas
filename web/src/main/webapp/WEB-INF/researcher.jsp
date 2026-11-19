<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Researchers</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
<header class="hero">
    <div class="topbar">
        <a class="brand" href="<c:url value='/'/>"><span class="logo">P2</span>Scholar Explorer</a>
        <nav>
            <a href="<c:url value='/'/>">Home</a>
            <a href="<c:url value='/titles'/>">Publications</a>
            <a href="<c:url value='/researcher'/>" class="active">Researchers</a>
        </nav>
    </div>
    <div class="hero-text">
        <c:choose>
            <c:when test="${not empty researcher}">
                <h1>Researcher profile</h1>
                <p>Profile, publications and citations per year.</p>
            </c:when>
            <c:otherwise>
                <h1>Researchers</h1>
                <p>${allResearchers.size()} researchers. Search by name or pick one below.</p>
            </c:otherwise>
        </c:choose>
    </div>
</header>

<main>
    <%-- Search box (always visible) --%>
    <div class="card">
        <form class="search" action="<c:url value='/researcher'/>" method="get">
            <input type="text" name="name" list="names" placeholder="Search a researcher by name…"
                   value="<c:out value='${searched}'/>">
            <datalist id="names">
                <c:forEach var="r" items="${allResearchers}">
                    <option value="<c:out value='${r.name}'/>">
                </c:forEach>
            </datalist>
            <button type="submit">Show profile</button>
        </form>
    </div>

    <%-- A) Researchers that share an interest --%>
    <c:if test="${not empty interest}">
        <div class="card">
            <div class="section-head">
                <h2>Interested in "<c:out value="${interest}"/>"</h2>
                <span class="count">${byInterest.size()} researchers</span>
            </div>
            <c:choose>
                <c:when test="${empty byInterest}">
                    <p class="empty">No researchers with this interest.</p>
                </c:when>
                <c:otherwise>
                    <ul class="grid">
                        <c:forEach var="r" items="${byInterest}">
                            <c:url var="link" value="/researcher"><c:param name="name" value="${r.name}"/></c:url>
                            <li><a href="${link}">
                                <span class="avatar">${fn:substring(r.name, 0, 1)}</span>
                                <span>
                                    <span class="name"><c:out value="${r.name}"/></span>
                                    <span class="where"><c:out value="${r.universities[0]}"/></span>
                                </span>
                            </a></li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <%-- B) Name searched but not found --%>
    <c:if test="${not empty searched and empty researcher}">
        <div class="card">
            <p class="error">No researcher named "<c:out value="${searched}"/>".</p>
        </div>
    </c:if>

    <%-- C) Profile --%>
    <c:if test="${not empty researcher}">
        <div class="card profile">
            <div class="profile-banner"></div>
            <div class="profile-body">
                <div class="profile-head">
                    <span class="avatar big">${fn:substring(researcher.name, 0, 1)}</span>
                    <div>
                        <h2><c:out value="${researcher.name}"/></h2>
                        <div class="muted">
                            <c:forEach var="u" items="${researcher.universities}" varStatus="s">
                                <c:out value="${u}"/><c:if test="${!s.last}">; </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <dl class="facts">
                    <dt>Interests</dt>
                    <dd>
                        <ul class="chips">
                            <c:forEach var="i" items="${researcher.interests}">
                                <c:url var="link" value="/researcher"><c:param name="interest" value="${i}"/></c:url>
                                <li><a class="chip" href="${link}"><c:out value="${i}"/></a></li>
                            </c:forEach>
                        </ul>
                    </dd>
                    <c:if test="${not empty researcher.homepage}">
                        <dt>Homepage</dt>
                        <dd><a href="<c:out value='${researcher.homepage}'/>" target="_blank"><c:out value="${researcher.homepage}"/></a></dd>
                    </c:if>
                    <c:if test="${not empty researcher.email}">
                        <dt>Email</dt>
                        <dd><c:out value="${researcher.email}"/></dd>
                    </c:if>
                </dl>

                <div class="stats">
                    <div class="stat"><span class="value">${publications.size()}</span><span class="label">Publications</span></div>
                    <div class="stat"><span class="value">${totalCitations}</span><span class="label">Total citations</span></div>
                    <div class="stat">
                        <c:choose>
                            <c:when test="${maxYearCitations > 0}">
                                <span class="value">${bestYear}</span><span class="label">Most cited year (${maxYearCitations})</span>
                            </c:when>
                            <c:otherwise>
                                <span class="value">&ndash;</span><span class="label">Most cited year</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="section-head">
                <h2>Publications</h2>
                <span class="count">${publications.size()} publications</span>
            </div>
            <c:choose>
                <c:when test="${empty publications}">
                    <p class="empty">This researcher has no publications in the database.</p>
                </c:when>
                <c:otherwise>
                    <div class="list-head"><span>Publication</span><span>Citations</span></div>
                    <ul class="pubs">
                        <c:forEach var="p" items="${publications}">
                            <li class="pub">
                                <div>
                                    <div class="pub-title"><c:out value="${p.title}"/></div>
                                    <div class="pub-meta">
                                        <c:url var="pubLink" value="/titles"><c:param name="publisher" value="${p.publisher}"/></c:url>
                                        <a class="badge pub-${p.publisher}" href="${pubLink}"><c:out value="${p.publisher}"/></a>
                                        <span>${p.year}</span>
                                    </div>
                                    <div class="pub-authors">
                                        <c:forEach var="a" items="${p.authors}" varStatus="s">
                                            <c:url var="link" value="/researcher"><c:param name="name" value="${a}"/></c:url>
                                            <%-- the researcher of this profile shows in black --%>
                                            <a href="${link}" class="${a == researcher.name ? 'self' : ''}"><c:out value="${a}"/></a><c:if test="${!s.last}">, </c:if>
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

        <c:if test="${not empty citationsPerYear}">
            <div class="card">
                <h2>Citations per year of publication</h2>
                <ul class="bars">
                    <c:forEach var="e" items="${citationsPerYear}">
                        <li>
                            <span class="year">${e.key}</span>
                            <span class="track"><span class="fill" style="width: ${maxYearCitations == 0 ? 0 : e.value * 100 / maxYearCitations}%"></span></span>
                            <span class="val">${e.value}</span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </c:if>

    <%-- D) Nothing chosen yet: list everyone --%>
    <c:if test="${empty searched and empty interest}">
        <div class="card">
            <div class="section-head">
                <h2>All researchers</h2>
                <span class="count">${allResearchers.size()} researchers</span>
            </div>
            <ul class="grid">
                <c:forEach var="r" items="${allResearchers}">
                    <c:url var="link" value="/researcher"><c:param name="name" value="${r.name}"/></c:url>
                    <li><a href="${link}">
                        <span class="avatar">${fn:substring(r.name, 0, 1)}</span>
                        <span>
                            <span class="name"><c:out value="${r.name}"/></span>
                            <span class="where"><c:out value="${r.universities[0]}"/></span>
                        </span>
                    </a></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
</main>
<footer>Enterprise Application Integration · Project 2</footer>
</body>
</html>

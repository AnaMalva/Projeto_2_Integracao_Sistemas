<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Researchers</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
    <nav>
        <a href="<c:url value='/'/>">Home</a> ·
        <a href="<c:url value='/titles'/>">Publications</a> ·
        <a href="<c:url value='/researcher'/>">Researchers</a>
    </nav>

    <h1>Researchers</h1>

    <form action="<c:url value='/researcher'/>" method="get">
        <input type="text" name="name" list="names" placeholder="Researcher name"
               value="<c:out value='${searched}'/>">
        <datalist id="names">
            <c:forEach var="r" items="${allResearchers}">
                <option value="<c:out value='${r.name}'/>">
            </c:forEach>
        </datalist>
        <button type="submit">Show profile</button>
    </form>

    <%-- A) Researchers that share an interest --%>
    <c:if test="${not empty interest}">
        <h2>Researchers interested in "<c:out value="${interest}"/>"</h2>
        <ul>
            <c:forEach var="r" items="${byInterest}">
                <c:url var="link" value="/researcher"><c:param name="name" value="${r.name}"/></c:url>
                <li><a href="${link}"><c:out value="${r.name}"/></a></li>
            </c:forEach>
        </ul>
    </c:if>

    <%-- B) Name searched but not found --%>
    <c:if test="${not empty searched and empty researcher}">
        <p class="error">No researcher named "<c:out value="${searched}"/>".</p>
    </c:if>

    <%-- C) Profile --%>
    <c:if test="${not empty researcher}">
        <h2><c:out value="${researcher.name}"/></h2>

        <p><strong>Universities:</strong>
            <c:forEach var="u" items="${researcher.universities}" varStatus="s">
                <c:out value="${u}"/><c:if test="${!s.last}">; </c:if>
            </c:forEach>
        </p>

        <p><strong>Interests:</strong>
            <c:forEach var="i" items="${researcher.interests}" varStatus="s">
                <c:url var="link" value="/researcher"><c:param name="interest" value="${i}"/></c:url>
                <a href="${link}"><c:out value="${i}"/></a><c:if test="${!s.last}">, </c:if>
            </c:forEach>
        </p>

        <c:if test="${not empty researcher.homepage}">
            <p><strong>Homepage:</strong> <a href="<c:out value='${researcher.homepage}'/>"><c:out value="${researcher.homepage}"/></a></p>
        </c:if>
        <c:if test="${not empty researcher.email}">
            <p><strong>Email:</strong> <c:out value="${researcher.email}"/></p>
        </c:if>

        <h3>Publications (${publications.size()})</h3>
        <table>
            <tr><th>Title</th><th>Year</th><th>Publisher</th><th>Citations</th></tr>
            <c:forEach var="p" items="${publications}">
                <tr>
                    <td><c:out value="${p.title}"/></td>
                    <td>${p.year}</td>
                    <td><c:out value="${p.publisher}"/></td>
                    <td class="num">${p.citations}</td>
                </tr>
            </c:forEach>
        </table>

        <h3>Citations per year (total: ${totalCitations})</h3>
        <table class="small">
            <tr><th>Year</th><th>Citations</th></tr>
            <c:forEach var="e" items="${citationsPerYear}">
                <tr><td>${e.key}</td><td class="num">${e.value}</td></tr>
            </c:forEach>
        </table>
    </c:if>

    <%-- D) Nothing chosen yet: list everyone --%>
    <c:if test="${empty searched and empty interest}">
        <h2>All researchers (${allResearchers.size()})</h2>
        <ul>
            <c:forEach var="r" items="${allResearchers}">
                <c:url var="link" value="/researcher"><c:param name="name" value="${r.name}"/></c:url>
                <li><a href="${link}"><c:out value="${r.name}"/></a></li>
            </c:forEach>
        </ul>
    </c:if>
</body>
</html>

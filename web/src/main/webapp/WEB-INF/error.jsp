<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Shown by the server (see web.xml) when something goes wrong. No technical details are shown to the user. --%>
<c:set var="code" value="${pageContext.errorData.statusCode}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Something went wrong</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
<header class="hero">
    <div class="topbar">
        <a class="brand" href="<c:url value='/'/>"><span class="logo">P2</span>Scholar Explorer</a>
        <nav>
            <a href="<c:url value='/'/>">Home</a>
            <a href="<c:url value='/titles'/>">Publications</a>
            <a href="<c:url value='/researcher'/>">Researchers</a>
        </nav>
    </div>
    <div class="hero-text">
        <c:choose>
            <c:when test="${code == 404}">
                <h1>Page not found</h1>
                <p>The address you followed does not exist.</p>
            </c:when>
            <c:otherwise>
                <h1>Something went wrong</h1>
                <p>We could not load the data right now.</p>
            </c:otherwise>
        </c:choose>
    </div>
</header>

<main>
    <div class="card error-box">
        <div class="code">${code == 404 ? '404' : 'Oops'}</div>
        <c:choose>
            <c:when test="${code == 404}">
                <p>Check the address, or go back to the home page and choose Publications or Researchers.</p>
            </c:when>
            <c:otherwise>
                <p>The database may be temporarily unavailable. Please try again in a few moments.
                   If the problem continues, check that PostgreSQL and the server are running.</p>
            </c:otherwise>
        </c:choose>
        <a class="button" href="<c:url value='/'/>">Back to home</a>
    </div>
</main>
<footer>Enterprise Application Integration · Project 2 · Ana Malva · Carolina Picado</footer>
</body>
</html>

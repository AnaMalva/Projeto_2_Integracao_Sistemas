<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Scholar Explorer</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
<header class="hero">
    <div class="topbar">
        <a class="brand" href="<c:url value='/'/>"><span class="logo">P2</span>Scholar Explorer</a>
        <nav>
            <a href="<c:url value='/'/>" class="active">Home</a>
            <a href="<c:url value='/titles'/>">Publications</a>
            <a href="<c:url value='/researcher'/>">Researchers</a>
        </nav>
    </div>
    <div class="hero-text">
        <h1>Researchers &amp; Publications</h1>
        <p>Explore the researchers and publications loaded from the Google Scholar data.</p>
    </div>
</header>

<main>
    <div class="tiles">
        <a class="card tile" href="<c:url value='/titles'/>">
            <div class="icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5V5a2 2 0 0 1 2-2h12v16H6a2 2 0 0 0-2 2"/><path d="M8 7h7M8 11h7"/></svg>
            </div>
            <h2>Publications</h2>
            <p class="muted">Every title in the database with year, publisher, citations and authors. Filter by publisher.</p>
            <span class="go">Browse publications &rarr;</span>
        </a>
        <a class="card tile alt" href="<c:url value='/researcher'/>">
            <div class="icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="8" r="3.5"/><path d="M2.5 20c.8-3.5 3.4-5.5 6.5-5.5s5.7 2 6.5 5.5"/><circle cx="17.5" cy="9" r="2.5"/><path d="M17 14.5c2.4.2 4 1.8 4.5 4.5"/></svg>
            </div>
            <h2>Researchers</h2>
            <p class="muted">Search a researcher to see their profile, interests, publications and citations per year.</p>
            <span class="go">Find researchers &rarr;</span>
        </a>
    </div>
</main>
<footer>Enterprise Application Integration · Project 2 · Ana Malva · Carolina Picado</footer>
</body>
</html>

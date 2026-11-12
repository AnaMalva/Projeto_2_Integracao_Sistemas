<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Project 2</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
    <h1>Google Scholar – Project 2</h1>
    <p>Choose what you want to see:</p>
    <ul class="menu">
        <li><a href="<c:url value='/titles'/>">Publications</a> – every title in the database</li>
        <li><a href="<c:url value='/researcher'/>">Researchers</a> – profile, publications and citations per year</li>
    </ul>
</body>
</html>

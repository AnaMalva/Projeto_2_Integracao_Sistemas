<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Publications</title>
    <link rel="stylesheet" href="<c:url value='/style.css'/>">
</head>
<body>
    <nav>
        <a href="<c:url value='/'/>">Home</a> ·
        <a href="<c:url value='/titles'/>">Publications</a> ·
        <a href="<c:url value='/researcher'/>">Researchers</a>
    </nav>

    <h1>Publications</h1>
    <p>${publications.size()} publications in the database.</p>

    <table>
        <tr><th>Title</th><th>Year</th><th>Publisher</th><th>Authors</th></tr>
        <c:forEach var="p" items="${publications}">
            <tr>
                <td><c:out value="${p.title}"/></td>
                <td>${p.year}</td>
                <td><c:out value="${p.publisher}"/></td>
                <td>
                    <c:forEach var="a" items="${p.authors}" varStatus="s">
                        <c:url var="link" value="/researcher"><c:param name="name" value="${a}"/></c:url>
                        <a href="${link}"><c:out value="${a}"/></a><c:if test="${!s.last}">, </c:if>
                    </c:forEach>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>

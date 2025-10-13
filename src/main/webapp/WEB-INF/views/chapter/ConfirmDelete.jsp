<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 8:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Delete Chapter — Confirm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="bg-light">
<div class="container py-4">
    <h1 class="h4 mb-3">Delete Chapter</h1>

    <div class="alert alert-warning">
        Are you sure you want to delete this chapter?
    </div>

    <div class="card mb-3">
        <div class="card-body">
            <div><strong>ID:</strong> ${chapter.chapterId}</div>
            <div><strong>No:</strong> ${chapter.chapterNumber}</div>
            <div><strong>Title:</strong> <c:out value="${chapter.title}"/></div>
            <div><strong>Status:</strong> ${chapter.status}</div>
        </div>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/delete-chapter">
        <input type="hidden" name="id" value="${chapter.chapterId}"/>
        <button class="btn btn-danger" type="submit">Yes, delete</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/chapter?id=${chapter.chapterId}">
            Cancel
        </a>
    </form>
</div>
</body>
</html>

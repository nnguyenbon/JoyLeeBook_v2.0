<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 7:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Edit Chapter — <c:out value="${series.title}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="bg-light">
<div class="container py-4">
    <h1 class="h3 mb-3">Edit Chapter</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/update-chapter">
        <input type="hidden" name="id" value="${chapter.chapterId}"/>

        <div class="mb-3">
            <label class="form-label">Series</label>
            <input class="form-control" value="${series.title}" disabled/>
        </div>

        <div class="mb-3">
            <label class="form-label">Chapter Number</label>
            <input type="number" class="form-control" name="chapterNumber"
                   value="${chapter.chapterNumber}" min="1" required/>
        </div>

        <div class="mb-3">
            <label class="form-label">Title</label>
            <input type="text" class="form-control" name="title"
                   value="${chapter.title}" maxlength="255" required/>
        </div>

        <div class="mb-3">
            <label class="form-label">Content</label>
            <textarea class="form-control" name="content" rows="12" required>${chapter.content}</textarea>
        </div>

        <div class="d-flex gap-2">
            <button class="btn btn-primary" type="submit">Save changes</button>
            <a class="btn btn-outline-secondary"
               href="${pageContext.request.contextPath}/chapter?id=${chapter.chapterId}">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>

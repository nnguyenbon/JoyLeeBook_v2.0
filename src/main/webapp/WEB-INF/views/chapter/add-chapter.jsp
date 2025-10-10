<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 12:48 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Add New Chapter</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">

    <!-- Thông báo lỗi chung -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
            <c:out value="${error}" />
        </div>
    </c:if>

    <!-- Thông báo thành công -->
    <c:if test="${not empty success}">
        <div class="alert alert-success" role="alert">
            <c:out value="${success}" />
        </div>
    </c:if>

    <h3 class="mb-1">Add New Chapter for: <c:out value="${series.title}" /></h3>
    <p class="text-muted">Series ID: <c:out value="${series.seriesId}" /></p>
    <hr>

    <form action="${pageContext.request.contextPath}/add-chapter" method="post">
        <!-- Bắt buộc truyền seriesId -->
        <input type="hidden" name="seriesId" value="<c:out value='${series.seriesId}'/>" />

        <div class="row g-3">
            <!-- Chapter Title -->
            <div class="col-md-6">
                <label for="chapterTitle" class="form-label">Chapter Title</label>
                <input
                        type="text"
                        class="form-control"
                        id="chapterTitle"
                        name="title"
                        required
                        maxlength="255"
                        value="<c:out value='${param.title}'/>"
                        placeholder="Ví dụ: Prologue: Relic Awakens"
                />
            </div>

            <!-- Content -->
            <div class="col-12">
                <label for="chapterContent" class="form-label">Content</label>
                <textarea
                        class="form-control"
                        id="chapterContent"
                        name="content"
                        rows="12"
                        placeholder="Nội dung chương (cho phép HTML cơ bản nếu bạn render ở view)"
                ><c:out value="${param.content}"/></textarea>
            </div>
        </div>

        <div class="mt-4 d-flex gap-2">
            <button type="submit" class="btn btn-primary">Save Chapter</button>
            <a href="${pageContext.request.contextPath}/manage-series?id=${series.seriesId}" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>

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
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Create New Chapter — <c:out value="${series.title}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body style="background:#FCFCFC">
<!-- Simple top bar -->
<div class="bg-white border-bottom">
    <div class="container" style="height:60px;">
        <div class="d-flex align-items-center justify-content-between h-100">
            <div class="fw-bold fs-4 text-truncate">
                <c:out value="${series.title}"/>
            </div>
        </div>
    </div>
</div>

<div class="container py-4">
    <div class="text-center mb-1 fw-bold" style="font-size:40px; line-height:1.1;">Create New Chapter</div>
    <div class="text-center mb-4" style="color:rgba(68,68,68,.75); font-size:20px;">
        Create a new chapter for your series
    </div>

    <!-- Alerts -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}" /></div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success" role="alert"><c:out value="${success}" /></div>
    </c:if>

    <!-- Centered form -->
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <form action="${pageContext.request.contextPath}/add-chapter" method="post" id="addChapterForm">
                <!-- Required for backend -->
                <input type="hidden" name="seriesId" value="<c:out value='${series.seriesId}'/>" />

                <!-- Chapter Title -->
                <div class="card border-1 mb-3">
                    <div class="card-body">
                        <label for="chapterTitle" class="form-label fw-semibold">Chapter Title *</label>
                        <input
                                type="text"
                                class="form-control"
                                id="chapterTitle"
                                name="title"
                                required
                                maxlength="255"
                                value="<c:out value='${param.title}'/>"
                                placeholder="Prologue: Relic Awakens"
                        />
                    </div>
                </div>

                <!-- Chapter Index (optional) -->
                <div class="card border-1 mb-3">
                    <div class="card-body">
                        <label for="chapterIndex" class="form-label fw-semibold">Chapter Index</label>
                        <input
                                type="number"
                                class="form-control"
                                id="chapterIndex"
                                name="chapterNumber"
                                min="1"
                                value="<c:out value='${param.chapterNumber}'/>"
                                placeholder="Enter chapter index (leave blank to auto-increment)"
                        />
                        <small class="text-muted">If left blank, the system will use the next available index.</small>
                    </div>
                </div>

                <!-- Content -->
                <div class="card border-1 mb-4">
                    <div class="card-body">
                        <label for="chapterContent" class="form-label fw-semibold">Content *</label>
                        <textarea
                                class="form-control"
                                id="chapterContent"
                                name="content"
                                rows="12"
                                placeholder="Write your chapter content here..."
                        ><c:out value="${param.content}"/></textarea>
                    </div>
                </div>

                <!-- Actions -->
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary px-4">Create</button>
                    <a href="${pageContext.request.contextPath}/manage-series?id=${series.seriesId}" class="btn btn-outline-secondary px-4">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
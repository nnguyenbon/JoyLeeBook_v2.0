<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 2:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title><c:out value="${vm.chapter.title}"/> — <c:out value="${vm.series.title}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        .chapter-content img {max-width: 100%; height: auto;}
        .chapter-content {line-height: 1.7;}
        .meta {font-size: 0.95rem; color: #666;}
    </style>
</head>
<body>
<div class="container my-4">

    <c:if test="${not empty error}">
        <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/series?id=${vm.series.seriesId}">
                    <c:out value="${vm.series.title}"/>
                </a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">
                Chapter <c:out value="${vm.chapter.chapterNumber}"/>
            </li>
        </ol>
    </nav>

    <h1 class="mb-1"><c:out value="${vm.chapter.title}"/></h1>
    <div class="meta mb-3">
        Series ID: <b><c:out value="${vm.series.seriesId}"/></b> •
        Chapter: <b><c:out value="${vm.chapter.chapterNumber}"/></b> •
        Status: <span class="badge bg-success">approved</span> •
        Updated at: <b><c:out value="${vm.chapter.updatedAt}"/></b>
    </div>

    <div class="d-flex align-items-center gap-3 mb-3">
        <span>👍 <b><c:out value="${vm.likeCount}"/></b></span>
        <span>💬 <b><c:out value="${vm.commentCount}"/></b></span>
        <c:if test="${vm.userLiked}">
            <span class="badge bg-primary">You liked</span>
        </c:if>
    </div>

    <hr/>

    <!-- Content may contain HTML, so do not escape -->
    <article class="chapter-content mb-4">
        <c:out value="${vm.chapter.content}" escapeXml="false"/>
    </article>

    <div class="d-flex justify-content-between mt-4">
        <div>
            <c:if test="${not empty vm.prev}">
                <a class="btn btn-outline-secondary"
                   href="${pageContext.request.contextPath}/chapter?id=${vm.prev.chapterId}">
                    ← Prev (Ch. <c:out value="${vm.prev.chapterNumber}"/>)
                </a>
            </c:if>
        </div>
        <div>
            <c:if test="${not empty vm.next}">
                <a class="btn btn-outline-secondary"
                   href="${pageContext.request.contextPath}/chapter?id=${vm.next.chapterId}">
                    Next (Ch. <c:out value="${vm.next.chapterNumber}"/>) →
                </a>
            </c:if>
        </div>
    </div>

    <hr class="my-4"/>

    <!-- Placeholder comments (to be loaded later) -->
    <div class="mt-3">
        <h5>Comments (<c:out value="${vm.commentCount}"/>)</h5>
        <p class="text-muted">Coming soon…</p>
    </div>
</div>
</body>
</html>

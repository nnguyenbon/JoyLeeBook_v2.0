<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 2:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <title>Lỗi xảy ra</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body {
            background: #f8f9fb;
        }

        .error-hero {
            text-align: center;
            padding: 48px 0 16px;
        }

        .error-code {
            font-size: 4rem;
            font-weight: 800;
            line-height: 1;
            letter-spacing: .05em;
        }

        .error-icon {
            font-size: 4rem;
        }

        .card {
            border: 0;
            box-shadow: 0 6px 24px rgba(0, 0, 0, .06);
        }

        .muted {
            color: #6c757d;
        }

        .mono {
            font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace;
        }

        details > summary {
            cursor: pointer;
        }

        .btn-wrap {
            gap: .5rem;
            flex-wrap: wrap;
        }
    </style>
</head>
<body>
<div class="container py-5">
    <c:set var="ctx" value="${pageContext.request.contextPath}"/>

    <!-- Lấy status code (ưu tiên jakarta.*) -->
    <c:set var="statusCode" value="${requestScope['jakarta.servlet.error.status_code']}"/>
    <c:if test="${empty statusCode}">
        <c:set var="statusCode" value="${requestScope['javax.servlet.error.status_code']}"/>
    </c:if>
    <c:if test="${empty statusCode}">
        <c:set var="statusCode" value="500"/>
    </c:if>

    <!-- Lấy request URI -->
    <c:set var="reqUri" value="${requestScope['jakarta.servlet.error.request_uri']}"/>
    <c:if test="${empty reqUri}">
        <c:set var="reqUri" value="${requestScope['javax.servlet.error.request_uri']}"/>
    </c:if>

    <!-- Lấy message (ưu tiên attribute 'error') -->
    <c:set var="errorMsg" value="${not empty error ? error : requestScope['jakarta.servlet.error.message']}"/>
    <c:if test="${empty errorMsg}">
        <c:set var="errorMsg" value="${requestScope['javax.servlet.error.message']}"/>
    </c:if>
    <c:if test="${empty errorMsg}">
        <c:set var="errorMsg" value="Rất tiếc, đã có lỗi xảy ra trong quá trình xử lý yêu cầu."/>
    </c:if>

    <!-- Lấy servlet name & exception type -->
    <c:set var="servletName" value="${requestScope['jakarta.servlet.error.servlet_name']}"/>
    <c:if test="${empty servletName}">
        <c:set var="servletName" value="${requestScope['javax.servlet.error.servlet_name']}"/>
    </c:if>
    <c:set var="exType" value="${requestScope['jakarta.servlet.error.exception_type']}"/>
    <c:if test="${empty exType}">
        <c:set var="exType" value="${requestScope['javax.servlet.error.exception_type']}"/>
    </c:if>

    <div class="error-hero">
        <div class="error-icon">😵‍💫</div>
        <div class="error-code text-danger mt-2"><c:out value="${statusCode}"/></div>
        <h1 class="h4 mt-2">Có lỗi rồi!</h1>
        <p class="text-muted mb-0">
            <c:out value="${errorMsg}"/>
        </p>
    </div>

    <div class="row justify-content-center mt-4">
        <div class="col-lg-8">
            <div class="card p-4">
                <div class="d-flex btn-wrap">
                    <button type="button" class="btn btn-outline-secondary" onclick="history.back()">⬅ Quay lại</button>
                    <a class="btn btn-primary" href="${ctx}/">🏠 Về trang chủ</a>
                    <c:if test="${not empty reqUri}">
                        <a class="btn btn-outline-primary" href="${reqUri}">🔁 Thử lại</a>
                    </c:if>
                </div>

                <hr class="my-4"/>

                <details>
                    <summary class="fw-semibold">Chi tiết kỹ thuật</summary>
                    <div class="mt-3 small">
                        <div class="row g-2">
                            <div class="col-md-6">
                                <div class="muted">Status Code</div>
                                <div class="mono"><c:out value="${statusCode}"/></div>
                            </div>
                            <div class="col-md-6">
                                <div class="muted">Servlet</div>
                                <div class="mono"><c:out value="${empty servletName ? '-' : servletName}"/></div>
                            </div>
                            <div class="col-md-12">
                                <div class="muted">Request URI</div>
                                <div class="mono"><c:out value="${empty reqUri ? '-' : reqUri}"/></div>
                            </div>
                            <div class="col-md-12">
                                <div class="muted">Exception</div>
                                <div class="mono">
                                    <c:choose>
                                        <c:when test="${not empty exception}">
                                            <c:out value="${exception}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <button id="copyBtn" type="button" class="btn btn-sm btn-outline-secondary mt-3">📋 Copy chi
                            tiết
                        </button>
                        <textarea id="copySrc" class="visually-hidden">
                            Status: ${statusCode}
                            Message: ${errorMsg}
                            Servlet: ${empty servletName ? '-' : servletName}
                            URI: ${empty reqUri ? '-' : reqUri}
                            Exception: ${not empty exception ? exception : '-'}
                        </textarea>
                    </div>
                </details>

                <div class="mt-4 text-muted">
                    Nếu lỗi vẫn tiếp diễn, vui lòng liên hệ hỗ trợ hoặc thử lại sau.
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    (function () {
        const btn = document.getElementById('copyBtn');
        const src = document.getElementById('copySrc');
        if (btn && src) {
            btn.addEventListener('click', async () => {
                try {
                    await navigator.clipboard.writeText(src.value.trim());
                    btn.textContent = '✅ Đã copy';
                    setTimeout(() => btn.textContent = '📋 Copy chi tiết', 1500);
                } catch (e) {
                    alert('Không copy được, hãy bôi đen và copy thủ công.');
                }
            });
        }
    })();
</script>
</body>
</html>

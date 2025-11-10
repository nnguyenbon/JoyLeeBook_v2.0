<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/13/2025
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page buffer="32kb" autoFlush="true" %>
<html>
<head>
    <title>
        ${pageTitle}
    </title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/shared/favicon.png">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/styles.css?v=<%= System.currentTimeMillis() %>"/>
    <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/fontawesome/css/all.min.css"
    />
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet"
    />
</head>
<body class="relative overflow-x-hidden">
<c:import url="/WEB-INF/views/components/_header.jsp"/>
<div class="max-w-7xl mx-auto pb-60">
    <c:if test="${not empty contentPage}">
        <c:import url="${contentPage}"/>
    </c:if>
</div>
<c:import url="/WEB-INF/views/components/_footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/main.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>

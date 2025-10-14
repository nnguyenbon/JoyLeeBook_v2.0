<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/13/2025
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>
        ${pageTitle}
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css"/>
    <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/fontawesome/css/all.min.css"
    />
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet"
    />
</head>
<body>
<div class="max-w-7xl mx-auto">
    <c:import url="/WEB-INF/views/components/_header.jsp" />

    <c:import url="${contentPage}" />

    <c:import url="/WEB-INF/views/components/_footer.jsp" />
</div>

<script src="${pageContext.request.contextPath}/js/main.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>

<%@ page import="model.Staff" %>
<%--
  Created by IntelliJ IDEA.
  User: trunguyen
  Date: 10/27/2025
  Time: 2:07 PM
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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=<%= System.currentTimeMillis() %>"/>
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
    <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
</head>
<body class="bg-white">
<div class="flex h-screen text-gray-500 overflow-x-hidden">
<c:import url="/WEB-INF/views/components/_navbarStaff.jsp"/>
    <div class="flex flex-col w-full bg-[#F5F4FA] ">
        <div class="h-10 flex items-center justify-between border-b border-gray-300 bg-white p-8">
            <p class="font-semibold text-lg "> <span class="text-[#041E3D]">
               ${pageTitle}
            </span></p>
            <p class="font-semibold text-lg ">Staff: <span class="text-[#041E3D]">
               ${sessionScope.loginedUser.username}
            </span></p>
        </div>

        <c:if test="${not empty contentPage}">
        <c:import url="${contentPage}" />
    </c:if>
    </div>
</div>
<c:if test="${not empty sessionScope.message}">
    <script>
        toastr.options = {
            "closeButton": true,
            "debug": false,
            "newestOnTop": false,
            "progressBar": true,
            "positionClass": "toast-bottom-right",
            "preventDuplicates": false,
            "onclick": null,
            "showDuration": "300",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        }


        toastr["success"]("${sessionScope.message}");
    </script>
    <c:remove var="message" scope="session" />
</c:if>
<script src="${pageContext.request.contextPath}/js/main.js?v=<%= System.currentTimeMillis() %>"></script>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>


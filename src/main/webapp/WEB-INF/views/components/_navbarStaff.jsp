<%--
  Created by IntelliJ IDEA.
  User: trung
  Date: 10/27/2025
  Time: 2:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<aside class="w-56 border-r border-gray-300 flex flex-col justify-between">
    <div>
        <div class="h-28 flex items-center justify-center border-b border-gray-300">
            <img src="${pageContext.request.contextPath}/img/shared/logo.png" alt="Logo" class="h-20 w-40 mx-auto ">
        </div>
        <nav class="">
            <a href="${pageContext.request.contextPath}/staff" class="flex items-center px-4 py-2  ${activePage == 'overview' ? 'bg-[#195DA9]/10 text-[#195DA9] font-medium' : 'hover:bg-gray-100'} ">
                <i class="fa-solid fa-chart-pie"></i>
                <span class="ml-2">Overview</span>
            </a>
            <a href="${pageContext.request.contextPath}/series/list?approvalStatus=pending" class="flex items-center px-4 py-2   ${activePage == 'series' ? 'bg-[#195DA9]/10 text-[#195DA9] font-medium' : 'hover:bg-gray-100'} ">
                <i class="fa-solid fa-book"></i>
                <span class="ml-2">Series Review</span>
            </a>
            <a href="${pageContext.request.contextPath}/chapter/list?status=pending" class="flex items-center px-4 py-2   ${activePage == 'chapter' ? 'bg-[#195DA9]/10 text-[#195DA9] font-medium' : 'hover:bg-gray-100'} ">
                <i class="fa-solid fa-book"></i>
                <span class="ml-2">Chapter Review</span>
            </a>
            <a href="${pageContext.request.contextPath}/report/list?status=pending" class="flex items-center px-4 py-2 hover:bg-gray-100  ${activePage == 'reports' ? 'bg-[#195DA9]/10 text-[#195DA9] font-medium' : 'hover:bg-gray-100'}">
                <i class="fa-solid fa-triangle-exclamation"></i>
                <span class="ml-2">Reports</span>
            </a>
            <a href="${pageContext.request.contextPath}/account?action=list" class="flex items-center px-4 py-2 hover:bg-gray-100  ${activePage == 'users' ? 'bg-[#195DA9]/10 text-[#195DA9] font-medium' : 'hover:bg-gray-100'}">
                <i class="fa-solid fa-users"></i>
                <span class="ml-2">Users</span>
            </a>
        </nav>
    </div>

    <div class="p-4 border-t border-gray-300">
        <a href="${pageContext.request.contextPath}/logout" class="flex items-center text-red-500 hover:text-red-600">
            <i class="fa-solid fa-arrow-right-from-bracket"></i>
            <span class="ml-2">Logout</span>
        </a>
    </div>
</aside>

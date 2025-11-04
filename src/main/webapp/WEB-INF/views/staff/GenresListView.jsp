<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 9:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- List Header -->
<p class="text-gray-500 text-sm mb-1 px-9">Total: ${size}</p>

<!-- Content Container with Scroll -->
<div class="overflow-y-auto px-9 bg-gray-100 max-h-[90vh] custom-scrollbar">
<!-- Series List -->
    <!-- Item -->
    <c:forEach var="category" items="${categoryInfoDTOList}" varStatus="loop">
    <div class="space-y-3">
        <div class="flex items-center justify-between border rounded-lg bg-white px-4 py-3 hover:shadow-sm">
            <div class="flex items-center gap-4">
                <p>${loop.index + 1}</p>
                <div>
                    <p class="font-semibold text-gray-800">${category.name}</p>
                </div>
            </div>
            <div class="flex items-center gap-10 text-sm">
                <p class="font-bold text-[#195DA9]">${category.totalSeries} Series</p>

                <div class="flex items-center gap-2 text-sm">

                    <button class="flex gap-2 border rounded-md px-2 py-1 text-sm hover:bg-gray-100">
                                <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                           fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                           stroke-linejoin="round" class="lucide lucide-square-pen-icon lucide-square-pen">
                                        <path d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                                        <path
                                                d="M18.375 2.625a1 1 0 0 1 3 3l-9.013 9.014a2 2 0 0 1-.853.505l-2.873.84a.5.5 0 0 1-.62-.62l.84-2.873a2 2 0 0 1 .506-.852z" />
                                    </svg></span>
                        Edit</button>
                    <button
                            class="flex gap-2 border rounded-md text-[#E23636] px-2 py-1 text-sm hover:bg-gray-100">
                                <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                           fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                           stroke-linejoin="round" class="lucide lucide-trash-icon lucide-trash">
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6" />
                                        <path d="M3 6h18" />
                                        <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                                    </svg></span>
                        Delete</button>
                </div>
            </div>
        </div>
    </div>
    </c:forEach>
</div>

<!-- Pagination -->
<div class="flex justify-end items-center gap-1 py-4 text-sm px-9 bg-gray-100">
    <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${currentPage-1}&sizePage=${sizePage}" class="page-link">
        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                <c:if test="${currentPage == 1}">disabled</c:if>>
            &lt;&lt;
        </button>
    </a>


    <c:if test="${currentPage > 3}">
        <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${1}&sizePage=${sizePage}" class="page-link">
            <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">1</button>
        </a>
        <span class="px-2 py-1">...</span>
    </c:if>

    <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
               end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
        <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${i}&sizePage=${sizePage}" class="page-link">
            <button class="border rounded-md px-2 py-1
                       ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                    ${i}
            </button>
        </a>
    </c:forEach>

    <c:if test="${currentPage < totalPage - 2}">
        <span class="px-2 py-1">...</span>
        <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${totalPage}&sizePage=${sizePage}" class="page-link">
            <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">${totalPage}</button>
        </a>
    </c:if>

    <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${currentPage+1}&sizePage=${sizePage}" class="page-link">
        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                <c:if test="${currentPage == totalPage}">disabled</c:if>>
            &gt;&gt;
        </button>
    </a>
</div>

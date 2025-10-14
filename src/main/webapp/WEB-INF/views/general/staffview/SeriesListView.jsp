<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- List Header -->
<p class="text-gray-500 text-sm mb-2 px-9 mt-2">Total: ${size}</p>

<!-- Content Container with Scroll -->
<div class="h-[calc(100vh-15rem)] overflow-y-auto px-9 bg-gray-100 custom-scrollbar">
    <!-- Series List -->

    <c:forEach var="series" items="${seriesInfoDTOList}">
        <div class="space-y-3">
            <div class="flex items-center justify-between border rounded-lg bg-white px-4 py-3 hover:shadow-sm">
                <div class="flex items-center gap-4">
                    <img src="${pageContext.request.contextPath}/${series.coverImgUrl}"
                         class="w-12 h-16 rounded object-cover" alt="">
                    <div>
                        <p class="font-semibold text-gray-800">${series.title}</p>
                        <c:forEach var="category" items="${series.categories}">
                            <span class="border text-xs px-2 rounded-full text-gray-600 bg-gray-100">${category}</span>
                        </c:forEach>
                    </div>
                </div>
                <div class="flex items-center gap-10 text-sm">
                    <p class="flex items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                             viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                             stroke-linecap="round" stroke-linejoin="round"
                             class="lucide lucide-star-icon lucide-star">
                            <path
                                    d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z"/>
                        </svg>
                        <span class="ml-1 text-gray-700">${series.avgRating} (${series.countRatings})</span>
                    </p>
                    <p>${series.totalChapters} Chapters</p>
                    <c:choose>
                        <c:when test="${series.status == 'Completed'}">
                                    <span class="w-20 text-center py-0.5 rounded-full bg-green-100 text-green-700 text-xs">
                                            ${series.status}
                                    </span>
                        </c:when>
                        <c:when test="${series.status == 'Ongoing'}">
                                    <span class="w-20 text-center py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs">
                                            ${series.status}
                                    </span>
                        </c:when>
                        <c:otherwise>
                                    <span class="w-20 text-center py-0.5 rounded-full bg-gray-100 text-gray-700 text-xs">
                                            ${series.status}
                                    </span>
                        </c:otherwise>
                    </c:choose>

                    <p class="text-gray-500">${series.createdAt}</p>
                    <a href="">
                        <button class="flex gap-2 border rounded-md px-2 py-1 text-sm hover:bg-gray-100">
                                <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
                                           fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                           stroke-linejoin="round" class="lucide lucide-eye-icon lucide-eye">
                                        <path
                                                d="M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0"/>
                                        <circle cx="12" cy="12" r="3"/>
                                    </svg></span>
                            Detail
                        </button>
                    </a>
                </div>
            </div>
        </div>
    </c:forEach>
</div>


<!-- Pagination -->
<div class="flex justify-end items-center gap-1 py-4 text-sm px-9 bg-gray-100">
    <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${currentPage-1}&sizePage=${sizePage}"
       class="page-link">
        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                <c:if test="${currentPage == 1}">disabled</c:if>>
            &lt;&lt;
        </button>
    </a>


    <c:if test="${currentPage > 3}">
        <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${1}&sizePage=${sizePage}"
           class="page-link">
            <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">1</button>
        </a>
        <span class="px-2 py-1">...</span>
    </c:if>

    <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
               end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
        <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${i}&sizePage=${sizePage}"
           class="page-link">
            <button class="border rounded-md px-2 py-1
                       ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                    ${i}
            </button>
        </a>
    </c:forEach>

    <c:if test="${currentPage < totalPage - 2}">
        <span class="px-2 py-1">...</span>
        <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${totalPage}&sizePage=${sizePage}"
           class="page-link">
            <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">${totalPage}</button>
        </a>
    </c:if>

    <a href="${pageContext.request.contextPath}/staff?type=${type}&totalPage=${totalPage}&currentPage=${currentPage+1}&sizePage=${sizePage}"
       class="page-link">
        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                <c:if test="${currentPage == totalPage}">disabled</c:if>>
            &gt;&gt;
        </button>
    </a>
</div>
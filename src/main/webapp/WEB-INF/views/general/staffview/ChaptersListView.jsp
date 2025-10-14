<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 4:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- List Header -->
<p class="text-gray-500 text-sm mb-1 px-9">Total: ${size}</p>
<!-- Series List -->
<div class="h-[calc(100vh-15rem)] overflow-y-auto px-9 bg-gray-100 custom-scrollbar">
    <!-- Item -->
    <c:forEach var="chapter" items="${chapterDetailDTOList}">
        <div class="space-y-3 px-9">
            <div class="flex items-center justify-between border rounded-lg bg-white px-4 py-3 hover:shadow-sm">
                <div class="flex items-center gap-4">
                    <div class="px-4">
                        <p class="font-semibold">Chapter ${chapter.chapterNumber}: ${chapter.title}</p>
                        <P class="text-gray-500">Series: ${chapter.seriesTitle}</P>
                    </div>
                </div>
                <div class="flex items-center gap-10 text-sm">
                    <p class="font-extrabold text-[#195DA9]">${chapter.action}</p>
                    <c:choose>
                        <c:when test="${chapter.status == 'Approved'}">
                                    <span class="w-20 text-center py-0.5 rounded-full bg-green-100 text-green-700 text-xs">
                                            ${chapter.status}
                                    </span>
                        </c:when>
                        <c:when test="${chapter.status == 'Pending'}">
                                    <span class="w-20 text-center py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs">
                                            ${chapter.status}
                                    </span>
                        </c:when>
                        <c:otherwise>
                                    <span class="w-20 text-center py-0.5 rounded-full bg-gray-100 text-red-700 text-xs">
                                            ${chapter.status}
                                    </span>
                        </c:otherwise>
                    </c:choose>
                    <div class="flex items-center gap-2 text-sm">
                        <p class="text-gray-500 mr-3">${chapter.updatedAt}</p>
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
                        <div class="relative">
                            <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                                     fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                     stroke-linejoin="round" class="lucide lucide-ellipsis">
                                    <circle cx="12" cy="12" r="1"/>
                                    <circle cx="19" cy="12" r="1"/>
                                    <circle cx="5" cy="12" r="1"/>
                                </svg>
                            </button>

                            <div
                                    class="dropdown-menu hidden absolute right-0 mt-2 w-30 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                                <button
                                        class="block w-full text-[#42CC75] flex gap-2 px-4 py-2 text-sm  hover:bg-gray-100">
                                    <svg
                                            xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                            viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                            stroke-linecap="round" stroke-linejoin="round"
                                            class="lucide lucide-circle-check-big-icon lucide-circle-check-big">
                                        <path d="M21.801 10A10 10 0 1 1 17 3.335"/>
                                        <path d="m9 11 3 3L22 4"/>
                                    </svg>
                                    Approve
                                </button>
                                <button
                                        class="block w-full  text-[#E23636] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                         viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                         stroke-linecap="round" stroke-linejoin="round"
                                         class="lucide lucide-circle-x-icon lucide-circle-x">
                                        <circle cx="12" cy="12" r="10"/>
                                        <path d="m15 9-6 6"/>
                                        <path d="m9 9 6 6"/>
                                    </svg>
                                    Reject
                                </button>
                            </div>
                        </div>
                    </div>
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
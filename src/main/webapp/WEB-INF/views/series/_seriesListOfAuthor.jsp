<%--
  Created by IntelliJ IDEA.
  User: trung
  Date: 11/2/2025
  Time: 8:19 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page buffer="32kb" autoFlush="true" %>
<c:forEach var="mySeries" items="${seriesList}">
    <div
            class="col-span-12 grid grid-cols-2 mb-3 justify-between p-2 bg-white rounded-lg shadow-lg border border-gray-300 hover:shadow-md transition">
        <div class="col-span-1 flex items-center gap-4">
            <img src="${pageContext.request.contextPath}/${mySeries.coverImgUrl}" alt="Cover"
                 class="w-16 h-24 rounded-md object-cover">
            <div>
                <h2 class="text-xl font-semibold text-gray-800">${mySeries.title}</h2>
                <c:if test="${fn:length(mySeries.authorNameList) > 1}">
                <span class="text-xs bg-gray-200 text-gray-600 px-2 py-0.5 rounded">Collab</span>
                </c:if>
            </div>
        </div>

        <div class="col-span-1 flex items-center justify-between gap-4 text-sm">
            <span class="text-yellow-500"><i class="fa-solid fa-star"></i> ${mySeries.avgRating} (${mySeries.totalRating})</span>
            <span>• ${mySeries.totalChapters} Chapters</span>

            <c:choose>
                <c:when test="${mySeries.approvalStatus == 'approved'}">
                    <span class="bg-green-100 text-green-600 px-3 py-1 rounded-full font-medium">${mySeries.approvalStatus}</span>
                </c:when>
                <c:when test="${mySeries.approvalStatus == 'pending'}">
                    <span class="bg-yellow-100 text-yellow-600 px-3 py-1 rounded-full font-medium">${mySeries.approvalStatus}</span>
                </c:when>
                <c:otherwise>
                    <span class="bg-red-100 text-red-600 px-3 py-1 rounded-full font-medium">${mySeries.approvalStatus}</span>
                </c:otherwise>
            </c:choose>

            <span class="text-gray-500">${mySeries.createdAt}</span>

            <div class="flex gap-2 items-center">
                <a href="${pageContext.request.contextPath}/series/detail?seriesId=${mySeries.seriesId}"
                   class="border border-blue-500 text-blue-500 hover:bg-blue-50 px-3 py-1 rounded-md flex items-center gap-1">
                    <i class="fa-solid fa-circle-info"></i> Detail
                </a>

                <div class="relative">
                    <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                        <i class="fa-solid fa-ellipsis"></i>
                    </button>

                    <div
                            class="dropdown-menu hidden absolute right-0 mt-2 w-30 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                        <a href="${pageContext.request.contextPath}/series/edit?seriesId=${mySeries.seriesId}"
                                class="block w-full text-[#42CC75] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                            <i class="fa-regular fa-pen-to-square"></i>
                            Edit
                        </a>
                        <a href="${pageContext.request.contextPath}/series/delete?seriesId=${mySeries.seriesId}"
                                class="block w-full text-[#E23636] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                            <i class="fa-regular fa-trash-can"></i>
                            Delete
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

</c:forEach>
<div class="flex justify-between items-center">
    <div class="mb-4">
        <p class="text-gray-500 text-sm">Total: ${size}</p>
    </div>
    <!-- Pagination -->
    <div class="flex justify-end items-center mb-0 gap-1 text-sm px-9">
        <c:if test="${totalPage > 1}">
            <a href="${pageContext.request.contextPath}/series?action=list&totalPage=${totalPage}&currentPage=${currentPage-1}&sizePage=${sizePage}"
               class="page-link">
                <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                        <c:if test="${currentPage == 1}">disabled</c:if>>
                    &lt;&lt;
                </button>
            </a>

            <c:if test="${currentPage > 3}">
                <a href="${pageContext.request.contextPath}/series?action=list&totalPage=${totalPage}&currentPage=${1}&sizePage=${sizePage}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">1</button>
                </a>
                <span class="px-2 py-1">...</span>
            </c:if>

            <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                       end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
                <a href="${pageContext.request.contextPath}/series?action=list&totalPage=${totalPage}&currentPage=${i}&sizePage=${sizePage}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1
                       ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                            ${i}
                    </button>
                </a>
            </c:forEach>

            <c:if test="${currentPage < totalPage - 2}">
                <span class="px-2 py-1">...</span>
                <a href="${pageContext.request.contextPath}/series?action=list&totalPage=${totalPage}&currentPage=${totalPage}&sizePage=${sizePage}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">${totalPage}</button>
                </a>
            </c:if>

            <a href="${pageContext.request.contextPath}/series?action=list&totalPage=${totalPage}&currentPage=${currentPage+1}&sizePage=${sizePage}"
               class="page-link">
                <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                        <c:if test="${currentPage == totalPage}">disabled</c:if>>
                    &gt;&gt;
                </button>
            </a>
        </c:if>
    </div>
</div>
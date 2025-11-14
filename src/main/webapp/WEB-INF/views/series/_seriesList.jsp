<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 12:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Content -->
<div class="col-span-8" id="result-container">
    <!-- Header: hiển thị tổng số kết quả -->
    <div class="col-span-8 col-start-5">
        <p class="text-sm text-gray-500">${size} results</p>
    </div>

                <!-- Danh sách 2 cột -->
    <c:choose>
        <c:when test="${empty seriesList}">
            <div class="text-center text-gray-500 mt-4">
                <p>No series found.</p>
            </div>
        </c:when>
        <c:otherwise>
            <ul class="grid grid-cols-4 gap-6">
                <c:forEach var="series" items="${seriesList}">
                    <li class="md:w-50 relative group border border-gray-200 shadow-lg rounded-xl overflow-hidden bg-white hover:shadow-2xl transition duration-300">
                        <a href="${pageContext.request.contextPath}/series/detail?seriesId=${series.seriesId}">
                            <!-- Hình ảnh -->
                            <div class="aspect-[3/4] overflow-hidden relative">
                                <img
                                        src="${pageContext.request.contextPath}/${series.coverImgUrl}"
                                        class="w-full h-full object-cover transition duration-300 group-hover:opacity-40"
                                        alt="${series.title}"
                                />

                                <!-- Overlay chứa nút -->
                                <div class="absolute inset-0 flex flex-col items-center justify-center gap-4 opacity-0 group-hover:opacity-100 transition duration-300">
                                    <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId="
                                       class="bg-[#195DA9] text-white px-4 py-2 rounded-md hover:bg-blue-700 transition">
                                        Read now
                                    </a>
                                    <a href="${pageContext.request.contextPath}/series/detail?seriesId=${series.seriesId}"
                                       class="bg-white text-[#195DA9] px-7 py-2 rounded-md border border-[#195DA9] hover:bg-gray-100 transition">
                                        Detail
                                    </a>
                                </div>
                            </div>
                        </a>

                        <!-- Nội dung -->
                        <div class="p-3 flex flex-col justify-between flex-grow">
                            <div>
                                <ul class="flex flex-wrap gap-2 text-xs mb-1">
                                    <c:forEach var="category" items="${series.categoryList}" varStatus="status">
                                        <c:if test="${status.index < 2}">
                                            <li class="rounded-md bg-blue-100 px-1">${category.name}</li>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                                <p class="font-semibold text-lg truncate mb-1">
                                        ${series.title}
                                </p>
                            </div>
                            <div class="text-sm opacity-70">
                                <div class="flex justify-between">
                                    <p>by <span class="font-medium">
                                <c:choose>
                                    <c:when test="${not empty series.authorList}">
                                        ${series.authorList[0].authorName}
                                    </c:when>
                                    <c:otherwise>Unknown</c:otherwise>
                                </c:choose>
                                </span>
                                    </p>
                                    <p>${series.totalChapters} chapters</p>
                                </div>
                                <p>★  <fmt:formatNumber value="${series.avgRating}" type="number" maxFractionDigits="1" minFractionDigits="1"/> (${series.totalRating})</p>
                            </div>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
    <!-- Pagination -->
    <div class="flex justify-end items-center mt-3 mb-3 gap-2 text-sm">
        <c:if test="${totalPage > 1}">
            <!-- Nút prev -->
            <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                    data-page="${currentPage - 1}"
                    <c:if test="${currentPage == 1}">disabled</c:if>>
                &lt;&lt;
            </button>

            <c:if test="${currentPage > 3}">
                <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white" data-page="1">1</button>
                <span class="px-2 py-1">...</span>
            </c:if>

            <!-- Vòng lặp số trang -->
            <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                       end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
                <button class="page-btn border rounded-md px-2 py-1
                                ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}"
                                data-page="${i}">${i}</button>
            </c:forEach>

            <c:if test="${currentPage < totalPage - 2}">
                <span class="px-2 py-1">...</span>
                <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white" data-page="${totalPage}">
                        ${totalPage}
                </button>
            </c:if>

            <!-- Nút next -->
            <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                    data-page="${currentPage + 1}"
                    <c:if test="${currentPage == totalPage}">disabled</c:if>>
                &gt;&gt;
            </button>
        </c:if>
    </div>
</div>


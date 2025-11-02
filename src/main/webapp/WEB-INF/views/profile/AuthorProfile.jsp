<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 1:37 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>


<main class="my-12 grid grid-cols-12 gap-x-12 justify-center">

    <div class="col-span-3 flex flex-col items-center text-center">
        <img src="${pageContext.request.contextPath}/img/shared/imgUser.png" alt="avatar" class="rounded-full mb-4">
    </div>
    <div class="col-span-9">
        <div class="flex items-center gap-4">
            <h2 class="flex-1 text-3xl font-semibold text-[#195DA9] text-nowrap">Nguyen Trung Nguyen</h2>
            <c:forEach var="badge" items="${badgeList}" begin="1" end="4">
                <div>
                    <img alt="${badge.name}" src="${badge.iconUrl}" class="rounded-full">
                </div>
            </c:forEach>


        </div>

        <div class=" mt-4 text-gray-600 border border-[#195DA9] rounded-lg p-3 leading-relaxed">
            <h3 class="font-semibold mb-1 text-3xl text-[#195DA9]">Bio</h3>
            <p class="text-lg whitespace-pre-wrap">${user.bio}</p>
        </div>
    </div>
    <div class="col-span-12 flex justify-evenly">
        <div class="w-1/4 bg-[#195DA9] text-white px-8 py-4  mt-10 rounded-lg shadow">
            <p class="text-2xl font-medium">Total Series</p>
            <div class="flex items-center justify-between">
                <p class="text-2xl font-bold mt-2">${totalSeriesCount} </p>
                <i class="fa-solid fa-book-open"></i>
            </div>
        </div>
        <div class="w-1/4 bg-[#195DA9] text-white px-8 py-4  mt-10 rounded-lg shadow">
            <p class="text-2xl font-medium">Total Likes</p>
            <div class="flex items-center justify-between">
                <p class="text-2xl font-bold mt-2">${totalLike} </p>
                <i class="fa-solid fa-thumbs-up"></i>
            </div>
        </div>
        <div class="w-1/4 bg-[#195DA9] text-white px-8 py-4  mt-10 rounded-lg shadow">
            <p class="text-2xl font-medium">Average Rating</p>
            <div class="flex items-center justify-between">
                <p class="text-2xl font-bold mt-2">${avgRating} </p>
                <i class="fa-regular fa-star"></i>
            </div>
        </div>
    </div>


    <!-- Danh sách Series -->
    <div class="col-span-12 mt-10 border border-[#195DA9] rounded-xl p-4 h-[400px] mb-5 overflow-y-auto">
        <c:choose>
            <c:when test="${empty seriesInfoDTOList}">
                <p class="text-center text-gray-800 text-xl font-semibold mt-32">
                    There are currently no series
                </p>
            </c:when>
            <c:otherwise>
                <div class="flex flex-col gap-4">
                    <!-- Card Series -->
                    <c:forEach var="series" items="${seriesInfoDTOList}" varStatus="loop">
                        <a href="${pageContext.request.contextPath}/series?action=detail&seriesId=${series.seriesId}">
                            <div
                                    class="flex items-center justify-between border border-gray-300 rounded-lg bg-white px-4 py-3 hover:shadow-sm">
                                <div class="flex items-center gap-4">
                                    <img src="./${series.coverImgUrl}" class="w-12 h-16 rounded object-cover" alt="">
                                    <div>
                                        <p class="font-semibold text-gray-800">${series.title}</p>
                                        <c:forEach var="category" items="${series.categories}">
                                            <span class="border text-xs px-2 rounded-full text-gray-600 bg-gray-100">${category}</span>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="flex items-center gap-10 text-sm">
                                    <p class="flex items-center">
                                        <i class="fa-regular fa-star"></i>
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
                                    <p class="text-gray-500">${series.updatedAt}</p>

                                </div>
                            </div>
                        </a>

                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>




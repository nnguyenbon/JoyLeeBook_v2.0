<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 10:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>
<div class=" mt-10">
    <!-- Tabs -->
    <div class="flex border-b border-gray-300 mb-6">
        <button id="tab-saved"
                class="text-lg font-semibold px-4 pb-2 border-b-2 border-blue-500 text-blue-600 focus:outline-none">
            Saved
        </button>
        <button id="tab-history"
                class="text-lg font-semibold px-4 pb-2 text-gray-500 hover:text-blue-500 focus:outline-none">
            History
        </button>
    </div>

    <!-- SAVED TAB -->
    <div id="content-saved" class="grid grid-cols-2 gap-6">
        <!-- Item -->
        <c:forEach var="series" items="${savedSeries}">
            <a href="${pageContext.request.contextPath}/series/detail?seriesId=${series.seriesId}"
               class="block">
                <div class="relative flex bg-white rounded-lg shadow-sm hover:shadow-md transition p-3 h-full">
                    <!-- Bookmark Remove Icon -->
                    <form action="${pageContext.request.contextPath}/library/save?type=unsave&seriesId=${series.seriesId}&isLibrary=true" method="post">
                        <button class="absolute top-2 right-2 z-10 text-gray-400 hover:text-red-500 transition"
                                type="submit" title="Remove from saved">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </form>

                    <!-- Content -->
                    <img src="${series.coverImgUrl}" alt="cover" class="w-24 h-32 object-cover rounded-md flex-shrink-0">
                    <div class="ml-4 flex-1 flex flex-col min-h-0">
                        <h3 class="font-semibold text-gray-900 line-clamp-2 mb-1">${series.title}</h3>
                        <p class="text-sm text-gray-500 mb-1 line-clamp-1">by
                            <c:forEach var="author" items="${series.authorList}" varStatus="loop">
                                ${author.authorName}<c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </p>
                        <p class="text-sm text-gray-600 mb-2 line-clamp-2">
                                ${series.description}
                        </p>
                        <div class="flex items-center flex-wrap gap-2 text-xs mb-2">
                            <c:forEach var="category" items="${series.categoryList}" varStatus="catStatus">
                                <c:if test="${catStatus.index < 2}">
                                    <span class="px-2 py-1 bg-pink-100 text-pink-600 rounded-full whitespace-nowrap">${category.name}</span>
                                </c:if>
                            </c:forEach>

                            <c:choose>
                                <c:when test="${series.status == 'Completed'}">
                                <span class="w-20 text-center py-0.5 rounded-full bg-green-100 text-green-700 text-xs whitespace-nowrap">
                                        ${series.status}
                                </span>
                                </c:when>
                                <c:when test="${series.status == 'Ongoing'}">
                                <span class="w-20 text-center py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs whitespace-nowrap">
                                        ${series.status}
                                </span>
                                </c:when>
                                <c:otherwise>
                                <span class="w-20 text-center py-0.5 rounded-full bg-gray-100 text-gray-700 text-xs whitespace-nowrap">
                                        ${series.status}
                                </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="flex justify-between text-xs text-gray-500 mt-auto pt-2">
                            <span class="whitespace-nowrap">⭐ ${series.avgRating} (${series.totalRating})</span>
                            <span class="whitespace-nowrap">${series.totalChapters} Chapters</span>
                            <span class="whitespace-nowrap">Updated: ${series.updatedAt}</span>
                        </div>
                    </div>
                </div>
            </a>
        </c:forEach>
        <!-- Load More -->
        <div class="col-span-2 flex justify-center mt-6">
            <div class="text-center">
                <button class="px-4 py-2 bg-gray-100 rounded-full text-sm text-gray-600 hover:bg-gray-200 transition">
                    Load more stories
                </button>
                <p class="text-xs text-gray-500 mt-2">
                    You are seeing ${savedSeries.size()} of ${savedSeries.size()}.
                </p>
            </div>
        </div>
    </div>


    <!-- HISTORY TAB -->
    <div id="content-history" class="hidden flex flex-col space-y-3">
        <c:if test="${not empty historyChapters}">
            <div class="col-span-2 flex justify-center mt-6">
                <form action="${pageContext.request.contextPath}/library/clearHistory" method="post">
                    <button class="px-4 py-2 bg-red-100 rounded-full text-sm text-red-600 hover:bg-red-200 transition">
                        <i class="fa-solid fa-trash-can"></i> Clear All History
                    </button>
                </form>
            </div>
        </c:if>
        <c:forEach var="chapter" items="${historyChapters}">
            <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}&returnUrl=${pageContext.request.requestURI}">
                <div class="flex items-center bg-white rounded-lg shadow-sm hover:shadow-md transition p-3">
                    <img src="${chapter.coverImgUrl}" alt="cover" class="w-16 h-20 object-cover rounded-md">
                    <div class="ml-4 flex-1">
                        <h3 class="font-semibold text-gray-900">${chapter.seriesTitle}</h3>
                        <p class="text-sm text-blue-500">Chapter ${chapter.chapterNumber} ${chapter.title}</p>
                    </div>
                    <div class="text-sm text-gray-500">${chapter.lastReadAt }</div>
                    <form action="${pageContext.request.contextPath}/library/deleteHistory?chapterId=${chapter.chapterId}" method="post">
                        <button class="ml-4 text-gray-400 hover:text-red-500" type="submit" title="Delete">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </form>
                </div>
            </a>
        </c:forEach>
        <div class="col-span-2 flex justify-center mt-6">
            <div class="text-center">
                <button class="px-4 py-2 bg-gray-100 rounded-full text-sm text-gray-600 hover:bg-gray-200 transition">
                    Load more chapters
                </button>
                <p class="text-xs text-gray-500 mt-2">
                    You are seeing ${historyChapters.size()} of ${historyChapters.size()}.
                </p>
            </div>
        </div>
    </div>


</div>

<script>
    const tabSaved = document.getElementById('tab-saved');
    const tabHistory = document.getElementById('tab-history');
    const contentSaved = document.getElementById('content-saved');
    const contentHistory = document.getElementById('content-history');

    tabSaved.addEventListener('click', () => {
        tabSaved.classList.add('border-blue-500', 'text-gray-900', 'border-b-2');
        tabSaved.classList.remove('text-gray-500');
        tabHistory.classList.remove('border-blue-500', 'text-gray-900', 'border-b-2');
        tabHistory.classList.add('text-gray-500');
        contentSaved.classList.remove('hidden');
        contentHistory.classList.add('hidden');
    });

    tabHistory.addEventListener('click', () => {
        tabHistory.classList.add('border-blue-500', 'text-gray-900', 'border-b-2');
        tabHistory.classList.remove('text-gray-500');
        tabSaved.classList.remove('border-blue-500', 'text-gray-900', 'border-b-2');
        tabSaved.classList.add('text-gray-500');
        contentSaved.classList.add('hidden');
        contentHistory.classList.remove('hidden');
    });
</script>

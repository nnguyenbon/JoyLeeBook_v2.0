<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 10:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<main class="flex-1 bg-gray-100 max-h-[90vh] custom-scrollbar overflow-y-auto">
    <div class="p-9">
        <div class="flex h-60 gap-5 mb-5">
            <img src="${series.coverImgUrl}" alt="Series cover" class=" rounded-lg shadow"/>
            <div class="">
                <h1 class="text-4xl font-bold">${series.title}</h1>
                <p class="text-gray-600 mb-4">by <span class="font-semibold">
                        <c:forEach var="author" items="${series.authorsName}" varStatus="loop">
                            ${author}<c:if test="${!loop.last}">, </c:if>
                        </c:forEach>
                        </span></p>

                <div class="flex flex-wrap gap-2 mb-10">
                    <c:forEach var="category" items="${seriesInfoDTO.categories}">
                        <span class="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">${category}</span>
                    </c:forEach>
                    <c:choose>
                        <c:when test="${seriesInfoDTO.status == 'Completed'}">
                            <span class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium">${seriesInfoDTO.status}</span>
                        </c:when>
                        <c:when test="${seriesInfoDTO.status == 'Ongoing'}">
                            <span class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium">${seriesInfoDTO.status}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-xs px-3 py-1 rounded-full bg-gray-100 text-gray-700 font-medium">
                                    ${seriesInfoDTO.status}
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="flex items-center gap-5 mb-10">
                    <span class="font-semibold text-lg">${series.totalChapters} Chapters</span>

                    <div class="text-gray-500 font-semibold text-lg">
                        <span class="text-yellow-400">★ ${series.avgRating}</span>
                        <span>(${series.countRatings})</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="border-2 rounded-lg bg-white mb-3">
            <h2 class="px-4 text-2xl font-semibold">Description</h2>
            <p class="p-4 text-gray-700">${series.description}</p>

        </div>
        <div class="border-2 rounded-lg bg-white px-4 max-h-[350px] overflow-y-auto py-4">
            <h2 class="text-2xl font-semibold mb-3">Chapter List</h2>
            <c:forEach var="chapter" items="${chapterDetailDTOList}" varStatus="loop">
                <div
                        class="flex justify-between items-center border rounded-lg px-4 py-3 mb-4 bg-white hover:bg-gray-50 cursor-pointer">
                    <span class="text-lg font-semibold">Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                    <div class="flex items-center gap-4">
                        <p class="font-extrabold text-[#195DA9]">Update chapter</p>
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
                            <a href="${pageContext.request.contextPath}/chapter?action=detail&chapterId=${chapter.chapterId}">
                                <button class="flex gap-2 border rounded-md px-2 py-1 text-sm hover:bg-gray-100">
                                    <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                                               viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                               stroke-linecap="round" stroke-linejoin="round"
                                               class="lucide lucide-eye-icon lucide-eye">
                                            <path
                                                    d="M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0"/>
                                            <circle cx="12" cy="12" r="3"/>
                                        </svg></span> Detail
                                </button>
                            </a>
                            <div class="relative">
                                <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                                         viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                         stroke-linecap="round" stroke-linejoin="round"
                                         class="lucide lucide-ellipsis">
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
            </c:forEach>
        </div>
    </div>
</main>
<script>
    document.querySelectorAll('.dropdown-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation(); // Ngăn việc click lan ra ngoài
            const menu = btn.nextElementSibling; // Tìm menu liền sau button
            document.querySelectorAll('.dropdown-menu').forEach(m => {
                if (m !== menu) m.classList.add('hidden'); // ẩn các menu khác
            });
            menu.classList.toggle('hidden');
        });
    });

    window.addEventListener('click', () => {
        document.querySelectorAll('.dropdown-menu').forEach(m => m.classList.add('hidden'));
    });
</script>
</body>

</html>
<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 10:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <main class="flex-1 bg-gray-100">

        <!-- Content -->
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
                        <c:forEach var="category" items="${series.categories}">
                            <span class="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">${category}</span>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${series.status == 'Completed'}">
                            <span class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium">${series.status}</span>
                        </c:when>
                        <c:when test="${series.status == 'Ongoing'}">
                            <span class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium">${series.status}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-xs px-3 py-1 rounded-full bg-gray-100 text-gray-700 font-medium">
                                    ${series.status}
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
                                    <div class="relative flex justify-end gap-2 text-left">
                                        <a href="${pageContext.request.contextPath}/chapter?action=detail&seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}"
                                           class="block px-2 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-blue-100 flex items-center gap-2">
                                            <i class="fa-regular fa-eye mr-2"></i>Detail
                                        </a>

                                        <button type="button"
                                                class="text-gray-500 hover:text-gray-700"
                                                data-bs-toggle="dropdown">
                                            <i class="fas fa-ellipsis-v"></i>
                                        </button>

                                        <ul class="absolute right-0 mt-2 w-44 bg-white border border-gray-200 rounded-lg shadow-lg hidden group-hover:block">
                                            <li>
                                                <button class="block w-full text-[#42CC75] flex items-center gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                                                    <i class="fa-regular fa-circle-check mr-2"></i>
                                                    Approve
                                                </button>
                                            </li>
                                            <li>
                                                <button class="block w-full text-[#E23636] flex items-center gap-2 px-4 py-2 text-sm hover:bg-red-50">
                                                    <i class="fa-regular fa-circle-xmark mr-2"></i>
                                                    Reject
                                                </button>
                                            </li>
                                        </ul>
                                    </div>
                                </a>


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
    //Lay all btn in menu (ellipsis)
    const dropdownButtons = document.querySelectorAll('[data-bs-toggle="dropdown"]');

    dropdownButtons.forEach(button => {
        const menu = button.parentElement.querySelector("ul");

        //When click on ellipsis -> toggle show menu
        button.addEventListener("click", (e) => {
            e.stopPropagation(); // Prevent the event from spreading
            closeAllDropdowns();
            menu.classList.toggle("hidden");
        })
    })

    //When click on out then hidden all menu
    document.addEventListener("click", () => {
        closeAllDropdowns();
    })

    //Function close all menu
    function closeAllDropdowns() {
        document.querySelectorAll('.relative ul').forEach(menu => {
            menu.classList.add("hidden");
        })
    }
</script>

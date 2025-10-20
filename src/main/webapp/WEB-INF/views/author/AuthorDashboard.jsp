<%--
  Created by IntelliJ IDEA.
  User: Trunguyen
  Date: 10/20/2025
  Time: 8:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<main class="mt-10 grid grid-cols-12 gap-x-5 gap-y-3 items-center">
    <h1 class="col-span-12 text-4xl font-bold text-[#195DA9] mb-5">Author Dashboard</h1>
    <div
            class="col-span-3 border border-blue-500 rounded-lg bg-blue-50 p-3 flex flex-col justify-between shadow-sm">
        <span class="text-xl font-semibold text-gray-700">Total Series</span>
        <div class="flex items-center justify-between gap-3 text-blue-600">
            <span class="text-2xl font-bold">${fn:length(mySeriesList)}</span>
            <i class="fa-solid fa-book text-xl"></i>
        </div>
    </div>

    <div
            class="col-span-3 border border-teal-500 rounded-lg bg-teal-50 p-3 flex flex-col justify-between shadow-sm">
        <span class="text-lg font-semibold text-gray-700">Total Chapters</span>
        <div class="flex items-center justify-between gap-3 text-teal-500">
            <span class="text-2xl font-bold">${totalChapters}</span>
            <i class="fa-regular fa-file-lines text-xl"></i>
        </div>
    </div>

    <div
            class="col-span-2 border border-amber-500 rounded-lg bg-amber-50 p-3 flex flex-col justify-between shadow-sm">
        <span class="text-lg font-semibold text-gray-700">Pending Chapters</span>
        <div class="flex items-center justify-between gap-3 text-amber-500">
            <span class="text-2xl font-bold">${pendingChapters}</span>
            <i class="fa-regular fa-clock text-xl"></i>
        </div>
    </div>

    <div
            class="col-span-2 border border-rose-500 rounded-lg bg-rose-50 p-3 flex flex-col justify-between shadow-sm">
        <span class="text-lg font-semibold text-gray-700">Total Likes</span>
        <div class="flex items-center justify-between gap-3 text-rose-500">
            <span class="text-2xl font-bold">${totalLikes}</span>
            <i class="fa-regular fa-heart text-xl"></i>
        </div>
    </div>

    <div
            class="col-span-2 border border-violet-500 rounded-lg bg-violet-50 p-3 flex flex-col justify-between shadow-sm">
        <span class="text-lg font-semibold text-gray-700">Average Rating</span>
        <div class="flex items-center justify-between gap-3 text-violet-500">
            <span class="text-2xl font-bold">${avgRating}</span>
            <i class="fa-regular fa-star text-xl"></i>
        </div>
    </div>

    <div class="col-span-12 flex justify-between items-center my-4">
        <div class="flex items-center gap-3">
            <span class="text-gray-600 text-lg font-medium">Total: 3</span>
            <div class="relative">
                <input type="text" placeholder="Search series..."
                       class="border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 w-64">
                <i class="fa-solid fa-magnifying-glass absolute right-3 top-3 text-gray-400"></i>
            </div>
        </div>

        <div class="flex items-center gap-3">
            <select
                    class="border border-gray-300 rounded-md px-3 py-2 text-gray-700 focus:ring-2 focus:ring-blue-400">
                <option>All status</option>
                <option>Ongoing</option>
                <option>Completed</option>
            </select>

            <button
                    class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded-md shadow-sm transition">
                <i class="fa-solid fa-plus"></i>
                Create
            </button>
        </div>
    </div>
    <c:forEach var="mySeries" items="${mySeriesList}">
        <div
                class="col-span-12 grid grid-cols-2 justify-between p-2 bg-white rounded-lg shadow-lg border border-gray-300 hover:shadow-md transition">
            <div class="col-span-1 flex items-center gap-4">
                <img src="${pageContext.request.contextPath}/${mySeries.coverImgUrl}" alt="Cover" class="w-12 h-16 rounded-md object-cover">
                <div>
                    <h2 class="text-xl font-semibold text-gray-800">${mySeries.title}</h2>
                    <span class="text-xs bg-gray-200 text-gray-600 px-2 py-0.5 rounded">collab</span>

                </div>
            </div>
            <div class="col-span-1 flex items-center justify-between gap-4 text-sm">
                <span class="text-yellow-500"><i class="fa-solid fa-star"></i> ${mySeries.avgRating} (${mySeries.countRatings})</span>
                <span>• ${mySeries.totalChapters} Chapters</span>
                <c:choose>
                    <c:when test="${mySeries.status == 'Completed'}">
                                    <span class="bg-green-100 text-green-600 px-3 py-1 rounded-full font-medium">
                                            ${mySeries.status}
                                    </span>
                    </c:when>
                    <c:when test="${mySeries.status == 'Ongoing'}">
                                    <span class="bg-yellow-100 text-yellow-600 px-3 py-1 rounded-full font-mediums">
                                            ${mySeries.status}
                                    </span>
                    </c:when>
                    <c:otherwise>
                                    <span class="bg-gray-100 text-gray-600 px-3 py-1 rounded-full font-medium">
                                            ${mySeries.status}
                                    </span>
                    </c:otherwise>
                </c:choose>
                <span class="text-gray-500">${mySeries.createdAt}</span>
                <div class="flex gap-2">
                    <button
                            class="border border-blue-500 text-blue-500 hover:bg-blue-50 px-3 py-1 rounded-md flex items-center gap-1">
                        <i class="fa-solid fa-circle-info"></i> Detail
                    </button>
                    <button
                            class="border border-gray-400 text-gray-600 hover:bg-gray-50 px-3 py-1 rounded-md flex items-center gap-1">
                        <i class="fa-solid fa-pen"></i> Edit
                    </button>
                </div>
            </div>
        </div>
    </c:forEach>
<%--    <div--%>
<%--            class="col-span-12 mb-2 grid grid-cols-2 justify-between p-4 bg-white rounded-lg shadow-lg border border-gray-300 hover:shadow-md transition">--%>
<%--        <div class="col-span-1 flex items-center gap-4">--%>
<%--            <img src="TheShattredVows1.jpg" alt="Cover" class="w-16 h-20 rounded-md object-cover">--%>
<%--            <div>--%>
<%--                <h2 class="text-xl font-semibold text-gray-800">The Shattred Vows</h2>--%>
<%--                <span class="text-xs bg-gray-200 text-gray-600 px-2 py-0.5 rounded">collab</span>--%>

<%--            </div>--%>
<%--        </div>--%>
<%--        <div class="col-span-1 flex items-center justify-between gap-4 text-sm">--%>
<%--            <span class="text-yellow-500"><i class="fa-solid fa-star"></i> 5.0 (1247)</span>--%>
<%--            <span>• 25 Chapters</span>--%>
<%--            <span class="bg-green-100 text-green-600 px-3 py-1 rounded-full font-medium">Completed</span>--%>
<%--            <span class="text-gray-500">25/09/2025</span>--%>
<%--            <div class="flex gap-2">--%>
<%--                <button--%>
<%--                        class="border border-blue-500 text-blue-500 hover:bg-blue-50 px-3 py-1 rounded-md flex items-center gap-1">--%>
<%--                    <i class="fa-solid fa-circle-info"></i> Detail--%>
<%--                </button>--%>
<%--                <button--%>
<%--                        class="border border-gray-400 text-gray-600 hover:bg-gray-50 px-3 py-1 rounded-md flex items-center gap-1">--%>
<%--                    <i class="fa-solid fa-pen"></i> Edit--%>
<%--                </button>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>


</main>

<script>
    const genreButton = document.getElementById("genreButton");
    const genreMenu = document.getElementById("genreMenu");
    const BtnAvatar = document.getElementById("BtnAvatar");
    const MenuAvatar = document.getElementById("MenuAvatar");

    genreButton.addEventListener("click", () => {
        genreMenu.classList.toggle("hidden");
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener("click", (e) => {
        if (!genreButton.contains(e.target) && !genreMenu.contains(e.target)) {
            genreMenu.classList.add("hidden");
        }
    });

    BtnAvatar.addEventListener("click", () => {
        MenuAvatar.classList.toggle("hidden");
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener("click", (e) => {
        if (!BtnAvatar.contains(e.target) && !MenuAvatar.contains(e.target)) {
            MenuAvatar.classList.add("hidden");
        }
    });
</script>


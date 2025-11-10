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
    <div class="col-span-12 gap-8 flex justify-between items-center">
        <div
                class="w-1/5 border border-blue-500 rounded-lg bg-blue-50 p-3 flex flex-col justify-between shadow-sm">
            <span class="text-xl font-semibold text-gray-700">Total Series</span>
            <div class="flex items-center justify-between gap-3 text-blue-600">
                <span class="text-2xl font-bold">${fn:length(mySeriesList)}</span>
                <i class="fa-solid fa-book text-xl"></i>
            </div>
        </div>

        <div
                class="w-1/5 border border-teal-500 rounded-lg bg-teal-50 p-3 flex flex-col justify-between shadow-sm">
            <span class="text-lg font-semibold text-gray-700">Total Chapters</span>
            <div class="flex items-center justify-between gap-3 text-teal-500">
                <span class="text-2xl font-bold">${totalChapters}</span>
                <i class="fa-regular fa-file-lines text-xl"></i>
            </div>
        </div>

        <div
                class="w-1/5 border border-amber-500 rounded-lg bg-amber-50 p-3 flex flex-col justify-between shadow-sm">
            <span class="text-lg font-semibold text-gray-700">Pending Chapters</span>
            <div class="flex items-center justify-between gap-3 text-amber-500">
                <span class="text-2xl font-bold">${pendingChapters}</span>
                <i class="fa-regular fa-clock text-xl"></i>
            </div>
        </div>

        <div
                class="w-1/5 border border-rose-500 rounded-lg bg-rose-50 p-3 flex flex-col justify-between shadow-sm">
            <span class="text-lg font-semibold text-gray-700">Total Likes</span>
            <div class="flex items-center justify-between gap-3 text-rose-500">
                <span class="text-2xl font-bold">${totalLikes}</span>
                <i class="fa-regular fa-heart text-xl"></i>
            </div>
        </div>

        <div
                class="w-1/5 border border-violet-500 rounded-lg bg-violet-50 p-3 flex flex-col justify-between shadow-sm">
            <span class="text-lg font-semibold text-gray-700">Average Rating</span>
            <div class="flex items-center justify-between gap-3 text-violet-500">
                <span class="text-2xl font-bold">${avgRating}</span>
                <i class="fa-regular fa-star text-xl"></i>
            </div>
        </div>

    </div>

    <div class="col-span-12 flex justify-between items-center my-4">
        <div class="flex items-center gap-3">
            <div class="relative">
                <input type="text" placeholder="Search series..."
                       class="border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400 w-145">
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

            <a href="${pageContext.request.contextPath}/series/add"
               class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded-md shadow-sm transition">
                <i class="fa-solid fa-plus"></i>
                Create
            </a>
        </div>
    </div>
    <div id="seriesListContainer" class="col-span-12">

    </div>

</main>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        loadSeriesList();

        function loadSeriesList() {
            fetch("${pageContext.request.contextPath}/series/list")
                .then(response => response.text())
                .then(html => {
                    document.getElementById("seriesListContainer").innerHTML = html;
                    initDropdowns();
                })
                .catch(err => console.error("Error loading series list:", err));
        }

        function initDropdowns() {
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
        }
    })

</script>


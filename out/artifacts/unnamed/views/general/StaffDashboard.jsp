<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/13/2025
  Time: 2:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css"/>
    <title>Staff Dashboard - JoyLeeBook</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>

<body class="bg-white text-gray-800">

<div class="flex h-screen">
    <!-- Sidebar -->
    <aside class="w-56 border-r flex flex-col justify-between">
        <div>
            <div class="h-28 flex items-center justify-center border-b">
                <img src="${pageContext.request.contextPath}/img/logo2.png" alt="Logo" class="h-15 w-30 mx-auto ">
            </div>
            <nav class="">
                <a href="${pageContext.request.contextPath}/staff" class="flex items-center px-4 py-2 bg-[#195DA9]/10 text-[#195DA9] font-medium ">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                         class="lucide lucide-book-open-text-icon lucide-book-open-text">
                        <path d="M12 7v14"/>
                        <path d="M16 12h2"/>
                        <path d="M16 8h2"/>
                        <path
                                d="M3 18a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1h5a4 4 0 0 1 4 4 4 4 0 0 1 4-4h5a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1h-6a3 3 0 0 0-3 3 3 3 0 0 0-3-3z"/>
                        <path d="M6 12h2"/>
                        <path d="M6 8h2"/>
                    </svg>
                    <span class="ml-2">Series List</span>
                </a>
                <a href="${pageContext.request.contextPath}/report" class="flex items-center px-4 py-2 hover:bg-gray-100">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                         class="lucide lucide-octagon-alert-icon lucide-octagon-alert">
                        <path d="M12 16h.01"/>
                        <path d="M12 8v4"/>
                        <path
                                d="M15.312 2a2 2 0 0 1 1.414.586l4.688 4.688A2 2 0 0 1 22 8.688v6.624a2 2 0 0 1-.586 1.414l-4.688 4.688a2 2 0 0 1-1.414.586H8.688a2 2 0 0 1-1.414-.586l-4.688-4.688A2 2 0 0 1 2 15.312V8.688a2 2 0 0 1 .586-1.414l4.688-4.688A2 2 0 0 1 8.688 2z"/>
                    </svg>
                    <span class="ml-2">Reports</span>
                </a>
                <a href="#" class="flex items-center px-4 py-2 hover:bg-gray-100">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                         class="lucide lucide-users-icon lucide-users">
                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                        <path d="M16 3.128a4 4 0 0 1 0 7.744"/>
                        <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
                        <circle cx="9" cy="7" r="4"/>
                    </svg>
                    <span class="ml-2">Users</span>
                </a>
            </nav>
        </div>

        <div class="p-4 border-t">
            <a href="#" class="flex items-center text-red-500 hover:text-red-600">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                     class="lucide lucide-log-out-icon lucide-log-out">
                    <path d="m16 17 5-5-5-5"/>
                    <path d="M21 12H9"/>
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                </svg>
                <span class="ml-2">Logout</span>
            </a>
        </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 bg-gray-100">
        <!-- Header -->
        <div class="h-28 flex items-center border-b bg-white p-9 mb-1">
            <p class="font-semibold text-lg">Staff: <span class="text-[#195DA9]">${staffName}</span></p>
        </div>

        <!-- Tabs -->
        <div class="border-b  flex items-center px-9">
            <button id="tab-series" data-type="series"
                    class="tab-btn font-extrabold border-b-4 border-[#195DA9] pb-1 px-4">Series List
            </button>
            <button id="tab-chapter" data-type="chapter"
                    class="tab-btn text-gray-500 px-4 pb-2 hover:text-[#195DA9]">Chapter review
            </button>
            <button id="tab-genres" data-type="genres"
                    class="tab-btn text-gray-500 px-4 pb-2 hover:text-[#195DA9]">Genres
            </button>

            <div class="ml-auto flex gap-2">
                <select class="border rounded-md text-sm px-2 py-1">
                    <option>All status</option>
                    <option>Completed</option>
                    <option>Ongoing</option>
                </select>
                <input type="text" placeholder="Search series, chapter..."
                       class="border rounded-md text-sm px-3 py-1 w-60 focus:outline-[#195DA9]"/>
            </div>
        </div>

        <div id="tab-content">
            <c:choose>
                <c:when test="${type == 'series'}">
                    <jsp:include page="/WEB-INF/views/general/staffview/SeriesListView.jsp"/>
                </c:when>
                <c:when test="${type == 'chapter'}">
                    <jsp:include page="/WEB-INF/views/general/staffview/ChaptersListView.jsp"/>
                </c:when>
                <c:when test="${type == 'genres'}">
                    <jsp:include page="/WEB-INF/views/general/staffview/GenresListView.jsp"/>
                </c:when>
            </c:choose>
        </div>

    </main>
</div>
</body>
<script>
    const contextPath = "${pageContext.request.contextPath}";
    const tabs = document.querySelectorAll(".tab-btn");
    const container = document.getElementById("tab-content");

    let currentType = "series";

    function highlightActiveTab(type) {
        tabs.forEach(tab => {
            if (tab.dataset.type === type) {
                tab.classList.add("font-extrabold", "border-b-4", "border-[#195DA9]", "text-[#195DA9]");
                tab.classList.remove("text-gray-500");
            } else {
                tab.classList.remove("font-extrabold", "border-b-4", "border-[#195DA9]", "text-[#195DA9]");
                tab.classList.add("text-gray-500");
            }
        });
    }

    function getSizeByType(type) {
        switch (type) {
            case "series": return 8;
            case "chapter": return 10;
            case "genre": return 20;
            default: return 5;
        }
    }

    function fetchContent(type, page = 1) {
        currentType = type;
        highlightActiveTab(type);

        const sizePage = getSizeByType(type);
        const url = contextPath+ '/staff?type=' + encodeURIComponent(type) + '&sizePage=' + sizePage+'&currentPage='+page;

        fetch(url)
            .then(res => res.text())
            .then(html => {
                container.innerHTML = html;


                if (window.tailwind?.refresh) window.tailwind.refresh();
                bindPaginationEvents();
                if (typeof bindFilterEvents === "function") bindFilterEvents();
            })
            .catch(err => {
                console.error("Fetch failed:", err);
                container.innerHTML = `<p class="text-red-500 p-4">Failed to load ${type} content.</p>`;
            });
    }

    function bindPaginationEvents() {
        const paginationLinks = container.querySelectorAll(".page-link, .pagination a");
        paginationLinks.forEach(link => {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                const url = new URL(link.href);
                const page = url.searchParams.get("currentPage") || url.searchParams.get("page") || 1;
                fetchContent(currentType, page);
            });
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        tabs.forEach(tab => {
            tab.addEventListener("click", () => {
                const type = tab.dataset.type;
                fetchContent(type);
            });
        });
        fetchContent(currentType);
    });
</script>
</html>
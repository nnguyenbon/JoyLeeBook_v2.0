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
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Document</title>
    <link rel="stylesheet" href="./styles.css"/>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet"/>
</head>

<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>
<!-- Content -->
<main class="main max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-x-[30px]">
    <div class="col-span-12 border-b-1 border-gray-300 flex items-center">
        <button id="btn-title" class="tab-btn font-bold text-2xl px-4" onclick="loadResults('title')">Series</button>
        <button id="btn-author" class="tab-btn font-bold text-2xl px-4" onclick="loadResults('author')">Author</button>
    </div>
    <div class="col-span-9" id="search-results">
            <jsp:include page="/WEB-INF/views/general/searchview/SearchTitleView.jsp"/>
    </div>

    <!-- CỘT PHẢI: BẢNG XẾP HẠNG -->
    <div class="col-span-3 col-start-10 space-y-6">
        <!-- Top Weekly Series -->
        <h3 class="font-semibold text-gray-800 mb-3">Top Weekly Series</h3>

        <div class="bg-white rounded-xl shadow p-5">
            <ol class="space-y-4 text-sm text-gray-500">
                <c:forEach var="weeklySeries" items="${weeklySeriesList}" varStatus="loop">
                    <li class="flex justify-between gap-4">
                        <p class="truncate">
                            <span class="pr-4">${loop.index + 1}</span>${weeklySeries.title}
                        </p>
                        <span>${weeklySeries.avgRating}</span>
                    </li>
                </c:forEach>
            </ol>
        </div>

        <!-- Top Reader Points -->
        <h3 class="font-semibold text-gray-800 mb-3">Top Reader Points</h3>

        <div class="bg-white rounded-xl shadow p-5">
            <ol class="space-y-4 text-sm text-gray-500">
                <c:forEach var="user" items="${userList}" varStatus="loop">
                    <li class="flex justify-between gap-4">
                        <p class="truncate">
                            <span class="pr-4">${loop.index + 1}</span>${user.username}
                        </p>
                        <span>${user.points}</span>
                    </li>
                </c:forEach>
            </ol>
        </div>
    </div>
</main>
<script>
    const keyword = "<%= request.getAttribute("keyword") %>";
    const contextPath = '<%= request.getContextPath() %>';

    // --- ĐỊNH NGHĨA CÁC LỚP STYLE TAILWIND ---
    const ACTIVE_CLASSES = "text-[#195DA9] border-b-4 border-[#195DA9]";
    const INACTIVE_CLASSES = "text-gray-500 hover:text-[#195DA9] border-b-4 border-transparent";


    function loadResults(type) {

        const activeBtnId = "btn-" + type;


        document.querySelectorAll(".tab-btn").forEach(button => {

            button.classList.remove(...ACTIVE_CLASSES.split(' '));
            button.classList.remove(...INACTIVE_CLASSES.split(' '));
            button.classList.add(...INACTIVE_CLASSES.split(' '));
        });

        const activeButton = document.getElementById(activeBtnId);
        if (activeButton) {
            activeButton.classList.remove(...INACTIVE_CLASSES.split(' '));
            activeButton.classList.add(...ACTIVE_CLASSES.split(' '));
        }

        fetch(contextPath + '/search?keyword=' + encodeURIComponent(keyword) + '&searchType=' + type)
            .then(res => res.text())
            .then(html => {
                const container = document.getElementById("search-results");
                container.innerHTML = html;

                if (window.tailwind && window.tailwind.refresh) {
                    window.tailwind.refresh();
                }
            });
    }

    document.addEventListener('DOMContentLoaded', (event) => {
        loadResults('title');
    });
</script>
</body>
</html>

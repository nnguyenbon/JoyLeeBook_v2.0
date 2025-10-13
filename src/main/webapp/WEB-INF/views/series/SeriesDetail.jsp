<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/11/2025
  Time: 7:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Series Details</title>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css"/>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet" />
</head>
<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>
<main class="main max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-[30px] items-center">

    <!-- Left Image -->
    <div class="col-span-3 col-start-2">
        <img src="./${seriesInfoDTO.coverImgUrl}" alt="Series cover" class="rounded-lg shadow"/>
    </div>

    <!-- Right (Title, Info, Tags) -->
    <div class="col-span-7">
        <h1 class="text-4xl font-bold">${seriesInfoDTO.title}</h1>
        <p class="text-gray-600 mb-4">by <span class="font-semibold">
            <c:forEach var="author" items="${seriesInfoDTO.authorsName}" varStatus="loop">
                ${author}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
        </span></p>

        <div class="flex flex-wrap gap-2 mb-15">
            <c:forEach var="category" items="${seriesInfoDTO.categories}">
                <span class="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">${category}</span>
            </c:forEach>
            <span class="bg-green-100 text-green-600 text-xs font-semibold px-2 py-0.5 rounded-full">${seriesInfoDTO.status}</span>
        </div>

        <div class="flex items-center gap-30 mb-10">
            <div class="flex flex-col items-center justify-center">
                <span class="font-semibold text-lg">${seriesInfoDTO.totalChapters}</span>
                Chapters
            </div>
            <div class="flex flex-col items-center justify-center">

                <div class="text-gray-500 font-semibold text-lg mb-1">
                    <span class="text-yellow-400">★ ${seriesInfoDTO.avgRating}</span>
                    <span>(${seriesInfoDTO.countRatings})</span>
                </div>

                <div id="starRatingContainer" class="flex">
                    <input type="radio" name="rating" id="star1" value="1" class="hidden"/>
                    <label for="star1"
                           class="cursor-pointer text-gray-400 text-3xl transition-colors duration-150">★</label>

                    <input type="radio" name="rating" id="star2" value="2" class="hidden"/>
                    <label for="star2"
                           class="cursor-pointer text-gray-400 text-3xl transition-colors duration-150">★</label>

                    <input type="radio" name="rating" id="star3" value="3" class="hidden"/>
                    <label for="star3"
                           class="cursor-pointer text-gray-400 text-3xl transition-colors duration-150">★</label>

                    <input type="radio" name="rating" id="star4" value="4" class="hidden"/>
                    <label for="star4"
                           class="cursor-pointer text-gray-400 text-3xl transition-colors duration-150">★</label>

                    <input type="radio" name="rating" id="star5" value="5" class="hidden"/>
                    <label for="star5"
                           class="cursor-pointer text-gray-400 text-3xl transition-colors duration-150">★</label>
                </div>
            </div>
        </div>


        <div class="flex items-center gap-4 mt-4">
            <a href="${pageContext.request.contextPath}/chapter-content?seriesId=${seriesInfoDTO.seriesId}">
                <button
                        class="bg-[#0A3776] text-white px-5 py-2 rounded-lg font-semibold hover:bg-indigo-800 transition-colors">
                    ▶ Start Reading
                </button>
            </a>
            <button
                    class="border border-red-400 flex items-center gap-2 text-red-400 px-5 py-2 rounded-lg font-semibold hover:bg-red-50 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                     class="lucide lucide-bookmark">
                    <path d="m19 21-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"/>
                </svg>
                Add to Library
            </button>
        </div>

    </div>
</main>
<!-- Summary -->
<section class="max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-[30px]">
    <div class="col-span-10 col-start-2">
        <h2 class="font-semibold text-xl mb-3">Summary</h2>
        <div class="border-2 border-[#0D2E55] rounded-lg bg-white p-4 leading-relaxed text-gray-700">
            ${seriesInfoDTO.description}
        </div>
    </div>
</section>
<!-- Chapter List -->
<section class="max-w-[1290px] mx-auto mt-5 mb-20 grid grid-cols-12 gap-[30px]">
    <div class="col-span-10 col-start-2">
        <h2 class="font-semibold text-xl mb-3">Chapter List</h2>
        <div class="border-2 border-[#0D2E55] p-3 rounded-lg">
            <c:forEach var="chapter" items="${chapterInfoDTOList}">
                <a href="${pageContext.request.contextPath}/chapter-content?seriesId=${seriesInfoDTO.seriesId}&chapterId=${chapter.chapterId}">
                    <div
                            class="flex justify-between items-center border rounded-lg px-4 py-3 bg-white hover:bg-gray-50 cursor-pointer mb-3">
                        <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                        <span class="text-sm text-gray-500">${chapter.totalLikes} Likes · ${chapter.updatedAt}</span>
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>
</section>
<script>
    const genreButton = document.getElementById("genreButton");
    const genreMenu = document.getElementById("genreMenu");

    genreButton.addEventListener("click", () => {
        genreMenu.classList.toggle("hidden");
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener("click", (e) => {
        if (!genreButton.contains(e.target) && !genreMenu.contains(e.target)) {
            genreMenu.classList.add("hidden");
        }
    });

    const BtnAvatar = document.getElementById("BtnAvatar");
    const MenuAvatar = document.getElementById("MenuAvatar");

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


<!-- ✅ Modal đưa ra ngoài container -->
<div id="confirmModal"
     class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center hidden z-50">
    <div class="bg-white p-8 rounded-lg shadow-2xl w-full max-w-md relative">
        <h2 class="text-xl font-semibold mb-4">Confirm Rating</h2>
        <p id="confirmText" class="mb-6 text-gray-700"></p>
        <button id="confirmBtn"
                class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors">
            OK
        </button>
    </div>
</div>

<script>
    const starContainer = document.getElementById('starRatingContainer');
    const radioButtons = starContainer.querySelectorAll('input[name="rating"]');
    const labels = starContainer.querySelectorAll('label');
    const modal = document.getElementById('confirmModal');
    const confirmText = document.getElementById('confirmText');
    const confirmBtn = document.getElementById('confirmBtn');

    let currentRating = 0;

    function colorStars(ratingValue) {
        labels.forEach((label, index) => {
            if (index < ratingValue) {
                label.classList.add('text-yellow-400');
                label.classList.remove('text-gray-400');
            } else {
                label.classList.add('text-gray-400');
                label.classList.remove('text-yellow-400');
            }
        });
    }

    labels.forEach((label, index) => {
        const ratingValue = index + 1;
        label.addEventListener('mouseover', () => colorStars(ratingValue));
        label.addEventListener('click', () => {
            currentRating = ratingValue;
            radioButtons[index].checked = true;
            confirmText.textContent = `You confirm ${currentRating}-star rating?`;
            modal.classList.remove('hidden');
        });
    });

    starContainer.addEventListener('mouseout', () => colorStars(currentRating));
    confirmBtn.addEventListener('click', () => modal.classList.add('hidden'));
</script>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/11/2025
  Time: 7:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<main class=" mt-10 grid grid-cols-12 gap-8 items-center">

    <!-- Left Image -->
    <div class="col-span-3 col-start-2">
        <img src="${pageContext.request.contextPath}/${series.coverImgUrl}" alt="Series cover"
             class="rounded-lg shadow aspect-[3/4]"/>
    </div>

    <!-- Right (Title, Info, Tags) -->
    <div class="col-span-7 h-full flex flex-col justify-between">
        <h1 class="text-4xl font-bold">${series.title}</h1>

        <!-- Tác giả -->
        <p class="text-gray-600">
            by
            <span class="font-semibold">
            <c:forEach var="author" items="${series.authorNameList}" varStatus="loop">
                ${author}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
        </span>
        </p>

        <!-- Thể loại + Trạng thái -->
        <div class="flex flex-wrap items-center gap-2">

            <!-- Danh mục -->
            <c:forEach var="category" items="${series.categoryList}">
            <span class="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">
                    ${category.name}
            </span>
            </c:forEach>

            <!-- Trạng thái -->
            <c:choose>
                <c:when test="${series.status == 'Completed'}">
                <span class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium">
                        ${series.status}
                </span>
                </c:when>
                <c:when test="${series.status == 'Ongoing'}">
                <span class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium">
                        ${series.status}
                </span>
                </c:when>
                <c:otherwise>
                <span class="text-xs px-3 py-1 rounded-full bg-gray-100 text-gray-700 font-medium">
                        ${series.status}
                </span>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="flex items-center gap-30">
            <div class="flex flex-col items-center justify-center">
                <span class="font-semibold text-lg">${series.totalChapters}</span>
                Chapters
            </div>
            <div class="flex flex-col items-center justify-center">

                <div class="text-gray-500 font-semibold text-lg mb-1">
                    <span id="avgRatingDisplay" class="text-yellow-400">★ ${series.avgRating}</span>
                    <span id="totalRatingsDisplay">(${series.totalRating})</span>
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
            <c:if test="${not empty chapterInfoDTOList and chapterInfoDTOList.get(0).chapterId != null}">
                <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=${chapterInfoDTOList.get(0).chapterId}">
                    <button class="bg-[#0A3776] text-white px-5 py-2 rounded-lg font-semibold hover:bg-indigo-800 transition-colors">
                        <i class="fa-solid fa-play"></i>
                        Start Reading
                    </button>
                </a>
            </c:if>

            <button id="saveBtn"
                    class="border border-pink-400 flex items-center gap-2 text-pink-400 px-2 py-2 rounded-lg font-semibold hover:bg-red-50 transition-colors"
                    data-user-id="10" data-series-id="${series.seriesId}">
                <i class="${saved ? 'fa-solid' : 'fa-regular'} fa-bookmark text-xl"></i>
            </button>
        </div>

    </div>
    <!-- Summary -->
    <section class="col-span-12 grid grid-cols-12 gap-8">
        <div class="col-span-10 col-start-2">
            <h2 class="font-semibold text-xl mb-3">Summary</h2>
            <div class="border-2 border-neutral-400 rounded-lg bg-white p-4 leading-relaxed text-gray-700">
                ${series.description}
            </div>
        </div>
    </section>
    <!-- Chapter List -->
    <section class="col-span-12 mb-16 grid grid-cols-12 gap-8">
        <div class="col-span-10 col-start-2">
            <h2 class="font-semibold text-xl mb-3">Chapter List</h2>
            <div class="space-y-3 border-2 border-neutral-400 p-3 rounded-lg  ">
                <c:choose>
                    <c:when test="${chapterInfoDTOList.size() != 0}">
                        <ul class="py-1 px-3 overflow-y-auto custom-scrollbar max-h-100">
                            <c:forEach var="chapter" items="${chapterInfoDTOList}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=${chapter.chapterId}">
                                        <div class="flex justify-between items-center border border-neutral-400 rounded-lg px-4 my-2 py-3 bg-white hover:bg-gray-50 cursor-pointer">
                                            <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                                            <span class="text-sm text-gray-500">${chapter.totalLike} Likes · ${chapter.updatedAt}</span>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <div>This series has no chapters</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>
</main>


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
    const userId = ${userId};
    const seriesId = "${series.seriesId}";

    const starContainer = document.getElementById('starRatingContainer');
    var radioButtons, labels;

    if (starContainer) {
        radioButtons = starContainer.querySelectorAll('input[name="rating"]');
        labels = starContainer.querySelectorAll('label');
    }
    let currentRating = ${userRating};

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

    //
    colorStars(currentRating);
    // Khi hover qua sao
    labels.forEach((label, index) => {
        const ratingValue = index + 1;

        label.addEventListener('mouseover', () => colorStars(ratingValue));

        label.addEventListener('click', () => {
            currentRating = ratingValue;
            radioButtons[index].checked = true;
            colorStars(currentRating);

            // Gửi request
            fetch("${pageContext.request.contextPath}/reaction/rate", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: new URLSearchParams({
                    userId: userId,
                    seriesId: seriesId,
                    rating: currentRating,
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        console.log("⭐ Rating saved:", data.rating);


                        const avgDisplay = document.getElementById("avgRatingDisplay");
                        const totalDisplay = document.getElementById("totalRatingsDisplay");
                        if (avgDisplay) avgDisplay.textContent = "★ " + data.avgRating.toFixed(1);
                        if (totalDisplay) totalDisplay.textContent = "(" + data.totalRatings + ")";
                    } else {
                        console.error("❌ Error saving rating!");
                    }
                })
                .catch(error => console.error("⚠️ Fetch error:", error));
        });
    });

    // Khi rời chuột ra ngoài
    starContainer.addEventListener('mouseout', () => colorStars(currentRating));

    document.getElementById("saveBtn").addEventListener("click", function () {

        const saveBtn = this;
        const saveIcon = saveBtn.querySelector("i");

        const userId = saveBtn.dataset.userId;
        const seriesId = saveBtn.dataset.seriesId;

        const type = saveIcon.classList.contains("fa-solid") ? "unsave" : "save";
        colorStars(currentRating);
        fetch("${pageContext.request.contextPath}/library/save", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({
                userId: userId,
                seriesId: seriesId,
                type: type
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network response was not ok.");
                }
                return response.json();
            })
            .then(data => {
                console.log(data);
                if (data.success) {
                    if (data.saved) {
                        saveBtn.classList.add("saved");
                        saveIcon.classList.replace("fa-regular", "fa-solid");
                    } else {
                        saveBtn.classList.remove("saved");
                        saveIcon.classList.replace("fa-solid", "fa-regular");
                    }
                    toastr["success"](data.message)

                } else {

                    toastr["warning"](data.message)
                    console.log(data.message)
                }
            })
            .catch(error => console.log("Error:", error));
    });
</script>
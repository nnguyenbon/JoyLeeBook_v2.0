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
        <img src="${seriesInfoDTO.coverImgUrl}" alt="Series cover" class="rounded-lg shadow aspect-[3/4]"/>
    </div>

    <!-- Right (Title, Info, Tags) -->
    <div class="col-span-7 h-full flex flex-col justify-between">
        <h1 class="text-4xl font-bold">${seriesInfoDTO.title}</h1>

        <!-- Tác giả -->
        <p class="text-gray-600">
            by
            <span class="font-semibold">
            <c:forEach var="author" items="${seriesInfoDTO.authorsName}" varStatus="loop">
                ${author}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
        </span>
        </p>

        <!-- Thể loại + Trạng thái -->
        <div class="flex flex-wrap items-center gap-2">

            <!-- Danh mục -->
            <c:forEach var="category" items="${seriesInfoDTO.categories}">
            <span class="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">
                    ${category}
            </span>
            </c:forEach>

            <!-- Trạng thái -->
            <c:choose>
                <c:when test="${seriesInfoDTO.status == 'Completed'}">
                <span class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium">
                        ${seriesInfoDTO.status}
                </span>
                </c:when>
                <c:when test="${seriesInfoDTO.status == 'Ongoing'}">
                <span class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium">
                        ${seriesInfoDTO.status}
                </span>
                </c:when>
                <c:otherwise>
                <span class="text-xs px-3 py-1 rounded-full bg-gray-100 text-gray-700 font-medium">
                        ${seriesInfoDTO.status}
                </span>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="flex items-center gap-30">
            <div class="flex flex-col items-center justify-center">
                <span class="font-semibold text-lg">${seriesInfoDTO.totalChapters}</span>
                Chapters
            </div>
                <div class="flex flex-col items-center justify-center">

                    <div class="text-gray-500 font-semibold text-lg mb-1">
                        <span id="avgRatingDisplay" class="text-yellow-400">★ ${seriesInfoDTO.avgRating}</span>
                        <span id="totalRatingsDisplay">(${seriesInfoDTO.countRatings})</span>
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
            <a href="${pageContext.request.contextPath}/chapter-content?seriesId=${seriesInfoDTO.seriesId}&chapterId=">
                <button
                        class="bg-[#0A3776] text-white px-5 py-2 rounded-lg font-semibold hover:bg-indigo-800 transition-colors">
                    ▶ Start Reading
                </button>
            </a>


            <button id="saveBtn"
                    class="border border-pink-400 flex items-center gap-2 text-pink-400 px-2 py-2 rounded-lg font-semibold hover:bg-red-50 transition-colors"
                    data-user-id="10" data-series-id="${seriesInfoDTO.seriesId}">
                <i class="${saved ? 'fa-solid' : 'fa-regular'} fa-bookmark text-xl"></i>

            </button>
        </div>

    </div>
    <!-- Summary -->
    <section class="col-span-12 grid grid-cols-12 gap-8">
        <div class="col-span-10 col-start-2">
            <h2 class="font-semibold text-xl mb-3">Summary</h2>
            <div class="border-2 border-neutral-400 rounded-lg bg-white p-4 leading-relaxed text-gray-700">
                ${seriesInfoDTO.description}
            </div>
        </div>
    </section>
    <!-- Chapter List -->
    <section class="col-span-12 mb-16 grid grid-cols-12 gap-8">
        <div class="col-span-10 col-start-2">
            <h2 class="font-semibold text-xl mb-3">Chapter List</h2>
            <div class="space-y-3 border-2 border-neutral-400 p-3 rounded-lg  ">
                <ul class="py-1 px-3 overflow-y-auto custom-scrollbar max-h-100">
                    <c:forEach var="chapter" items="${chapterInfoDTOList}">
                        <li>
                            <a href="${pageContext.request.contextPath}/chapter-content?seriesId=${seriesInfoDTO.seriesId}&chapterId=${chapter.chapterId}">
                                <div class="flex justify-between items-center border border-neutral-400 rounded-lg px-4 my-2 py-3 bg-white hover:bg-gray-50 cursor-pointer">
                                    <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                                    <span class="text-sm text-gray-500">${chapter.totalLike} Likes · ${chapter.updatedAt}</span>
                                </div>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </section>
</main>


<script>
    const userId = 10;
    const seriesId = "${seriesInfoDTO.seriesId}";

        const starContainer = document.getElementById('starRatingContainer');
        const radioButtons = starContainer.querySelectorAll('input[name="rating"]');
        const labels = starContainer.querySelectorAll('label');

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
                fetch("rate-series", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: new URLSearchParams({
                        userId: userId,
                        seriesId: seriesId,
                        rating: currentRating
                    })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            console.log("⭐ Rating saved:", data.rating);

                            // Cập nhật hiển thị trung bình và tổng
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



    document.getElementById("saveBtn").addEventListener("click", function() {
        const saveBtn = this;
        const saveIcon = saveBtn.querySelector("i");

        const userId = saveBtn.dataset.userId;
        const seriesId = saveBtn.dataset.seriesId;

        const action = saveBtn.classList.contains("saved") ? "unsave" : "save";

        fetch("save-series", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: "userId=" + encodeURIComponent(userId) +
                  "&seriesId=" + encodeURIComponent(seriesId) +
                "&action=" + encodeURIComponent(action)

        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    if (data.saved) {
                        saveBtn.classList.add("saved");
                        saveIcon.classList.replace("fa-regular", "fa-solid");
                    } else {
                        saveBtn.classList.remove("saved");
                        saveIcon.classList.replace("fa-solid", "fa-regular");
                    }
                }
            })
            .catch(error => console.error("Error:", error));
    });
</script>
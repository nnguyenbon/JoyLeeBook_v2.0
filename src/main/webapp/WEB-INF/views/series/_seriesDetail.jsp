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
            <c:set var="user" value="${loginedUser}" />
            <c:set var="role" value="${user.role}" />
            <c:if test="${not empty chapterList and chapterList.get(0) != null}">
                <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=">
                    <button class="bg-[#0A3776] text-white px-5 py-2 rounded-lg font-semibold hover:bg-indigo-800 transition">
                        <i class="fa-solid fa-play"></i> Start Reading
                    </button>
                </a>
            </c:if>
            <c:choose>
                <c:when test="${role == 'author'}">
                    <a href="${pageContext.request.contextPath}/series/edit?seriesId=${series.seriesId}">
                        <button class="bg-blue-600 text-white px-5 py-2 rounded-lg font-semibold hover:bg-blue-700 transition">
                            <i class="fa-solid fa-pen"></i> Edit
                        </button>
                    </a>
                    <form action="${pageContext.request.contextPath}/series/delete" method="post"
                          onsubmit="return confirm('Are you sure you want to delete this series?')">
                        <input type="hidden" name="seriesId" value="${series.seriesId}">
                        <button type="submit"
                                class="bg-red-600 text-white px-5 py-2 rounded-lg font-semibold hover:bg-red-700 transition">
                            <i class="fa-solid fa-trash"></i> Delete
                        </button>
                    </form>
                </c:when>
                <c:when test="${role == 'reader'}">
                    <button id="saveBtn"
                            class="border border-pink-400 flex items-center gap-2 text-pink-400 px-2 py-2 rounded-lg font-semibold hover:bg-red-50 transition-colors"
                            data-user-id="10" data-series-id="${series.seriesId}">
                        <i class="${saved ? 'fa-solid' : 'fa-regular'} fa-bookmark text-xl"></i>
                    </button>
                </c:when>
                <c:otherwise></c:otherwise>
            </c:choose>
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
                    <c:when test="${chapterList.size() != 0}">
                        <ul class="py-1 px-3 overflow-y-auto custom-scrollbar max-h-100">
                            <c:forEach var="chapter" items="${chapterList}">
                                <li>
                                    <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=${chapter.chapterId}">
                                        <div class="flex justify-between items-center border border-neutral-400 rounded-lg px-4 my-2 py-3 bg-white hover:bg-gray-50 cursor-pointer">
                                            <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                                            <span class="text-sm text-gray-500">${chapter.updatedAt}</span>
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
<script>
    document.addEventListener("DOMContentLoaded", async () => {
        const userId = ${loginedUser.userId};
        const seriesId = "${series.seriesId}";

        const starContainer = document.getElementById('starRatingContainer');
        const saveBtn = document.getElementById("saveBtn");
        const saveIcon = saveBtn.querySelector("i");
        const radioButtons = starContainer.querySelectorAll('input[name="rating"]');
        const labels = starContainer.querySelectorAll('label');

        let currentRating = 0;
        let isSaved = false;

        // ✅ Hàm tô màu sao
        function colorStars(ratingValue) {
            labels.forEach((label, index) => {
                label.classList.toggle('text-yellow-400', index < ratingValue);
                label.classList.toggle('text-gray-400', index >= ratingValue);
            });
        }

        // ✅ Hàm load trạng thái ban đầu (đã lưu? đã rate?)
        async function loadUserSeriesStatus() {
            try {
                const [saveRes, rateRes] = await Promise.all([
                    fetch(`${pageContext.request.contextPath}/reading/save?userId=${userId}&seriesId=${seriesId}`),
                    fetch(`${pageContext.request.contextPath}/reaction/rate-series?userId=${userId}&seriesId=${seriesId}`)
                ]);

                const saveData = await saveRes.json();
                const rateData = await rateRes.json();

                // ✅ Gán trạng thái Save
                if (saveData.saved) {
                    isSaved = true;
                    saveIcon.classList.replace("fa-regular", "fa-solid");
                    saveBtn.classList.add("saved");
                }

                // ✅ Gán trạng thái Rating
                if (rateData.userRating > 0) {
                    currentRating = rateData.userRating;
                    colorStars(currentRating);
                    radioButtons[currentRating - 1].checked = true;
                }

                // ✅ Hiển thị điểm trung bình mới nhất
                const avgDisplay = document.getElementById("avgRatingDisplay");
                const totalDisplay = document.getElementById("totalRatingsDisplay");
                if (rateData.avgRating) {
                    avgDisplay.textContent = `★ ${rateData.avgRating.toFixed(1)}`;
                    totalDisplay.textContent = `(${rateData.totalRatings})`;
                }

            } catch (error) {
                console.error("⚠️ Lỗi khi tải trạng thái người dùng:", error);
            }
        }

        // ✅ Sự kiện nhấn lưu / huỷ lưu
        saveBtn.addEventListener("click", async () => {
            const type = isSaved ? "unsave" : "save";
            try {
                const res = await fetch(`${pageContext.request.contextPath}/reading/save`, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: new URLSearchParams({ userId, seriesId, type })
                });

                const data = await res.json();
                if (data.success) {
                    isSaved = data.saved;
                    saveIcon.classList.toggle("fa-solid", isSaved);
                    saveIcon.classList.toggle("fa-regular", !isSaved);
                    toastr["success"](data.message);
                } else {
                    toastr["warning"](data.message);
                }
            } catch (error) {
                console.error("❌ Lỗi khi lưu series:", error);
            }
        });

        // ✅ Sự kiện click rating
        labels.forEach((label, index) => {
            const ratingValue = index + 1;

            label.addEventListener("mouseover", () => colorStars(ratingValue));

            label.addEventListener("click", async () => {
                currentRating = ratingValue;
                colorStars(currentRating);
                radioButtons[index].checked = true;

                try {
                    const res = await fetch(`${pageContext.request.contextPath}/reaction/rate-series`, {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: new URLSearchParams({ userId, seriesId, rating: currentRating })
                    });

                    const data = await res.json();
                    if (data.success) {
                        document.getElementById("avgRatingDisplay").textContent = `★ ${data.avgRating.toFixed(1)}`;
                        document.getElementById("totalRatingsDisplay").textContent = `(${data.totalRatings})`;
                        toastr["success"]("Your rating has been saved!");
                    } else {
                        toastr["warning"](data.message);
                    }
                } catch (error) {
                    console.error("⚠️ Lỗi khi gửi rating:", error);
                }
            });
        });

        // ✅ Khi rời chuột khỏi vùng sao
        starContainer.addEventListener('mouseout', () => colorStars(currentRating));

        // 🚀 Load trạng thái ban đầu khi mở trang
        await loadUserSeriesStatus();
    });
</script>

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
    <div class="col-span-4 h-full flex flex-col justify-between">
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
                <c:if test="${loginedUser.role != 'author'}">
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
                </c:if>
            </div>

        </div>


        <div class="flex items-center gap-4 mt-4">
            <c:set var="user" value="${loginedUser}"/>
            <c:set var="role" value="${user.role}"/>
            <c:if test="${totalChapter != 0}">
                <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=">
                    <button class="bg-[#0A3776] text-white px-4 py-2 rounded-lg font-semibold hover:bg-indigo-800 transition">
                        <i class="fa-solid fa-play"></i> Start Reading
                    </button>
                </a>
            </c:if>
            <c:choose>
                <c:when test="${role == 'author'}">
                    <a href="${pageContext.request.contextPath}/series/edit?seriesId=${series.seriesId}">
                        <button class="bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700 transition">
                            <i class="fa-solid fa-pen"></i> Edit
                        </button>
                    </a>
                    <form action="${pageContext.request.contextPath}/series/delete" method="post"
                          onsubmit="return confirm('Are you sure you want to delete this series?')">
                        <input type="hidden" name="seriesId" value="${series.seriesId}">
                        <button type="submit"
                                class="bg-red-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-red-700 transition">
                            <i class="fa-solid fa-trash"></i> Delete
                        </button>
                    </form>
                </c:when>
                <c:when test="${role == 'reader'}">
                    <button id="saveBtn" aria-label="Save series"
                            class="border border-pink-400 flex items-center gap-2 text-pink-400 px-2 py-2 rounded-lg font-semibold hover:bg-red-50 transition-colors"
                            data-user-id="10" data-series-id="${series.seriesId}">
                        <i class="${saved ? 'fa-solid' : 'fa-regular'} fa-bookmark text-xl"></i>
                    </button>
                </c:when>
<%--                <c:otherwise></c:otherwise>--%>
            </c:choose>
        </div>
    </div>

    <div class="col-span-3 ring-2 ring-sky-600/50 rounded-lg p-4" ${loginedUser.role == 'author' ? "" : "hidden"}>
        <p class="font-bold text-lg mb-2">
            <i class="fa-regular fa-user"></i>
            Authors
        </p>

        <ul class="list-disc list-inside">
            <c:forEach var="name" items="${series.authorNameList}">
                <li class="">
                        ${name}
                </li>
            </c:forEach>
        </ul>

        <c:if test="${owner}">
            <a class="block mt-4 text-center"
               href="${pageContext.request.contextPath}/manage-coauthors?seriesId=${series.seriesId}">
                <button class=" p-2 bg-sky-100 text-sky-700 font-semibold rounded-lg w-full hover:bg-sky-200 transition duration-300 cursor-pointer">
                    Add Co-Author
                </button>
            </a>
        </c:if>
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
    <section class="col-span-12 mb-16 grid grid-cols-12 gap-8" id="chapter-list-container">
        <jsp:include page="/WEB-INF/views/chapter/_chapterList.jsp"/>
    </section>
</main>
<script>
    const userId = ${userId};
    const seriesId = "${series.seriesId}";

    function loadChapterList() {
        fetch("${pageContext.request.contextPath}/chapter/list?seriesId=" + encodeURIComponent(seriesId))
            .then(response => response.text())
            .then(html => {
                document.getElementById("chapter-list-container").innerHTML = html;
            })
            .catch(error => console.error("Error:", error));
    }
    document.addEventListener("DOMContentLoaded", () => {
        loadChapterList();
    });

    const starContainer = document.getElementById('starRatingContainer');
    var radioButtons, labels;

    if (starContainer) {
        radioButtons = starContainer.querySelectorAll('input[name="rating"]');
        labels = starContainer.querySelectorAll('label');
    }

    let currentRating = ${ratingByUser};

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
    };

    // Khởi tạo màu sao
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
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({
                    userId: userId,
                    seriesId: seriesId,
                    rating: currentRating,
                }),
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
        toastr.options = {
            "closeButton": true,
            "debug": false,
            "newestOnTop": false,
            "progressBar": true,
            "positionClass": "toast-bottom-right",
            "preventDuplicates": false,
            "onclick": null,
            "showDuration": "300",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };

        const saveBtn = this;
        const saveIcon = saveBtn.querySelector("i");
        const userId = saveBtn.dataset.userId;
        const seriesId = saveBtn.dataset.seriesId;
        const type = saveIcon.classList.contains("fa-solid") ? "unsave" : "save";

        colorStars(currentRating);

        fetch("${pageContext.request.contextPath}/library/save", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                userId: userId,
                seriesId: seriesId,
                type: type,
            }),
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
                    toastr["success"](data.message);
                } else {
                    toastr["warning"](data.message);
                    console.log(data.message);
                }
            })
            .catch(error => console.log("Error:", error));
    });
</script>

</script>

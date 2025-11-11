<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/11/2025
  Time: 7:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
            <c:set var="user" value="${loginedUser}"/>
            <c:set var="role" value="${user.role}"/>
            <c:if test="${not empty chapterList and chapterList.get(0) != null}">
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

    <div class="col-span-3 ring-2 ring-sky-600/50 rounded-lg p-4">
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
            <button onclick="showModal()"
                    class="mt-4 p-2 bg-sky-100 text-sky-700 font-semibold rounded-lg w-full hover:bg-sky-200 transition duration-300 cursor-pointer">
                Add Co-Author
            </button>
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
    <section class="col-span-12 mb-16 grid grid-cols-12 gap-8">
        <div class="col-span-10 col-start-2">
            <div class="flex justify-between items-center w-full mb-3">
                <h2 class="font-semibold text-xl ">Chapter List</h2>
                <a
                        href="${pageContext.request.contextPath}/chapter/add?seriesId=${series.seriesId}"
                        class="px-4 py-2 bg-sky-800 text-white rounded-lg hover:bg-sky-900 cursor-pointer transition duration-300">Create
                    Chapter
                    <i class="fa-solid fa-circle-plus"></i>
                </a>
            </div>
            <div class="space-y-3 border-2 border-neutral-400 p-6 rounded-lg  ">

                <c:choose>
                    <c:when test="${chapterList.size() != 0}">
                        <ul class="overflow-y-auto custom-scrollbar max-h-100">
                            <c:forEach var="chapter" items="${chapterList}">
                                <li class="flex justify-between items-center gap-4 border border-neutral-400 rounded-lg px-4 my-2 py-3 bg-white hover:bg-gray-50 cursor-pointer">
                                    <a class="flex justify-between gap-2 items-center w-full"
                                       href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=${chapter.chapterId}">

                                        <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>

                                        <div class="flex gap-4 items-center">
                                            <span class="text-sm text-gray-500">${chapter.updatedAt}</span>

                                            <c:choose>
                                                <c:when test="${chapter.approvalStatus == 'approved'}">
                                                    <span class="bg-green-100 text-green-600 px-3 py-1 rounded-full font-medium">${chapter.approvalStatus}</span>
                                                </c:when>
                                                <c:when test="${chapter.approvalStatus == 'pending'}">
                                                    <span class="bg-yellow-100 text-yellow-600 px-3 py-1 rounded-full font-medium">${chapter.approvalStatus}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="bg-red-100 text-red-600 px-3 py-1 rounded-full font-medium">${chapter.approvalStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                    </a>
                                    <div class="flex gap-2 items-center">
                                        <a type="button"
                                           class="text-green-600 hover:text-green-700 hover:scale-110 transition-all duration-300"
                                           href="${pageContext.request.contextPath}/chapter/edit?seriesId=${series.seriesId}&chapterId=${chapter.chapterId}">
                                            <i class="fa-regular fa-pen-to-square"></i>
                                        </a>

                                        <a type="button"
                                           class="text-red-600 hover:text-red-700 hover:scale-110 transition-all duration-300"
                                           href="${pageContext.request.contextPath}/chapter/delete?seriesId=${series.seriesId}&chapterId=${chapter.chapterId}">
                                            <i class="fa-regular fa-trash-can"></i>
                                        </a>
                                    </div>
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

    <dialog closedby="any" id="modalCoauthor" class="md:w-lg w-sm">
        <div class="p-4">
            <div>
                <p class="text-2xl font-semibold">Add Co-Author</p>
                <p class="text-gray-500 font-light">
                    Invite a co-author to collaborate on "${series.title}"
                </p>
            </div>

            <form id="coauthorForm" class="relative mt-6">
                <input type="hidden" name="seriesId" value="${series.seriesId}"/>

                <label for="username" class="block text-xl">Username or Email</label>
                <input
                        type="text"
                        class="py-2 px-3 mt-2 mb-6 border border-gray-400 rounded-xl w-full"
                        id="username"
                        name="username"
                        placeholder="username or email@example.com"
                        autocomplete="off"
                        required
                />

                <ul id="suggestion"
                    class="hidden absolute top-24 left-0 right-0 bg-white h-32 w-full border border-gray-400 rounded-lg overflow-hidden overflow-y-scroll z-10">
                </ul>

                <div class="flex w-full justify-between gap-6">
                    <button
                            type="submit"
                            class="flex-1 border border-gray-400 bg-sky-200 py-2 px-3 rounded-xl cursor-pointer hover:bg-sky-300"
                    >
                        Send Invitation
                    </button>
                    <button onclick="closeModal()" type="button"
                            class="flex-1 border border-gray-400 rounded-xl cursor-pointer hover:bg-gray-400">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    </dialog>
</main>
<script>
    document.addEventListener("DOMContentLoaded", async () => {
        const userId = ${loginedUser.userId};
        const seriesId = "${series.seriesId}";

        const starContainer = document.getElementById('starRatingContainer');
        <c:if test="loginedUser.role = author">
        const saveBtn = document.getElementById("saveBtn");
        const saveIcon = saveBtn.querySelector("i");
        </c:if>
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
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: new URLSearchParams({userId, seriesId, type})
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
                        headers: {"Content-Type": "application/x-www-form-urlencoded"},
                        body: new URLSearchParams({userId, seriesId, rating: currentRating})
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

    const modalCoauthor = document.getElementById('modalCoauthor');
    const coauthorForm = document.getElementById('coauthorForm');

    function showModal() {
        modalCoauthor.showModal();
    }

    function closeModal() {
        modalCoauthor.close();
        coauthorForm.reset();
        suggestionList.classList.add('hidden');
    }

    // Handle form submission
    coauthorForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(coauthorForm);
        const submitButton = coauthorForm.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        submitButton.textContent = 'Sending...';

        try {
            const response = await fetch('${pageContext.request.contextPath}/manage-coauthors/add', {
                method: 'POST',
                body: new URLSearchParams(formData)
            });

            const result = await response.json();

            if (result.success) {
                toastr.success(result.message);
                closeModal();
            } else {
                toastr.error(result.message);
            }
        } catch (error) {
            console.error('Error sending invitation:', error);
            toastr.error('Failed to send invitation. Please try again.');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Send Invitation';
        }
    });

    const usernameInput = document.getElementById('username');
    const suggestionList = document.getElementById('suggestion');
    let debounceTimer;

    usernameInput.addEventListener('input', async () => {
        clearTimeout(debounceTimer);

        if (usernameInput.value.length > 2) {
            debounceTimer = setTimeout(async () => {
                const users = await getUserName(usernameInput.value);
                if (users && users.length > 0) {
                    suggestion(users);
                } else {
                    suggestionList.classList.add('hidden');
                }
            }, 300);
        } else {
            suggestionList.classList.add('hidden');
        }
    });

    async function getUserName(username) {
        const url = "${pageContext.request.contextPath}/manage-coauthors/users?username=" + encodeURIComponent(username);
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            const result = await response.json();
            return result;
        } catch (error) {
            console.error('Error fetching users:', error.message);
            return [];
        }
    }

    // Helper function to prevent XSS
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function suggestion(users) {
        suggestionList.classList.remove('hidden');
        suggestionList.innerHTML = ''; // Clear previous suggestions

        users.forEach(user => {
            const li = document.createElement('li');
            li.className = 'py-2 px-4 hover:bg-sky-300 cursor-pointer';

            // Safely set text content (automatically escapes HTML)
            const displayName = user.username;
            li.textContent = displayName;

            // Add click handler
            li.addEventListener('click', () => selectAuthor(user.username));

            suggestionList.appendChild(li);
        });
    }

    function selectAuthor(username) {
        suggestionList.classList.add('hidden');
        usernameInput.value = username;
    }

    // Close suggestions when clicking outside
    document.addEventListener('click', (e) => {
        if (!usernameInput.contains(e.target) && !suggestionList.contains(e.target)) {
            suggestionList.classList.add('hidden');
        }
    });
</script>

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
    <div class="col-span-4 h-full flex flex-col justify-between"><h1 class="text-4xl font-bold">${series.title}</h1>
        <!-- Tác giả --> <p class="text-gray-600"> by <span class="font-semibold">
            <c:forEach var="author"
                       items="${series.authorList}"
                       varStatus="loop"> ${author.authorName}
                <c:if test="${!loop.last}">, </c:if> </c:forEach> </span></p> <!-- Thể loại + Trạng thái -->
        <div class="flex flex-wrap items-center gap-2"> <!-- Danh mục -->
            <c:forEach var="category"
                       items="${series.categoryList}">
            <span class="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">
                    ${category.name} </span>
            </c:forEach>
            <!-- Trạng thái -->
            <c:choose>
                <c:when test="${series.status == 'Completed'}"> <span
                        class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium"> ${series.status} </span> </c:when>
                <c:when test="${series.status == 'Ongoing'}"> <span
                        class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium"> ${series.status} </span> </c:when>
                <c:otherwise> <span
                        class="text-xs px-3 py-1 rounded-full bg-gray-100 text-gray-700 font-medium"> ${series.status} </span> </c:otherwise>
            </c:choose>
            <c:if test="${loginedUser.role == 'author'}">
                <c:choose>
                    <c:when test="${series.approvalStatus == 'approved'}"><span
                            class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium"> ${series.approvalStatus} </span> </c:when>
                    <c:when test="${series.approvalStatus == 'pending'}"><span
                            class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium"> ${series.approvalStatus} </span> </c:when>
                </c:choose>
            </c:if>
        </div>
        <div class="flex items-center gap-30">
            <div class="flex flex-col items-center justify-center"><span
                    class="font-semibold text-lg">${series.totalChapters}</span> Chapters
            </div>
            <div class="flex flex-col items-center justify-center" ${loginedUser.role == 'author' ? "hidden" : ""}>

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
            <c:choose>
                <c:when test="${authorCurrent.owner && authorCurrent.authorId == loginedUser.userId}">
                    <a href="${pageContext.request.contextPath}/series/edit?seriesId=${series.seriesId}">
                        <button class="bg-blue-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-blue-700 transition">
                            <i class="fa-solid fa-pen"></i> Edit
                        </button>
                    </a>
                    <button
                            class="upload-series-btn bg-green-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-green-700 transition"
                            data-series-id="${series.seriesId}">
                        <i class="fa-solid fa-upload"></i> Upload Series
                    </button>
                    <form action="${pageContext.request.contextPath}/series/delete" method="post"
                          onsubmit="return confirm('Are you sure you want to delete this series?')">
                        <input type="hidden" name="seriesId" value="${series.seriesId}">
                        <button type="submit"
                                class="bg-red-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-red-700 transition">
                            <i class="fa-solid fa-trash"></i> Delete
                        </button>
                    </form>
                </c:when>
                <c:when test="${role != 'author'}">
                    <c:if test="${totalChapter != 0}">
                        <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${series.seriesId}&chapterId=">
                            <button class="bg-[#0A3776] text-white px-4 py-2 rounded-lg font-semibold hover:bg-indigo-800 transition">
                                <i class="fa-solid fa-play"></i> Start Reading
                            </button>
                        </a>
                    </c:if>
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

    <c:if test="${loginedUser.role == 'author'}">
        <div class="col-span-3 ring-2 ring-sky-600/50 rounded-lg p-4">
            <p class="font-bold text-lg mb-2">
                <i class="fa-regular fa-user"></i>
                Authors
            </p>

        <ul class="space-y-2">
            <c:forEach var="author" items="${series.authorList}" varStatus="status">
                <li class="flex items-center justify-between gap-2}">
                    <span class="flex items-center gap-2">
                        <c:choose>
                            <c:when test="${author.owner}">
                                <i class="fa-solid fa-crown text-yellow-500"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="fa-solid fa-circle text-xs text-gray-400"></i>
                            </c:otherwise>
                        </c:choose>
                        ${author.authorName}
                    </span>
                    <c:if test="${authorCurrent.owner && author.authorId != loginedUser.userId}">
                        <button
                                onclick="deleteCoauthor('${author.authorName}')"
                                class="text-red-600 hover:text-red-700 hover:scale-110 transition-all duration-300"
                                title="Remove co-author">
                            <i class="fa-solid fa-user-minus"></i>
                        </button>
                    </c:if>
                </li>
            </c:forEach>
        </ul>

            <c:if test="${authorCurrent.owner}">
                <button onclick="showModal()"
                        class="mt-4 p-2 bg-sky-100 text-sky-700 font-semibold rounded-lg w-full hover:bg-sky-200 transition duration-300 cursor-pointer">
                    Add Co-Author
                </button>
            </c:if>
        </div>
    </c:if>


    <!-- Summary -->
    <section class="col-span-12 grid grid-cols-12 gap-8">
        <div class="col-span-10 col-start-2"><h2 class="font-semibold text-xl mb-3">Summary</h2>
            <div class="border-2 border-neutral-400 rounded-lg bg-white p-4 leading-relaxed text-gray-700"> ${series.description} </div>
        </div>
    </section>
    <!-- Chapter List -->
    <section class="col-span-12 mb-16 grid grid-cols-12 gap-8" id="chapter-list-container">
        <jsp:include page="/WEB-INF/views/chapter/_chapterList.jsp"/>
    </section>

<%--    Model add co-author--%>
    <dialog id="modalCoauthor" class="md:w-lg w-sm rounded-xl p-4 min-h-128">
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
    </dialog>
</main>

<c:if test="${not empty sessionScope.message}">
    <script>
        toastr["success"]("${sessionScope.message}");
    </script>
    <c:remove var="message" scope="session" />
</c:if>

<script>

    document.addEventListener("DOMContentLoaded", async () => {
//====================================
        // UPLOAD
        const contextPath = "${pageContext.request.contextPath}";

        document.addEventListener("click", function(e) {
            const btn = e.target.closest(".upload-chapter-btn");
            if (btn) {
                e.preventDefault();       // ✅ Chặn redirect của thẻ <a>
                e.stopImmediatePropagation();     // ✅ Ngăn bubble lên thẻ <li> hoặc <a>

                const seriesId = btn.dataset.seriesId;
                const chapterId = btn.dataset.chapterId;
                fetch(contextPath + '/chapter/upload?seriesId=' + seriesId + '&chapterId=' + chapterId, {
                    method: 'POST'
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            toastr.success(data.message, 'Success!');
                            loadChapterList()
                        } else {
                            toastr.error(data.message, 'Error!');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        toastr.error('An error occurred while uploading the chapter!', 'Error!');
                    });
            }
        });

        document.addEventListener("click", function (e) {
            const btnSeries = e.target.closest(".upload-series-btn");
            if (btnSeries) {
                e.preventDefault();
                e.stopPropagation();

                const seriesId = btnSeries.dataset.seriesId;

                fetch(contextPath + `/series/upload?seriesId=`+seriesId, {
                    method: "POST"
                })
                    .then(r => r.text())
                    .then(text => {
                        try {
                            const json = JSON.parse(text);
                            if (json.success) {
                                toastr.success(json.message || "Uploaded!");
                            } else {
                                toastr.error(json.message || "Failed");
                            }
                        } catch (err) {
                            console.error("Invalid JSON:", text);
                            toastr.error("Server returned invalid JSON");
                        }
                    })
                    .catch(err => toastr.error("Request failed"));
            }
        });
        /* ==========================================================
           ✅ 1. Khởi tạo biến từ server
        ========================================================== */
        const currentUserId = ${userId};
        const seriesId = "${series.seriesId}";
        let currentRating = ${ratingByUser};

        /* ==========================================================
           ✅ 2. Load danh sách chapter
        ========================================================== */
        function loadChapterList() {
            fetch("${pageContext.request.contextPath}/chapter/list?seriesId=" + encodeURIComponent(seriesId))
                .then(response => response.text())
                .then(html => {
                    document.getElementById("chapter-list-container").innerHTML = html;
                })
                .catch(error => console.error("Error:", error));
        }

        loadChapterList();


        /* ==========================================================
           ✅ 3. Rating – xử lý sao
        ========================================================== */
        const starContainer = document.getElementById("starRatingContainer");
        let radioButtons = null;
        let labels = null;

        if (starContainer) {
            radioButtons = starContainer.querySelectorAll('input[name="rating"]');
            labels = starContainer.querySelectorAll('label');
        }

        function colorStars(rating) {
            if (!labels) return;
            labels.forEach((lbl, index) => {
                if (index < rating) {
                    lbl.classList.add("text-yellow-400");
                    lbl.classList.remove("text-gray-400");
                } else {
                    lbl.classList.add("text-gray-400");
                    lbl.classList.remove("text-yellow-400");
                }
            });
        }

        // Khởi tạo màu sao ban đầu
        colorStars(currentRating);

        // Hover & click rating
        if (labels && radioButtons) {
            labels.forEach((label, index) => {
                const ratingValue = index + 1;

                label.addEventListener("mouseover", () => colorStars(ratingValue));

                label.addEventListener("click", () => {
                    currentRating = ratingValue;
                    radioButtons[index].checked = true;
                    colorStars(currentRating);

                    // Gửi rating
                    fetch("${pageContext.request.contextPath}/reaction/rate", {
                        method: "POST",
                        headers: {"Content-Type": "application/x-www-form-urlencoded"},
                        body: new URLSearchParams({
                            userId: currentUserId,
                            seriesId: seriesId,
                            rating: currentRating
                        }),
                    })
                        .then(res => res.json())
                        .then(data => {
                            if (data.success) {
                                const avgDisplay = document.getElementById("avgRatingDisplay");
                                const totalDisplay = document.getElementById("totalRatingsDisplay");

                                if (avgDisplay) avgDisplay.textContent = "★ " + data.avgRating.toFixed(1);
                                if (totalDisplay) totalDisplay.textContent = "(" + data.totalRatings + ")";
                            }
                        })
                        .catch(err => console.error("Rating error:", err));
                });
            });

            // Reset màu khi rời chuột
            starContainer.addEventListener("mouseout", () => colorStars(currentRating));
        }


        /* ==========================================================
           ✅ 4. Save / Unsave Series (dành cho author)
        ========================================================== */
        const saveBtn = document.getElementById("saveBtn");

        if (saveBtn) {
            const saveIcon = saveBtn.querySelector("i");

            saveBtn.addEventListener("click", () => {
                const type = saveIcon.classList.contains("fa-solid") ? "unsave" : "save";

                fetch("${pageContext.request.contextPath}/library/save", {
                    method: "POST",
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: new URLSearchParams({
                        userId: currentUserId,
                        seriesId: seriesId,
                        type: type
                    }),
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {

                            if (data.saved) {
                                saveIcon.classList.replace("fa-regular", "fa-solid");
                            } else {
                                saveIcon.classList.replace("fa-solid", "fa-regular");
                            }

                            toastr.success(data.message);
                        } else {
                            toastr.warning(data.message);
                        }
                    })
                    .catch(err => console.error("Save error:", err));
            });
        }
    });
    /* ==========================================================
           ✅ 5. Co-author Modal
        ========================================================== */
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


    async function deleteCoauthor(username) {
        if (!confirm(`Are you sure you want to remove ${username} as co-author?`)) {
            return;
        }

        try {
            const response = await fetch(`${pageContext.request.contextPath}/manage-coauthors/remove`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    seriesId: '${series.seriesId}',
                    username: username
                })
            });

            const result = await response.json();

            if (result.success) {
                toastr.success(result.message);
                // Reload page to update the co-authors list
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                toastr.error(result.message);
            }
        } catch (error) {
            console.error('Error removing co-author:', error);
            toastr.error('Failed to remove co-author. Please try again.');
        }
    }
</script>

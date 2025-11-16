<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/11/2025
  Time: 9:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<main class="mt-10 grid grid-cols-12 gap-8 items-center">
    <!-- Nội dung chính -->
    <div class="col-span-8 col-start-3 bg-white p-6 rounded-xl shadow ">
        <a class="text-4xl font-bold text-center mb-3 block mx-auto"
           href="${pageContext.request.contextPath}/series/detail?seriesId=${chapter.seriesId}">
            ${chapter.seriesTitle}
        </a>


        <h2 class="text-center font-semibold text-gray-700 mb-3">by
            ${chapter.authorName}
        </h2>
        <div class="flex items-center justify-center gap-3 mb-6">

            <div class="relative flex gap-3 w-fit mx-auto mb-10">
                <!-- Nút mở danh sách chương -->
                <button id="chapterListBtn"
                        class="flex items-center justify-between gap-2 w-100 border border-[#195DA9] text-[#195DA9] px-4 py-2 rounded-md text-md font-medium hover:bg-blue-50 transition-all duration-200">
                    <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                    <i class="fa-solid fa-list-ul"></i>
                </button>

                <!-- Dropdown danh sách chương -->
                <div id="chapterList"
                     class="hidden absolute left-1/2 -translate-x-1/2 top-full mt-2 w-100 bg-white border border-gray-200 rounded-lg shadow-lg p-3 z-50">
                    <div class="text-base">
                        <h4
                                class="mb-2 bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent font-bold">
                            Chapter List
                        </h4>
                        <hr class="mb-3 border-gray-300"/>

                        <c:forEach var="chapterItem" items="${chapterList}" varStatus="">
                            <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${chapter.seriesId}&chapterId=${chapterItem.chapterId}">
                                <button
                                        class="block w-full text-left hover:bg-blue-50 rounded px-2 py-1 mb-1 text-gray-700 transition-all duration-150
                                                    ${chapterItem.chapterId == chapter.chapterId ? 'bg-blue-50' : ''} ">
                                    Chapter ${chapterItem.chapterNumber}: ${chapterItem.title}
                                </button>
                            </a>
                        </c:forEach>
                        <!-- Các chương -->
                    </div>
                </div>

                    <div class="relative inline-block">
                        <!-- Nút settings -->
                        <button id="settingsBtn"
                                class="text-gray-600 px-2 py-2 border rounded-md hover:bg-[#195DA9] hover:text-white transition-all duration-200">
                            <i class="fa-solid fa-gear text-2xl"></i>
                        </button>

                        <!-- Dropdown menu -->
                        <div id="settingsMenu"
                             class="hidden absolute right-0 mt-2 w-60 bg-white border border-gray-200 rounded-lg shadow-lg z-50 p-4 space-y-4">

                            <h3 class="text-lg font-semibold">Reading Settings</h3>

                            <!-- Font size -->
                            <div>
                                <label class="block text-sm font-medium mb-2">Font Size</label>
                                <div class="flex items-center justify-between">
                                    <button id="decreaseFont"
                                            class="w-8 h-8 flex items-center justify-center border rounded-md text-xl hover:bg-gray-100">−</button>
                                    <span id="fontSizeDisplay" class="font-medium">16px</span>
                                    <button id="increaseFont"
                                            class="w-8 h-8 flex items-center justify-center border rounded-md text-xl hover:bg-gray-100">+</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Dropdown menu -->
                    <!-- <div id="settingsMenu"
                        class="hidden absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-lg z-50 p-4 space-y-3 transition-all duration-200">
                        <h3 class="text-lg font-semibold mb-3">Reading Settings</h3>
                        Font size -->
                    <!-- <div class="mb-4">
                            <label class="block text-sm font-medium mb-2">Font Size</label>
                            <div class="flex items-center justify-between">
                                <button id="decreaseFont"
                                    class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-md text-xl hover:bg-gray-100 transition">
                                    −
                                </button>
                                <span id="fontSizeDisplay" class="text-base font-medium">16px</span>
                                <button id="increaseFont"
                                    class="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-md text-xl hover:bg-gray-100 transition">
                                    +
                                </button>
                            </div>
                        </div>
                    </div> -->
                </div>
            </div>

        <div id="contentArea" class="mt-6 text-gray-800 text-base transition-all duration-200">
            <p class="text-gray-700 leading-relaxed mb-4">
                ${chapter.content}
            </p>
        </div>
        <!-- Navigation buttons -->
        <div class="flex items-center justify-between mt-8">
            <a href="${pageContext.request.contextPath}/chapter/navigate?seriesId=${chapter.seriesId}&chapterNumber=${chapter.chapterNumber}&type=previous"
               class="border border-gray-300 px-4 py-2 rounded-lg
                      <c:if test='${chapter.chapterId == firstChapterId}'>opacity-50 cursor-not-allowed pointer-events-none text-gray-400</c:if>">
                &lt; Previous Chapter
            </a>

            <!-- Reactions -->
            <div class="flex items-center gap-5"     >
                <button ${loginedUser.role == 'author' ? "hidden" : ""}
                        class="openReportChapterBtn text-gray-600 px-2 py-2  border rounded-full hover:bg-[#195DA9] hover:text-white transition-all duration-200">
                    <i class="fa-regular fa-flag"></i></button>

                <p class="text-sm text-gray-500" ${loginedUser.role == 'author' ? "hidden" : ""}>Chapter ${chapter.chapterNumber}
                    of ${chapterList.size()}</p>
                <button id="likeBtn"
                        class="like-btn flex items-center justify-center gap-5 w-19 px-2 py-1 border rounded-full transition-all duration-200
                             text-gray-600 hover:bg-[#195DA9] hover:text-white"
                        data-user-id="${10}"
                        data-chapter-id="${chapter.chapterId}">
                    <i id="like" class="${liked ? 'fa-solid fa-heart text-red-500' : 'fa-regular fa-heart'}"></i>
                    <span id="likeCount">${chapter.totalLike}</span>
                </button>

            </div>
            <a href="${pageContext.request.contextPath}/chapter/navigate?seriesId=${chapter.seriesId}&chapterNumber=${chapter.chapterNumber}&type=next"
               class="bg-[#195DA9] text-white px-4 py-2 rounded-lg hover:bg-indigo-700
                      <c:if test='${chapter.chapterId >= lastChapterId}'>opacity-50 cursor-not-allowed pointer-events-none bg-gray-400 hover:bg-gray-400</c:if>">
                Next Chapter &gt;
            </a>
        </div>


        <!-- Modal Report Chapter -->
        <div id="reportChapterModal"
             class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center bg-opacity-50 hidden z-50">
            <div class="bg-white rounded-xl shadow-xl w-full max-w-md p-6 relative">

                <!-- Nút đóng -->
                <button id="closeReportChapterBtn"
                        class="absolute top-3 right-4 text-gray-400 hover:text-gray-700 text-2xl font-bold">&times;
                </button>

                <!-- Header -->
                <div class="flex items-center mb-2 gap-5">
                    <i class="fa-regular fa-flag text-red-500"></i>
                    <div>
                        <h2 class="text-lg font-bold text-red-600">Report Chapter</h2>
                        <p class="text-sm text-gray-500">Help us maintain a safe community</p>
                    </div>
                </div>

                <!-- Form -->
                <form action="${pageContext.request.contextPath}/report/report-chapter?type=chapter&seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}"
                      method="post" class="mt-4">
                    <input type="hidden" name="chapterId" id="reportChapterId">

                    <p class="font-medium text-gray-700 mb-2">Reason for reporting:</p>

                    <div class="space-y-2 mb-4">
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Inappropriate Content" required>
                            <span>Inappropriate Content</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Copyright violation">
                            <span>Copyright violation</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Spam or misleading">
                            <span>Spam or misleading</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Hate speech">
                            <span>Hate speech</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Violence content">
                            <span>Violence content</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Other">
                            <span>Other</span>
                        </label>
                    </div>

                    <label class="font-medium text-gray-700 mb-1 block">
                        Additional details (optional):
                    </label>
                    <textarea name="description"
                              placeholder="Please describe the issue briefly..."
                              maxlength="300"
                              class="w-full border border-blue-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-400 focus:outline-none resize-none"
                              rows="3"></textarea>

                    <div class="flex justify-end space-x-2 mt-5">
                        <button type="submit"
                                class="px-5 py-2 rounded-md bg-red-500 text-white font-medium hover:bg-red-600 transition" ${userId == 0 ? "disabled" : ""}>
                            Submit
                        </button>
                        <button type="button" id="cancelReportChapterBtn"
                                class="px-5 py-2 rounded-md bg-gray-200 text-gray-700 hover:bg-gray-300 transition">
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Comment box -->
        <form id="commentForm"
              action="${pageContext.request.contextPath}/comment/create?seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}"
              method="post"
              class="mt-8 flex items-center gap-2" ${loginedUser.role == 'author' ? "hidden" : ""}>

            <!-- Hidden khi edit -->
            <input type="hidden" id="commentId" name="commentId" value="">

            <input type="text" id="commentContent" name="content"
                   class="flex-1 border border-gray-300 rounded-lg p-3 text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                   placeholder="Write a comment..." ${userId == 0 ? "disabled" : ""}
                   required
            />

            <button id="commentSubmitBtn" type="submit" ${userId == 0 ? "disabled" : ""}
                    class="bg-indigo-600 hover:bg-indigo-700 text-white p-2 rounded-lg transition duration-200 flex items-center justify-center shadow-md hover:shadow-lg">
                <i class="fa-solid fa-comment"></i>
            </button>
        </form>


        <!-- Comments -->
        <div id="comment-list-container" class="mt-6 space-y-4" ${loginedUser.role == 'author' ? "hidden" : ""}>
            <jsp:include page="/WEB-INF/views/chapter/_commentList.jsp" flush="true" />
        </div>



        <div class="text-center">
            <button class="mt-4 text-gray-400 text-sm border rounded-full px-3 py-2 hover:text-black " ${loginedUser.role == 'author' ? "hidden" : ""}>Show
                more
            </button>
        </div>
    </div>
</main>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        const likeBtn = document.getElementById("likeBtn");
        loadComments();
        likeBtn.addEventListener("click", function () {
            if (likeBtn.classList.contains("liked")) return;
            if ("${loginedUser.userId}" == "") {
                toastr["warning"]("You must login to like chapter")
                return;
            }
            const chapterId = likeBtn.dataset.chapterId;
            const icon = likeBtn.querySelector("i");
            const likeCount = likeBtn.querySelector("span");
            // Gửi yêu cầu đến server
            fetch("${pageContext.request.contextPath}/reaction/like", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: "chapterId=" + encodeURIComponent(chapterId)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success && data.liked) {
                        likeBtn.classList.add("liked");
                        likeCount.textContent = data.newLikeCount;

                        icon.classList.remove("fa-regular");
                        icon.classList.add("fa-solid", "text-red-500");

                        likeBtn.disabled = true;
                    }
                })
                .catch(error => console.error("Error:", error));
        });

    });

    const contextPath = '${pageContext.request.contextPath}';
    const chapterId = ${chapterId};

    function loadComments() {
        fetch("${pageContext.request.contextPath}/comment/list?chapterId=" + encodeURIComponent(chapterId))
            .then(response => response.text())
            .then(html => {
                document.getElementById("comment-list-container").innerHTML = html;
            })
            .catch(error => console.error("Error:", error));
    }
    document.querySelector("#chapterListBtn").addEventListener("click", () => {
        document.querySelector("#chapterList").classList.toggle("hidden");
    })


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
</script>

<script>
    function editComment(commentId, content) {
        // Gán dữ liệu vào form hiện tại
        const form = document.getElementById('commentForm');
        const inputContent = document.getElementById('commentContent');
        const hiddenId = document.getElementById('commentId');
        const btn = document.getElementById('commentSubmitBtn');

        // Gán dữ liệu cũ
        inputContent.value = content;
        hiddenId.value = commentId;

        // Đổi action form sang edit-comment
        form.action = form.action.replace('create', 'edit');

        // Đổi màu nút để dễ nhận biết
        btn.classList.remove('bg-indigo-600', 'hover:bg-indigo-700');
        btn.classList.add('bg-yellow-500', 'hover:bg-yellow-600');

        // Đổi placeholder
        inputContent.placeholder = "Edit your comment...";

        // Khi bấm lại nút, ta quay lại chế độ create (nếu user muốn hủy)
        inputContent.focus();
    }
</script>

<c:if test="${not empty successReportMessage}">
    <script>
        if ("${successReportMessage}" != "") {
            toastr["success"]("${successReportMessage}")
        }
    </script>
    <c:remove var="successReportMessage" scope="session"/>
</c:if>


<%--Report Chapter Modal--%>
<script>
    document.querySelectorAll('.openReportChapterBtn').forEach(btn => {
        btn.addEventListener('click', () => {
            const chapterId = btn.dataset.chapterId;
            document.getElementById('reportChapterId').value = chapterId;
            document.getElementById('reportChapterModal').classList.remove('hidden');
        });
    });

    document.getElementById('closeReportChapterBtn').addEventListener('click', () => {
        document.getElementById('reportChapterModal').classList.add('hidden');
    });
    document.getElementById('cancelReportChapterBtn').addEventListener('click', () => {
        document.getElementById('reportChapterModal').classList.add('hidden');
    });

    function deleteComment(commentId, chapterId) {
        fetch("${pageContext.request.contextPath}/comment/delete", {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                commentId,
                chapterId
            })
        }).then(response => {
            if (response.ok) {
                return response.json()
            }
            else alert("Xóa thất bại!");
        }).then(data => {
            if(data.success) {
                location.reload()
            }
        });
    }
    document.addEventListener("DOMContentLoaded", () => {
        const settingsBtn = document.getElementById("settingsBtn");
        const settingsMenu = document.getElementById("settingsMenu");

        const content = document.getElementById("contentArea");
        const fontDisplay = document.getElementById("fontSizeDisplay");
        const increaseBtn = document.getElementById("increaseFont");
        const decreaseBtn = document.getElementById("decreaseFont");

        /* -------------------------------
           Load Settings từ localStorage
        -------------------------------- */
        let fontSize = localStorage.getItem("readerFontSize") || 16;
        applyFontSize(fontSize);


        /* -------------------------------
            Toggle Settings Menu (luôn mở)
        -------------------------------- */
        settingsBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            settingsMenu.classList.remove("hidden"); // luôn mở
        });

        // Không tắt khi click vào bên trong menu
        settingsMenu.addEventListener("click", (e) => {
            e.stopPropagation();
        });

        // Click ra ngoài => ẩn menu
        window.addEventListener("click", () => {
            settingsMenu.classList.add("hidden");
        });


        /* -------------------------------
            Font Size Functions
        -------------------------------- */
        increaseBtn.addEventListener("click", () => {
            fontSize = Math.min(30, Number(fontSize) + 1);
            saveFontSize();
        });

        decreaseBtn.addEventListener("click", () => {
            fontSize = Math.max(12, Number(fontSize) - 1);
            saveFontSize();
        });

        function saveFontSize() {
            localStorage.setItem("readerFontSize", fontSize);
            applyFontSize(fontSize);
        }

        function applyFontSize(size) {
            content.style.fontSize = size + "px";
            fontDisplay.textContent = size + "px";
        }
    });
</script>
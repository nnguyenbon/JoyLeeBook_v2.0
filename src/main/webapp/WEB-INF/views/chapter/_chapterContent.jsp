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
        <h1 class="text-4xl font-bold text-center mb-3">${chapter.seriesTitle}</h1>

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
            <div class="flex items-center gap-5">
                <button
                        class="openReportChapterBtn text-gray-600 px-2 py-2  border rounded-full hover:bg-[#195DA9] hover:text-white transition-all duration-200">
                    <i class="fa-regular fa-flag"></i></button>

                <p class="text-sm text-gray-500">Chapter ${chapter.chapterNumber}
                    of ${chapterList.size()}</p>
                <button id="likeBtn"
                        class="like-btn flex items-center justify-center gap-5 w-19 px-2 py-1 border rounded-full transition-all duration-200
                             text-gray-600 hover:bg-[#195DA9] hover:text-white"
                        data-user-id="${10}"
                        data-chapter-id="${chapter.chapterId}">
                    <i id="like" class="${liked ? 'fa-solid fa-heart text-red-500' : 'fa-regular fa-heart'}"></i>
                    <span id="likeCount">${totalLike}</span>
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
             class="fixed inset-0 flex items-center justify-center bg-opacity-50 hidden z-50">
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
                <form action="${pageContext.request.contextPath}/report/report-chapter?chapterId=${chapterId}"
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
                                class="px-5 py-2 rounded-md bg-red-500 text-white font-medium hover:bg-red-600 transition
                                     <c:if test='${userId == 0}'>opacity-50 cursor-not-allowed pointer-events-none text-gray-400</c:if>">
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
              action="${pageContext.request.contextPath}/comment/insert?chapterId=${chapterId}"
              method="post"
              class="mt-8 flex items-center gap-2">

            <!-- Hidden khi edit -->
            <input type="hidden" id="commentId" name="commentId" value="">

            <input type="text" id="commentContent" name="content"
                   class="flex-1 border border-gray-300 rounded-lg p-3 text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                   placeholder="Write a comment..." ${sessionScope.loginedUser == null ? "disabled" : ""}
                   required
            />

            <button id="commentSubmitBtn" type="submit"
                    class="bg-indigo-600 hover:bg-indigo-700 text-white p-2 rounded-lg transition duration-200 flex items-center justify-center shadow-md hover:shadow-lg
                        <c:if test='${userId == 0}'>opacity-50 cursor-not-allowed pointer-events-none text-gray-400</c:if>">
                <i class="fa-regular fa-paper-plane text-2xl"></i>
            </button>
        </form>


        <!-- Comments -->
        <div class="mt-6 space-y-4">
            <!-- Comment 1 -->
            <c:forEach var="comment" items="${commentList}" varStatus="loop">
                <div class="flex justify-between gap-3">
                    <div class="flex gap-5">
                        <div class="w-10 h-10 rounded-full bg-gray-200"></div>
                        <div>
                            <p class="font-semibold text-gray-800">${comment.username}</p>
                            <p class="text-gray-600 text-sm">${comment.content}</p>
                            <p class="text-xs text-gray-400 mt-1">${comment.updatedAt}</p>
                        </div>
                    </div>
                    <div class="relative">
                        <c:if test='${userId != 0}'>
                            <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                                <i class="fa-solid fa-ellipsis"></i>
                            </button>
                        </c:if>

                        <div
                                class="dropdown-menu hidden absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                            <c:if test="${comment.userId == userId}">
                                <button
                                        class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                        onclick="editComment(${comment.commentId}, '${comment.content}')">
                                    Edit
                                </button>

                                <button onclick="deleteComment(${comment.commentId}, ${chapter.seriesId}, ${chapterId})"
                                        class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                    Delete
                                </button>
                            </c:if>
                            <c:if test="${userId != 0 && comment.userId != userId}">
                                <button class="openReportCmtBtn block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                        data-comment-id="${comment.commentId}">
                                    Report
                                </button>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Modal Report -->
        <div id="reportModal"
             class="fixed inset-0 flex items-center justify-center bg-opacity-50 hidden z-50">
            <div class="bg-white rounded-xl shadow-xl w-full max-w-md p-6 relative">
                <!-- Nút đóng -->
                <button id="closeReportBtn"
                        class="absolute top-3 right-4 text-gray-400 hover:text-gray-700 text-2xl font-bold">&times;
                </button>

                <!-- Header -->
                <div class="flex items-center mb-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7 text-red-500 mr-2" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <i class="fa-regular fa-flag text-red-500"></i>
                    <div>
                        <h2 class="text-lg font-bold text-red-600">Report Comment</h2>
                        <p class="text-sm text-gray-500">Help us maintain a safe community</p>
                    </div>
                </div>

                <!-- Form -->
                <form action="${pageContext.request.contextPath}/report/report-comment?chapterId=${chapterId}"
                      method="post" class="mt-4">
                    <input type="hidden" name="commentId" id="reportCommentId">
                    <p class="font-medium text-gray-700 mb-2">Reason for reporting:</p>

                    <div class="space-y-2 mb-4">
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Hate Speech/Harassment" required>
                            <span>Hate Speech/Harassment</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Spam/Misleading Content">
                            <span>Spam/Misleading Content</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Violence/Threats">
                            <span>Violence/Threats</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Inappropriate/Explicit Content">
                            <span>Inappropriate/Explicit Content</span>
                        </label>
                        <label class="flex items-center space-x-2">
                            <input type="radio" name="reason" value="Impersonation">
                            <span>Impersonation</span>
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
                              placeholder="Please provide more information about the issue..."
                              maxlength="300"
                              class="w-full border border-blue-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-400 focus:outline-none resize-none"
                              rows="3"></textarea>

                    <div class="flex justify-end space-x-2 mt-5">
                        <button type="submit"
                                class="px-5 py-2 rounded-md bg-red-500 text-white font-medium hover:bg-red-600 transition
                                        <c:if test='${userId == 0}'>opacity-50 cursor-not-allowed pointer-events-none text-gray-400</c:if>">
                            Submit
                        </button>
                        <button type="button" id="cancelReportBtn"
                                class="px-5 py-2 rounded-md bg-gray-200 text-gray-700 hover:bg-gray-300 transition">
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="text-center">
            <button class="mt-4 text-gray-400 text-sm border rounded-full px-3 py-2 hover:text-black ">Show
                more
            </button>
        </div>
    </div>
</main>

<script>
        document.addEventListener("DOMContentLoaded", async () => {
        const likeBtn = document.getElementById("likeBtn");
        const icon = likeBtn.querySelector("i");
        const likeCount = likeBtn.querySelector("span");
        const userId = likeBtn.dataset.userId;
        const chapterId = likeBtn.dataset.chapterId;

        // 🟩 Lấy thông tin like ban đầu
        try {
        const res = await fetch(`${pageContext.request.contextPath}/reaction/get-like?chapterId=${chapterId}&userId=${userId}`);
        const data = await res.json();

        likeCount.textContent = data.totalLike || 0;
        if (data.liked) {
        likeBtn.classList.add("liked");
        icon.classList.remove("fa-regular");
        icon.classList.add("fa-solid", "text-red-500");
    }
    } catch (err) {
        console.error("Không thể lấy thông tin like:", err);
    }

        // 🟥 Khi nhấn Like/Unlike
        likeBtn.addEventListener("click", async () => {
        if (userId == 0) {
        toastr["warning"]("Vui lòng đăng nhập để like chapter!");
        return;
    }

        const action = likeBtn.classList.contains("liked") ? "unlike-chapter" : "like-chapter";

        try {
        const res = await fetch(`${pageContext.request.contextPath}/reaction/${action}`, {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: new URLSearchParams({ userId, chapterId })
    });

        const data = await res.json();
        likeCount.textContent = data.totalLike;

        if (action === "like-chapter") {
        likeBtn.classList.add("liked");
        icon.classList.remove("fa-regular");
        icon.classList.add("fa-solid", "text-red-500");
    } else {
        likeBtn.classList.remove("liked");
        icon.classList.remove("fa-solid", "text-red-500");
        icon.classList.add("fa-regular");
    }
    } catch (err) {
        console.error("Lỗi khi gửi like:", err);
    }
    });
    });
</script>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const chapterListBtn = document.getElementById("chapterListBtn");
        const chapterList = document.getElementById("chapterList");

        chapterListBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        chapterList.classList.toggle("hidden");
    });

        // Click ra ngoài ẩn dropdown
        window.addEventListener("click", (e) => {
        if (!chapterList.contains(e.target) && !chapterListBtn.contains(e.target)) {
        chapterList.classList.add("hidden");
    }
    });
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
        if(${successReportMessage}) {
            toastr["success"](${successReportMessage})
        }
    </script>
    <c:remove var="successReportMessage" scope="session"/>
</c:if>

<%--Modal Report--%>
<script>


    document.querySelectorAll('.openReportCmtBtn').forEach(btn => {
        btn.addEventListener('click', () => {
            const commentId = btn.dataset.commentId;
            document.getElementById('reportCommentId').value = commentId;
            document.getElementById('reportModal').classList.remove('hidden');
        });
    });

    document.getElementById('closeReportBtn').addEventListener('click', () => {
        document.getElementById('reportModal').classList.add('hidden');
    });
    document.getElementById('cancelReportBtn').addEventListener('click', () => {
        document.getElementById('reportModal').classList.add('hidden');
    });
</script>

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

    function deleteComment(commentId, seriesId, chapterId) {
        fetch(`comment`, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'delete',
                commentId,
                seriesId,
                chapterId
            })
        }).then(response => {
            if (response.ok) location.reload();
            else alert("Xóa thất bại!");
        });
    }
</script>


<!-- <script>
const settingsBtn = document.getElementById('settingsBtn');
const settingsMenu = document.getElementById('settingsMenu');
const fontButtons = document.querySelectorAll('.font-btn');
const content = document.getElementById('contentArea');
const lightBtn = document.getElementById('lightMode');
const darkBtn = document.getElementById('darkMode');

// Toggle dropdown
settingsBtn.addEventListener('click', (e) => {
e.stopPropagation();
settingsMenu.classList.toggle('hidden');
});

// Font size change
fontButtons.forEach(btn => {
btn.addEventListener('click', () => {
const size = btn.dataset.size;
if (size === 'small') content.className = 'mt-6 text-gray-800 text-sm transition-all duration-200';
if (size === 'medium') content.className = 'mt-6 text-gray-800 text-base transition-all duration-200';
if (size === 'large') content.className = 'mt-6 text-gray-800 text-lg transition-all duration-200';
});
});

// Theme change
lightBtn.addEventListener('click', () => {
document.documentElement.classList.remove('dark');
document.body.classList.remove('bg-gray-900', 'text-white');
document.body.classList.add('bg-white', 'text-gray-800');
});

darkBtn.addEventListener('click', () => {
document.documentElement.classList.add('dark');
document.body.classList.add('bg-gray-900', 'text-white');
document.body.classList.remove('bg-white', 'text-gray-800');
});

// Đóng khi click ra ngoài
window.addEventListener('click', (e) => {
if (!settingsBtn.contains(e.target) && !settingsMenu.contains(e.target)) {
settingsMenu.classList.add('hidden');
}
});
</script> -->
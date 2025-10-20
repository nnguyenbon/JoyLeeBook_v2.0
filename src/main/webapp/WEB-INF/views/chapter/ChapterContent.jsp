<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/11/2025
  Time: 9:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">

<main class=" mb-10">
    <div class="max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-[30px]">
        <!-- Nội dung chính -->
        <div class="col-span-8 col-start-3 bg-white p-6 rounded-xl shadow">
            <h1 class="text-4xl font-bold text-center mb-3">${chapterDetailDTO.seriesTitle}</h1>

            <h2 class="text-center font-semibold text-gray-700 mb-3">by
                <c:forEach var="author" items="${chapterDetailDTO.authorsName}" varStatus="loop">
                    ${author}<c:if test="${!loop.last}">, </c:if>
                </c:forEach>
            </h2>
            <div class="flex items-center justify-center gap-3 mb-6">

                <div class="relative flex gap-3 w-fit mx-auto mb-10">
                    <!-- Nút mở danh sách chương -->
                    <button id="chapterListBtn"
                            class="flex items-center justify-between gap-2 w-100 border border-[#195DA9] text-[#195DA9] px-4 py-2 rounded-md text-md font-medium hover:bg-blue-50 transition-all duration-200">
                        <span>Chapter ${chapterDetailDTO.chapterNumber}: ${chapterDetailDTO.title}</span>
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
                            <hr class="mb-3 border-gray-300" />

                            <c:forEach var="chapterItem" items="${chapterInfoDTOList}" varStatus="">
                                <a href="${pageContext.request.contextPath}/chapter-content?seriesId=${chapterDetailDTO.seriesId}&chapterId=${chapterItem.chapterId}">
                                    <button
                                            class="block w-full text-left hover:bg-blue-50 rounded px-2 py-1 mb-1 text-gray-700 transition-all duration-150">
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
                    ${chapterDetailDTO.content}
                </p>
            </div>
            <!-- Navigation buttons -->
            <div class="flex items-center justify-between mt-8">
                <a href="${pageContext.request.contextPath}/navigate-chapter?seriesId=${chapterDetailDTO.seriesId}&chapterNumber=${chapterDetailDTO.chapterNumber}&action=previous">
                <button class="border border-gray-300 text-gray-600 px-4 py-2 rounded-lg hover:bg-gray-100">
                    &lt; Previous Chapter
                </button>
                </a>


                <!-- Reactions -->
                <div class="flex items-center gap-5">
                    <button
                            class="openReportChapterBtn text-gray-600 px-2 py-2  border rounded-full hover:bg-[#195DA9] hover:text-white transition-all duration-200">
                        <i class="fa-regular fa-flag"></i></button>

                    <p class="text-sm text-gray-500">Chapter ${chapterDetailDTO.chapterNumber} of ${chapterInfoDTOList.size()}</p>
                    <button id="likeBtn"
                            class="like-btn flex items-center justify-center gap-5 w-19 px-2 py-1 border rounded-full transition-all duration-200
                             text-gray-600 hover:bg-[#195DA9] hover:text-white"
                            data-user-id="${10}"
                            data-chapter-id="${chapterDetailDTO.chapterId}">
                        <i id="like" class="${liked ? 'fa-solid fa-heart text-red-500' : 'fa-regular fa-heart'}"></i>
                        <span id="likeCount">${chapterDetailDTO.totalLike}</span>
                    </button>

                </div>
                <a href="${pageContext.request.contextPath}/navigate-chapter?seriesId=${chapterDetailDTO.seriesId}&chapterNumber=${chapterDetailDTO.chapterNumber}&action=next">
                    <button class="bg-[#195DA9] text-white px-4 py-2 rounded-lg hover:bg-indigo-700">
                        Next Chapter &gt;
                    </button>
                </a>

            </div>


            <!-- Modal Report Chapter -->
            <div id="reportChapterModal"
                 class="fixed inset-0 flex items-center justify-center bg-opacity-50 hidden z-50">
                <div class="bg-white rounded-xl shadow-xl w-full max-w-md p-6 relative">

                    <!-- Nút đóng -->
                    <button id="closeReportChapterBtn"
                            class="absolute top-3 right-4 text-gray-400 hover:text-gray-700 text-2xl font-bold">&times;</button>

                    <!-- Header -->
                    <div class="flex items-center mb-2">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7 text-red-500 mr-2" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M4 4v16m0-12h16l-4 4 4 4H4" />
                        </svg>
                        <div>
                            <h2 class="text-lg font-bold text-red-600">Report Chapter</h2>
                            <p class="text-sm text-gray-500">Help us maintain a safe community</p>
                        </div>
                    </div>

                    <!-- Form -->
                    <form action="${pageContext.request.contextPath}/report-chapter?seriesId=${seriesId}&chapterId=${chapterId}" method="post" class="mt-4">
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
                                    class="px-5 py-2 rounded-md bg-red-500 text-white font-medium hover:bg-red-600 transition">
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
                  action="${pageContext.request.contextPath}/create-comment?seriesId=${seriesId}&chapterId=${chapterId}"
                  method="post"
                  class="mt-8 flex items-center gap-2">

                <!-- Hidden khi edit -->
                <input type="hidden" id="commentId" name="commentId" value="">

                <input type="text" id="commentContent" name="content"
                       class="flex-1 border border-gray-300 rounded-lg p-3 text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                       placeholder="Write a comment..." />

                <button id="commentSubmitBtn" type="submit"
                        class="bg-indigo-600 hover:bg-indigo-700 text-white p-2 rounded-lg transition duration-200 flex items-center justify-center shadow-md hover:shadow-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" class="w-5 h-5">
                        <path stroke-linecap="round" stroke-linejoin="round"
                              d="M3 10l9-6 9 6m-9 4v10m0-10L3 10m9 4l9-4" />
                    </svg>
                </button>
            </form>


            <!-- Comments -->
            <div class="mt-6 space-y-4">
                <!-- Comment 1 -->
                <c:forEach var="comment" items="${commentDetailDTOList}" varStatus="loop">
                    <div class="flex justify-between gap-3">
                        <div class="flex gap-5">
                            <div class="w-10 h-10 rounded-full bg-gray-200"></div>
                            <div>
                                <p class="font-semibold text-gray-800">${comment.username}</p>
                                <p class="text-gray-600 text-sm">${comment.content}</p>
                                <p class="text-xs text-gray-400 mt-1">${comment.updateAt}</p>
                            </div>
                        </div>

                        <div class="relative">
                            <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                                <i class="fa-solid fa-ellipsis"></i>
                            </button>

                            <div
                                    class="dropdown-menu hidden absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-lg shadow-lg z-50">

                                <button
                                        class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                        onclick="editComment(${comment.commentId}, '${fn:escapeXml(comment.content)}')">
                                    Edit
                                </button>


                                <a href="${pageContext.request.contextPath}/delete-comment?commentId=${comment.commentId}&seriesId=${seriesId}&chapterId=${chapterId}"
                                   class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                    Delete
                                </a>

                                <button class="openReportCmtBtn block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                        data-comment-id="${comment.commentId}">
                                    Report</button>
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
                            class="absolute top-3 right-4 text-gray-400 hover:text-gray-700 text-2xl font-bold">&times;</button>

                    <!-- Header -->
                    <div class="flex items-center mb-2">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7 text-red-500 mr-2" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M4 4v16m0-12h16l-4 4 4 4H4" />
                        </svg>
                        <div>
                            <h2 class="text-lg font-bold text-red-600">Report Comment</h2>
                            <p class="text-sm text-gray-500">Help us maintain a safe community</p>
                        </div>
                    </div>

                    <!-- Form -->
                    <form action="${pageContext.request.contextPath}/report-comment?seriesId=${seriesId}&chapterId=${chapterId}" method="post" class="mt-4">
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
                                    class="px-5 py-2 rounded-md bg-red-500 text-white font-medium hover:bg-red-600 transition">
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
                    more</button>
            </div>
        </div>


    </div>

</main>
<footer class="bg-white h-2 text-gray-600 text-center py-3 border-t border-gray-200 relative">
    <!-- Logo -->
    <div class="max-w-3xl mx-auto px-4">
        <h2 class="text-xl font-bold mb-2">
            <span class="text-2xl text-blue-700">J</span><span class="text-blue-600">oyLeeBook</span>
        </h2>

        <!-- Description -->
        <p class="text-sm leading-relaxed mb-4">
            Your gateway to endless stories. Discover, read, and connect with millions of readers worldwide
            in the ultimate reading experience.
        </p>

        <!-- Social icons -->
        <div class="flex justify-center gap-5 text-gray-500 mb-4">
            <a href="#" class="hover:text-blue-500 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                    stroke-linejoin="round" class="lucide lucide-twitter-icon lucide-twitter w-5 h-5">
                <path
                        d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z" />
            </svg></a>

            <a href="#" class="hover:text-blue-500 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                    stroke-linejoin="round" class="lucide lucide-facebook-icon lucide-facebook w-5 h-5">
                <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z" />
            </svg></a>
            <a href="#" class="hover:text-blue-500 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                    stroke-linejoin="round" class="lucide lucide-instagram-icon lucide-instagram w-5 h-5">
                <rect width="20" height="20" x="2" y="2" rx="5" ry="5" />
                <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
                <line x1="17.5" x2="17.51" y1="6.5" y2="6.5" />
            </svg></a>
            <a href="#" class="hover:text-gray-800 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    class="w-5 h-5 lucide lucide-mail-icon lucide-mail" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                                                    stroke-linecap="round" stroke-linejoin="round">
                <path d="m22 7-8.991 5.727a2 2 0 0 1-2.009 0L2 7" />
                <rect x="2" y="4" width="20" height="16" rx="2" />
            </svg></a>
        </div>

        <!-- Divider -->
        <div class="border-t border-gray-300 w-5/5 mx-auto mb-3"></div>

        <!-- Copyright -->
        <p class="text-xs text-gray-500">
            © 2024 <span class="font-medium text-gray-700">JoyLeeBook</span>. All rights reserved.
        </p>
    </div>

    <!-- Back to Top Button -->
    <a href="#" class="absolute right-6 top-7 text-gray-400 hover:text-gray-700 transition">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="2"
             stroke-linecap="round" stroke-linejoin="round" class="w-5 h-5">
            <path d="M12 19V5M5 12l7-7 7 7" />
        </svg>
    </a>
</footer>

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

    const chapterListBtn = document.getElementById("chapterListBtn");
    const chapterList = document.getElementById("chapterList");

    chapterListBtnl.addEventListener("click", () => {
        chapterList.classList.toggle("hidden");
    })
    document.addEventListener("click", (e) => {
        if (!chapterListBtn.contains(e.target) && !chapterList.contains(e.target)) {
            chapterList.classList.add("hidden");
        }
    })
</script>
<script>
    // Toggle dropdown
    document.getElementById("chapterListBtn").addEventListener("click", () => {
        document.getElementById("chapterList").classList.toggle("hidden");
    });

    // Đóng khi click ra ngoài
    window.addEventListener("click", (e) => {
        const btn = document.getElementById("chapterListBtn");
        const list = document.getElementById("chapterList");
        if (!btn.contains(e.target) && !list.contains(e.target)) {
            list.classList.add("hidden");
        }
    });


</script>
<script>

        document.addEventListener("DOMContentLoaded", () => {
        const likeBtn = document.getElementById("likeBtn");

        likeBtn.addEventListener("click", function() {
        // Nếu người dùng đã like rồi thì không cho click nữa
        if (likeBtn.classList.contains("liked")) return;

        const userId = likeBtn.dataset.userId;
        const chapterId = likeBtn.dataset.chapterId;
        const icon = likeBtn.querySelector("i");
        const likeCount = likeBtn.querySelector("span");

        // Gửi yêu cầu đến server
        fetch("like-chapter", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "userId=" + encodeURIComponent(userId) +
        "&chapterId=" + encodeURIComponent(chapterId)
    })
        .then(response => response.json())
        .then(data => {
        if (data.success && data.liked) {
        // Cập nhật giao diện
        likeBtn.classList.add("liked");
        likeCount.textContent = data.newLikeCount;

        icon.classList.remove("fa-regular");
        icon.classList.add("fa-solid", "text-red-500");

        // Chặn click tiếp
        likeBtn.disabled = true;
    }
    })
        .catch(error => console.error("Error:", error));
    });
    });

</script>
<script>
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
        form.action = form.action.replace('create-comment', 'edit-comment');

        // Đổi màu nút để dễ nhận biết
        btn.classList.remove('bg-indigo-600', 'hover:bg-indigo-700');
        btn.classList.add('bg-yellow-500', 'hover:bg-yellow-600');

        // Đổi placeholder
        inputContent.placeholder = "Edit your comment...";

        // Khi bấm lại nút, ta quay lại chế độ create (nếu user muốn hủy)
        inputContent.focus();
    }
</script>

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

</body>

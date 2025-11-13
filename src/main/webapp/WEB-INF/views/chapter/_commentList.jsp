<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 11/10/2025
  Time: 9:43 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:forEach var="comment" items="${commentList}" varStatus="loop">
    <div class="flex justify-between gap-3">
        <div class="flex gap-5">
            <div class="w-10 h-10 rounded-full bg-gray-200"></div>
            <div class="overflow-hidden">
                <p class="font-semibold text-gray-800">${comment.username}</p>
                <p class="text-gray-600 text-sm truncate">${comment.content}</p>
                <p class="text-xs text-gray-400 mt-1">${comment.updatedAt}</p>
            </div>
        </div>
        <div class="relative">
            <c:if test='${userId != 0}'>
                <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                    <i class="fa-solid fa-ellipsis"></i>
                </button>
            </c:if>

            <div class="dropdown-menu hidden absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                <c:if test="${comment.userId == userId}">
                    <button
                            class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                            onclick="editComment(${comment.commentId}, '${comment.content}')">
                        Edit
                    </button>

                    <button onclick="deleteComment(${comment.commentId}, ${comment.chapterId})"
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
                 viewBox="0 0 24 24" stroke="currentColor"></svg>
            <i class="fa-regular fa-flag text-red-500"></i>
            <div>
                <h2 class="text-lg font-bold text-red-600">Report Comment</h2>
                <p class="text-sm text-gray-500">Help us maintain a safe community</p>
            </div>
        </div>

        <!-- Form -->
        <form action="${pageContext.request.contextPath}/report/report-comment?type=comment&seriesId=${seriesId}&chapterId=${chapterId}"
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
<script>
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.dropdown-btn');
        const menu = e.target.closest('.dropdown-menu');

        // Nếu click vào nút ba chấm
        if (btn) {
            e.stopPropagation();
            const thisMenu = btn.nextElementSibling;
            document.querySelectorAll('.dropdown-menu').forEach(m => {
                if (m !== thisMenu) m.classList.add('hidden');
            });
            thisMenu.classList.toggle('hidden');
            return;
        }

        // Nếu click vào bên trong menu thì không đóng
        if (menu) {
            e.stopPropagation();
            return;
        }

        // Nếu click ra ngoài => đóng hết
        document.querySelectorAll('.dropdown-menu').forEach(m => m.classList.add('hidden'));
    });

    document.addEventListener('click', function(e) {
        const btn = e.target.closest('.openReportCmtBtn');
        if (btn) {
            const commentId = btn.dataset.commentId;
            document.getElementById('reportCommentId').value = commentId;
            document.getElementById('reportModal').classList.remove('hidden');
        }
    });
    document.getElementById('closeReportBtn').addEventListener('click', () => {
        document.getElementById('reportModal').classList.add('hidden');
    });
    document.getElementById('cancelReportBtn').addEventListener('click', () => {
        document.getElementById('reportModal').classList.add('hidden');
    });
</script>
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
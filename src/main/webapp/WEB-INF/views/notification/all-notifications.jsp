<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 03-Nov-25
  Time: 17:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="max-w-12xl mx-auto my-8 p-6 bg-white shadow-lg rounded-lg">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-800">All Notifications</h1>

        <%-- Nút "Mark all as read" --%>
        <c:if test="${totalNotifications > 0}">
            <a href="${pageContext.request.contextPath}/notifications/mark-all"
               class="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700">
                Mark All as Read
            </a>
        </c:if>
    </div>

    <%-- Danh sách thông báo--%>
    <div class="notification-list-container space-y-4">
        <c:choose>
            <c:when test="${not empty notificationList}">
                <c:forEach var="noti" items="${notificationList}">
                    <a href="${pageContext.request.contextPath}${noti.urlRedirect}"
                       class="block p-4 rounded-lg border notification-item ${noti.isRead() ? 'bg-white hover:bg-gray-50' : 'bg-blue-50 hover:bg-blue-100'}"
                       data-id="${noti.notificationId}"
                    >
                        <div class="flex justify-between items-center">
                            <p class="text-lg font-semibold ${noti.isRead() ? 'text-gray-700' : 'text-blue-800'}">
                                    ${noti.title}
                            </p>
                            <c:if test="${!noti.isRead()}">
                                <span class="w-3 h-3 bg-blue-500 rounded-full"></span>
                            </c:if>
                        </div>
                        <p class="text-sm text-gray-600 mt-1">${noti.message}</p>
                        <p class="text-xs text-gray-400 mt-2">
                                ${noti.createdAt}
                        </p>
                    </a>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p class="text-lg text-gray-500 text-center py-10">You have no notifications.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- Thanh phân trang--%>
    <c:if test="${totalPages > 1}">
        <nav class="flex justify-center mt-8" aria-label="Pagination">
            <ul class="inline-flex items-center -space-x-px">
                    <%-- Nút Previous --%>
                <li>
                    <a href="${currentPage > 1 ? pageContext.request.contextPath.concat('/notifications/all?page=').concat(currentPage - 1) : '#'}"
                       class="py-2 px-3 ml-0 leading-tight text-gray-500 bg-white rounded-l-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700 ${currentPage == 1 ? 'opacity-50 cursor-not-allowed' : ''}">
                        Previous
                    </a>
                </li>

                    <%-- Các số trang--%>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li>
                        <a href="${pageContext.request.contextPath}/notifications/all?page=${i}"
                           class="py-2 px-3 leading-tight ${i == currentPage ? 'text-blue-600 bg-blue-50 border border-blue-300' : 'text-gray-500 bg-white border border-gray-300'} hover:bg-gray-100 hover:text-gray-700">
                                ${i}
                        </a>
                    </li>
                </c:forEach>

                    <%-- Nút Next --%>
                <li>
                    <a href="${currentPage < totalPages ? pageContext.request.contextPath.concat('/notifications/all?page=').concat(currentPage + 1) : '#'}"
                       class="py-2 px-3 leading-tight text-gray-500 bg-white rounded-r-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700 ${currentPage == totalPages ? 'opacity-50 cursor-not-allowed' : ''}">
                        Next
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>

</div>

<script>
    document.querySelectorAll('.notification-item').forEach(item => {
        item.addEventListener('click', function (e) {
            const notiId = this.getAttribute('data-id');
            const isRead = !this.classList.contains('bg-blue-50');
            const originalUrl = this.href;

            if (notiId && !isRead) {
                e.preventDefault();

                fetch('${pageContext.request.contextPath}/notification/mark-read', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'id=' + notiId
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok.');
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.success) {
                            this.classList.remove('bg-blue-50', 'hover:bg-blue-100');
                            this.classList.add('bg-white', 'hover:bg-gray-50');
                            this.querySelector('.font-semibold').classList.remove('text-blue-800');
                            this.querySelector('.font-semibold').classList.add('text-gray-700');
                            const dot = this.querySelector('.w-3.h-3.bg-blue-500');
                            if (dot) dot.remove();
                        }
                    })
                    .catch(err => {
                        console.error('Failed to mark notification as read:', err);
                    })
                    .finally(() => {
                        window.location.href = originalUrl;
                    });
            }
        });
    });
</script>
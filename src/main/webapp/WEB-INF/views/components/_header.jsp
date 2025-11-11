<%-- Created by IntelliJ IDEA. User: DPhuc Date: 10/10/25 Time: 12:48 AM To
change this template use File | Settings | File Templates. --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dao.CategoryDAO" %>
<%@ page import="model.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="db.DBConnection" %>
<%@ page import="java.sql.SQLException" %>

<%@ page import="model.Account" %>
<%@ page import="model.User" %>
<%@ page import="model.Notification" %>
<%@ page import="dao.NotificationsDAO" %>

<%
    CategoryDAO categoryDAO;
    try {
        categoryDAO = new CategoryDAO(DBConnection.getConnection());
        List<Category> categories = categoryDAO.getAll();
        request.setAttribute("categories", categories);
    } catch (SQLException | ClassNotFoundException e) {
        throw new RuntimeException(e);
    }

    Account loginedAccount = (Account) session.getAttribute("loginedUser");

    if (loginedAccount != null) {
        if (loginedAccount instanceof User) {
            User loginedUser = (User) loginedAccount;
            try {
                NotificationsDAO notiDAO = new NotificationsDAO(DBConnection.getConnection());

                List<Notification> notifications = notiDAO.findRecentByUserId(loginedUser.getUserId(), 10);
                request.setAttribute("userNotifications", notifications);

                int unreadCount = notiDAO.getUnreadCountByUserId(loginedUser.getUserId());
                request.setAttribute("unreadCount", unreadCount);

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
%>
<header class="relative top-0 left-0 right-0 shadow-md bg-white ">
    <div class="max-w-7xl mx-auto grid grid-cols-12 gap-8 items-center">
        <div class="col-span-2 flex items-center gap-2 h-20">
            <a href="${pageContext.request.contextPath}/homepage">
                <img src="${pageContext.request.contextPath}/img/shared/logo.png" alt="logo"/>
            </a>
        </div>
        <div class="col-span-1 relative">
            <!-- Nút Genre -->
            <button
                    id="genreButton"
                    class="px-4 py-2 bg-white rounded-md hover:bg-gray-100 font-medium flex items-center gap-1"
            >
                Genre
                <i class="fa-solid fa-caret-down"></i>
            </button>

            <!-- Dropdown -->
            <div
                    id="genreMenu"
                    class="hidden absolute left-0 top-full mt-2 w-[600px] bg-white border border-gray-200 rounded-lg shadow-lg p-4 z-50"
            >
                <div class="grid grid-cols-5 gap-3 text-sm">
                    <c:forEach var="category" items="${categories}">
                        <a href="${pageContext.request.contextPath}/search?searchType=&genres=${category.name}"
                           class="inline-block">
                            <button class="hover:bg-blue-100 rounded px-2 py-1 text-left w-full">
                                    ${category.name}
                            </button>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="col-span-5">
            <form action="${pageContext.request.contextPath}/series/list" class="w-full">
                <input
                        type="text"
                        placeholder="Search series, author"
                        class="py-2 px-3 border border-gray-300 rounded-md focus:outline-none w-full"
                        name="search"
                        id="search"
                />
            </form>
        </div>
        <c:if test="${loginedUser != null}">

            <c:if test="${loginedUser.role == 'reader' || loginedUser.role == 'author'}">
                <div class="col-span-1 relative flex justify-center items-center">
                    <button id="BtnNotify"
                            class="relative px-3 py-2 flex items-center gap-1 text-2xl text-gray-600 hover:text-blue-600">
                        <i class="fa-solid fa-bell"></i>
                        <c:if test="${unreadCount > 0}">
                            <span class="absolute top-2 right-2 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
                                    ${unreadCount > 9 ? '9+' : unreadCount}
                            </span>
                        </c:if>
                    </button>

                    <div id="MenuNotify"
                         class="hidden absolute right-0 top-full mt-2 w-96 bg-white border border-gray-200 rounded-lg shadow-lg p-3 z-50 text-left">
                        <h4 class="font-bold text-lg mb-2 px-2">Notifications</h4>
                        <div class="max-h-96 overflow-y-auto">
                            <c:choose>
                                <c:when test="${not empty userNotifications}">
                                    <c:forEach var="noti" items="${userNotifications}">
                                        <ul class="h-96 overflow-y-auto">

<%--                                            noti bình thường --%>
                                            <li class="border-b border-gray-500">
                                                <a
                                                        href="${pageContext.request.contextPath}${noti.urlRedirect}"
                                                        class="block p-2  rounded-lg notification-item ${noti.isRead() ? 'bg-white' : 'bg-blue-50'}"
                                                        data-id="${noti.notificationId}"
                                                >
                                                    <p
                                                            class="font-semibold truncate ${noti.isRead() ? 'text-gray-700' : 'text-blue-800'}"
                                                    >
                                                            ${noti.title}
                                                    </p>
                                                    <p class="text-xs text-gray-600 py-1">${noti.message}</p>
                                                </a>
                                            </li>

<%--                                            noti author mời người khác --%>
                                            <li class="border-b border-gray-500">
                                                <a
                                                        href="${pageContext.request.contextPath}${noti.urlRedirect}"
                                                        class="block p-2  rounded-lg notification-item ${noti.isRead() ? 'bg-white' : 'bg-blue-50'}"
                                                        data-id="${noti.notificationId}"
                                                >
                                                    <p
                                                            class="font-semibold truncate ${noti.isRead() ? 'text-gray-700' : 'text-blue-800'}"
                                                    >
                                                            ${noti.title}
                                                    </p>
                                                    <p class="text-xs text-gray-600 py-1">${noti.message}</p>

                                                    <div class="flex justify-end gap-2 text-xs font-semibold">
                                                        <button class="py-1 px-3 bg-sky-200 rounded-md hover:bg-sky-300 text-sky-700 hover:text-black cursor-pointer ">
                                                            Accept
                                                        </button>
                                                        <button class="py-1 px-3 border border-gray-300 rounded-md hover:bg-gray-300 cursor-pointer">
                                                            Cancel
                                                        </button>
                                                    </div>
                                                </a>
                                            </li>
                                        </ul>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-sm text-gray-500 p-2">You have no notifications.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${loginedUser.role == 'staff' || loginedUser.role == 'admin'}">
                <div class="col-span-1"></div>
            </c:if>

            <div class="col-span-2 text-right">
                <button
                        class="bg-gradient-to-r from-[#341661] via-[#491894] to-[#195DA9] font-black text-lg px-3 py-1 rounded-3xl border-2 border-[#E3E346]"
                >
                    <a href="${pageContext.request.contextPath}/register-author"
                       class="bg-gradient-to-r from-[#D2D200] via-[#F8F881] to-[#999400] bg-clip-text text-transparent"
                    >
                        Write Now
                    </a>
                </button>
            </div>
            <div class="col-span-1 relative">
                <button id="BtnAvatar" class="px-4 py-2 flex items-center gap-1">
                    <div class="w-10 h-10 bg-gray-300 rounded-full">
                        <img
                                class="w-10 h-10 rounded-full"
                                src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                        />
                    </div>
                    <i class="fa-solid fa-caret-down"></i>
                </button>

                <div
                        id="MenuAvatar"
                        class="hidden absolute right-0 top-full mt-2 w-50 h-65 bg-white border border-gray-200 rounded-lg shadow-lg p-3 z-50"
                >
                    <div class="text-lg">
                        <div class="mb-2">
                            <h4
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent font-bold"
                            >
                                    ${loginedUser.username}
                            </h4>
                            <p class="text-[#195DA9] text-xs">${loginedUser.role}</p>
                        </div>
                        <hr class="mb-2 border-gray-300"/>
                        <a
                                href="${pageContext.request.contextPath}/profile?userId=${loginedUser.userId}"
                                class="flex items-center gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <i class="fa-solid fa-user"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Profile</span
                            >
                        </a>
                        <a
                                href="${pageContext.request.contextPath}/register-author"
                                class="flex items-center gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <i class="fa-solid fa-pen"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Author</span
                            >
                        </a>
                        <a
                                href="${pageContext.request.contextPath}/library?action=view"
                                class="flex items-center gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <i class="fa-solid fa-bookmark"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Library</span
                            >
                        </a>
                        <a
                                href="${pageContext.request.contextPath}/logout"
                                class="flex items-center gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 text-lg"
                        >
                            <i class="fa-solid fa-right-from-bracket"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Logout</span
                            >
                        </a>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${loginedUser == null}">
            <div class="col-span-3 text-right">
                <button>
                    <a
                            href="${pageContext.request.contextPath}/login"
                            class="inline-block bg-sky-700 text-white py-1 px-3 rounded-lg hover:bg-sky-900"
                    >Login</a
                    >
                </button>
                <button>
                    <a
                            href="${pageContext.request.contextPath}/register"
                            class="inline-block py-1 px-3 rounded-lg hover:bg-neutral-200 mx-4"
                    >Sign Up</a
                    >
                </button>
            </div>
        </c:if>
    </div>
</header>

<script>

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
    }

    const genreButton = document.getElementById('genreButton');
    const genreMenu = document.getElementById('genreMenu');

    genreButton.addEventListener('click', () => {
        genreMenu.classList.toggle('hidden');
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener('click', (e) => {
        if (!genreButton.contains(e.target) && !genreMenu.contains(e.target)) {
            genreMenu.classList.add('hidden');
        }

        if (BtnAvatar && MenuAvatar && !BtnAvatar.contains(e.target) && !MenuAvatar.contains(e.target)) {
            MenuAvatar.classList.add('hidden');
        }

        if (BtnNotify && MenuNotify && !BtnNotify.contains(e.target) && !MenuNotify.contains(e.target)) {
            MenuNotify.classList.add('hidden');
        }
    });

    const BtnAvatar = document.getElementById('BtnAvatar');
    const MenuAvatar = document.getElementById('MenuAvatar');

    if (BtnAvatar) {
        BtnAvatar.addEventListener('click', () => {
            MenuAvatar.classList.toggle('hidden');
        });
    }

    const BtnNotify = document.getElementById('BtnNotify');
    const MenuNotify = document.getElementById('MenuNotify');

    if (BtnNotify) {
        BtnNotify.addEventListener('click', () => {
            MenuNotify.classList.toggle('hidden');
        });
    }

    document.querySelectorAll('.notification-item').forEach(item => {
        item.addEventListener('click', function (e) {
            const notiId = this.getAttribute('data-id');
            const isRead = this.classList.contains('bg-white');
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
                        if (response.ok) {
                            return response.json();
                        }
                        throw new Error('Network response was not ok.');
                    })
                    .then(data => {
                        if (data.success) {
                            window.location.href = originalUrl;
                        } else {
                            window.location.href = originalUrl;
                        }
                    })
                    .catch(err => {
                        console.error('Failed to mark notification as read:', err);
                        window.location.href = originalUrl;
                    });
            }
        });
    });
</script>
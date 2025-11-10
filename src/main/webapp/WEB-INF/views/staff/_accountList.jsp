<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main-content flex-1 px-5 py-3 bg-[#F5F4FA] max-h-full custom-scrollbar flex flex-col m-0">
    <div class="bg-white h-full shadow-lg shadow-gray-400 rounded-2xl px-5 py-4 m-0 flex flex-col h-full">

        <!-- Header -->
        <div class="flex justify-between items-center mb-2">
            <!-- Search & Filter Form -->
            <form method="GET" action="${pageContext.request.contextPath}/account/list" id="filterForm"
                  class="grid grid-cols-1 sm:grid-cols-3 lg:grid-cols-5 gap-4 mb-1">

                <!-- Search Input -->
                <div class="col-span-2 relative">
                    <i class="fas fa-search text-gray-400 absolute top-1/2 left-3 transform -translate-y-1/2"></i>
                    <input type="text" name="search" id="searchInput"
                           placeholder="Search username, full name..."
                           value="${search}"
                           class="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none">
                </div>

                <!-- Role Filter -->
                <div class="col-span-1">
                    <select name="roleFilter" id="roleFilter"
                            class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                        <option value="">All Roles</option>

                        <c:choose>
                            <c:when test="${loginedUser.role eq 'staff'}">
                                <option value="reader" ${roleFilter eq 'reader' ? 'selected' : ''}>Reader</option>
                                <option value="author" ${roleFilter eq 'author' ? 'selected' : ''}>Author</option>
                            </c:when>

                            <c:otherwise>
                                <option value="reader" ${roleFilter eq 'reader' ? 'selected' : ''}>Reader</option>
                                <option value="author" ${roleFilter eq 'author' ? 'selected' : ''}>Author</option>
                                <option value="staff" ${roleFilter eq 'staff' ? 'selected' : ''}>Staff</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
            </form>

            <!-- Add Button - chỉ admin -->
            <c:if test="${loginedUser.role eq 'admin'}">
                <a href="${pageContext.request.contextPath}/account/add"
                   class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors flex items-center gap-2">
                    <i class="fas fa-plus"></i>
                    <span>Add Account</span>
                </a>
            </c:if>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty sessionScope.success}">
            <div class="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
                <i class="fas fa-check-circle mr-2"></i>${sessionScope.success}
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.error}">
            <div class="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
                <i class="fas fa-exclamation-circle mr-2"></i>${sessionScope.error}
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <!-- Table -->
        <div class="overflow-x-auto overflow-y-auto max-h-[70vh] rounded-sm mb-3">
            <table class="min-w-full text-sm text-left">
                <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold sticky top-0 z-10">
                <tr>
                    <th class="px-4 py-3">ID</th>
                    <th class="px-4 py-3">Username</th>
                    <th class="px-4 py-3">Full Name</th>
                    <th class="px-4 py-3">Email</th>
                    <th class="px-4 py-3">Role</th>
                    <th class="px-4 py-3">Status</th>
                    <th class="px-4 py-3">Created At</th>
                    <th class="px-4 py-3 text-center">Actions</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-300">
                <c:forEach var="account" items="${accounts}">
                    <tr class="hover:bg-gray-50">
                        <td class="px-4 py-3">${account.accountId}</td>

                        <td class="px-4 py-3">
                            <a href="${pageContext.request.contextPath}/account/detail?accountId=${account.accountId}&role=${account.role}"
                               class="font-medium text-blue-600 hover:text-blue-800 hover:underline">
                                    ${account.username}
                            </a>
                        </td>

                        <td class="px-4 py-3 font-semibold text-gray-800">${account.fullName}</td>

                        <td class="px-4 py-3 text-gray-700">
                            <c:choose>
                                <c:when test="${not empty account.email}">
                                    ${account.email}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-gray-400 italic">N/A</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td class="px-4 py-3">
                            <span class="px-2 py-1 rounded-full text-xs font-semibold
                                <c:choose>
                                    <c:when test="${account.role eq 'staff'}">bg-indigo-100 text-indigo-700</c:when>
                                    <c:when test="${account.role eq 'author'}">bg-blue-100 text-blue-700</c:when>
                                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                                </c:choose>">
                                <i class="fas fa-user-tag mr-1"></i>${account.role}
                            </span>
                        </td>

                        <td class="px-4 py-3">

                            <c:choose>
                                <c:when test="${not empty account.status}">
                                      <span class="px-2 py-1 rounded-full text-xs font-semibold
                                <c:choose>
                                    <c:when test="${account.status eq 'active'}">bg-green-100 text-green-700</c:when>
                                    <c:when test="${account.status eq 'banned'}">bg-red-100 text-red-700</c:when>
                                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                                </c:choose>">
                                              ${account.status}
                                      </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-gray-400 italic">N/A</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td class="px-4 py-3 text-gray-700">${account.createdAt}</td>

                        <td class="px-4 py-3 text-center">
                            <div class="relative flex justify-end gap-2 items-center">
                                <!-- Detail Button -->
                                <a href="${pageContext.request.contextPath}/account/detail?accountId=${account.accountId}&role=${account.role}"
                                   class="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-blue-50 hover:border-blue-500 transition-colors">
                                    <i class="fa-regular fa-eye mr-1"></i>Detail
                                </a>

                                <!-- ADMIN dropdown -->
                                <!-- ADMIN & STAFF DROPDOWN -->
                                <c:if test="${loginedUser.role eq 'admin' || loginedUser.role eq 'staff'}">
                                    <div class="relative">
                                        <button type="button" class="text-gray-500 hover:text-gray-700 px-2" data-bs-toggle="dropdown">
                                            <i class="fas fa-ellipsis-v"></i>
                                        </button>

                                        <ul class="absolute right-0 bottom-1 w-44 bg-white border border-gray-200 rounded-lg shadow-lg hidden z-20">

                                            <!-- Edit (Admin only) -->
                                            <c:if test="${loginedUser.role eq 'admin' and account.accountType eq 'staff'}">
                                                <li>
                                                    <a href="${pageContext.request.contextPath}/account/edit?staffId=${account.accountId}"
                                                       class="block px-4 py-2 text-gray-700 hover:bg-gray-50 flex items-center gap-2">
                                                        <i class="fas fa-edit"></i>Edit
                                                    </a>
                                                </li>
                                            </c:if>

                                            <!-- Ban/Unban (Admin & Staff, only for user accounts) -->
                                            <c:if test="${account.accountType eq 'user'}">
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/account/update-status" method="post">
                                                        <input type="hidden" name="userId" value="${account.accountId}">
                                                            <c:if test="${account.status eq 'active'}">
                                                                <input type="hidden" name="status" value="banned">
                                                                <button type="submit"
                                                                        class="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 flex items-center gap-2"
                                                                        onclick="return confirm('Ban this account?')">
                                                                    <i class="fas fa-ban"></i>Ban
                                                                </button>
                                                            </c:if>
                                                            <c:if test="${account.status eq 'banned'}">
                                                                <input type="hidden" name="status" value="active">
                                                                <button type="submit"
                                                                        class="w-full text-left px-4 py-2 text-green-600 hover:bg-green-50 flex items-center gap-2"
                                                                        onclick="return confirm('Unban this account?')">
                                                                    <i class="fas fa-check-circle"></i>Unban
                                                                </button>
                                                            </c:if>
                                                    </form>
                                                </li>
                                            </c:if>

                                            <!-- Delete (Admin only) -->
                                            <c:if test="${loginedUser.role eq 'admin'}">
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/account/delete" method="post">
                                                        <input type="hidden" name="accountId" value="${account.accountId}">
                                                        <input type="hidden" name="accountType" value="${account.accountType}">
                                                        <button type="submit"
                                                                class="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 flex items-center gap-2"
                                                                onclick="return confirm('Delete this account permanently?')">
                                                            <i class="fas fa-trash"></i>Delete
                                                        </button>
                                                    </form>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="flex justify-between items-center mt-auto">
            <p class="text-gray-500 text-sm">Total: ${size}</p>
            <c:if test="${totalPage > 1}">
                <div class="flex justify-end items-center gap-1 text-sm px-9">

                    <a href="${pageContext.request.contextPath}/account/list?currentPage=${currentPage-1}&roleFilter=${roleFilter}"
                       class="page-link">
                        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                                <c:if test="${currentPage == 1}">disabled</c:if>>
                            &lt;&lt;
                        </button>
                    </a>

                    <c:forEach var="i" begin="${1}" end="${totalPage}">
                        <a href="${pageContext.request.contextPath}/account/list?currentPage=${i}&roleFilter=${roleFilter}">
                            <button class="border rounded-md px-2 py-1
                            ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                                    ${i}
                            </button>
                        </a>
                    </c:forEach>

                    <a href="${pageContext.request.contextPath}/account/list?currentPage=${currentPage+1}&roleFilter=${roleFilter}"
                       class="page-link">
                        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                                <c:if test="${currentPage == totalPage}">disabled</c:if>>
                            &gt;&gt;
                        </button>
                    </a>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script>
    document.getElementById('searchInput').addEventListener('keypress', function(event) {
        if (event.keyCode === 13) document.getElementById('filterForm').submit();
    });

    document.getElementById('roleFilter').addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });

    const dropdownButtons = document.querySelectorAll('[data-bs-toggle="dropdown"]');
    dropdownButtons.forEach(button => {
        const menu = button.parentElement.querySelector("ul");
        button.addEventListener("click", e => {
            e.stopPropagation();
            closeAllDropdowns();
            menu.classList.toggle("hidden");
        });
    });

    document.addEventListener("click", closeAllDropdowns);
    function closeAllDropdowns() {
        document.querySelectorAll('.relative ul').forEach(menu => menu.classList.add("hidden"));
    }
</script>

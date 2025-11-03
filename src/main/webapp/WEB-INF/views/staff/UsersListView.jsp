<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Content -->
<div class="main-content p-5 bg-[#F5F4FA] overflow-y-auto max-h-[90vh] px-2 custom-scrollbar">
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl p-5">

        <!-- Search & Filter Form -->
        <form method="GET" action="${pageContext.request.contextPath}/account" id="filterForm"
              class="grid grid-cols-1 sm:grid-cols-3 lg:grid-cols-6 gap-4 mb-6">
            <input type="hidden" name="action" value="viewAccountList">
            <input type="hidden" name="page" value="1">

            <!-- Ô tìm kiếm -->
            <div class="col-span-2 relative">
                <i class="fas fa-search text-gray-400 absolute top-1/2 left-3 transform -translate-y-1/2"></i>
                <input type="text" name="search" id="searchInput" placeholder="Search by Name, Email..."
                       value="${search}"
                       class="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none">
            </div>

            <!-- Lọc role -->
            <div class="col-span-1">
                <select name="filterByRole" id="filterByRole"
                        class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">All role</option>
                    <option value="reader" ${filterByRole eq 'reader' ? 'selected' : ''}>Reader</option>
                    <option value="author" ${filterByRole eq 'author' ? 'selected' : ''}>Author</option>
                </select>
            </div>
        </form>

        <!-- Table -->
        <div class="overflow-x-auto rounded-sm ">
            <table class="min-w-full text-sm text-left">
                <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold">
                <tr>
                    <th class="px-4 py-3">ID</th>
                    <th class="px-4 py-3">Name</th>
                    <th class="px-4 py-3">Role</th>
                    <th class="px-4 py-3">Email</th>
                    <th class="px-4 py-3">Status</th>
                    <th class="px-4 py-3">Create Date</th>
                    <th class="px-4 py-4 text-center"></th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-300">
                <c:forEach var="account" items="${accountList}">
                    <tr class="hover:bg-gray-50">
                        <td class="px-4 py-3">${account.id}</td>
                        <td class="px-4 py-3 flex items-center gap-3">
                            <img src="${pageContext.request.contextPath}/img/shared/imgUser.png" alt="${account.fullName}"
                                 class="w-10 h-10 rounded-full object-cover">
                            <div>
                                <div class="font-semibold text-gray-800">${account.fullName}</div>
                                <small class="text-gray-500">${account.username}</small>
                            </div>
                        </td>
                        <td class="px-4 py-3">
                            <span class="px-2 py-1 rounded-full text-xs font-semibold
                                ${account.role eq 'user' ? 'bg-blue-100 text-blue-700' : 'bg-yellow-100 text-yellow-700'}">
                                    ${account.role}
                            </span>
                        </td>
                        <td class="px-4 py-3 text-gray-700">${account.email}</td>
                        <td class="px-4 py-3">
                            <span class="px-2 py-1 rounded-full text-xs font-semibold
                                ${account.status eq 'active' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">
                                    ${account.status}
                            </span>
                        </td>
                        <td class="px-4 py-3 text-gray-700">
                            ${account.createdAt}
                        </td>
                        <td class="px-4 py-3 text-center">
                            <div class="relative flex justify-end gap-2 text-left">

                                    <a href="${pageContext.request.contextPath}/account?action=viewDetail&id=${account.id}"
                                       class="block px-2 py-2 text-gray-700 border border-gray-300  rounded-lg hover:bg-blue-100">
                                        <i class="fa-regular fa-eye mr-2"></i>Detail
                                    </a>

                                <button type="button"
                                        class="text-gray-500 hover:text-gray-700"
                                        data-bs-toggle="dropdown">
                                    <i class="fas fa-ellipsis-v"></i>
                                </button>

                                <ul class="absolute right-0 mt-2 w-44 bg-white border border-gray-200 rounded-lg shadow-lg hidden group-hover:block">

                                    <c:if test="${sessionScope.role eq 'staff' or sessionScope.role eq 'admin'}">
                                        <li>
                                            <a href="${pageContext.request.contextPath}/account?action=banAccount&id=${account.id}&reason=VIOLATION"
                                               onclick="return confirm('Xác nhận cấm tài khoản?')"
                                               class="block px-4 py-2 text-red-600 hover:bg-red-50">
                                                <i class="fas fa-ban mr-2"></i>Ban
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.role eq 'admin'}">
                                        <li>
                                            <a href="${pageContext.request.contextPath}/account?action=showEditAccount&id=${account.id}"
                                               class="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                                                <i class="fas fa-edit mr-2"></i>Edit
                                            </a>
                                        </li>
                                        <li>
                                            <a href="${pageContext.request.contextPath}/account?action=deleteAccount&id=${account.id}"
                                               onclick="return confirm('Xác nhận xóa tài khoản?')"
                                               class="block px-4 py-2 text-red-600 hover:bg-red-50">
                                                <i class="fas fa-trash mr-2"></i>Delete
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- Pagination -->
        <div class="flex justify-end items-center mb-0 gap-1 pt-2 text-sm px-9">
            <a href="${pageContext.request.contextPath}/account?totalPage=${totalPage}&currentPage=${currentPage-1}&sizePage=${sizePage}"
               class="page-link">
                <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                        <c:if test="${currentPage == 1}">disabled</c:if>>
                    &lt;&lt;
                </button>
            </a>

            <c:if test="${currentPage > 3}">
                <a href="${pageContext.request.contextPath}/account?totalPage=${totalPage}&currentPage=${1}&sizePage=${sizePage}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">1</button>
                </a>
                <span class="px-2 py-1">...</span>
            </c:if>

            <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                       end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
                <a href="${pageContext.request.contextPath}/account?totalPage=${totalPage}&currentPage=${i}&sizePage=${sizePage}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1
                       ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                            ${i}
                    </button>
                </a>
            </c:forEach>

            <c:if test="${currentPage < totalPage - 2}">
                <span class="px-2 py-1">...</span>
                <a href="${pageContext.request.contextPath}/account?totalPage=${totalPage}&currentPage=${totalPage}&sizePage=${sizePage}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">${totalPage}</button>
                </a>
            </c:if>

            <a href="${pageContext.request.contextPath}/account?totalPage=${totalPage}&currentPage=${currentPage+1}&sizePage=${sizePage}"
               class="page-link">
                <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                        <c:if test="${currentPage == totalPage}">disabled</c:if>>
                    &gt;&gt;
                </button>
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Submit form khi nhấn Enter trong ô search
    document.getElementById('searchInput').addEventListener('keypress', function (event) {
        if (event.keyCode === 13) {
            document.getElementById('filterForm').submit();
        }
    });

    // Tự động submit khi thay đổi role
    document.getElementById('filterByRole').addEventListener('change', function () {
        document.getElementById('filterForm').submit();
    });

    document.addEventListener("DOMContentLoaded", function () {
        //Lay all btn in menu (ellipsis)
        const dropdownButtons = document.querySelectorAll('[data-bs-toggle="dropdown"]');

        dropdownButtons.forEach(button => {
            const menu = button.parentElement.querySelector("ul");

            //When click on ellipsis -> toggle show menu
            button.addEventListener("click", (e) => {
                e.stopPropagation(); // Prevent the event from spreading
                closeAllDropdowns();
                menu.classList.toggle("hidden");
            })
        })

        //When click on out then hidden all menu
        document.addEventListener("click", () => {
            closeAllDropdowns();
        })

        //Function close all menu
        function closeAllDropdowns() {
            document.querySelectorAll('.relative ul').forEach(menu => {
                menu.classList.add("hidden");
            })
        }
    })
</script>
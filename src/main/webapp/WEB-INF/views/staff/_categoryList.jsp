<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main-content flex-1 px-5 py-3 bg-[#F5F4FA] overflow-y-auto max-h-full custom-scrollbar">
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-5 py-3 flex flex-col h-full">

        <!-- Header -->
        <div class="flex justify-between items-center mb-3 mt-1">
            <!-- Search -->
            <form method="GET" action="${pageContext.request.contextPath}/category/list"
                  class="flex gap-3 items-center" id="filterForm">
                <div class="relative">
                    <i class="fas fa-search text-gray-400 absolute top-1/2 left-3 transform -translate-y-1/2"></i>
                    <input type="text" name="search" id="searchInput"
                           placeholder="Search category name..."
                           value="${search}"
                           class="w-64 border border-gray-300 rounded-lg pl-10 pr-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none">
                </div>
            </form>

            <!-- Add Button -->
            <c:if test="${loginedUser.role eq 'admin'}">
                <button id="openAddModal"
                        class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition flex items-center gap-2">
                    <i class="fas fa-plus"></i>
                    <span>Add Category</span>
                </button>
            </c:if>
        </div>

        <!-- Alerts -->
        <c:if test="${not empty sessionScope.success}">
            <div class="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg text-green-700">
                <i class="fas fa-check-circle mr-2"></i>${sessionScope.success}
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.error}">
            <div class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700">
                <i class="fas fa-exclamation-circle mr-2"></i>${sessionScope.error}
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <!-- ✅ Table -->
        <div class="overflow-x-auto rounded-sm mb-3">
            <table class="min-w-full text-sm text-left">
                <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold">
                <tr>
                    <th class="px-4 py-3">No.</th>
                    <th class="px-4 py-3">Name</th>
                    <th class="px-4 py-3">Created At</th>
                    <th class="px-4 py-3 text-center w-32">Actions</th>
                </tr>
                </thead>

                <tbody class="divide-y divide-gray-300">
                <c:forEach var="category" items="${categories}" varStatus="loop">
                    <!-- Normal row -->
                    <tr class="hover:bg-gray-50 transition" data-id="${category.categoryId}">
                        <td class="px-4 py-3">
                                ${(currentPage - 1) * sizePage + loop.index + 1}
                        </td>
                        <td class="px-4 py-3 font-semibold text-gray-800">${category.name}</td>
                        <td class="px-4 py-3 text-gray-700">${category.createdAt}</td>
                        <td class="px-1 py-3 text-center">
                            <div class="relative flex justify-between gap-2 items-center">
                                <button type="button"
                                        class="px-2 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-blue-100 detail-btn"
                                        data-id="${category.categoryId}">
                                    <i class="fa-regular fa-eye mr-1"></i>Detail
                                </button>

                                <!-- Dropdown Toggle -->
                                <button type="button" class="text-gray-500 hover:text-gray-700 px-2"
                                        data-bs-toggle="dropdown">
                                    <i class="fas fa-ellipsis-v"></i>
                                </button>

                                <!-- Dropdown -->
                                <ul class="absolute right-0 top-8 w-40 bg-white border border-gray-200 rounded-lg shadow-lg hidden z-20">
                                    <c:if test="${loginedUser.role eq 'admin'}">
                                        <li>
                                            <button type="button"
                                                    class="w-full text-left px-4 py-2 text-yellow-600 hover:bg-yellow-50 flex items-center gap-2 edit-btn"
                                                    data-id="${category.categoryId}"
                                                    data-name="${category.name}"
                                                    data-description="${category.description}">
                                                <i class="fas fa-edit"></i> Edit
                                            </button>
                                        </li>
                                        <li>
                                            <form action="${pageContext.request.contextPath}/category/delete" method="post"
                                                  onsubmit="return confirm('Delete this category permanently?');">
                                                <input type="hidden" name="id" value="${category.categoryId}">
                                                <button type="submit"
                                                        class="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 flex items-center gap-2">
                                                    <i class="fas fa-trash"></i> Delete
                                                </button>
                                            </form>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </td>
                    </tr>

                    <!-- Description row (hidden by default) -->
                    <tr id="desc-${category.categoryId}" class="hidden bg-gray-50">
                        <td colspan="4" class="px-6 py-3 text-gray-700">
                            <strong>Description:</strong>
                            <c:choose>
                                <c:when test="${not empty category.description}">
                                    ${category.description}
                                </c:when>
                                <c:otherwise>
                                    <span class="italic text-gray-400">No description available</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="flex justify-between items-center mt-auto">
            <p class="text-gray-500 text-sm">Total: ${size}</p>
        </div>
    </div>
</div>

<!-- ✅ Modal Template -->
<div id="categoryModal"
     class="hidden fixed inset-0 z-50 bg-black/40 backdrop-blur-sm items-center justify-center transition-opacity duration-200">
    <div class="bg-white w-96 rounded-2xl shadow-lg p-6 relative animate-fadeIn">
        <h2 id="modalTitle" class="text-lg font-semibold mb-3">Add New Category</h2>

        <form id="categoryForm" method="post" action="">
            <input type="hidden" name="id" id="categoryId">

            <div class="mb-3">
                <label class="block text-gray-700 font-medium mb-1">Name</label>
                <input type="text" name="name" id="categoryName" required
                       class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none">
            </div>

            <div class="mb-4">
                <label class="block text-gray-700 font-medium mb-1">Description</label>
                <textarea name="description" id="categoryDescription"
                          class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none"></textarea>
            </div>

            <div class="flex justify-end gap-3">
                <button type="button" id="closeModal"
                        class="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300">Cancel</button>
                <button type="submit"
                        class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">Save</button>
            </div>
        </form>
    </div>
</div>

<!-- ✅ Script (vẫn giữa file, như bạn yêu cầu) -->
<script>
    // ===== Dropdown toggle =====
    document.querySelectorAll('[data-bs-toggle="dropdown"]').forEach(button => {
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

    // ===== Toggle Description =====
    document.querySelectorAll('.detail-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.dataset.id;
            const descRow = document.getElementById('desc-' + id);
            descRow.classList.toggle('hidden');
            closeAllDropdowns();
        });
    });

    // ===== Modal Handling =====
    const modal = document.getElementById('categoryModal');
    const closeBtn = document.getElementById('closeModal');
    const form = document.getElementById('categoryForm');
    const modalTitle = document.getElementById('modalTitle');
    const nameField = document.getElementById('categoryName');
    const descField = document.getElementById('categoryDescription');
    const idField = document.getElementById('categoryId');

    // Open Add Modal
    const addBtn = document.getElementById('openAddModal');
    if (addBtn) {
        addBtn.addEventListener('click', () => {
            modalTitle.textContent = "Add New Category";
            form.action = "${pageContext.request.contextPath}/category/insert";
            nameField.value = "";
            descField.value = "";
            idField.value = "";
            showModal();
        });
    }

    // Open Edit Modal
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            modalTitle.textContent = "Edit Category";
            form.action = "${pageContext.request.contextPath}/category/update";
            idField.value = btn.dataset.id;
            nameField.value = btn.dataset.name;
            descField.value = btn.dataset.description || "";
            showModal();
            closeAllDropdowns();
        });
    });

    // Show & Hide Modal Functions
    function showModal() {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }

    function hideModal() {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    }

    if (closeBtn) closeBtn.addEventListener('click', hideModal);
    modal.addEventListener('click', e => {
        if (e.target === modal) hideModal();
    });
</script>

<style>
    /* Hiệu ứng fadeIn nhẹ khi mở modal */
    @keyframes fadeIn {
        from { opacity: 0; transform: scale(0.97); }
        to { opacity: 1; transform: scale(1); }
    }
    .animate-fadeIn {
        animation: fadeIn 0.25s ease-in-out;
    }
</style>

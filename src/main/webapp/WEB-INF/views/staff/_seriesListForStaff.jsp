<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 4:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page buffer="32kb" autoFlush="true" %>
<!-- Content -->
<div class="main-content flex-1 px-5 py-3 bg-[#F5F4FA] overflow-y-auto max-h-full custom-scrollbar ">
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-5 py-2 flex flex-col h-full">
        <!-- Search & Filter Form -->
        <form method="GET" action="${pageContext.request.contextPath}/series/list" id="filterForm"
              class="grid grid-cols-1 sm:grid-cols-3 lg:grid-cols-6 gap-4 mb-6">
            <!-- Ô tìm kiếm -->
            <div class="col-span-2 relative">
                <i class="fas fa-search text-gray-400 absolute top-1/2 left-3 transform -translate-y-1/2"></i>
                <input type="text" name="search" id="searchInput" placeholder="Search series, chapter..."
                       value="${search}"
                       class="w-full border border-gray-300 rounded-lg pl-10 pr-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none">
            </div>

            <!-- Lọc status -->
            <div class="col-span-1">
                <select name="filterByStatus" id="filterByStatus"
                        class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">All status</option>
                    <option value="approved" ${filterByStatus eq 'approved' ? 'selected' : ''}>Approved</option>
                    <option value="pending" ${filterByStatus eq 'pending' ? 'selected' : ''}>Pending</option>
                    <option value="rejected" ${filterByStatus eq 'rejected' ? 'selected' : ''}>Rejected</option>
                </select>
            </div>
        </form>

        <!-- Table -->
        <div class="overflow-x-auto rounded-sm mb-3">
            <table class="min-w-full text-sm text-left">
                <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold">
                <tr>
                    <th class="px-4 py-3">ID</th>
                    <th class="px-4 py-3">Cover</th>
                    <th class="px-4 py-3">Title</th>
                    <th class="px-4 py-3">Categories</th>
                    <th class="px-4 py-3">Avg Rating</th>
                    <th class="px-4 py-3">Total Chapters</th>
                    <th class="px-4 py-3">Status</th>
                    <th class="px-4 py-3">Created At</th>
                    <th class="px-4 py-4 text-center">Actions</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-300">
                <c:forEach var="series" items="${seriesList}">
                    <tr class="hover:bg-gray-50">
                        <td class="px-4 py-3">${series.seriesId}</td>
                        <td class="px-4 py-3">
                            <img src="${pageContext.request.contextPath}/${series.coverImgUrl}" alt="${series.title}"
                                 class="w-10 h-12 rounded object-cover">
                        </td>
                        <td class="px-4 py-3 font-semibold text-gray-800">${series.title}</td>
                        <td class="px-4 py-3">
                                <span class="border border-gray-200 text-xs px-2 rounded-full text-gray-600 bg-gray-100 mr-1">${series.categoryList.get(0).name}</span>
                        </td>
                        <td class="px-4 py-3 text-gray-700">
                            <div class="flex items-center">
                                <i class="fa-regular fa-star text-yellow-500 mr-1"></i>
                                <fmt:formatNumber value="${series.avgRating}" type="number" maxFractionDigits="1" minFractionDigits="1"/> (${series.totalRating})
                            </div>
                        </td>
                        <td class="px-4 py-3 text-gray-700">${series.totalChapters} Chapters</td>
                        <td class="px-4 py-3">
                            <span class="px-2 py-1 rounded-full text-xs font-semibold
                                <c:choose>
                                    <c:when test="${series.approvalStatus == 'approved'}">bg-green-100 text-green-700</c:when>
                                    <c:when test="${series.approvalStatus == 'rejected'}">bg-red-100 text-red-700</c:when>
                                    <c:when test="${series.approvalStatus == 'pending'}">bg-yellow-100 text-yellow-700</c:when>
                                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                                </c:choose>">
                                    ${series.approvalStatus}
                            </span>
                        </td>
                        <td class="px-4 py-3 text-gray-700">
                              ${series.createdAt}
                        </td>
                        <td class="px-4 py-3 text-center">
                            <div class="relative flex justify-end gap-2 text-left">
                                <!-- Detail Button -->
                                <a href="${pageContext.request.contextPath}/series/detail?seriesId=${series.seriesId}"
                                   class="block px-3 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-blue-100">
                                    <i class="fa-regular fa-eye mr-2"></i>Detail
                                </a>

                                <!-- Dropdown Toggle -->
                                <button type="button"
                                        class="text-gray-500 hover:text-gray-700"
                                        data-bs-toggle="dropdown">
                                    <i class="fas fa-ellipsis-v"></i>
                                </button>

                                <!-- Dropdown Menu -->
                                <ul class="absolute right-0 bottom-1 w-44 bg-white border border-gray-200 rounded-lg shadow-lg hidden">
                                    <!-- Approve / Reject logic -->
                                    <c:choose>
                                        <c:when test="${series.approvalStatus == 'pending'}">
                                            <li>
                                                <form action="${pageContext.request.contextPath}/series/approve" method="post">
                                                    <input type="hidden" name="seriesId" value="${series.seriesId}">
                                                    <input type="hidden" name="approveStatus" value="approved">
                                                    <button type="submit"
                                                            class="w-full text-left flex items-center gap-2 px-4 py-2 text-green-600 hover:bg-green-50">
                                                        <i class="fa-solid fa-check"></i> Approve
                                                    </button>
                                                </form>
                                            </li>
                                            <li>
                                                <form action="${pageContext.request.contextPath}/series/approve" method="post">
                                                    <input type="hidden" name="seriesId" value="${series.seriesId}">
                                                    <input type="hidden" name="approveStatus" value="rejected">
                                                    <button type="submit"
                                                            class="w-full text-left flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50">
                                                        <i class="fa-solid fa-xmark"></i> Reject
                                                    </button>
                                                </form>
                                            </li>
                                        </c:when>

                                        <c:when test="${series.approvalStatus == 'approved'}">
                                            <li>
                                                <form action="${pageContext.request.contextPath}/series/approve" method="post">
                                                    <input type="hidden" name="seriesId" value="${series.seriesId}">
                                                    <input type="hidden" name="approveStatus" value="rejected">
                                                    <button type="submit"
                                                            class="w-full text-left flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50">
                                                        <i class="fa-solid fa-xmark"></i> Reject
                                                    </button>
                                                </form>
                                            </li>
                                        </c:when>
                                    </c:choose>

                                    <!-- Admin-only: Delete -->
                                    <c:if test="${sessionScope.role eq 'admin'}">
                                        <li>
                                            <a href="${pageContext.request.contextPath}/series?action=delete&seriesId=${series.seriesId}"
                                               onclick="return confirm('Confirm delete this series?')"
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
        <div class="flex justify-between items-center mt-auto">
            <div class="mb-4">
                <p class="text-gray-500 text-sm">Total: ${size}</p>
            </div>
            <!-- Pagination -->
            <div class="flex justify-end items-center mb-0 gap-1 text-sm px-9">
                <c:if test="${totalPage > 1}">
                <a href="${pageContext.request.contextPath}/series/list?currentPage=${currentPage-1}&filterByStatus=${filterByStatus}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                            <c:if test="${currentPage == 1}">disabled</c:if>>
                        &lt;&lt;
                    </button>
                </a>

                <c:if test="${currentPage > 3}">
                    <a href="${pageContext.request.contextPath}/series/list?currentPage=${1}&filterByStatus=${filterByStatus}"
                       class="page-link">
                        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">1</button>
                    </a>
                    <span class="px-2 py-1">...</span>
                </c:if>

                <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                           end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
                    <a href="${pageContext.request.contextPath}/series/list?currentPage=${i}&filterByStatus=${filterByStatus}"
                       class="page-link">
                        <button class="border rounded-md px-2 py-1
                       ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                                ${i}
                        </button>
                    </a>
                </c:forEach>

                <c:if test="${currentPage < totalPage - 2}">
                    <span class="px-2 py-1">...</span>
                    <a href="${pageContext.request.contextPath}/series/list?currentPage=${totalPage}&filterByStatus=${filterByStatus}"
                       class="page-link">
                        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">${totalPage}</button>
                    </a>
                </c:if>

                <a href="${pageContext.request.contextPath}/series/list?currentPage=${currentPage+1}&filterByStatus=${filterByStatus}"
                   class="page-link">
                    <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                            <c:if test="${currentPage == totalPage}">disabled</c:if>>
                        &gt;&gt;
                    </button>
                </a>
                </c:if>
            </div>
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

    // Tự động submit khi thay đổi status
    document.getElementById('filterByStatus').addEventListener('change', function () {
        document.getElementById('filterForm').submit();
    });

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


</script>

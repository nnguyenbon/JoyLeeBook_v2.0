<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Content -->
<div class="main-content flex-1 px-5 py-3 bg-[#F5F4FA] overflow-y-auto max-h-[90vh] custom-scrollbar">
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-5 py-2 flex flex-col h-full">
        <div class="flex items-center gap-5">
            <!-- Tabs Header -->
            <div class="border-b border-gray-200 flex items-center gap-5 mb-3">
                <a href="${pageContext.request.contextPath}/report/list?type=chapter&filterByStatus=pending"
                   id="tab-chapter"
                   class="tab-btn text-xl ${type == 'chapter' ? 'text-[#195DA9] border-b-4 border-[#195DA9]' : 'text-gray-500 border-b-4 border-white hover:text-[#195DA9]'} py-1">
                    Chapter Report
                </a>
                <a href="${pageContext.request.contextPath}/report/list?type=comment&filterByStatus=pending"
                   id="tab-comment"
                   class="tab-btn text-xl ${type == 'comment' ? 'text-[#195DA9] border-b-4 border-[#195DA9]' : 'text-gray-500 border-b-4 border-white hover:text-[#195DA9]'} py-1">
                    Comment Report
                </a>
            </div>
            <!-- Search & Filter Form -->
            <form method="GET" action="${pageContext.request.contextPath}/report/list" id="filterForm" class="mb-3">
                <!-- Lọc status -->
                <div class="col-span-1">
                    <select name="filterByStatus" id="filterByStatus"
                            class="w-full border border-gray-300 rounded-lg py-2 px-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                        <option value="">All status</option>
                        <option value="resolved" ${statusFilter eq 'resolved' ? 'selected' : ''}>Resolved</option>
                        <option value="rejected" ${statusFilter eq 'rejected' ? 'selected' : ''}>Rejected</option>
                        <option value="pending" ${statusFilter eq 'pending' ? 'selected' : ''}>Pending</option>
                    </select>
                </div>
            </form>
        </div>

        <!-- Table -->
        <div class=" rounded-sm mb-3 ">
            <table class="min-w-full text-sm text-left">
                <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold">
                <tr>
                    <th class="px-4 py-3">ID</th>
                    <c:choose>
                        <c:when test="${type == 'chapter'}">
                            <th class="px-4 py-3">Chapter</th>
                        </c:when>
                        <c:otherwise>
                            <th class="px-4 py-3">Comment</th>
                        </c:otherwise>
                    </c:choose>
                    <th class="px-4 py-3">Reporter</th>
                    <th class="px-4 py-3">Reason</th>
                    <th class="px-4 py-3">Status</th>
                    <th class="px-4 py-3">Staff</th>
                    <th class="px-4 py-3">Created At</th>
                    <th class="px-4 py-4 text-center">Actions</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-300">
                <c:forEach var="report" items="${reportList}">
                    <tr class="hover:bg-gray-50">
                        <td class="px-4 py-3">${report.reportId}</td>
                        <td class="px-4 py-3">
                            <c:choose>
                                <c:when test="${type == 'chapter'}">
                                    <div class="flex flex-col">
                                        <p class="font-semibold text-gray-800">Chapter ${report.chapterNumber}: ${report.chapterTitle}</p>
                                        <p class="text-gray-500 text-sm">Series: ${report.seriesName}</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="flex flex-col">
                                        <p class="font-semibold text-gray-800">By ${report.commenterUsername}</p>
                                        <p class="text-gray-500 text-sm">${report.commentContent}</p>
                                        <p class="text-gray-400 text-xs">In: ${report.chapterTitle}</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="px-4 py-3 text-gray-700">${report.reporterUsername}</td>
                        <td class="px-4 py-3">
                            <p class="text-gray-600 text-sm max-w-xs truncate" title="${report.reason}">
                                    ${report.reason}
                            </p>
                        </td>
                        <td class="px-4 py-3">
                            <span class="px-2 py-1 rounded-full text-xs font-semibold
                                <c:choose>
                                    <c:when test="${report.status == 'resolved'}">bg-green-100 text-green-700</c:when>
                                    <c:when test="${report.status == 'pending'}">bg-yellow-100 text-yellow-700</c:when>
                                    <c:when test="${report.status == 'rejected'}">bg-red-100 text-red-700</c:when>
                                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                                </c:choose>">
                                    ${report.status}
                            </span>
                        </td>
                        <td class="px-4 py-3 text-gray-700">
                            <c:choose>
                                <c:when test="${not empty report.staffUsername}">
                                    ${report.staffUsername}
                                </c:when>
                                <c:otherwise>
                                    <span class="text-gray-400 italic">Unassigned</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="px-4 py-3 text-gray-700">
                            ${report.createdAt}
                        </td>
                        <td class="px-4 py-3 text-center">
                            <div class="relative flex justify-end gap-2 text-left">
                                <a href="${pageContext.request.contextPath}/report/detail?reportId=${report.reportId}&type=${type}"
                                   class="block px-2 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-blue-100">
                                    <i class="fa-regular fa-eye mr-2"></i>Detail
                                </a>

                                <c:if test="${report.status == 'pending'}">
                                    <button type="button"
                                            class="text-gray-500 hover:text-gray-700"
                                            data-bs-toggle="dropdown">
                                        <i class="fas fa-ellipsis-v"></i>
                                    </button>

                                    <ul class="absolute right-0 mt-2 w-44 bg-white border border-gray-200 rounded-lg shadow-lg hidden z-10">
                                        <li>
                                            <form method="post" action="${pageContext.request.contextPath}/report/handle" class="w-full">
                                                <input type="hidden" name="reportId" value="${report.reportId}">
                                                <input type="hidden" name="type" value="${type}">
                                                <input type="hidden" name="status" value="approved">
                                                <input type="hidden" name="chapterId" value="${report.chapterId}">
                                                <button type="submit" class="block w-full text-[#42CC75] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                                                    <i class="fas fa-check mr-2 text-green-500"></i>Resolve
                                                </button>
                                            </form>
                                        </li>
                                        <li>
                                            <form method="post" action="${pageContext.request.contextPath}/report/handle" class="w-full">
                                                <input type="hidden" name="reportId" value="${report.reportId}">
                                                <input type="hidden" name="type" value="${type}">
                                                <input type="hidden" name="status" value="rejected">
                                                <input type="hidden" name="chapterId" value="${report.chapterId}">
                                                <button type="submit" class="block w-full text-[#E23636] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                                                    <i class="fas fa-times mr-2 text-red-500"></i>Reject
                                                </button>
                                            </form>
                                        </li>
                                    </ul>
                                </c:if>
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
                    <a href="${pageContext.request.contextPath}/report?action=list&type=${type}&currentPage=${currentPage-1}&sizePage=${sizePage}"
                       class="page-link">
                        <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                                <c:if test="${currentPage == 1}">disabled</c:if>>
                            &lt;&lt;
                        </button>
                    </a>

                    <c:if test="${currentPage > 3}">
                        <a href="${pageContext.request.contextPath}/report?action=list&type=${type}&currentPage=${1}&sizePage=${sizePage}"
                           class="page-link">
                            <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">1</button>
                        </a>
                        <span class="px-2 py-1">...</span>
                    </c:if>

                    <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                               end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
                        <a href="${pageContext.request.contextPath}/report?action=list&type=${type}&currentPage=${i}&sizePage=${sizePage}"
                           class="page-link">
                            <button class="border rounded-md px-2 py-1
                       ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}">
                                    ${i}
                            </button>
                        </a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPage - 2}">
                        <span class="px-2 py-1">...</span>
                        <a href="${pageContext.request.contextPath}/report?action=list&type=${type}&currentPage=${totalPage}&sizePage=${sizePage}"
                           class="page-link">
                            <button class="border rounded-md px-2 py-1 hover:bg-gray-100 bg-white">${totalPage}</button>
                        </a>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/report?action=list&type=${type}&currentPage=${currentPage+1}&sizePage=${sizePage}"
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

    function closeAllDropdowns() {
        document.querySelectorAll('.relative ul').forEach(menu => {
            menu.classList.add("hidden");
        })
    }
</script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Content -->
<div class="main-content flex-1 px-5 py-3 overflow-y-auto max-h-[100vh] custom-scrollbar bg-[#F5F4FA]">
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-6 py-4">

        <!-- Header with Back Button -->
        <div class="flex items-center justify-between mb-6">
            <div class="flex items-center gap-3">
                <a href="${pageContext.request.contextPath}/series/list"
                   class="text-gray-600 hover:text-gray-800 transition">
                    <i class="fas fa-arrow-left text-xl"></i>
                </a>
                <div>
                    <h1 class="text-2xl font-bold text-gray-800">Series Review</h1>
                    <p class="text-sm text-gray-500">Review and approve series</p>
                </div>
            </div>

            <!-- Status Badge -->
            <span class="px-4 py-2 rounded-full text-sm font-semibold
                <c:choose>
                    <c:when test="${series.approvalStatus == 'approved'}">bg-green-100 text-green-700</c:when>
                    <c:when test="${series.approvalStatus == 'pending'}">bg-yellow-100 text-yellow-700</c:when>
                    <c:when test="${series.approvalStatus == 'rejected'}">bg-red-100 text-red-700</c:when>
                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                </c:choose>">
                ${series.approvalStatus}
            </span>
        </div>

        <!-- Series Information Card -->
        <div class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg p-6 mb-6 border border-blue-200">
            <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
                <!-- Cover Image -->
                <div class="lg:col-span-1">
                    <img src="${pageContext.request.contextPath}/${series.coverImgUrl}"
                         alt="Series cover"
                         class="w-full rounded-lg shadow-lg object-cover aspect-[3/4]"/>
                </div>

                <!-- Series Details -->
                <div class="lg:col-span-4 space-y-4">
                    <!-- Title & Author -->
                    <div>
                        <h2 class="text-3xl font-bold text-gray-800 mb-2">${series.title}</h2>
                        <p class="text-gray-600">
                            by <span class="font-semibold text-gray-800">
                                <c:forEach var="author" items="${series.authorList}" varStatus="loop">
                                    ${author}<c:if test="${!loop.last}">, </c:if>
                                </c:forEach>
                            </span>
                        </p>
                    </div>

                    <!-- Categories & Status -->
                    <div class="flex flex-wrap gap-2">
                        <c:forEach var="category" items="${series.categoryList}">
                            <span class="bg-blue-100 text-blue-700 text-sm px-3 py-1 rounded-full font-medium">
                                    ${category.name}
                            </span>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${series.status == 'Completed'}">
                                <span class="bg-green-100 text-green-700 text-sm px-3 py-1 rounded-full font-medium">
                                        ${series.status}
                                </span>
                            </c:when>
                            <c:when test="${series.status == 'Ongoing'}">
                                <span class="bg-yellow-100 text-yellow-700 text-sm px-3 py-1 rounded-full font-medium">
                                        ${series.status}
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="bg-gray-100 text-gray-700 text-sm px-3 py-1 rounded-full font-medium">
                                        ${series.status}
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Stats Grid -->
                    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <div class="bg-white rounded-lg p-4 shadow-sm">
                            <p class="text-xs text-gray-500 uppercase mb-1">Chapters</p>
                            <p class="text-2xl font-bold text-gray-800">${series.totalChapters}</p>
                        </div>
                        <div class="bg-white rounded-lg p-4 shadow-sm">
                            <p class="text-xs text-gray-500 uppercase mb-1">Rating</p>
                            <p class="text-2xl font-bold text-gray-800">
                                <span class="text-yellow-400"><i class="fas fa-star"></i></span>
                                <fmt:formatNumber value="${series.avgRating}" type="number" maxFractionDigits="1"
                                                  minFractionDigits="1"/>
                            </p>
                        </div>
                        <div class="bg-white rounded-lg p-4 shadow-sm">
                            <p class="text-xs text-gray-500 uppercase mb-1">Total Rating</p>
                            <p class="text-2xl font-bold text-gray-800">${series.totalRating}</p>
                        </div>
                        <div class="bg-white rounded-lg p-4 shadow-sm">
                            <p class="text-xs text-gray-500 uppercase mb-1">Status</p>
                            <p class="text-lg font-bold text-gray-800 capitalize">${series.status}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-3 lg:grid-cols-3 gap-6">

            <!-- Main Content - Left Side -->
            <div class="lg:col-span-2 space-y-6">

                <!-- Description -->
                <div class="bg-white rounded-lg p-5 border border-gray-200">
                    <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                        <i class="fas fa-align-left text-blue-500"></i>
                        Description
                    </h2>
                    <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
                        <p class="text-gray-700 leading-relaxed">${series.description}</p>
                    </div>
                </div>
            </div>
            <!-- Sidebar - Right Side -->
            <div class="col-span-1">

                <!-- Approval Actions -->
                <c:if test="${series.approvalStatus == 'pending'}">
                    <div class="bg-white rounded-lg p-5 border-2 border-yellow-300 shadow-lg">
                        <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                            <i class="fas fa-tasks text-yellow-600"></i>
                            Review Actions
                        </h2>
                        <p class="text-sm text-gray-600 mb-4">Review the series and take appropriate action.</p>

                        <div class="space-y-3">
                            <form method="post" action="${pageContext.request.contextPath}/series/approve">
                                <input type="hidden" name="seriesId" value="${series.seriesId}">
                                <input type="hidden" name="approveStatus" value="approved">
                                <button type="submit"
                                        onclick="return confirm('Are you sure you want to approve this series?')"
                                        class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                                    <i class="fas fa-check-circle"></i>
                                    Approve Series
                                </button>
                            </form>

                            <form method="post" action="${pageContext.request.contextPath}/series/approve">
                                <input type="hidden" name="seriesId" value="${series.seriesId}">
                                <input type="hidden" name="approveStatus" value="rejected">
                                <button type="submit"
                                        onclick="return confirm('Are you sure you want to reject this series?')"
                                        class="w-full bg-red-500 hover:bg-red-600 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                                    <i class="fas fa-times-circle"></i>
                                    Reject Series
                                </button>
                            </form>
                        </div>
                    </div>
                </c:if>

                <!-- Status Info for Approved/Rejected -->
                <c:if test="${series.approvalStatus == 'approved'}">
                    <div class="bg-white rounded-lg p-5 border-2 border-green-300">
                        <div class="text-center py-4">
                            <i class="fas fa-check-circle text-green-500 text-5xl mb-3"></i>
                            <h3 class="text-lg font-semibold text-gray-800 mb-2">Series Approved</h3>
                            <p class="text-sm text-gray-600 mb-4">This series has been reviewed and approved.</p>

                            <form method="post" action="${pageContext.request.contextPath}/series/approve" class="mt-4">
                                <input type="hidden" name="seriesId" value="${series.seriesId}">
                                <input type="hidden" name="approveStatus" value="rejected">
                                <button type="submit"
                                        onclick="return confirm('Are you sure you want to reject this approved series?')"
                                        class="text-red-600 hover:text-red-800 text-sm font-medium">
                                    <i class="fas fa-times-circle mr-1"></i>
                                    Revoke Approval
                                </button>
                            </form>
                        </div>
                    </div>
                </c:if>

                <c:if test="${series.approvalStatus == 'rejected'}">
                    <div class="bg-white rounded-lg p-5 border-2 border-red-300">
                        <div class="text-center py-4">
                            <i class="fas fa-times-circle text-red-500 text-5xl mb-3"></i>
                            <h3 class="text-lg font-semibold text-gray-800 mb-2">Series Rejected</h3>
                            <p class="text-sm text-gray-600 mb-4">This series has been rejected.</p>

                            <form method="post" action="${pageContext.request.contextPath}/series/approve" class="mt-4">
                                <input type="hidden" name="seriesId" value="${series.seriesId}">
                                <input type="hidden" name="approveStatus" value="approved">
                                <button type="submit"
                                        onclick="return confirm('Are you sure you want to approve this series?')"
                                        class="text-green-600 hover:text-green-800 text-sm font-medium">
                                    <i class="fas fa-check-circle mr-1"></i>
                                    Approve Series
                                </button>
                            </form>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        <!-- Chapter List -->
        <div class="bg-white rounded-lg p-5 border border-gray-200 mt-5">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                <i class="fas fa-list text-green-500"></i>
                Chapter List
                <span class="text-sm text-gray-500 font-normal ml-2">(${series.totalChapters} chapters)</span>
            </h2>

            <div class="overflow-x-auto rounded-sm mb-3">
                <table class="min-w-full text-sm text-left">
                    <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold">
                    <tr>
                        <th class="px-4 py-3">Id</th>
                        <th class="px-4 py-3">Chapter</th>
                        <th class="px-4 py-3">Action</th>
                        <th class="px-4 py-3">Status</th>
                        <th class="px-4 py-3">Updated At</th>
                        <th class="px-4 py-4 text-center">Actions</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-300">
                    <c:forEach var="chapter" items="${chapterList}">
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-3">${chapter.chapterId}</td>
                            <td class="px-4 py-3">
                                <div class="flex flex-col">
                                    <p class="font-semibold text-gray-800">
                                        Chapter ${chapter.chapterNumber}: ${chapter.title}</p>
                                    <p class="text-gray-500 text-sm">Series: ${chapter.seriesTitle}</p>
                                </div>
                            </td>
                            <td class="px-4 py-3 font-extrabold text-[#195DA9]">${chapter.updatedAt}</td>
                            <td class="px-4 py-3">
                            <span class="px-2 py-1 rounded-full text-xs font-semibold
                                <c:choose>
                                    <c:when test="${chapter.approvalStatus == 'approved'}">bg-green-100 text-green-700</c:when>
                                    <c:when test="${chapter.approvalStatus == 'pending'}">bg-yellow-100 text-yellow-700</c:when>
                                    <c:otherwise>bg-red-100 text-red-700</c:otherwise>
                                </c:choose>">
                                    ${chapter.approvalStatus}
                            </span>
                            </td>
                            <td class="px-4 py-3 text-gray-700">${chapter.updatedAt}</td>
                            <td class="px-4 py-3 text-center">
                                <div class="relative flex justify-end gap-2 text-left">
                                    <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}"
                                       class="block px-2 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-blue-100 flex items-center gap-2">
                                        <i class="fa-regular fa-eye mr-2"></i>Detail
                                    </a>

                                    <button type="button"
                                            class="text-gray-500 hover:text-gray-700"
                                            data-bs-toggle="dropdown">
                                        <i class="fas fa-ellipsis-v"></i>
                                    </button>

                                    <!-- Dropdown Menu -->
                                    <ul class="absolute right-0 bottom-1 w-44 bg-white border border-gray-200 rounded-lg shadow-lg hidden">
                                        <!-- Approve / Reject logic -->
                                        <c:choose>
                                            <c:when test="${chapter.approvalStatus == 'pending'}">
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/chapter/approve"
                                                          method="post">
                                                        <input type="hidden" name="chapterId"
                                                               value="${chapter.chapterId}">
                                                        <input type="hidden" name="approveStatus" value="approved">
                                                        <button type="submit"
                                                                class="w-full text-left flex items-center gap-2 px-4 py-2 text-green-600 hover:bg-green-50">
                                                            <i class="fa-solid fa-check"></i> Approve
                                                        </button>
                                                    </form>
                                                </li>
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/chapter/approve"
                                                          method="post">
                                                        <input type="hidden" name="chapterId"
                                                               value="${chapter.chapterId}">
                                                        <input type="hidden" name="approveStatus" value="rejected">
                                                        <button type="submit"
                                                                class="w-full text-left flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-red-50">
                                                            <i class="fa-solid fa-xmark"></i> Reject
                                                        </button>
                                                    </form>
                                                </li>
                                            </c:when>

                                            <c:when test="${chapter.approvalStatus == 'approved'}">
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/chapter/approve"
                                                          method="post">
                                                        <input type="hidden" name="chapterId"
                                                               value="${chapter.chapterId}">
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
        </div>
    </div>
</div>
</div>


<script>
    // Auto-hide success/error messages after 3 seconds
    setTimeout(() => {
        const messages = document.querySelectorAll('.animate-fade-in');
        messages.forEach(msg => {
            msg.style.animation = 'fade-in 0.3s ease-out reverse';
            setTimeout(() => msg.remove(), 300);
        });
    }, 3000);

    // Dropdown menu handling
    document.querySelectorAll('.dropdown-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const menu = btn.nextElementSibling;
            document.querySelectorAll('.dropdown-menu').forEach(m => {
                if (m !== menu) m.classList.add('hidden');
            });
            menu.classList.toggle('hidden');
        });
    });

    window.addEventListener('click', () => {
        document.querySelectorAll('.dropdown-menu').forEach(m => m.classList.add('hidden'));
    });
</script>
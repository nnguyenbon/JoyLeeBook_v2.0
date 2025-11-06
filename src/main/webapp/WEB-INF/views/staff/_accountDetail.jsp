<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="main-content flex-1 px-5 py-3 bg-[#F5F4FA] overflow-y-auto max-h-full overflow-visible">
    <div class="max-w-7xl mx-auto flex flex-col h-full">

        <!-- Account Profile Card -->
        <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl overflow-hidden mb-6">
            <!-- Header Background -->
            <div class="h-24 bg-gradient-to-r from-blue-500 to-purple-600"></div>

            <!-- Profile Info -->
            <div class="px-8 pb-8">
                <div class="flex flex-col md:flex-row items-start md:items-end gap-6 -mt-10">
                    <!-- Avatar -->
                    <div class="relative">
                        <img src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                             alt="${account.username}"
                             class="w-32 h-32 rounded-full border-4 border-white shadow-lg object-cover bg-white">
                        <c:if test="${account.status eq 'active'}">
                            <span class="absolute bottom-2 right-2 w-5 h-5 bg-green-500 border-2 border-white rounded-full"></span>
                        </c:if>
                    </div>

                    <!-- Account Info & Actions -->
                    <div class="flex-1 flex flex-col md:flex-row justify-between items-start md:items-end gap-4 mt-4 md:mt-0">
                        <div>
                            <h1 class="text-3xl font-bold text-gray-900 mb-1">${account.fullName}</h1>
                            <p class="text-gray-500 text-lg mb-2">@${account.username}</p>
                            <div class="flex gap-3 mb-5">
                                <span class="px-3 py-1 rounded-full text-xs font-semibold
                                    <c:choose>
                                        <c:when test="${account.role eq 'admin'}">bg-purple-100 text-purple-700</c:when>
                                        <c:when test="${account.role eq 'staff'}">bg-indigo-100 text-indigo-700</c:when>
                                        <c:when test="${account.role eq 'author'}">bg-blue-100 text-blue-700</c:when>
                                        <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                                    </c:choose>">
                                    <i class="fas fa-user-tag mr-1"></i>${account.role}
                                </span>
                                <span class="px-3 py-1 rounded-full text-xs font-semibold
                                    <c:choose>
                                        <c:when test="${account.status eq 'active'}">bg-green-100 text-green-700</c:when>
                                        <c:when test="${account.status eq 'banned'}">bg-red-100 text-red-700</c:when>
                                        <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                                    </c:choose>">
                                    <i class="fas fa-circle mr-1" style="font-size: 6px;"></i>${account.status}
                                </span>


                                <span class="px-3 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-600">
                                    <i class="fas fa-database mr-1"></i>${account.accountType}
                                </span>
                                    <div class="flex items-center gap-2 text-sm text-yellow-500">
                                        <i class="fas fa-coins "></i>
                                        <span>Points</span>
                                        <span class="font-bold">${account.points}</span>
                                    </div>


                            </div>
                            <div class="flex flex-row gap-10">
                                <c:if test="${not empty account.email}">
                                    <div class="flex flex-row items-center gap-3">
                                        <i class="fas fa-envelope text-gray-400 mt-1"></i>

                                            <p class="text-xs text-gray-500">Email</p>
                                            <p class="text-sm text-gray-900 font-medium">${account.email}</p>

                                    </div>
                                </c:if>
                                <div class="flex  flex-row items-center gap-3">
                                    <i class="fas fa-calendar-alt text-gray-400 mt-1"></i>
                                        <p class="text-xs text-gray-500">Member Since</p>
                                        <p class="text-sm text-gray-900 font-medium">
                                            ${account.createdAt}
                                        </p>
                                </div>
                            </div>
                        </div>

                        <!-- Action Buttons -->
                        <div class="flex gap-2">
                            <!-- Admin: Edit & Delete -->
                            <c:if test="${loginedUser.role eq 'admin'}">
                                <a href="${pageContext.request.contextPath}/account/edit?id=${account.accountId}&type=${account.accountType}"
                                   class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors flex items-center gap-2">
                                    <i class="fas fa-edit"></i>
                                    <span>Edit</span>
                                </a>
                            </c:if>

                            <!-- Admin/Staff: Ban/Unban (chỉ users) -->
                            <c:if test="${(loginedUser.role eq 'admin' || loginedUser.role eq 'staff') && account.accountType eq 'user'}">
                                <form action="${pageContext.request.contextPath}/account/update-status" method="post" style="display:inline;">
                                    <input type="hidden" name="userId" value="${account.accountId}"/>
                                    <c:choose>
                                        <c:when test="${account.status eq 'active'}">
                                            <input type="hidden" name="status" value="banned">
                                            <button type="submit" onclick="return confirm('Ban this account?')"
                                                    class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors flex items-center gap-2">
                                                <i class="fas fa-ban"></i>
                                                <span>Ban Account</span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="hidden" name="status" value="active" />
                                            <button type="submit" onclick="return confirm('Unban this account?')"
                                                    class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors flex items-center gap-2">
                                                <i class="fas fa-check-circle"></i>
                                                <span>Unban Account</span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Bio Section (Users only) -->
        <c:if test="${account.accountType eq 'user'}">
            <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl p-6 mb-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <i class="fas fa-info-circle text-blue-500"></i>
                    Biography
                </h3>
                <div class="prose max-w-none">
                    <p class="text-gray-700 leading-relaxed">
                            ${not empty account.bio ? account.bio : 'This user hasn\'t added a biography yet.'}
                    </p>
                </div>
            </div>
        </c:if>

        <div class="flex-1 py-3 bg-[#F5F4FA]">
            <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-5 py-2">
                <!-- Table -->
                <div class="overflow-x-auto overflow-y-auto max-h-[75vh] rounded-sm mb-3">
                    <table class="min-w-full text-sm text-left">
                        <thead class="bg-gray-100 text-gray-700 uppercase text-xs font-semibold sticky top-0 z-10">
                        <tr>
                            <th class="px-4 py-3">ID</th>
                            <th class="px-4 py-3">Cover</th>
                            <th class="px-4 py-3">Title</th>
                            <th class="px-4 py-3">Avg Rating</th>
                            <th class="px-4 py-3">Total Chapters</th>
                            <th class="px-4 py-3">Status</th>
                            <th class="px-4 py-3">Created At</th>
                            <th class="px-4 py-4 text-center">Actions</th>
                        </tr>
                        </thead>
                        <tbody class="divide-y divide-gray-300">
                        <c:forEach var="series" items="${authorSeriesList}">
                            <tr class="hover:bg-gray-50">
                                <td class="px-4 py-3">${series.seriesId}</td>
                                <td class="px-4 py-3">
                                    <img src="${pageContext.request.contextPath}/${series.coverImgUrl}" alt="${series.title}"
                                         class="w-10 h-12 rounded object-cover">
                                </td>
                                <td class="px-4 py-3 font-semibold text-gray-800">${series.title}</td>
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
            </div>
        </div>
    </div>
</div>
<script>
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
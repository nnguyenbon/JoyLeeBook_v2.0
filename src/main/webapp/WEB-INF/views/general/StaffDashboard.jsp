<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/13/2025
  Time: 2:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<main class="">


    <!-- Content -->
    <section class="p-6 space-y-6">
        <!-- Top stats: 2 nhóm 5 + 5 (tổng 10 box) -->
        <div class="grid grid-cols-12 gap-6">
            <!-- Left group (5 boxes) -->
            <div class="col-span-6 grid grid-cols-3 gap-4">
                <div class="col-span-3 border border-gray-300 rounded-lg bg-white px-6 py-5 flex items-center justify-between shadow-sm">
                    <div>
                        <p class="text-gray-600 font-medium">Total Series</p>
                        <p class="text-3xl font-bold text-[#195DA9]">${dashboardStats.totalSeries}</p>
                    </div>
                    <i class="fa-solid fa-book text-4xl text-[#195DA9]"></i>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Active Users</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-green-600">${dashboardStats.activeUsers}</p>
                        <i class="fa-solid fa-users text-2xl text-green-600"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Authors</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-blue-600">${dashboardStats.authors}</p>
                        <i class="fa-solid fa-pen-nib text-2xl text-blue-600"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Banned Users</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-red-500">${dashboardStats.bannedUsers}</p>
                        <i class="fa-solid fa-user-slash text-2xl text-red-500"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Total Reports</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-red-500">${dashboardStats.totalReports}</p>
                        <i class="fa-solid fa-flag text-2xl text-red-500"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Pending Reports</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-rose-600">${dashboardStats.pendingReports}</p>
                        <i class="fa-regular fa-clock text-2xl text-rose-600"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Reports You’ve Handled</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-rose-600">${dashboardStats.handledReports}</p>
                        <i class="fa-solid fa-check-double text-2xl text-rose-600"></i>
                    </div>
                </div>
            </div>

            <!-- Right group (5 boxes) -->
            <div class="col-span-6 grid grid-cols-2 gap-4">
                <div class="col-span-1 border border-gray-300 rounded-lg bg-white px-6 py-5 flex items-center justify-between shadow-sm">
                    <div>
                        <p class="text-gray-600 font-medium">Total Chapters</p>
                        <p class="text-3xl font-bold text-[#195DA9]">${dashboardStats.totalChapters}</p>
                    </div>
                    <i class="fa-solid fa-file-lines text-4xl text-[#195DA9]"></i>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Your Reviews</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-indigo-600">${dashboardStats.yourReviews}</p>
                        <i class="fa-solid fa-check-to-slot text-2xl text-indigo-600"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Pending Chapters</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-yellow-500">${dashboardStats.pendingChapters}</p>
                        <i class="fa-regular fa-clock text-2xl text-yellow-500"></i>
                    </div>
                </div>

                <div class="border border-gray-300 rounded-lg bg-white p-4 flex flex-col justify-between shadow-sm">
                    <p class="text-gray-600 font-semibold">Reject</p>
                    <div class="flex items-center justify-between">
                        <p class="text-2xl font-bold text-red-500">${dashboardStats.yourRejects}</p>
                        <i class="fa-solid fa-circle-xmark text-2xl text-red-500"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bottom: two panels (Recent Actions + Quick Stats) -->
        <div class="grid grid-cols-12 gap-6">
            <!-- Recent Actions -->
            <div class="col-span-6 bg-white border border-gray-300 rounded-lg p-6 shadow-sm">
                <h3 class="text-xl font-semibold mb-3">Recent Actions</h3>
                <p class="text-sm text-gray-500 mb-4">Your recent moderation activity</p>

                <ul class="space-y-4">
                    <c:forEach items="${recentActions}" var="action">
                        <li class="flex items-start gap-4">
                            <span class="mt-1 inline-flex h-3 w-3 rounded-full bg-${action.statusColor}-500"></span>
                            <div>
                                <p class="font-medium">${action.actionDescription}</p>
                                <p class="text-sm text-gray-500">${action.timestamp}</p>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </div>

            <!-- Quick Stats -->
            <div class="col-span-6 bg-white border border-gray-300 rounded-lg p-6 shadow-sm">
                <h3 class="text-xl font-semibold mb-3">Quick Stats</h3>
                <p class="text-sm text-gray-500 mb-4">Today's moderation overview</p>

                <ul class="grid grid-cols-1 gap-8">
                    <li class="flex justify-between items-center font-medium">
                        <p>Reviews completed</p>
                        <p class="font-semibold">${quickStats.reviewsCompleted}</p>
                    </li>

                    <li class="flex justify-between items-center font-medium">
                        <p>Content approved</p>
                        <p class="font-semibold">${quickStats.contentApproved}</p>
                    </li>

                    <li class="flex justify-between items-center font-medium">
                        <p>Content rejected</p>
                        <p class="font-semibold">${quickStats.contentRejected}</p>
                    </li>

                    <li class="flex justify-between items-center font-medium">
                        <p>Reports resolved</p>
                        <p class="font-semibold">${quickStats.reportsResolved}</p>
                    </li>
                </ul>
            </div>
        </div>
    </section>
</main>

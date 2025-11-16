<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Content -->
<div class="main-content flex-1 px-5 py-3 overflow-y-auto max-h-[100vh] custom-scrollbar">
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-5 py-4">

        <!-- Header with Back Button -->
        <div class="flex items-center justify-between mb-6">
            <div class="flex items-center gap-3">
                <a href="${pageContext.request.contextPath}/chapter/list"
                   class="text-gray-600 hover:text-gray-800 transition">
                    <i class="fas fa-arrow-left text-xl"></i>
                </a>
                <div>
                    <h1 class="text-2xl font-bold text-gray-800">Chapter Review</h1>
                    <p class="text-sm text-gray-500">Review and approve chapter content</p>
                </div>
            </div>

            <!-- Status Badge -->
            <span class="px-4 py-2 rounded-full text-sm font-semibold
                <c:choose>
                    <c:when test="${chapter.approvalStatus == 'approved'}">bg-green-100 text-green-700</c:when>
                    <c:when test="${chapter.approvalStatus == 'pending'}">bg-yellow-100 text-yellow-700</c:when>
                    <c:when test="${chapter.approvalStatus == 'rejected'}">bg-red-100 text-red-700</c:when>
                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                </c:choose>">
                ${chapter.approvalStatus}
            </span>
        </div>

        <!-- Chapter Information Card -->
        <div class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg p-6 mb-6 border border-blue-200">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <!-- Chapter ID -->
                <div class="bg-white rounded-lg p-4 shadow-sm">
                    <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
                        <i class="fas fa-hashtag text-blue-500"></i>
                        Chapter ID
                    </p>
                    <p class="text-gray-800 font-bold text-lg">${chapter.chapterId}</p>
                </div>

                <!-- Chapter Number -->
                <div class="bg-white rounded-lg p-4 shadow-sm">
                    <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
                        <i class="fas fa-list-ol text-purple-500"></i>
                        Chapter Number
                    </p>
                    <p class="text-gray-800 font-semibold text-lg">Chapter ${chapter.chapterNumber}</p>
                </div>

                <!-- Series -->
                <div class="bg-white rounded-lg p-4 shadow-sm">
                    <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
                        <i class="fas fa-book text-indigo-500"></i>
                        Series
                    </p>
                    <p class="text-gray-800 font-semibold truncate" title="${chapter.seriesTitle}">${chapter.seriesTitle}</p>
                </div>

                <!-- Author -->
                <div class="bg-white rounded-lg p-4 shadow-sm">
                    <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
                        <i class="fas fa-user-pen text-orange-500"></i>
                        Author
                    </p>
                    <p class="text-gray-800 font-semibold">${chapter.authorName}</p>
                </div>
            </div>

            <!-- Timestamps -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                <div class="bg-white rounded-lg p-3 shadow-sm flex items-center justify-between">
                    <div>
                        <p class="text-xs text-gray-500">Created At</p>
                        <p class="text-gray-700 font-medium text-sm">${chapter.createdAt}</p>
                    </div>
                    <i class="fas fa-clock text-gray-400"></i>
                </div>
                <div class="bg-white rounded-lg p-3 shadow-sm flex items-center justify-between">
                    <div>
                        <p class="text-xs text-gray-500">Last Updated</p>
                        <p class="text-gray-700 font-medium text-sm">${chapter.updatedAt}</p>
                    </div>
                    <i class="fas fa-sync text-gray-400"></i>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

            <!-- Main Content - Left Side -->
            <div class="lg:col-span-2 space-y-6">

                <!-- Chapter Title -->
                <div class="bg-white rounded-lg p-5 border border-gray-200">
                    <h2 class="text-lg font-semibold text-gray-800 mb-3 flex items-center gap-2">
                        <i class="fas fa-heading text-blue-500"></i>
                        Chapter Title
                    </h2>
                    <div class="bg-blue-50 p-4 rounded-lg border-l-4 border-blue-500 mb-4">
                        <p class="text-gray-800 text-xl font-semibold">${chapter.title}</p>
                    </div>
                    <div class="flex items-center justify-between mb-4">
                        <h2 class="text-lg font-semibold text-gray-800 flex items-center gap-2">
                            <i class="fas fa-file-alt text-green-500"></i>
                            Chapter Content
                        </h2>
                    </div>

                    <div class="bg-gray-50 p-6 rounded-lg border border-gray-300 max-h-[600px] overflow-y-auto custom-scrollbar">
                        <div class="prose max-w-none text-gray-800 leading-relaxed whitespace-pre-wrap">
                            ${chapter.content}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Sidebar - Right Side -->
            <div class="space-y-6">

                <!-- Approval Actions -->
                <c:if test="${chapter.approvalStatus == 'pending'}">
                    <div class="bg-white rounded-lg p-5 border-2 border-yellow-300 shadow-lg">
                        <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                            <i class="fas fa-tasks text-yellow-600"></i>
                            Review Actions
                        </h2>
                        <p class="text-sm text-gray-600 mb-4">Review the chapter content and take appropriate action.</p>

                        <div class="space-y-3">
                            <button type="button"
                                    onclick="openReasonModal('approved', '${chapter.chapterId}')"
                                    class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                                <i class="fas fa-check-circle"></i>
                                Approve Chapter
                            </button>

                            <button type="button"
                                    onclick="openReasonModal('rejected', '${chapter.chapterId}')"
                                    class="w-full bg-red-500 hover:bg-red-600 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                                <i class="fas fa-times-circle"></i>
                                Reject Chapter
                            </button>
                        </div>
                    </div>
                </c:if>

                <!-- Status Info for Approved/Rejected -->
                <c:if test="${chapter.approvalStatus == 'approved'}">
                    <div class="bg-white rounded-lg p-5 border-2 border-green-300">
                        <div class="text-center py-4">
                            <i class="fas fa-check-circle text-green-500 text-5xl mb-3"></i>
                            <h3 class="text-lg font-semibold text-gray-800 mb-2">Chapter Approved</h3>
                            <p class="text-sm text-gray-600 mb-4">This chapter has been reviewed and approved.</p>

                            <button type="button"
                                    onclick="openReasonModal('rejected', '${chapter.chapterId}')"
                                    class="text-red-600 hover:text-red-800 text-sm font-medium">
                                <i class="fas fa-times-circle mr-1"></i>
                                Revoke Approval
                            </button>
                        </div>
                    </div>
                </c:if>

                <c:if test="${chapter.approvalStatus == 'rejected'}">
                    <div class="bg-white rounded-lg p-5 border-2 border-red-300">
                        <div class="text-center py-4">
                            <i class="fas fa-times-circle text-red-500 text-5xl mb-3"></i>
                            <h3 class="text-lg font-semibold text-gray-800 mb-2">Chapter Rejected</h3>
                            <p class="text-sm text-gray-600 mb-4">This chapter has been rejected.</p>

                            <button type="button"
                                    onclick="openReasonModal('approved', '${chapter.chapterId}')"
                                    class="text-green-600 hover:text-green-800 text-sm font-medium">
                                <i class="fas fa-check-circle mr-1"></i>
                                Approve Chapter
                            </button>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- Reason Modal -->
<div id="reasonModal" class="hidden fixed inset-0 bg-opacity-50 z-50 flex items-center justify-center">
    <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div class="p-6">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-xl font-semibold text-gray-800" id="modalTitle">Confirm Action</h3>
                <button onclick="closeReasonModal()" class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-times text-xl"></i>
                </button>
            </div>

            <form id="reasonForm" method="post" action="${pageContext.request.contextPath}/chapter/approve">
                <input type="hidden" name="chapterId" value="${chapter.chapterId}">
                <input type="hidden" name="approveStatus" id="approveStatus">

                <div class="mb-4">
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        Reason <span class="text-red-500">*</span>
                    </label>
                    <textarea
                            name="reason"
                            id="reasonInput"
                            rows="4"
                            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                            placeholder="Please provide a reason for this action..."></textarea>
                </div>

                <div class="flex gap-3">
                    <button type="button"
                            onclick="closeReasonModal()"
                            class="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition">
                        Cancel
                    </button>
                    <button type="submit"
                            id="submitBtn"
                            class="flex-1 px-4 py-2 text-white rounded-lg transition">
                        Confirm
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openReasonModal(action, chapterId) {
        const modal = document.getElementById('reasonModal');
        const modalTitle = document.getElementById('modalTitle');
        const submitBtn = document.getElementById('submitBtn');
        const reasonInput = document.getElementById('reasonInput');

        // Set status
        document.getElementById('approveStatus').value = action;

        // Update UI based on action
        if (action === 'approved') {
            modalTitle.innerHTML = '<i class="fas fa-check-circle text-green-500 mr-2"></i>Approve Chapter';
            submitBtn.className = 'flex-1 px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded-lg transition';
            submitBtn.innerHTML = '<i class="fas fa-check mr-2"></i>Approve';
            reasonInput.placeholder = 'Example: Content meets quality standards, well-written, follows guidelines...';
        } else {
            modalTitle.innerHTML = '<i class="fas fa-times-circle text-red-500 mr-2"></i>Reject Chapter';
            submitBtn.className = 'flex-1 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition';
            submitBtn.innerHTML = '<i class="fas fa-times mr-2"></i>Reject';
            reasonInput.placeholder = 'Example: Contains inappropriate content, violates guidelines, poor quality...';
        }

        // Clear previous input
        reasonInput.value = '';

        // Show modal
        modal.classList.remove('hidden');

        // Focus on textarea
        setTimeout(() => reasonInput.focus(), 100);
    }

    function closeReasonModal() {
        document.getElementById('reasonModal').classList.add('hidden');
    }

    // Close modal when clicking outside
    document.getElementById('reasonModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeReasonModal();
        }
    });

    // Close modal on Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeReasonModal();
        }
    });

</script>

<style>
    .custom-scrollbar::-webkit-scrollbar {
        width: 8px;
    }

    .custom-scrollbar::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 4px;
    }

    .custom-scrollbar::-webkit-scrollbar-thumb {
        background: #888;
        border-radius: 4px;
    }

    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
        background: #555;
    }
</style>
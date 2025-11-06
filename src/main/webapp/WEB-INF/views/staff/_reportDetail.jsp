<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Content -->
<div class="main-content px-5 py-3 bg-[#F5F4FA]">
  <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl px-6 py-4 overflow-y-auto max-h-[90vh] custom-scrollbar">

    <!-- Header with Back Button -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <a href="${pageContext.request.contextPath}/report/list?type=${type}"
           class="text-gray-600 hover:text-gray-800">
          <i class="fas fa-arrow-left text-xl"></i>
        </a>
        <h1 class="text-2xl font-bold text-gray-800">Report Detail</h1>
      </div>

      <!-- Status Badge -->
      <span class="px-4 py-2 rounded-full text-sm font-semibold
                <c:choose>
                    <c:when test="${report.status == 'resolved'}">bg-green-100 text-green-700</c:when>
                    <c:when test="${report.status == 'pending'}">bg-yellow-100 text-yellow-700</c:when>
                    <c:when test="${report.status == 'rejected'}">bg-red-100 text-red-700</c:when>
                    <c:otherwise>bg-gray-100 text-gray-700</c:otherwise>
                </c:choose>">
        ${report.status}
      </span>
    </div>

    <!-- Main Report Information Card -->
    <div class="bg-gradient-to-br from-blue-50 to-purple-50 rounded-lg p-6 mb-6 border border-blue-200">
      <div class="grid grid-cols-4 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Report ID -->
        <div class="bg-white rounded-lg p-4 shadow-sm">
          <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
            <i class="fas fa-hashtag text-blue-500"></i>
            Report ID
          </p>
          <p class="text-gray-800 font-bold text-lg">${report.reportId}</p>
        </div>

        <!-- Reporter -->
        <div class="bg-white rounded-lg p-4 shadow-sm">
          <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
            <i class="fas fa-user text-purple-500"></i>
            Reporter
          </p>
          <p class="text-gray-800 font-semibold">${report.reporterUsername}</p>
        </div>

        <!-- Staff -->
        <div class="bg-white rounded-lg p-4 shadow-sm">
          <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
            <i class="fas fa-user-shield text-indigo-500"></i>
            Assigned Staff
          </p>
          <c:choose>
            <c:when test="${not empty report.staffUsername}">
              <p class="text-gray-800 font-semibold">${report.staffUsername}</p>
            </c:when>
            <c:otherwise>
              <p class="text-gray-400 italic text-sm">Unassigned</p>
            </c:otherwise>
          </c:choose>
        </div>

        <!-- Report Type -->
        <div class="bg-white rounded-lg p-4 shadow-sm">
          <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
            <i class="fas ${type == 'chapter' ? 'fa-book' : 'fa-comment'} text-orange-500"></i>
            Type
          </p>
          <p class="text-gray-800 font-semibold capitalize">${type} Report</p>
        </div>
        <div class="col-span-2 bg-white rounded-lg p-4 shadow-sm">
          <p class="text-xs text-gray-500 uppercase mb-1 flex items-center gap-2">
            <i class="fas fa-exclamation-circle text-orange-500"></i>
            Report Reason
          </p>
          <p class="text-gray-800 font-bold text-lg">${report.reason}</p>
        </div>
        <div class="bg-white rounded-lg p-3 shadow-sm flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">Created At</p>
            <p class="text-gray-700 font-medium text-sm">${report.createdAt}</p>
          </div>
          <i class="fas fa-clock text-gray-400"></i>
        </div>
        <div class="bg-white rounded-lg p-3 shadow-sm flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">Last Updated</p>
            <p class="text-gray-700 font-medium text-sm">${report.updatedAt}</p>
          </div>
          <i class="fas fa-sync text-gray-400"></i>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

      <!-- Main Content - Left Side -->
      <div class="lg:col-span-2 space-y-6">

        <!-- Reported Content Section -->
        <div class="bg-gray-50 rounded-lg p-5 border border-gray-200 ">
          <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <i class="fas fa-flag text-red-500"></i>
            Reported Content
          </h2>

          <c:choose>
            <c:when test="${type == 'chapter'}">
              <!-- Chapter Report Details -->
              <div class="space-y-4 ">
                <div class="bg-white p-4 rounded-lg border border-gray-300">
                  <div class="flex items-start justify-between">
                    <div class="flex-1">
                      <p class="text-sm text-gray-500 mb-2">Series</p>
                      <p class="text-gray-800 font-bold text-xl mb-3">${report.seriesName}</p>

                      <p class="text-sm text-gray-500 mb-1">Chapter</p>
                      <p class="text-gray-800 font-semibold text-lg">
                        Chapter ${report.chapterNumber}: ${report.chapterTitle}
                      </p>
                    </div>
                    <i class="fas fa-book text-4xl text-blue-200"></i>
                  </div>

                  <div class="mt-4 pt-4 border-t border-gray-200">
                    ${chapter.content}
                  </div>
                </div>
              </div>
            </c:when>
            <c:otherwise>
              <!-- Comment Report Details -->
              <div class="space-y-4">
                <div class="bg-white p-4 rounded-lg border border-gray-300">
                  <div class="flex items-start gap-3 mb-3">
                    <div class="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center flex-shrink-0">
                      <i class="fas fa-user text-purple-600"></i>
                    </div>
                    <div class="flex-1">
                      <p class="text-gray-800 font-semibold">${report.commenterUsername}</p>
                      <p class="text-xs text-gray-500">Comment Author</p>
                    </div>
                  </div>

                  <div class="bg-gray-50 p-4 rounded border border-gray-200 mb-3">
                    <p class="text-sm text-gray-500 mb-2">Comment Content</p>
                    <p class="text-gray-800 leading-relaxed">${report.commentContent}</p>
                  </div>

                  <div class="flex items-center justify-between text-sm">
                    <div>
                      <p class="text-gray-500">In Chapter:</p>
                      <p class="text-gray-700 font-medium">${report.chapterTitle}</p>
                    </div>
                  </div>
                </div>
              </div>
            </c:otherwise>
          </c:choose>
        </div>


      </div>

      <!-- Action Panel - Right Side -->
      <div class="space-y-6">

        <!-- Action Buttons (for pending reports) -->
        <c:if test="${report.status == 'pending'}">
          <div class="bg-white rounded-lg p-5 border-2 border-yellow-300 shadow-lg">
            <h2 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
              <i class="fas fa-tasks text-yellow-600"></i>
              Take Action
            </h2>
            <p class="text-sm text-gray-600 mb-4">Review the report and decide the appropriate action.</p>

            <div class="space-y-3">
              <form method="post" action="${pageContext.request.contextPath}/report/handle">
                <input type="hidden" name="reportId" value="${report.reportId}">
                <input type="hidden" name="type" value="${type}">
                <input type="hidden" name="status" value="resolved">
                <button type="submit"
                        class="w-full bg-green-500 hover:bg-green-600 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                  <i class="fas fa-check-circle"></i>
                  Accept Report
                </button>
              </form>

              <form method="post" action="${pageContext.request.contextPath}/report/handle">
                <input type="hidden" name="reportId" value="${report.reportId}">
                <input type="hidden" name="type" value="${type}">
                <input type="hidden" name="status" value="rejected">
                <button type="submit"
                        class="w-full bg-red-500 hover:bg-red-600 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 flex items-center justify-center gap-2 shadow-md hover:shadow-lg">
                  <i class="fas fa-times-circle"></i>
                  Reject Report
                </button>
              </form>
            </div>
          </div>
        </c:if>

        <!-- Status Message for Resolved/Rejected -->
        <c:if test="${report.status != 'pending'}">
          <div class="bg-white rounded-lg p-5 border-2 ${report.status == 'resolved' ? 'border-green-300' : 'border-red-300'}">
            <div class="text-center py-4">
              <i class="fas ${report.status == 'resolved' ? 'fa-check-circle text-green-500' : 'fa-times-circle text-red-500'} text-5xl mb-3"></i>
              <h3 class="text-lg font-semibold text-gray-800 mb-2">
                Report ${report.status == 'resolved' ? 'Resolved' : 'Rejected'}
              </h3>
              <p class="text-sm text-gray-600">
                  ${report.status == 'resolved' ? 'This report has been resolved by staff.' : 'This report has been rejected.'}
              </p>
            </div>
          </div>
        </c:if>
      </div>
    </div>
  </div>
</div>

<!-- Success/Error Messages -->
<c:if test="${not empty sessionScope.successMessage}">
  <div class="fixed top-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50 animate-fade-in">
    <div class="flex items-center gap-2">
      <i class="fas fa-check-circle"></i>
      <span>${sessionScope.successMessage}</span>
    </div>
  </div>
  <c:remove var="successMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.errorMessage}">
  <div class="fixed top-4 right-4 bg-red-500 text-white px-6 py-3 rounded-lg shadow-lg z-50 animate-fade-in">
    <div class="flex items-center gap-2">
      <i class="fas fa-exclamation-circle"></i>
      <span>${sessionScope.errorMessage}</span>
    </div>
  </div>
  <c:remove var="errorMessage" scope="session"/>
</c:if>

<script>
  // Auto-hide success/error messages after 3 seconds
  setTimeout(() => {
    const messages = document.querySelectorAll('.animate-fade-in');
    messages.forEach(msg => {
      msg.style.animation = 'fade-in 0.3s ease-out reverse';
      setTimeout(() => msg.remove(), 300);
    });
  }, 3000);

  // Confirm before taking action
  document.querySelectorAll('form[action*="resolve"] button').forEach(btn => {
    btn.addEventListener('click', function(e) {
      const action = this.textContent.includes('Resolved') ? 'resolve' : 'reject';
      if (!confirm(`Are you sure you want to ${action} this report?`)) {
        e.preventDefault();
      }
    });
  });
</script>
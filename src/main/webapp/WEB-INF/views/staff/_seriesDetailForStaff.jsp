<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-yv7FFx5UP7gP6+VwMlP97+YzUQEMCQZx1Hn0bnh5Zf0CBlSZx1ZCwYoSGB94EIMhDdjwUgt4fYxgR4L5iqn5Mg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<%@ page buffer="32kb" autoFlush="true" %>
<main class="flex-1 bg-gray-100 max-h-[90vh] custom-scrollbar overflow-y-auto">
  <div class="p-9">
    <div class="flex h-60 gap-5 mb-5">
      <img src="${pageContext.request.contextPath}/${series.coverImgUrl}" alt="Series cover" class="rounded-lg shadow"/>
      <div>
        <h1 class="text-4xl font-bold">${series.title}</h1>
        <p class="text-gray-600 mb-4">by
          <span class="font-semibold">
            <c:forEach var="author" items="${series.authorNameList}" varStatus="loop">
              ${author}<c:if test="${!loop.last}">, </c:if>
            </c:forEach>
          </span>
        </p>

        <div class="flex flex-wrap gap-2 mb-10">
          <c:forEach var="category" items="${series.categoryList}">
            <span class="bg-gray-200 text-gray-600 text-sm px-3 py-1 rounded-full">${category.name}</span>
          </c:forEach>
          <c:choose>
            <c:when test="${series.status == 'Completed'}">
              <span class="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 font-medium">${series.status}</span>
            </c:when>
            <c:when test="${series.status == 'Ongoing'}">
              <span class="text-xs px-3 py-1 rounded-full bg-yellow-100 text-yellow-700 font-medium">${series.status}</span>
            </c:when>
            <c:otherwise>
              <span class="text-xs px-3 py-1 rounded-full bg-gray-100 text-gray-700 font-medium">${series.status}</span>
            </c:otherwise>
          </c:choose>
        </div>

        <div class="flex items-center gap-5 mb-10">
          <span class="font-semibold text-lg">${series.totalChapters} Chapters</span>
          <div class="text-gray-500 font-semibold text-lg">
            <span class="text-yellow-400"><i class="fa-solid fa-star"></i> ${series.avgRating}</span>
            <span>(${series.totalRating})</span>
          </div>
        </div>
      </div>
    </div>

    <div class="border-2 rounded-lg bg-white mb-3">
      <h2 class="px-4 text-2xl font-semibold">Description</h2>
      <p class="p-4 text-gray-700">${series.description}</p>
    </div>

    <div class="border-2 rounded-lg bg-white px-4 max-h-[350px] overflow-y-auto py-4">
      <h2 class="text-2xl font-semibold mb-3">Chapter List</h2>
      <c:forEach var="chapter" items="${chapterDetailDTOList}" varStatus="loop">
        <div class="flex justify-between items-center border rounded-lg px-4 py-3 mb-4 bg-white hover:bg-gray-50 cursor-pointer">
          <span class="text-lg font-semibold">Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
          <div class="flex items-center gap-4">
            <p class="font-extrabold text-[#195DA9]">Update chapter</p>
            <c:choose>
              <c:when test="${chapter.status == 'Approved'}">
                <span class="w-20 text-center py-0.5 rounded-full bg-green-100 text-green-700 text-xs">${chapter.status}</span>
              </c:when>
              <c:when test="${chapter.status == 'Pending'}">
                <span class="w-20 text-center py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs">${chapter.status}</span>
              </c:when>
              <c:otherwise>
                <span class="w-20 text-center py-0.5 rounded-full bg-gray-100 text-red-700 text-xs">${chapter.status}</span>
              </c:otherwise>
            </c:choose>
            <div class="flex items-center gap-2 text-sm">
              <p class="text-gray-500 mr-3">${chapter.updatedAt}</p>
              <a href="${pageContext.request.contextPath}/chapter?action=detail&chapterId=${chapter.chapterId}">
                <button class="flex gap-2 border rounded-md px-2 py-1 text-sm hover:bg-gray-100">
                  <i class="fa-solid fa-eye"></i> Detail
                </button>
              </a>
              <div class="relative">
                <button class="dropdown-btn text-gray-400 hover:text-gray-600 focus:outline-none">
                  <i class="fa-solid fa-ellipsis"></i>
                </button>
                <div class="dropdown-menu hidden absolute right-0 mt-2 w-30 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                  <button class="block w-full text-[#42CC75] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                    <i class="fa-solid fa-circle-check"></i> Approve
                  </button>
                  <button class="block w-full text-[#E23636] flex gap-2 px-4 py-2 text-sm hover:bg-gray-100">
                    <i class="fa-solid fa-circle-xmark"></i> Reject
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>

    <!-- Thêm hai nút hành động ở cuối -->
    <div class="flex justify-end gap-4 mt-6">
      <c:choose>
        <c:when test="${series.approvalStatus == 'pending'}">
          <form action="${pageContext.request.contextPath}/series/approve" method="post">
            <input type="hidden" name="seriesId" value="${series.seriesId}">
            <input type="hidden" name="approveStatus" value="approved">

            <button type="submit" class="flex items-center gap-2 bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg shadow">
              <i class="fa-solid fa-check"></i> Approve
            </button>
          </form>
          <form action="${pageContext.request.contextPath}/series/approve" method="post">
            <input type="hidden" name="seriesId" value="${series.seriesId}">
            <input type="hidden" name="approStatus" value="rejected">

            <button type="submit" class="flex items-center gap-2 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg shadow">
              <i class="fa-solid fa-xmark"></i> Reject
            </button>
          </form>
        </c:when>
        <c:when test="${series.approvalStatus == 'approved'}">
          <form action="${pageContext.request.contextPath}/series/approve" method="post">
            <input type="hidden" name="seriesId" value="${series.seriesId}">
            <input type="hidden" name="approStatus" value="rejected">

            <button type="submit" class="flex items-center gap-2 bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg shadow">
              <i class="fa-solid fa-xmark"></i> Reject
            </button>
          </form>
        </c:when>
      </c:choose>
    </div>

  </div>
</main>

<script>
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

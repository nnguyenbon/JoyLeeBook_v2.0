<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="col-span-8 col-start-5">
    <p class="text-sm text-gray-500">${authorItemDTOList.size()} results</p>
</div>
<!-- CỘT GIỮA: DANH SÁCH Author -->
<div class="col-span-8">
    <div class="space-y-6">
        <c:forEach var="author" items="${authorItemDTOList}">
            <div class="flex justify-between items-center bg-white p-4 rounded-xl shadow shadow-[#195DA9] hover:shadow-md transition">
                <div class="flex items-center gap-4">
                    <img src="https://placehold.co/120x120" alt="cover"
                         class="w-[80px] h-[80px] object-cover rounded-full">
                    <div>
                        <h3 class="font-semibold text-lg text-gray-800">${author.userName}</h3>
                        <p class="text-sm text-gray-500 mb-1">${author.totalChapters} series</p>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/author-profile?authorId=${author.authorId}">
                    <button class="bg-[#ECF1FE] text-[#195DA9] font-medium px-4 py-2 rounded-lg hover:bg-[#195DA9] hover:text-white transition flex items-center justify-center">
                        View Profile
                    </button>
                </a>
            </div>
        </c:forEach>
    </div>
</div>
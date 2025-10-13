<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
<div class="grid grid-cols-12 gap-6">
    <!-- FILTER -->
    <div class="col-span-4 bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-4">Filter</h2>
        <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

        <div class="space-y-3">
            <label class="flex items-center gap-2">
                <input type="checkbox" name="status" value="Completed" class="accent-[#195DA9]" /> Completed
            </label>
            <label class="flex items-center gap-2">
                <input type="checkbox" name="status" value="Ongoing" class="accent-[#195DA9]" /> Ongoing
            </label>
        </div>

        <h2 class="text-xl font-semibold mt-6 mb-3">Genre</h2>
        <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

        <div class="grid grid-cols-2 gap-2 text-gray-700">
            <c:forEach var="category" items="${categories}">
                <label class="flex items-center gap-2"><input type="checkbox"
                                                              name="genre"
                                                              value="${category.name}"
                                                              class="accent-[#195DA9]" /> ${category.name}</label>
            </c:forEach>
        </div>
    </div>
    <div class="col-span-8" id="result-container">
        <jsp:include page="/WEB-INF/views/general/searchview/SearchFilterView.jsp"/>
    </div>
</div>
            
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="grid grid-cols-12 gap-6 mt-4">
    <!-- FILTER -->
    <div class="col-span-4 bg-white rounded-xl shadow p-4 h-fit">
        <h2 class="text-xl font-semibold mb-4">Filter</h2>
        <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

        <div class="space-y-3">
            <label class="flex items-center gap-2">
                <input type="checkbox" name="status" value="completed" class="accent-[#195DA9]"
                       <c:if test="${statusParam.contains('completed')}">checked</c:if>
                /> Completed
            </label>
            <label class="flex items-center gap-2">
                <input type="checkbox" name="status" value="ongoing" class="accent-[#195DA9]"
                       <c:if test="${statusParam.contains('ongoing')}">checked</c:if>
                /> Ongoing
            </label>
        </div>

        <h2 class="text-xl font-semibold mt-6 mb-3">Genre</h2>
        <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

        <div class="grid grid-cols-2 gap-2 text-gray-700">
            <c:forEach var="category" items="${categories}">
                <label class="flex items-center gap-2"><input type="checkbox"
                                                              name="genre"
                                                              value="${category.name}"
                                                              class="accent-[#195DA9]"
                                                              <c:if test="${genresParam.contains(category.name)}">checked</c:if>
                /> ${category.name}</label>
            </c:forEach>
        </div>
    </div>
    <div class="col-span-8" id="result-container">
        <jsp:include page="/WEB-INF/views/general/searchview/SearchFilterView.jsp"/>
    </div>
</div>
            
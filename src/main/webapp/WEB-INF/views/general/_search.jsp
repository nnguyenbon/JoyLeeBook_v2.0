<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 12:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>

<!-- Content -->
<main class="mt-5 grid grid-cols-12 gap-x-8" id="container">
    <div class="col-span-12 border-b border-gray-300 flex items-center gap-5 mb-3">

        <button id="btn-title"
                class="tab-btn text-2xl py-2 border-b-4 border-[#195DA9] text-[#195DA9] hover:text-[#195DA9]"
                onclick="setActiveTab('title')">
            All Series
        </button>

        <button id="btn-author"
                class="tab-btn text-2xl py-2 border-b-4 border-white text-gray-500 hover:text-[#195DA9]"
                onclick="setActiveTab('author')">
            Authors
        </button>
    </div>
    <div class="col-span-12" id="search-results">
        <div class="grid grid-cols-12 gap-6 mt-4">
            <!-- FILTER -->
            <div class="col-span-4 bg-white rounded-xl shadow p-4 h-fit" id="genre-filter">
                <h2 class="text-xl font-semibold mt-1 mb-3">Genre</h2>
                <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

                <div class="grid grid-cols-2 gap-2 text-gray-700">
                    <c:forEach var="category" items="${categories}">
                        <label class="flex items-center gap-2"><input type="checkbox"
                                                                      name="genre"
                                                                      value="${category.categoryId}"
                                                                      class="accent-[#195DA9]" onclick="updateFilter()"
                                                                      <c:if test="${genresParam.contains(category.name)}" >checked</c:if>
                        /> ${category.name}</label>
                    </c:forEach>
                </div>

            </div>

            <jsp:include page="/WEB-INF/views/series/_seriesList.jsp"/>

        </div>
    </div>
</main>


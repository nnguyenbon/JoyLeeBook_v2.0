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
<main class="mt-10 grid grid-cols-12 gap-x-8" id="container">
    <div class="col-span-12 border-b-1 border-gray-300 flex items-center">
        <button id="btn-title" class="tab-btn font-bold text-2xl px-4" onclick="loadResults('title')">All Series</button>
<%--        <button id="btn-author" class="tab-btn font-bold text-2xl px-4" onclick="loadResults('author')">Author</button>--%>
    </div>
    <div class="col-span-12" id="search-results">
        <div class="grid grid-cols-12 gap-6 mt-4">
            <!-- FILTER -->
            <div class="col-span-4 bg-white rounded-xl shadow p-4 h-fit">
<%--                <h2 class="text-xl font-semibold mb-4">Filter</h2>--%>
<%--                <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>--%>

<%--                <div class="space-y-3">--%>
<%--                    <label class="flex items-center gap-2">--%>
<%--                        <input type="checkbox" name="status" value="completed" class="accent-[#195DA9]"--%>
<%--                               <c:if test="${statusParam.contains('completed')}">checked</c:if>--%>
<%--                        /> Completed--%>
<%--                    </label>--%>
<%--                    <label class="flex items-center gap-2">--%>
<%--                        <input type="checkbox" name="status" value="ongoing" class="accent-[#195DA9]"--%>
<%--                               <c:if test="${statusParam.contains('ongoing')}">checked</c:if>--%>
<%--                        /> Ongoing--%>
<%--                    </label>--%>
<%--                </div>--%>

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
<%--<script>--%>
<%--    function updateFilter() {--%>
<%--        const selectedGenres = Array.from(document.querySelectorAll("input[name=genre]:checked")).map(cb => cb.value);--%>

<%--        const params = new URLSearchParams();--%>
<%--        if (selectedGenres.length > 0) params.append("genres", selectedGenres.join(","));--%>

<%--        fetch(contextPath + "/series/list?" + params.toString(), {--%>
<%--            method: "GET",--%>
<%--            headers: { "X-Requested-With": "XMLHttpRequest" }--%>
<%--        })--%>
<%--            .then(res => res.text())--%>
<%--            .then(html => {--%>
<%--                document.querySelector("#result-container").innerHTML = html;--%>
<%--                restoreFilterState(selectedGenres);--%>
<%--            })--%>
<%--            .catch(err => console.error("Filter load error:", err));--%>
<%--    }--%>

<%--</script>--%>
<%--<script>--%>
<%--    const keyword = "<%= request.getAttribute("keyword") %>";--%>
<%--    const contextPath = '<%= request.getContextPath() %>';--%>

<%--    const ACTIVE_CLASSES = "text-[#195DA9] border-b-4 border-[#195DA9]";--%>
<%--    const INACTIVE_CLASSES = "text-gray-500 hover:text-[#195DA9] border-b-4 border-transparent";--%>


<%--    function loadResults(type) {--%>
<%--        const activeBtnId = "btn-" + type;--%>

<%--        document.querySelectorAll(".tab-btn").forEach(button => {--%>
<%--            button.classList.remove(...ACTIVE_CLASSES.split(' '));--%>
<%--            button.classList.remove(...INACTIVE_CLASSES.split(' '));--%>
<%--            button.classList.add(...INACTIVE_CLASSES.split(' '));--%>
<%--        });--%>

<%--        const activeButton = document.getElementById(activeBtnId);--%>
<%--        if (activeButton) {--%>
<%--            activeButton.classList.remove(...INACTIVE_CLASSES.split(' '));--%>
<%--            activeButton.classList.add(...ACTIVE_CLASSES.split(' '));--%>
<%--        }--%>

<%--        fetch(contextPath + '/search?keyword=' + encodeURIComponent(keyword) + '&searchType=' + type)--%>
<%--            .then(res => res.text())--%>
<%--            .then(html => {--%>
<%--                const container = document.getElementById("search-results");--%>
<%--                container.innerHTML = html;--%>

<%--                if (window.tailwind && window.tailwind.refresh) {--%>
<%--                    window.tailwind.refresh();--%>
<%--                }--%>

<%--                bindFilterEvents();--%>
<%--            });--%>
<%--    }--%>

<%--    function bindFilterEvents() {--%>
<%--        const checkboxes = document.querySelectorAll("input[type=checkbox]");--%>
<%--        checkboxes.forEach(cb => {--%>
<%--            cb.removeEventListener("change", updateFilter); // tránh gắn trùng--%>
<%--            cb.addEventListener("change", updateFilter);--%>
<%--        });--%>
<%--    }--%>

<%--    function updateFilter() {--%>
<%--        const selectedStatus = Array.from(document.querySelectorAll("input[name=status]:checked")).map(cb => cb.value);--%>
<%--        const selectedGenres = Array.from(document.querySelectorAll("input[name=genre]:checked")).map(cb => cb.value);--%>

<%--        const params = new URLSearchParams();--%>
<%--        params.append("searchType", "filter");--%>
<%--        if (selectedStatus.length > 0) params.append("status", selectedStatus.join(","));--%>
<%--        if (selectedGenres.length > 0) params.append("genres", selectedGenres.join(","));--%>

<%--        fetch(contextPath + "/search?" + params.toString(), {--%>
<%--            method: "GET",--%>
<%--            headers: { "X-Requested-With": "XMLHttpRequest" }--%>
<%--        })--%>
<%--            .then(res => res.text())--%>
<%--            .then(html => {--%>
<%--                document.querySelector("#result-container").innerHTML = html;--%>
<%--                restoreFilterState(selectedStatus, selectedGenres);--%>
<%--            })--%>
<%--            .catch(err => console.error("Filter load error:", err));--%>
<%--    }--%>

<%--    function restoreFilterState(selectedStatus, selectedGenres) {--%>
<%--        document.querySelectorAll("input[name=status]").forEach(cb => {--%>
<%--            cb.checked = selectedStatus.includes(cb.value);--%>
<%--        });--%>
<%--        document.querySelectorAll("input[name=genre]").forEach(cb => {--%>
<%--            cb.checked = selectedGenres.includes(cb.value);--%>
<%--        });--%>
<%--    }--%>
<%--    document.addEventListener("DOMContentLoaded", () => {--%>
<%--        loadResults("title");--%>

<%--        const hasStatusChecked = document.querySelector("input[name=status]:checked");--%>
<%--        const hasGenreChecked = document.querySelector("input[name=genre]:checked");--%>

<%--        if (hasStatusChecked || hasGenreChecked) {--%>
<%--            // đợi trang render xong kết quả đầu tiên rồi mới filter--%>
<%--            setTimeout(() => {--%>
<%--                updateFilter();--%>
<%--            },1);--%>
<%--        }--%>
<%--    });--%>
<%--</script>--%>

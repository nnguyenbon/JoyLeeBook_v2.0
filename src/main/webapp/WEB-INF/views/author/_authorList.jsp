<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>

<!-- Author List Content -->
<div class="col-span-12 m-auto" id="result-container">
    <!-- Header: hiển thị tổng số kết quả -->
    <div class="col-span-8 col-start-5">
        <p class="text-sm text-gray-500">${size} authors found</p>
    </div>

    <!-- Danh sách authors -->
    <c:choose>
        <c:when test="${empty authorList}">
            <div class="text-center text-gray-500 mt-4">
                <p>No authors found.</p>
            </div>
        </c:when>
        <c:otherwise>
            <ul class="grid grid-cols-2 gap-6">
                <c:forEach var="author" items="${authorList}">
                    <li class="border border-gray-200 shadow-lg rounded-xl overflow-hidden bg-white hover:shadow-2xl transition duration-300">
                        <a href="${pageContext.request.contextPath}/profile?userId=${author.accountId}"
                           class="flex items-center p-4 gap-4">
                            <!-- Avatar -->
                            <div class="w-20 h-20 rounded-full overflow-hidden flex-shrink-0 bg-gradient-to-br from-[#6531B4] to-[#195DA9]">
                                        <img src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                                             class="w-full h-full object-cover"
                                             alt="${author.username}"/>



                            </div>

                            <!-- Thông tin author -->
                            <div class="flex-1 min-w-0">
                                <p class="font-bold text-xl truncate mb-1">${author.username}</p>

                                <div class="flex items-center gap-4 text-sm text-gray-600">
<%--                                    <span class="flex items-center gap-1">--%>
<%--                                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">--%>
<%--                                            <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>--%>
<%--                                        </svg>--%>
<%--&lt;%&ndash;                                        ${author.totalSeries} series&ndash;%&gt;--%>
<%--                                    </span>--%>

<%--                                    <span class="flex items-center gap-1">--%>
<%--                                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">--%>
<%--                                            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>--%>
<%--                                        </svg>--%>
<%--                                        ${author.totalFollowers} followers--%>
<%--                                    </span>--%>
                                </div>

<%--                                <!-- Bio nếu có -->--%>
<%--                                <c:if test="${not empty author.bio}">--%>
<%--                                    <p class="text-sm text-gray-500 mt-2 line-clamp-2">--%>
<%--                                            ${author.bio}--%>
<%--                                    </p>--%>
<%--                                </c:if>--%>
                            </div>

                            <!-- Icon mũi tên -->
                            <div class="flex-shrink-0">
                                <i class="fa-solid fa-chevron-right"></i>
                            </div>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

    <!-- Pagination -->
    <div class="flex justify-end items-center mt-3 mb-3 gap-2 text-sm">
        <c:if test="${totalPage > 1}">
            <!-- Nút prev -->
            <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                    data-page="${currentPage - 1}"
                    <c:if test="${currentPage == 1}">disabled</c:if>>
                &lt;&lt;
            </button>

            <c:if test="${currentPage > 3}">
                <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white" data-page="1">1</button>
                <span class="px-2 py-1">...</span>
            </c:if>

            <!-- Vòng lặp số trang -->
            <c:forEach var="i" begin="${currentPage - 2 > 1 ? currentPage - 2 : 1}"
                       end="${currentPage + 2 < totalPage ? currentPage + 2 : totalPage}">
                <button class="page-btn border rounded-md px-2 py-1
                                ${i == currentPage ? 'bg-blue-500 text-white' : 'hover:bg-gray-100 bg-white'}"
                        data-page="${i}">${i}</button>
            </c:forEach>

            <c:if test="${currentPage < totalPage - 2}">
                <span class="px-2 py-1">...</span>
                <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white" data-page="${totalPage}">
                        ${totalPage}
                </button>
            </c:if>

            <!-- Nút next -->
            <button class="page-btn border rounded-md px-2 py-1 hover:bg-gray-100 bg-white"
                    data-page="${currentPage + 1}"
                    <c:if test="${currentPage == totalPage}">disabled</c:if>>
                &gt;&gt;
            </button>
        </c:if>
    </div>
</div>
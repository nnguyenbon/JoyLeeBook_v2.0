<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/13/2025
  Time: 9:23 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="col-span-8 col-start-5">
    <p class="text-sm text-gray-500">${seriesInfoDTOList.size()} results</p>
</div>
<div class="space-y-6">
    <c:forEach var="series" items="${seriesInfoDTOList}">
        <a href="${pageContext.request.contextPath}/series?action=detail&seriesId=${series.seriesId}" class="block mb-4">
            <div class="flex gap-4 bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                <img src="./${series.coverImgUrl}" alt="cover"
                     class="w-[120px] h-[160px] object-cover rounded-lg">
                <div class="flex-1">
                    <h3 class="font-semibold text-lg text-gray-800">${series.title}</h3>
                    <p class="text-sm text-gray-500 mb-1">by
                        <c:forEach var="author" items="${series.authorsName}" varStatus="loop">
                            ${author}<c:if test="${!loop.last}">, </c:if>
                        </c:forEach>
                    </p>
                    <p class="text-gray-700 text-sm mb-3">
                            ${series.description}
                    </p>
                    <div class="flex gap-2 mb-2">
                        <c:forEach var="category" items="${series.categories}">
                            <span class="bg-gray-100 text-gray-600 text-xs font-semibold px-2 py-0.5 rounded-full">${category}</span>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${series.status == 'Completed'}">
                                    <span class="w-20 text-center py-0.5 rounded-full bg-green-100 text-green-700 text-xs">
                                            ${series.status}
                                    </span>
                            </c:when>
                            <c:when test="${series.status == 'Ongoing'}">
                                    <span class="w-20 text-center py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs">
                                            ${series.status}
                                    </span>
                            </c:when>
                            <c:otherwise>
                                    <span class="w-20 text-center py-0.5 rounded-full bg-gray-100 text-gray-700 text-xs">
                                            ${series.status}
                                    </span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="flex items-center justify-between text-sm text-gray-500">
                                <span class="flex gap-2 items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="yellow" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-star-icon lucide-star">
                                        <path d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
                                    </svg> ${series.avgRating} (${series.countRatings})
                                </span>
                        <span>${series.totalChapters} Chapters</span>
                        <span>Updated: ${series.updatedAt}</span>
                    </div>
                </div>
            </div>
        </a>
    </c:forEach>
</div>

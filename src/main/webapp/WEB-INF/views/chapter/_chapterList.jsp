<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 11/10/2025
  Time: 11:03 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="col-span-10 col-start-2">
    <div class="flex justify-between items-center w-full mb-3">
        <h2 class="font-semibold text-xl ">Chapter List</h2>
        <a
                href="${pageContext.request.contextPath}/chapter/add?seriesId=${seriesId}"
                class="px-4 py-2 bg-sky-800 text-white rounded-lg hover:bg-sky-900 cursor-pointer transition duration-300" ${loginedUser.role == 'author' ? "" : "hidden"}>Create
            Chapter
            <i class="fa-solid fa-circle-plus"></i>
        </a>
    </div>
    <div class="space-y-3 border-2 border-neutral-400 p-6 rounded-lg  ">

        <c:choose>
            <c:when test="${chapterList.size() != 0}">
                <ul class="overflow-y-auto custom-scrollbar max-h-100">
                    <c:forEach var="chapter" items="${chapterList}">
                        <li class="flex justify-between items-center gap-4 border border-neutral-400 rounded-lg px-4 my-2 py-3 bg-white hover:bg-gray-50 cursor-pointer">
                            <a class="flex justify-between gap-2 items-center w-full"
                               href="${pageContext.request.contextPath}/chapter/detail?seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}">

                                <span>Chapter ${chapter.chapterNumber}: ${chapter.title}</span>
                                <div class="flex gap-4 items-center">
                                    <span class="text-sm text-gray-500">${chapter.totalLike} Likes · ${chapter.updatedAt}</span>
                                    <c:if test="${loginedUser.role == 'author'}">
                                        <c:choose>
                                            <c:when test="${chapter.approvalStatus == 'approved'}">
                                                <span class="bg-green-100 text-green-600 px-3 py-1 rounded-full font-medium">${chapter.approvalStatus}</span>
                                            </c:when>
                                            <c:when test="${chapter.approvalStatus == 'pending'}">
                                                <span class="bg-yellow-100 text-yellow-600 px-3 py-1 rounded-full font-medium">${chapter.approvalStatus}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="bg-red-100 text-red-600 px-3 py-1 rounded-full font-medium">${chapter.approvalStatus}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </div>
                            </a>
                            <c:if test="${loginedUser.role == 'author'}">
                                <div class="flex gap-2 items-center">
                                    <a type="button"
                                       class="text-green-600 hover:text-green-700 hover:scale-110 transition-all duration-300"
                                       href="${pageContext.request.contextPath}/chapter/edit?seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}">
                                        <i class="fa-regular fa-pen-to-square"></i>
                                    </a>

                                    <button type="button"
                                            class="upload-chapter-btn text-blue-600 hover:text-blue-700 hover:scale-110 transition-all duration-300"
                                            data-series-id="${chapter.seriesId}"
                                            data-chapter-id="${chapter.chapterId}">
                                        <i class="fa-solid fa-arrow-up-from-bracket"></i>
                                    </button>

                                    <a type="button"
                                       class="text-red-600 hover:text-red-700 hover:scale-110 transition-all duration-300"
                                       href="${pageContext.request.contextPath}/chapter/delete?seriesId=${chapter.seriesId}&chapterId=${chapter.chapterId}">
                                        <i class="fa-regular fa-trash-can"></i>
                                    </a>
                                </div>
                            </c:if>
                        </li>

                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <div>This series has no chapters</div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
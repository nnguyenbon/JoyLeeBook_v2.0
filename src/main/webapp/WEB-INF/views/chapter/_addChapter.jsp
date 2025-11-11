<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 12:48 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<main class="mt-10 grid grid-cols-12 gap-x-8 gap-y-4 items-center">
    <p class="col-span-12 text-center font-bold text-3xl">
        <c:if test="${not empty chapter}">
            Edit Chapter
        </c:if>
        <c:if test="${empty chapter}">
            Create New Chapter
        </c:if>
    </p>
    <p class="col-span-12 text-center fold-semibold text-xl text-gray-500">
        Create a new chapter for "${series.title}"
    </p>

    <form
            action="${pageContext.request.contextPath}/chapter/${action}?seriesId=${series.seriesId}&chapterId=${chapter.chapterId}"
            method="POST"
            class="col-span-12 mx-auto"
    >
        <div class="md:w-3xl">
            <div
                    class="border border-gray-400 rounded-xl flex flex-col p-4 gap-1"
            >
                <label for="title" class="text-lg">Chapter Title *</label>
                <input
                        class="px-3 py-1 rounded-lg border border-gray-400 focus:outline-none focus:border-gray-600"
                        type="text"
                        id="title"
                        name="title"
                        placeholder="Enter title"
                        value="${chapter.title}"
                        required
                />
                <p id="error-title" class="text-xs text-red-500 mt-1 tracking-wide"></p>
            </div>

            <div
                    class="border border-gray-400 rounded-xl flex flex-col p-4 gap-1 mt-6"
            >
                <label for="chapterNumber">Chapter Number *</label>
                <input
                        class="px-3 py-1 rounded-lg border border-gray-400 focus:outline-none focus:border-gray-600"
                        type="number"
                        id="chapterNumber"
                        name="chapterNumber"
                        placeholder="Enter chapter number"
                        value="${chapter.chapterNumber}"
                        min="0"
                />
                <p class="text-sm text-gray-400 mt-1 ml-2">If left blank, the system will use the next available
                    index.</p>
            </div>

            <div
                    class="border border-gray-400 rounded-xl flex flex-col p-4 gap-1 mt-6"
            >
                <label for="content">Content *</label>
                <textarea
                        class="px-3 py-1 rounded-lg border border-gray-400 focus:outline-none focus:border-gray-600"
                        type="text"
                        id="content"
                        name="content"
                        rows="10"
                        required
                >${chapter.content}</textarea>
            </div>

            <div class="flex gap-2 justify-between my-4 ">
                <c:if test="${owner}">
                    <div>
                        <label for="status">Status: </label>
                        <select class="border border-gray-500 rounded-sm px-2 py-1" name="status" id="status">
                            <option value="draft"
                                    <c:if test="${chapter.status == 'draft'}">selected</c:if>
                            >Draft
                            </option>
                            <option value="published"
                                    <c:if test="${chapter.status == 'published'}">selected</c:if>
                            >Public
                            </option>
                        </select>
                    </div>
                </c:if>
                <div class="flex gap-2">
                    <button type="submit"
                            class="px-3 py-1 bg-sky-600 text-white rounded-md cursor-pointer hover:bg-sky-700 transition duration-300">
                        <c:if test="${not empty chapter}">
                            Edit
                        </c:if>
                        <c:if test="${empty chapter}">
                            Create
                        </c:if>
                    </button>
                    <a class="block px-3 py-1 bg-neutral-300 rounded-md hover:bg-neutral-400 transition duration-300"
                       href="${pageContext.request.contextPath}/series/detail?seriesId=${series.seriesId}">
                        Cancel
                    </a>

                </div>
            </div>
        </div>
    </form>
</main>

<script>
    const titleElement = document.querySelector('input[type=text]');
    const errorTitleElement = document.querySelector("#error-title");
    console.log(titleElement);

    titleElement.addEventListener('input', () => {
        if (titleElement.value.length >= 20) {
            errorTitleElement.textContent = "title cannot exceed 70 characters";
        } else {
            errorTitleElement.textContent = "";
        }
    });
</script>
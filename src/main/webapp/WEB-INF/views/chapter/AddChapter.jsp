<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/10/25
  Time: 12:48 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Create New Chapter — <c:out value="${series.title}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Istok+Web:wght@400;700&family=Inter:wght@400;700&display=swap"
          rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }

        .istok-web {
            font-family: 'Istok Web', sans-serif;
        }
    </style>
</head>
<body class="bg-[#FCFCFC]">

<div class="bg-white border-b border-[#D3D8DE]">
    <div class="container mx-auto h-[60px] flex items-center justify-between">
        <div class="flex items-center">
            <h1 class="istok-web font-bold text-3xl text-center text-black"><c:out value="${series.title}"/></h1>
        </div>
    </div>
</div>

<div class="container mx-auto py-10">
    <div class="text-center mb-4">
        <h2 class="istok-web font-bold text-5xl leading-tight">Create new Chapter</h2>
        <p class="istok-web text-2xl text-[rgba(68,68,68,0.58)] mt-2">Create a new chapter for your series</p>
    </div>

    <c:if test="${not empty error}">
        <div class="max-w-3xl mx-auto bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
             role="alert">
            <span class="block sm:inline"><c:out value="${error}"/></span>
        </div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="max-w-3xl mx-auto bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative"
             role="alert">
            <span class="block sm:inline"><c:out value="${success}"/></span>
        </div>
    </c:if>

    <div class="flex justify-center">
        <div class="w-full max-w-3xl">
            <form action="${pageContext.request.contextPath}/add-chapter" method="post" id="addChapterForm"
                  class="space-y-6">
                <input type="hidden" name="seriesId" value="<c:out value='${series.seriesId}'/>"/>

                <div class="flex items-center space-x-4">
                    <label for="chapterTitle" class="w-1/4 text-xl">Chapter Title *</label>
                    <input
                            type="text"
                            class="block w-3/4 border border-[#D9D9D9] rounded-lg py-2 px-4 text-lg"
                            id="chapterTitle"
                            name="title"
                            required
                            maxlength="255"
                            value="<c:out value='${param.title}'/>"
                            placeholder="Prologue: Relic Awakens"
                    />
                </div>

                <div class="flex items-center space-x-4">
                    <label for="chapterIndex" class="w-1/4 text-xl">Chapter Index *</label>
                    <input
                            type="number"
                            class="block w-3/4 border border-[#D9D9D9] rounded-lg py-2 px-4 text-lg"
                            id="chapterIndex"
                            name="chapterNumber"
                            min="1"
                            value="<c:out value='${param.chapterNumber}'/>"
                            placeholder="Enter chapter index"
                    />
                </div>
                <small class="text-muted">If left blank, the system will use the next available index.</small>

                <div>
                    <label for="chapterContent" class="text-xl">Content *</label>
                    <textarea
                            class="block w-full border border-[#D3D8DE] rounded-lg p-4 mt-2 h-64"
                            id="chapterContent"
                            name="content"
                            placeholder="Type your content ..."><c:out value="${param.content}"/></textarea>
                </div>

                <div class="flex justify-center space-x-4">
                    <button type="submit" class="bg-[#0A3776] text-white font-bold text-sm rounded-lg px-6 py-2">
                        Create
                    </button>
                    <a href="${pageContext.request.contextPath}/manage-series?id=${series.seriesId}"
                       class="border border-[#D9D9D9] bg-white text-black font-normal text-sm rounded-lg px-6 py-2">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
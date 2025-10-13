<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/10/2025
  Time: 11:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Document</title>
    <link rel="stylesheet" href="css/styles.css"/>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet"
    />
    <script src="./js/main.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>

<!-- Hero Section -->
<section class="relative w-full h-auto">
    <img src="./img/shared/hero-reading.png" class="w-full" alt=""/>
    <div
            class="bg-gradient-to-r from-[#6531B4] to-[#195DA9] absolute top-0 bottom-0 right-0 left-0 opacity-95"
    ></div>
    <div
            class="absolute top-1/2 left-1/2 -translate-1/2 text-center text-white"
    >
        <div class="text-6xl font-bold">
            <p>Discover Your Next</p>
            <p
                    class="bg-linear-to-r from-orange-300 to-neutral-900 bg-clip-text text-transparent my-2"
            >
                Great Story
            </p>
        </div>

        <p class="text-lg max-w-2/3 text-center mx-auto mt-6 mb-10">
            Dive into thousands of captivating novel with passionate readers, and
            embark on extraodinary adventures
        </p>

        <ul class="flex justify-center gap-12">
            <li>
                <p class="font-bold text-2xl">50K+</p>
                <p class="opacity-50 text-sm">SERIES</p>
            </li>
            <li>
                <p class="font-bold text-2xl">2M+</p>
                <p class="opacity-50 text-sm">READER</p>
            </li>
            <li>
                <p class="font-bold text-2xl">15K+</p>
                <p class="opacity-50 text-sm">AUTHOR</p>
            </li>
            <li>
                <p class="font-bold text-2xl">10M+</p>
                <p class="opacity-50 text-sm">CHAPTER</p>
            </li>
        </ul>
    </div>
</section>
<main
        class="max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-x-8 relative"
>
    <section class="flex justify-center gap-16 col-span-12">
        <div class="w-3/4">
            <p class="pt-6 pb-4 font-bold text-3xl">Hot Series</p>
            <div
                    class="border border-neutral-800/50 rounded-xl py-6 overflow-hidden shadow-xl"
            >
                <div
                        class="flex overflow-x-hidden snap-x snap-mandatory scroll-smooth"
                >
                    <c:forEach
                            var="hotSeries"
                            items="${hotSeriesList}"
                            varStatus="loop"
                    >
                        <div
                                id="slide-${loop.index + 1}"
                                class="snap-start shrink-0 w-full h-full origin-center flex gap-16 px-16"
                        >
                            <div class="flex-1">
                                <img
                                        src="./img/thenewkidinschool.png"
                                        class="w-full h-full"
                                        alt="hehe"
                                />
                            </div>

                        <div class="flex-3 ">
                            <div>
                                <p class="text-2xl font-bold">${hotSeries.title}</p>
                                <p class="text-gray-400 text-lg">
                                    by <span class="text-primary">InstinctualWater</span>
                                </p>
                                <p class="mt-2 whitespace-pre-line text-lg">
                                        ${hotSeries.description}
                                </p>
                            </div>
                            <a href="${pageContext.request.contextPath}/series-detail?seriesId=${hotSeries.seriesId}"
                               class="inline-block py-2 px-4 mt-4 bg-[#195DA9] text-white rounded-md hover:bg-blue-600">
                                Read now
                            </a>
                        </div>

                <ul class="flex justify-center gap-4 py-2" id="indicator">
                    <li>
                        <a
                                class="block size-3 bg-sky-500 rounded-full border border-sky-500"
                                href="#slide-1"
                                onclick="toggleIndicator(1, event)"
                        ></a>
                    </li>
                    <li>
                        <a
                                class="block size-3 rounded-full border border-sky-500"
                                href="#slide-2"
                                onclick="toggleIndicator(2, event)"
                        ></a>
                    </li>
                    <li>
                        <a
                                class="block size-3 rounded-full border border-sky-500"
                                href="#slide-3"
                                onclick="toggleIndicator(3, event)"
                        ></a>
                    </li>
                </ul>
            </div>

            <ul class="flex justify-center gap-4 mt-6 mb-3" id="indicator">
                <li>
                    <a
                            class="block size-2 bg-sky-500 rounded-full border border-sky-500"
                            href="#slide-1"
                            onclick="toggleIndicator(1)"
                    ></a>
                </li>
                <li>
                    <a
                            class="block size-2 rounded-full border border-sky-500"
                            href="#slide-2"
                            onclick="toggleIndicator(2)"
                    ></a>
                </li>
                <li>
                    <a
                            class="block size-2 rounded-full border border-sky-500"
                            href="#slide-3"
                            onclick="toggleIndicator(3)"
                    ></a>
                </li>
            </ul>
        </div>
    </div>

    <!-- Top Weekly Series Section (col-span-3) -->
    <div class="col-span-3 flex flex-col weekly-top">
        <p class="pt-6 pb-4 font-bold text-3xl">Top Weekly Series</p>
        <div class="border border-gray-300 rounded-xl pr-3 flex-1 shadow-xl">
            <ul class="">
                <c:forEach var="weeklySeries" items="${weeklySeriesList}" varStatus="loop">
                    <li class="flex justify-between items-center py-2
                   ${loop.index == 0 ? 'text-[#E23636] font-semibold' :
                     loop.index == 1 ? 'text-[#F5A83D] font-semibold' :
                     loop.index == 2 ? 'text-[#195DA9] font-semibold' : 'text-gray-700'}">
                        <div class="flex items-center gap-3">
                            <span class="font-semibold w-6 text-right">${loop.index + 1}.</span>
                            <p class="truncate">${weeklySeries.title}</p>
                        </div>
                        <span class="float-right">${weeklySeries.avgRating}</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <!-- New Release Section (col-span-9) -->
    <div class="col-span-9">
        <p class="font-bold text-3xl pt-6 pb-4">New release</p>

        <ul class="grid grid-cols-4 gap-6">
            <c:forEach var="newReleaseSeries" items="${newReleaseSeriesList}" varStatus="loop">
                <li class="relative group border border-gray-200 shadow-lg rounded-xl overflow-hidden bg-white hover:shadow-2xl transition duration-300">
                    <a href="${pageContext.request.contextPath}/series-detail?seriesId=${newReleaseSeries.seriesId}">
                        <!-- Hình ảnh -->
                        <div class="aspect-[3/4] overflow-hidden relative">
                            <img
                                    src="./img/thenewkidinschool.png"
                                    class="w-full h-full object-cover transition duration-300 group-hover:opacity-40"
                                    alt="${newReleaseSeries.title}"
                            />

                            <!-- Overlay chứa nút -->
                            <div class="absolute inset-0 flex flex-col items-center justify-center gap-4 opacity-0 group-hover:opacity-100 transition duration-300">
                                <a href="${pageContext.request.contextPath}/chapter-content?seriesId=${newReleaseSeries.seriesId}&chapterId=1"
                                   class="bg-[#195DA9] text-white px-4 py-2 rounded-md hover:bg-blue-700 transition">
                                    Read now
                                </a>
                                <a href="${pageContext.request.contextPath}/series-detail?seriesId=${newReleaseSeries.seriesId}"
                                   class="bg-white text-[#195DA9] px-7 py-2 rounded-md border border-[#195DA9] hover:bg-gray-100 transition">
                                    Detail
                                </a>
                            </div>
                        </div>

                        <!-- Nội dung -->
                        <div class="p-3">
                            <ul class="flex flex-wrap gap-2 text-xs">
                                <c:forEach var="category" items="${newReleaseSeries.categories}" varStatus="status">
                                    <c:if test="${status.index < 2}">
                                        <li class="rounded-md bg-blue-100 px-1">${category}</li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-lg truncate mt-1">
                                    ${newReleaseSeries.title}
                            </p>
                            <div class="flex justify-between text-sm text-gray-500">
                                <p>by <span class="text-gray-700 font-medium">Alex</span></p>
                                <p>${newReleaseSeries.totalChapters} chapters</p>
                            </div>
                            <p class="text-sm text-gray-600">
                                ★ ${newReleaseSeries.avgRating}
                                <span class="text-gray-400">(${newReleaseSeries.countRatings})</span>
                            </p>
                        </div>
                    </a>
                </li>

            </c:forEach>
        </ul>
    </div>

    <!-- Top Reader Points Section (col-span-3) -->
    <div class="col-span-3 flex flex-col weekly-top">
        <p class="pt-6 pb-4 font-bold text-3xl">Top reader points</p>
        <div class="border border-gray-300 rounded-xl pr-3 flex-1 shadow-xl">
            <ul class="">
                <c:forEach var="user" items="${userList}" varStatus="loop">
                    <li class="flex justify-between items-center py-2
                   ${loop.index == 0 ? 'text-[#E23636] font-semibold' :
                     loop.index == 1 ? 'text-[#F5A83D] font-semibold' :
                     loop.index == 2 ? 'text-[#195DA9] font-semibold' : 'text-gray-700'}">
                        <div class="flex items-center gap-3">
                            <span class="font-semibold w-6 text-right">${loop.index + 1}.</span>
                            <p class="truncate">${user.username}</p>
                        </div>
                        <span class="float-right">${user.points}</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <!-- Recently Updated Section (col-span-12) -->
    <div class="col-span-12 flex justify-between items-center">
        <p class="font-bold text-3xl pt-6">Recently Update</p>
        <a href="./series.html" class="hover:text-neutral-600 text-right">View all</a>
    </div>


    <ul class="col-span-12 grid grid-cols-5 gap-[30px]">
        <c:forEach var="recentlyUpdatedSeries" items="${recentlyUpdatedSeriesList}" varStatus="loop">
            <li class="border border-neutral-900/50 rounded-xl overflow-hidden hover:shadow-2xl transition duration-200">
                <a href="${pageContext.request.contextPath}/series-detail?seriesId=${recentlyUpdatedSeries.seriesId}" class="flex flex-col h-full">
                    <!-- Hình ảnh -->
                    <div class="aspect-[3/4] overflow-hidden">
                        <img
                                src="./img/thenewkidinschool.png"
                                class="w-full h-full object-cover"
                                alt="${recentlyUpdatedSeries.title}"
                        />
                    </div>

                    <!-- Nội dung -->
                    <div class="p-3 flex flex-col justify-between flex-grow">
                        <div>
                            <ul class="flex flex-wrap gap-2 text-xs mb-1">
                                <c:forEach var="category" items="${recentlyUpdatedSeries.categories}" varStatus="status">
                                    <c:if test="${status.index < 2}">
                                        <li class="rounded-md bg-blue-100 px-1">${category}</li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-lg truncate mb-1">
                                    ${recentlyUpdatedSeries.title}
                            </p>
                        </div>
                        <div class="text-sm opacity-70">
                            <div class="flex justify-between">
                                <p>by <span class="font-medium">Alex</span></p>
                                <p>${recentlyUpdatedSeries.totalChapters} chapters</p>
                            </div>
                            <p>
                                ★ ${recentlyUpdatedSeries.avgRating}
                                (${recentlyUpdatedSeries.countRatings})
                            </p>
                        </div>
                    </div>
                </a>
            </li>
        </c:forEach>
    </ul>
</main>

<!-- Explore by Genre Section -->
<section class="bg-gradient-to-r from-[#6531B4] to-[#195DA9] py-20 mt-8 text-center">
    <div class="">
        <p class="font-bold text-6xl mb-12 text-white">Explore by Genre</p>

        <ul class="max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-[30px]">
            <c:forEach var="category" items="${categoryList}" varStatus="loop">
                <c:if test="${loop.index < 6}">
                    <li class="col-span-2 bg-white rounded-2xl p-8 text-center shadow-lg
                               hover:scale-105 hover:shadow-2xl transition duration-300">
                        <p class="text-[#195DA9] text-2xl font-semibold mb-2">${category.name}</p>
                        <p class="text-neutral-500 text-lg">${category.totalSeries} series</p>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </div>
</section>

<!-- Completed Series Section -->
<main class="max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-[30px]">
    <!-- Tiêu đề + View all -->
    <div class="col-span-12 flex justify-between items-center">
        <p class="font-bold text-3xl">Completed Series</p>
        <a href="./series.html" class="hover:text-neutral-600 text-right">View all</a>
    </div>

    <!-- Danh sách series -->
    <ul class="col-span-12 grid grid-cols-5 gap-[30px]">
        <c:forEach var="completedSeries" items="${recentlyUpdatedSeriesList}" varStatus="loop">
            <li class="border border-neutral-900/50 rounded-xl overflow-hidden hover:shadow-2xl transition duration-200">
                <a href="${pageContext.request.contextPath}/series-detail?seriesId=${completedSeries.seriesId}" class="flex flex-col h-full">
                    <!-- Hình ảnh -->
                    <div class="aspect-[3/4] overflow-hidden">
                        <img
                                src="./img/thenewkidinschool.png"
                                class="w-full h-full object-cover"
                                alt="${completedSeries.title}"
                        />
                    </div>

                    <!-- Nội dung -->
                    <div class="p-3 flex flex-col justify-between flex-grow">
                        <div>
                            <ul class="flex flex-wrap gap-2 text-xs mb-1">
                                <c:forEach var="category" items="${completedSeries.categories}" varStatus="status">
                                    <c:if test="${status.index < 2}">
                                        <li class="rounded-md bg-blue-100 px-1">${category}</li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-lg truncate mb-1">
                                    ${completedSeries.title}
                            </p>
                        </div>
                        <div class="text-sm opacity-70">
                            <div class="flex justify-between">
                                <p>by <span class="font-medium">Alex</span></p>
                                <p>${completedSeries.totalChapters} chapters</p>
                            </div>
                            <p>
                                ★ ${completedSeries.avgRating}
                                (${completedSeries.countRatings})
                            </p>
                        </div>
                    </div>
                </a>
            </li>
        </c:forEach>
    </ul>

    <!-- Call to Action Section -->
    <div class="col-span-12 bg-gradient-to-r from-[#4B2BAE] to-[#1A56B6] rounded-2xl p-16 text-center text-white relative overflow-hidden mb-10">
        <!-- Lớp overlay để làm mờ ảnh nền -->
        <div class="absolute inset-0 bg-[url('./img/background-books.jpg')] bg-cover bg-center opacity-20"></div>

        <!-- Nội dung chính -->
        <div class="relative z-10">
            <h2 class="text-5xl font-extrabold mb-4">Unleash Your Imagination</h2>
            <p class="text-lg text-gray-200 mb-8 max-w-2xl mx-auto">
                Share your stories with millions of readers, inspire new worlds, and become part of a thriving community of authors.
            </p>
            <a
                    class="bg-gradient-to-r from-[#341661] via-[#491894] to-[#195DA9] font-black text-lg px-3 py-1 rounded-3xl border-2 border-[#E3E346]">
                    <span
                            class="bg-gradient-to-r from-[#D2D200] via-[#F8F881] to-[#999400] bg-clip-text text-transparent">
                        Write Now
                    </span>
            </a>
        </div>
    </div>
</main>

<!-- Footer Section -->
<jsp:include page="/WEB-INF/views/components/_footer.jsp" />
<script src="js/main.js"></script>
</body>
</html>
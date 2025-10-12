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
</head>
<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>

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

                            <div class="flex-3">
                                <p class="text-2xl font-bold">${hotSeries.title}</p>
                                <p class="text-gray-400 text-lg">
                                    by <span class="text-primary">InstinctualWater</span>
                                </p>
                                <p class="mt-2 whitespace-pre-line text-lg">
                                        ${hotSeries.description}
                                </p>
                                <a
                                        href="${pageContext.request.contextPath}/series-detail?seriesId=${hotSeries.seriesId}"
                                        class="inline-block py-2 px-4 mt-4 bg-primary rounded-md"
                                >
                                    Read now
                                </a>
                            </div>
                        </div>
                    </c:forEach>
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
        </div>

        <div class="w-1/4 flex flex-col weekly-top">
            <p class="pt-6 pb-4 font-bold text-3xl">Top Weekly Series</p>
            <div
                    class="border border-neutral-800/50 rounded-xl py-6 px-8 flex-1 shadow-xl"
            >
                <ul class="text-xl flex flex-col justify-between gap-2 h-full">
                    <c:forEach
                            var="weeklySeries"
                            items="${weeklySeriesList}"
                            varStatus="loop"
                    >
                        <li class="flex justify-between gap-4">
                            <p class="truncate">
                    <span class="pr-4">${loop.index + 1}</span
                    >${weeklySeries.title}
                            </p>
                            <span>${weeklySeries.avgRating}</span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </section>

    <section class="flex justify-center gap-16 py-8 col-span-12">
        <div class="w-3/4">
            <p class="font-bold text-3xl pt-6 pb-4">New release</p>
            <ul class="flex gap-12">
                <c:forEach
                        var="newReleaseSeries"
                        items="${newReleaseSeriesList}"
                        varStatus="loop"
                >
                    <li
                            class="border border-neutral-900/50 shadow-2xl w-1/4 rounded-xl overflow-hidden"
                    >
                        <a
                                href="${pageContext.request.contextPath}/series-detail?seriesId=${newReleaseSeries.seriesId}"
                        >
                            <div>
                                <img
                                        src="./img/thenewkidinschool.png"
                                        class="w-full"
                                        alt="hehe"
                                />
                            </div>
                            <div class="p-2">
                                <ul class="flex gap-2 text-xs">
                                    <c:forEach
                                            var="category"
                                            items="${newReleaseSeries.categories}"
                                    >
                                        <li class="rounded-md bg-amber-500 px-2">
                                                ${category}
                                        </li>
                                    </c:forEach>
                                </ul>
                                <p class="font-semibold text-xl truncate my-1">
                                        ${newReleaseSeries.title}
                                </p>
                                <div class="flex justify-between opacity-50 text-sm">
                                    <p>by <span>Alex</span></p>
                                    <p>${newReleaseSeries.totalChapters} chapters</p>
                                </div>
                                <p>
                                    ★ ${newReleaseSeries.avgRating}
                                    (${newReleaseSeries.countRatings})
                                </p>
                            </div>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="w-1/4 flex flex-col weekly-top">
            <p class="pt-6 pb-4 font-bold text-3xl">Top reader points</p>
            <div
                    class="shadow-xl border border-neutral-800/50 rounded-xl py-6 px-8 flex-1"
            >
                <ul class="text-xl flex flex-col justify-between gap-2 h-full">
                    <c:forEach var="user" items="${userList}" varStatus="loop">
                        <li class="flex justify-between gap-4">
                            <p class="truncate">
                                <span class="pr-4">${loop.index + 1}</span>${user.username}
                            </p>
                            <span>${user.points}</span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </section>

    <section class="col-span-12">
        <p class="font-bold text-3xl pt-6">Recently Update</p>
        <p class="text-right">
            <a href="./series.html" class="hover:text-neutral-600">View all</a>
        </p>
        <ul class="flex gap-12 py-4">
            <c:forEach
                    var="recentlyUpdatedSeries"
                    items="${recentlyUpdatedSeriesList}"
                    varStatus="loop"
            >
                <li
                        class="border border-neutral-900/50 shadow-2xl w-1/4 rounded-xl overflow-hidden"
                >
                    <a
                            href="${pageContext.request.contextPath}/series-detail?seriesId=${recentlyUpdatedSeries.seriesId}"
                    >
                        <div>
                            <img
                                    src="./img/thenewkidinschool.png"
                                    class="w-full"
                                    alt="hehe"
                            />
                        </div>
                        <div class="p-2">
                            <ul class="flex gap-2 text-xs">
                                <c:forEach
                                        var="category"
                                        items="${recentlyUpdatedSeries.categories}"
                                >
                                    <li class="rounded-md bg-amber-500 px-2">${category}</li>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-xl truncate my-1">
                                    ${recentlyUpdatedSeries.title}
                            </p>
                            <div class="flex justify-between opacity-50 text-sm">
                                <p>by <span>Alex</span></p>
                                <p>${recentlyUpdatedSeries.totalChapters} chapters</p>
                            </div>
                            <p>
                                ★ ${recentlyUpdatedSeries.avgRating}
                                (${recentlyUpdatedSeries.countRatings})
                            </p>
                        </div>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </section>

    <section
            class="bg-gradient-to-r from-[#6531B4] to-[#195DA9] py-20 text-white mt-8 text-center col-span-12 relative left-1/2 right-1/2 -mx-[50vw] w-screen"
    >
        <p class="font-bold text-6xl">Explore by Genre</p>
        <ul class="flex justify-center gap-8 mt-12">
            <c:forEach var="category" items="${categoryList}" varStatus="loop">
                <li class="p-12 bg-white rounded-lg">
                    <p class="text-primary mb-2 text-3xl font-semibold">
                            ${category.name}
                    </p>
                    <p class="text-neutral-500" text-xl>
                            ${category.totalSeries} series
                    </p>
                </li>
            </c:forEach>
        </ul>
    </section>

    <section class="col-span-12">
        <p class="font-bold text-3xl pt-12">Completed Series</p>
        <p class="text-right">
            <a href="./series.html" class="hover:text-neutral-600">View all</a>
        </p>
        <ul class="flex gap-12 py-4">
            <c:forEach
                    var="completedSeries"
                    items="${completedSeriesList}"
                    varStatus="loop"
            >
                <li
                        class="border border-neutral-900/50 shadow-2xl w-1/4 rounded-xl overflow-hidden"
                >
                    <a
                            href="${pageContext.request.contextPath}/series-detail?seriesId=${completedSeries.seriesId}"
                    >
                        <div>
                            <img
                                    src="./img/thenewkidinschool.png"
                                    class="w-full"
                                    alt="hehe"
                            />
                        </div>
                        <div class="p-2">
                            <ul class="flex gap-2 text-xs">
                                <c:forEach
                                        var="category"
                                        items="${completedSeries.categories}"
                                >
                                    <li class="rounded-md bg-amber-500 px-2">${category}</li>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-xl truncate my-1">
                                    ${completedSeries.title}
                            </p>
                            <div class="flex justify-between opacity-50 text-sm">
                                <p>by <span>Alex</span></p>
                                <p>${completedSeries.totalChapters} chapters</p>
                            </div>
                            <p>
                                ★ ${completedSeries.avgRating}
                                (${completedSeries.countRatings})
                            </p>
                        </div>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </section>

    <section
            class="bg-gradient-to-r from-[#6531B4] to-[#195DA9] py-20 text-white mt-8 text-center rounded-4xl col-span-12"
    >
        <div class="w-1/2 m-auto">
            <p class="font-bold text-6xl">Unleash Your Imagination</p>
            <p class="text-2xl py-8">
                Share your stories with millions of readers, inspire new worlds, and
                become part of a thriving community of authors.
            </p>
            <a
                    href="./signup.html"
                    class="text-2xl font-semibold inline-block bg-primary py-4 px-8 rounded-lg hover:bg-sky-200"
            >Start Writing</a
            >
        </div>
    </section>
</main>

<footer class="border border-t-neutral-800/70 text-center mt-8">
    <div class="max-w-1/3 mx-auto">
        <div class="w-44 mx-auto">
            <img
                    src="./img/logo.png"
                    class="w-full h-full"
                    alt="day la mot cai logo"
            />
        </div>
        <p class="text-xl text-neutral-500">
            Your gateway to endless stories. Discover, read, and connect with
            millions of readers worldwide in the ultimate reading experience.
        </p>
        <ul class="flex justify-center gap-6 mt-4 opacity-50">
            <li class="size-6">
                <img
                        src="./img/iconSVG/twitter.svg"
                        class="w-full h-full"
                        alt="brand"
                />
            </li>
            <li class="size-6">
                <img
                        src="./img/iconSVG/facebook.svg"
                        class="w-full h-full"
                        alt="brand"
                />
            </li>
            <li class="size-6">
                <img
                        src="./img/iconSVG/instagram.svg"
                        class="w-full h-full"
                        alt="brand"
                />
            </li>
            <li class="size-6">
                <img
                        src="./img/iconSVG/envelope.svg"
                        class="w-full h-full"
                        alt="brand"
                />
            </li>
        </ul>
        <p class="pt-4 pb-2 text-neutral-600">
            © 2024 JoyLeeBook. All rights reserved.
        </p>
    </div>
</footer>
<script src="js/main.js"></script>
</body>
</html>

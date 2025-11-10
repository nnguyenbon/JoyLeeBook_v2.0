<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/10/2025
  Time: 11:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<section class="relative h-auto left-1/2 right-1/2 -mx-[50vw] w-[100vw]">
    <img src="${pageContext.request.contextPath}/img/shared/hero-reading.png" class="w-full" alt=""/>
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
<main class="">
    <section class="mt-10 grid grid-cols-12 gap-x-5 relative">
        <div class="col-span-9">
            <p class="pt-6 pb-4 font-bold text-3xl">Hot Series</p>
            <div class="border border-gray-300 rounded-xl pt-8 overflow-hidden shadow-xl">
                <div class="flex overflow-x-hidden snap-x snap-mandatory scroll-smooth">
                    <c:forEach
                            var="hotSeries"
                            items="${hotSeriesList}"
                            varStatus="loop"
                    >
                        <div
                                id="slide-${hotSeries.seriesId}"
                                class="snap-start shrink-0 w-full h-full origin-center flex gap-16 px-16"
                        >
                            <div class="flex-1 overflow-hidden rounded-lg aspect-[3/4]">
                                <img
                                        src="${pageContext.request.contextPath}/${hotSeries.coverImgUrl}"
                                        class="w-full h-full"
                                        alt="${hotSeries.title}"
                                />
                            </div>

                            <div class="flex-3 flex flex-col justify-between">
                                <div>
                                    <p class="text-2xl font-bold truncate">${hotSeries.title}</p>
                                    <p class="text-gray-400">
                                        by <span class="text-primary">
                                        <c:forEach var="author" items="${hotSeries.authorNameList}" varStatus="loop">
                                            ${author}<c:if test="${!loop.last}">, </c:if>
                                        </c:forEach>
                                    </span>
                                    </p>
                                </div>
                                <p class="flex-1 mt-2 whitespace-pre-line text-lg line-clamp-5">${hotSeries.description}</p>
                                <a
                                        href="${pageContext.request.contextPath}/series/detail?seriesId=${hotSeries.seriesId}"
                                        class="inline-block w-32 text-center py-2 px-4 mt-2 bg-primary rounded-md"
                                >
                                    Read now
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <ul class="flex justify-center gap-4 mt-8 mb-4" id="indicator">
                    <c:forEach var="hotSeries" items="${hotSeriesList}" varStatus="loop">
                        <li>
                            <a
                                    class="block size-3 rounded-full border border-[#195da9]"
                                    href="#slide-${hotSeries.seriesId}"
                                    onclick="toggleIndicator(${loop.index}, event)"
                            ></a>
                        </li>
                    </c:forEach>
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
                   ${loop.index == 0 ? 'text-[#E23636] font-semibold text-lg mt-2' :
                     loop.index == 1 ? 'text-[#F5A83D] font-semibold' :
                     loop.index == 2 ? 'text-[#195DA9] font-semibold' : 'text-gray-700'}">
                            <div class="flex items-center gap-1">
                                <span class="font-semibold w-6 text-right">${loop.index + 1}.</span>
                                <p class="truncate w-48" title="${weeklySeries.title}">${weeklySeries.title} </p>
                            </div>
                            <span class="float-right">${weeklySeries.avgRating}</span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </section>

    <section class="mt-10 grid grid-cols-12 gap-x-5 relative">
        <!-- New Release Section (col-span-9) -->
        <div class="col-span-9">
            <p class="font-bold text-3xl pt-6 pb-4">New release</p>

            <ul class="flex  gap-8">
                <c:forEach var="newReleaseSeries" items="${newReleaseSeriesList}" varStatus="loop">
                    <li class="md:w-50 relative group border border-gray-200 shadow-lg rounded-xl overflow-hidden bg-white hover:shadow-2xl transition duration-300">
                        <a href="${pageContext.request.contextPath}/series/detail?seriesId=${newReleaseSeries.seriesId}">
                            <!-- Hình ảnh -->
                            <div class="aspect-[3/4] overflow-hidden relative">
                                <img
                                        src="${pageContext.request.contextPath}/${newReleaseSeries.coverImgUrl}"
                                        class="w-full h-full object-cover transition duration-300 group-hover:opacity-40"
                                        alt="${newReleaseSeries.title}"
                                />

                                <!-- Overlay chứa nút -->
                                <div class="absolute inset-0 flex flex-col items-center justify-center gap-4 opacity-0 group-hover:opacity-100 transition duration-300">
                                    <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${newReleaseSeries.seriesId}"
                                       class="bg-[#195DA9] text-white px-4 py-2 rounded-md hover:bg-blue-700 transition">
                                        Read now
                                    </a>
                                    <a href="${pageContext.request.contextPath}/series/detail?seriesId=${newReleaseSeries.seriesId}"
                                       class="bg-white text-[#195DA9] px-7 py-2 rounded-md border border-[#195DA9] hover:bg-gray-100 transition">
                                        Detail
                                    </a>
                                </div>
                            </div>

                            <!-- Nội dung -->
                            <div class="p-3">
                                <ul class="flex flex-wrap gap-2 text-xs">
                                    <c:forEach var="category" items="${newReleaseSeries.categoryList}" varStatus="status">
                                        <c:if test="${status.index < 2}">
                                            <li class="rounded-md bg-blue-100 px-1">${category.name}</li>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                                <p class="font-semibold text-lg truncate mt-1">
                                        ${newReleaseSeries.title}
                                </p>
                                <div class="flex justify-between text-sm text-gray-500">
                                    <p>by <span class="text-gray-700 font-medium">
                                        <c:choose>
                                            <c:when test="${not empty newReleaseSeries.authorNameList}">
                                                ${newReleaseSeries.authorNameList[0]}
                                            </c:when>
                                            <c:otherwise>Unknown</c:otherwise>
                                        </c:choose>
                                    </span>
                                    </p>
                                    <p>${newReleaseSeries.totalChapters} chapters</p>
                                </div>
                                <p class="text-sm text-gray-600">
                                    ★ ${newReleaseSeries.avgRating}
                                    <span class="text-gray-400">(${newReleaseSeries.totalRating})</span>
                                </p>
                            </div>
                        </a>
                    </li>

                </c:forEach>
            </ul>
        </div>
        <!-- Top Reader Points Section (col-span-3) -->
        <div class="col-span-3 flex flex-col weekly-top" id="reader-ranking-container">
            <jsp:include page="/WEB-INF/views/general/_userRanking.jsp" flush="true"/>
        </div>
    </section>
    <section class="mt-10 grid grid-cols-12 gap-x-5 relative">
        <!-- Recently Updated Section (col-span-12) -->
        <div class="col-span-12 flex justify-between items-center">
            <p class="font-bold text-3xl">Recently Update</p>
            <a href="${pageContext.request.contextPath}/search" class="hover:text-neutral-600 text-right">View all</a>
        </div>


        <ul class="col-span-12 flex justify-between gap-5 pt-6">
            <c:forEach var="recentlyUpdatedSeries" items="${recentlyUpdatedSeriesList}" varStatus="loop">
                <li class="md:w-50 relative group border border-gray-200 shadow-lg rounded-xl overflow-hidden bg-white hover:shadow-2xl transition duration-300">

                    <div class="aspect-[3/4] overflow-hidden relative">
                        <img
                                src="${pageContext.request.contextPath}/${recentlyUpdatedSeries.coverImgUrl}"
                                class="w-full h-full object-cover transition duration-300 group-hover:opacity-40"
                                alt="${recentlyUpdatedSeries.title}"
                        />

                        <!-- Overlay chứa nút -->
                        <div class="absolute inset-0 flex flex-col items-center justify-center gap-4 opacity-0 group-hover:opacity-100 transition duration-300">
                            <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${recentlyUpdatedSeries.seriesId}&chapterId="
                               class="bg-[#195DA9] text-white px-4 py-2 rounded-md hover:bg-blue-700 transition">
                                Read now
                            </a>
                            <a href="${pageContext.request.contextPath}/series/detail?seriesId=${recentlyUpdatedSeries.seriesId}"
                               class="bg-white text-[#195DA9] px-7 py-2 rounded-md border border-[#195DA9] hover:bg-gray-100 transition">
                                Detail
                            </a>
                        </div>
                    </div>
                    <!-- Nội dung -->
                    <div class="p-3 flex flex-col justify-between flex-grow">
                        <div>
                            <ul class="flex flex-wrap gap-2 text-xs mb-1">
                                <c:forEach var="category" items="${recentlyUpdatedSeries.categoryList}"
                                           varStatus="status">
                                    <c:if test="${status.index < 2}">
                                        <li class="rounded-md bg-blue-100 px-1">${category.name}</li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-lg truncate mb-1">
                                    ${recentlyUpdatedSeries.title}
                            </p>
                        </div>
                        <div class="text-sm opacity-70">
                            <div class="flex justify-between">
                                <p>by <span class="font-medium">
                                    <c:choose>
                                        <c:when test="${not empty recentlyUpdatedSeries.authorNameList}">
                                            ${recentlyUpdatedSeries.authorNameList[0]}
                                        </c:when>
                                        <c:otherwise>Unknown</c:otherwise>
                                    </c:choose>
                                    </span>
                                </p>
                                <p>${recentlyUpdatedSeries.totalChapters} chapters</p>
                            </div>
                            <p>★ ${recentlyUpdatedSeries.avgRating} (${recentlyUpdatedSeries.totalRating})</p>
                        </div>
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
                <li class=" bg-white rounded-lg hover:shadow-4xl hover:scale-110 transition duration-300">
                    <a class="p-8 inline-block"
                       href="${pageContext.request.contextPath}/search?searchType=&genres=${category.name}">
                        <p class="text-primary mb-2 text-3xl font-semibold">
                                ${category.name}
                        </p>
                        <p class="text-neutral-500 text-xl">
                                ${category.totalSeries} series
                        </p>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </section>
    <!-- Completed Series Section -->
    <section class="mt-10 grid grid-cols-12 gap-x-5 relative">

        <!-- Tiêu đề + View all -->
        <div class="col-span-12 flex justify-between items-center">
            <p class="font-bold text-3xl">Completed Series</p>
            <a href="${pageContext.request.contextPath}/search?searchType=&status=completed"
               class="hover:text-neutral-600 text-right">View all</a>
        </div>

        <!-- Danh sách series -->
        <ul class="col-span-12 flex justify-between gap-5 pt-6">
            <c:forEach var="completedSeries" items="${recentlyUpdatedSeriesList}" varStatus="loop">
                <li class="md:w-50 relative group border border-gray-200 shadow-lg rounded-xl overflow-hidden bg-white hover:shadow-2xl transition duration-300">
                    <a href="${pageContext.request.contextPath}/series/detail?seriesId=${completedSeries.seriesId}">
                        <!-- Hình ảnh -->
                        <div class="aspect-[3/4] overflow-hidden relative">
                            <img
                                    src="${pageContext.request.contextPath}/${completedSeries.coverImgUrl}"
                                    class="w-full h-full object-cover transition duration-300 group-hover:opacity-40"
                                    alt="${completedSeries.title}"
                            />

                            <!-- Overlay chứa nút -->
                            <div class="absolute inset-0 flex flex-col items-center justify-center gap-4 opacity-0 group-hover:opacity-100 transition duration-300">
                                <a href="${pageContext.request.contextPath}/chapter/detail?seriesId=${completedSeries.seriesId}&chapterId="
                                   class="bg-[#195DA9] text-white px-4 py-2 rounded-md hover:bg-blue-700 transition">
                                    Read now
                                </a>
                                <a href="${pageContext.request.contextPath}/series/detail?seriesId=${completedSeries.seriesId}"
                                   class="bg-white text-[#195DA9] px-7 py-2 rounded-md border border-[#195DA9] hover:bg-gray-100 transition">
                                    Detail
                                </a>
                            </div>
                        </div>

                        <!-- Nội dung -->
                        <div class="p-3 flex flex-col justify-between flex-grow">
                            <div>
                            <ul class="flex flex-wrap gap-2 text-xs mb-1">
                                <c:forEach var="category" items="${completedSeries.categoryList}" varStatus="status">
                                    <c:if test="${status.index < 2}">
                                        <li class="rounded-md bg-blue-100 px-1">${category.name}</li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                            <p class="font-semibold text-lg truncate mb-1">
                                    ${completedSeries.title}
                            </p>
                            </div>
                        <div class="text-sm opacity-70">
                            <div class="flex justify-between">
                                <p>by <span class="font-medium">
                                    <c:choose>
                                        <c:when test="${not empty completedSeries.authorNameList}">
                                            ${completedSeries.authorNameList[0]}
                                        </c:when>
                                        <c:otherwise>Unknown</c:otherwise>
                                    </c:choose>
                                    </span>
                                </p>
                                <p>${completedSeries.totalChapters} chapters</p>
                            </div>
                            <p>★ ${completedSeries.avgRating} (${completedSeries.totalRating})</p>
                        </div>
                        </div>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </section>
    <section class="mt-10 grid grid-cols-12 gap-x-5 relative">
        <!-- Call to Action Section -->
        <div class="col-span-12 bg-gradient-to-r from-[#4B2BAE] to-[#1A56B6] rounded-2xl p-24 text-center text-white relative overflow-hidden mb-10">
            <!-- Lớp overlay để làm mờ ảnh nền -->
            <div class="absolute inset-0 bg-cover bg-center opacity-20"></div>

            <!-- Nội dung chính -->
            <div class="relative z-10">
                <h2 class="text-5xl font-extrabold mb-4">Unleash Your Imagination</h2>
                <p class="text-lg text-gray-200 mb-8 max-w-2xl mx-auto">
                    Share your stories with millions of readers, inspire new worlds, and become part of a thriving
                    community of authors.
                </p>
                <a src=""
                   class="bg-gradient-to-r from-[#341661] via-[#491894] to-[#195DA9] font-black text-2xl px-3 py-1 rounded-xl border-2 border-[#E3E346]">
                    <span
                            class="bg-gradient-to-r from-[#D2D200] via-[#F8F881] to-[#999400] bg-clip-text text-transparent ">
                        Write Now
                    </span>
                </a>
            </div>
        </div>
    </section>
    <jsp:include page="/WEB-INF/views/general/_search.jsp"/>
</main>
<script>
    function loadReaderRanking() {
        fetch("${pageContext.request.contextPath}/account/ranking")
            .then(response => response.text())
            .then(html => {
                document.getElementById("reader-ranking-container").innerHTML = html;
            })
            .catch(error => console.error("Error:", error));
    }
    document.addEventListener("DOMContentLoaded", () => {
        loadReaderRanking();
    });
    function updateFilter(page = 1) {
        const selectedGenres = Array.from(document.querySelectorAll("input[name=genre]:checked")).map(cb => cb.value);
        // console.log(selectedGenres);
        const searchKeyword = document.querySelector("#search")?.value?.trim() || "";

        const params = new URLSearchParams();
        if (searchKeyword) params.append("search", searchKeyword);
        if (selectedGenres.length > 0) params.append("genre", selectedGenres.join(" "));
        params.append("currentPage", page);
        params.append("sizePage", 12);
        console.log(params.get("search"));
        fetch("${pageContext.request.contextPath}/series/list?" + params.toString(), {
            method: "GET",
            headers: {"X-Requested-With": "XMLHttpRequest"}
        })
            .then(res => res.text())
            .then(html => {
                const container = document.querySelector("#result-container");
                container.innerHTML = html;

                bindPagination(); // gắn lại sự kiện sau khi load JSP mới

            })
            .catch(err => console.error("Pagination load error:", err));
    }
    document.getElementById("search").addEventListener("keypress", e => {
        if (e.key === "Enter") {
            // const container = document.querySelector("#container");
            // container.scrollIntoView({ behavior: "instant", block: "start" });

            e.preventDefault();
            updateFilter();
            const container = document.querySelector("#container");
            const header = document.querySelector("header");
            const headerHeight = header ? header.offsetHeight : 0;

            // Cuộn đến vị trí trừ chiều cao header
            const elementPosition = container.getBoundingClientRect().top + window.scrollY;
            const offsetPosition = elementPosition - headerHeight - 10; // trừ thêm 10px để thoáng

            window.scrollTo({
                top: offsetPosition,
                behavior: "instant"
            });
        }
    });
    // Gắn click event cho nút trang
    function bindPagination() {
        document.querySelectorAll(".page-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                if (!btn.disabled) {
                    const page = parseInt(btn.dataset.page);
                    updateFilter(page);
                }
            });
        });
    }
    function debounce(func, delay) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }
    // Lần đầu load trang
    document.addEventListener("DOMContentLoaded", () => {
        updateFilter(); // tự load trang đầu
    });

    window.addEventListener("scroll", () => {
        const header = document.querySelector("header");
        if (window.scrollY > 20) {
            header.classList.add("shadow-lg", "bg-white/90", "backdrop-blur-md");
        } else {
            header.classList.remove("shadow-lg", "bg-white/90", "backdrop-blur-md");
        }
    });
</script>

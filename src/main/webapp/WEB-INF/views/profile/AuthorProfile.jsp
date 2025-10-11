<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 1:37 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page buffer="32kb" autoFlush="true" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <link rel="stylesheet" href="./styles.css" />
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet" />
    <style>
        .header {
            background-color: white;
            border-bottom: 1px solid #D9D9D9;
            margin: auto;
            height: 70px;
            box-shadow: 0px 0px 15px #195DA9;
        }
    </style>
</head>

<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>
<
<main class="max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-x-[30px] gap-y-0">

    <div class="col-span-3 flex flex-col items-center text-center">
        <img src="https://placehold.co/160x160" alt="avatar" class="rounded-full mb-4">

    </div>
    <div class="col-span-9">
        <div class="flex gap-4">
            <h2 class="text-xl font-semibold text-[#195DA9]">Nguyen Trung Nguyen</h2>
            <c:forEach var="badge" items="${badgeList}">
                <img src="${badge.iconUrl}" class="rounded-full">
            </c:forEach>


        </div>

        <div class=" mt-4 text-gray-600 border border-[#195DA9] rounded-lg p-3 leading-relaxed">
            <h3 class="font-semibold mb-1 text-lg text-[#195DA9]">Bio</h3>
           ${user.bio}
        </div>
    </div>
    <div class="col-span-3 col-start-2 bg-[#195DA9] text-white px-8 py-2 mt-10 rounded-lg shadow">
        <p class="text-lg font-medium">Total Series</p>
        <div class="flex items-center justify-between mb-2">
            <p class="text-2xl font-bold mt-1">${totalSeriesCount} </p>
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                 class="lucide lucide-book-open-icon lucide-book-open">
                <path d="M12 7v14" />
                <path
                        d="M3 18a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1h5a4 4 0 0 1 4 4 4 4 0 0 1 4-4h5a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1h-6a3 3 0 0 0-3 3 3 3 0 0 0-3-3z" />
            </svg>
        </div>
    </div>
    <div class="col-span-3 col-start-6 bg-[#195DA9] text-white px-8 py-2 mt-10 rounded-lg shadow">
        <p class="text-lg font-medium">Total Likes</p>
        <div class="flex items-center justify-between mb-2">
            <p class="text-2xl font-bold mt-1">${totalLike} </p>
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                 class="lucide lucide-thumbs-up-icon lucide-thumbs-up">
                <path d="M7 10v12" />
                <path
                        d="M15 5.88 14 10h5.83a2 2 0 0 1 1.92 2.56l-2.33 8A2 2 0 0 1 17.5 22H4a2 2 0 0 1-2-2v-8a2 2 0 0 1 2-2h2.76a2 2 0 0 0 1.79-1.11L12 2a3.13 3.13 0 0 1 3 3.88Z" />
            </svg>
        </div>
    </div>
    <div class="col-span-2 col-start-10 bg-[#195DA9] text-white px-8 py-2 mt-10 rounded-lg shadow">
        <p class="text-lg font-medium">Average Rating</p>
        <div class="flex items-center justify-between mb-2">
            <p class="text-2xl font-bold mt-1">${avgRating} </p>
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                 class="lucide lucide-star-icon lucide-star">
                <path
                        d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
            </svg>
        </div>
    </div>


    <!-- Danh sách Series -->
    <div class="col-span-12 mt-10 border border-[#195DA9] rounded-xl p-4 h-[400px] mb-5 overflow-y-auto">
        <div class="flex flex-col gap-4">
            <!-- Card Series -->
            <c:forEach var="series" items="${seriesInfoDTOList}" varStatus="loop">
                <div
                        class="flex items-center justify-between border border-gray-300 rounded-lg bg-white px-4 py-3 hover:shadow-sm">
                    <div class="flex items-center gap-4">
                        <img src="https://via.placeholder.com/60" class="w-12 h-16 rounded object-cover" alt="">
                        <div>
                            <p class="font-semibold text-gray-800">${series.title}</p>
                            <c:forEach var="category" items="${series.categories}">
                                <span class="border text-xs px-2 rounded-full text-gray-600 bg-gray-100">${category}</span>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="flex items-center gap-10 text-sm">
                        <p class="flex items-center"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                          viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                                          stroke-linecap="round" stroke-linejoin="round"
                                                          class="lucide lucide-star-icon lucide-star">
                            <path
                                    d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
                        </svg>
                            <span class="ml-1 text-gray-700">${series.avgRating} (${series.countRatings})</span>
                        </p>
                        <p>${series.totalChapters} Chapters</p>
                        <span
                                class="w-20 text-center py-0.5 rounded-full bg-green-100 text-green-700 text-xs">${series.status}</span>
                        <p class="text-gray-500">${series.updatedAt}</p>

                    </div>
                </div>
            </c:forEach>

            <div
                    class="flex items-center justify-between border border-gray-300 rounded-lg bg-white px-4 py-3 hover:shadow-sm">
                <div class="flex items-center gap-4">
                    <img src="https://via.placeholder.com/60" class="w-12 h-16 rounded object-cover" alt="">
                    <div>
                        <p class="font-semibold text-gray-800">The Shattrred Vows</p>
                        <span class="border text-xs px-2 rounded-full text-gray-600 bg-gray-100">collab</span>
                    </div>
                </div>
                <div class="flex items-center gap-10 text-sm">
                    <p class="flex items-center"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                      viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                                      stroke-linecap="round" stroke-linejoin="round"
                                                      class="lucide lucide-star-icon lucide-star">
                        <path
                                d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
                    </svg> <span class="ml-1 text-gray-700">5.0 (1247)</span></p>
                    <p>25 Chapters</p>
                    <span
                            class="w-20 text-center py-0.5 rounded-full bg-yellow-100 text-yellow-700 text-xs">Ongoing</span>
                    <p class="text-gray-500">25/09/2025</p>

                </div>
            </div>
        </div>
    </div>
</main>



<footer class="bg-white h-2 text-gray-600 text-center py-3 border-t border-gray-200 relative">
    <!-- Logo -->
    <div class="max-w-3xl mx-auto px-4">
        <h2 class="text-xl font-bold mb-2">
            <span class="text-2xl text-blue-700">J</span><span class="text-blue-600">oyLeeBook</span>
        </h2>

        <!-- Description -->
        <p class="text-sm leading-relaxed mb-4">
            Your gateway to endless stories. Discover, read, and connect with millions of readers worldwide
            in the ultimate reading experience.
        </p>

        <!-- Social icons -->
        <div class="flex justify-center gap-5 text-gray-500 mb-4">
            <a href="#" class="hover:text-blue-500 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                    stroke-linejoin="round" class="lucide lucide-twitter-icon lucide-twitter w-5 h-5">
                <path
                        d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z" />
            </svg></a>

            <a href="#" class="hover:text-blue-500 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                    stroke-linejoin="round" class="lucide lucide-facebook-icon lucide-facebook w-5 h-5">
                <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z" />
            </svg></a>
            <a href="#" class="hover:text-blue-500 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                                    stroke-linejoin="round" class="lucide lucide-instagram-icon lucide-instagram w-5 h-5">
                <rect width="20" height="20" x="2" y="2" rx="5" ry="5" />
                <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z" />
                <line x1="17.5" x2="17.51" y1="6.5" y2="6.5" />
            </svg></a>
            <a href="#" class="hover:text-gray-800 transition"><svg xmlns="http://www.w3.org/2000/svg"
                                                                    class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                                                    stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-mail-icon lucide-mail">
                <path d="m22 7-8.991 5.727a2 2 0 0 1-2.009 0L2 7" />
                <rect x="2" y="4" width="20" height="16" rx="2" />
            </svg></a>
        </div>

        <!-- Divider -->
        <div class="border-t border-gray-300 w-5/5 mx-auto mb-3"></div>

        <!-- Copyright -->
        <p class="text-xs text-gray-500">
            © 2024 <span class="font-medium text-gray-700">JoyLeeBook</span>. All rights reserved.
        </p>
    </div>

    <!-- Back to Top Button -->
    <a href="#" class="absolute right-6 top-7 text-gray-400 hover:text-gray-700 transition">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" stroke="currentColor" stroke-width="2"
             stroke-linecap="round" stroke-linejoin="round" class="w-5 h-5">
            <path d="M12 19V5M5 12l7-7 7 7" />
        </svg>
    </a>
</footer>

<script>
    const genreButton = document.getElementById("genreButton");
    const genreMenu = document.getElementById("genreMenu");

    genreButton.addEventListener("click", () => {
        genreMenu.classList.toggle("hidden");
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener("click", (e) => {
        if (!genreButton.contains(e.target) && !genreMenu.contains(e.target)) {
            genreMenu.classList.add("hidden");
        }
    });

    const BtnAvatar = document.getElementById("BtnAvatar");
    const MenuAvatar = document.getElementById("MenuAvatar");

    BtnAvatar.addEventListener("click", () => {
        MenuAvatar.classList.toggle("hidden");
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener("click", (e) => {
        if (!BtnAvatar.contains(e.target) && !MenuAvatar.contains(e.target)) {
            MenuAvatar.classList.add("hidden");
        }
    });

    const chapterListBtn = document.getElementById("chapterListBtn");
    const chapterList = document.getElementById("chapterList");

    chapterListBtnl.addEventListener("click", () => {
        chapterList.classList.toggle("hidden");
    })
    document.addEventListener("click", (e) => {
        if (!chapterListBtn.contains(e.target) && !chapterList.contains(e.target)) {
            chapterList.classList.add("hidden");
        }
    })
</script>
</body>

</html>
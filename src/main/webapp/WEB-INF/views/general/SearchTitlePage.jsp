<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 1:21 AM
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
    <link rel="stylesheet" href="css/styles.css" type="text/css">

    <style>
        .header {
            background-color: white;
            border-bottom: 1px solid #D9D9D9;
            margin: auto;
            height: 65px;
            box-shadow: 0px 0px 15px #195DA9;
        }
    </style>
</head>

<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>

<!-- Content -->
<main class="main max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-x-[30px]">
    <div class="col-span-12 border-b-1 border-gray-300 flex items-center">
        <button class="font-bold text-2xl border-b-5 border-[#195DA9] px-4">Series</button>
        <button class="text-gray-500 font-bold text-2xl px-4 hover:text-[#195DA9]">Author</button>

    </div>
    <div class="col-span-12 mb-1">
        <p class="text-sm text-gray-500">30 results</p>
    </div>
    <!-- CỘT TRÁI: FILTER -->
    <div class="col-span-3 bg-white rounded-xl shadow p-5">
        <h2 class="text-xl font-semibold mb-4">Filter</h2>
        <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

        <div class="space-y-3">
            <label class="flex items-center gap-2">
                <input type="checkbox" class="accent-[#195DA9]" />
                <span>Completed</span>
            </label>
            <label class="flex items-center gap-2">
                <input type="checkbox" class="accent-[#195DA9]" />
                <span>Ongoing</span>
            </label>
        </div>

        <h2 class="text-xl font-semibold mt-6 mb-3">Genre</h2>
        <p class="text-sm text-gray-500 mb-2">You can select multiple options</p>

        <div class="grid grid-cols-2 gap-2 text-gray-700">
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" /> Action</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" />
                Fantasy</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" /> Love</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" /> Drama</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" /> Action</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" />
                Fantasy</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" /> Love</label>
            <label class="flex items-center gap-2"><input type="checkbox" class="accent-[#195DA9]" /> Drama</label>
        </div>
    </div>

    <!-- CỘT GIỮA: DANH SÁCH SERIES -->
    <div class="col-span-6">


        <!-- CARD SERIES -->
        <div class="space-y-6">
            <!-- 1 series -->
            <div class="flex gap-4 bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                <img src="https://placehold.co/120x160" alt="cover"
                     class="w-[120px] h-[160px] object-cover rounded-lg">
                <div class="flex-1">
                    <h3 class="font-semibold text-lg text-gray-800">The Shattered Vows</h3>
                    <p class="text-sm text-gray-500 mb-1">by Alix</p>
                    <p class="text-gray-700 text-sm mb-3">
                        “The Shattered Vows” has the potential to be an emotional love story, focusing on themes of
                        regret,
                        missed opportunities, and the complexities of relationships.
                    </p>
                    <div class="flex gap-2 mb-2">
                            <span
                                    class="bg-gray-100 text-gray-600 text-xs font-semibold px-2 py-0.5 rounded-full">Romance</span>
                        <span
                                class="bg-green-100 text-green-600 text-xs font-semibold px-2 py-0.5 rounded-full">Completed</span>
                    </div>
                    <div class="flex items-center justify-between text-sm text-gray-500">
                            <span class="flex gap-2 items-center"><svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                       height="16" viewBox="0 0 24 24" fill="yellow" stroke-width="2"
                                                                       stroke-linecap="round" stroke-linejoin="round"
                                                                       class="lucide lucide-star-icon lucide-star">
                                    <path
                                            d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
                                </svg> 5.0 (1247)</span>
                        <span>25 Chapters</span>
                        <span>Updated: 25/09/2025</span>
                    </div>
                </div>
            </div>

            <!-- Lặp thêm các card khác nếu cần -->
            <div class="flex gap-4 bg-white p-4 rounded-xl shadow hover:shadow-md transition">
                <img src="https://placehold.co/120x160" alt="cover"
                     class="w-[120px] h-[160px] object-cover rounded-lg">
                <div class="flex-1">
                    <h3 class="font-semibold text-lg text-gray-800">Lovers on the Sea</h3>
                    <p class="text-sm text-gray-500 mb-1">by Adam Silvera</p>
                    <p class="text-gray-700 text-sm mb-3">
                        A romantic story about fate, distance, and self-discovery through love and pain.
                    </p>
                    <div class="flex gap-2 mb-2">
                            <span
                                    class="bg-gray-100 text-gray-600 text-xs font-semibold px-2 py-0.5 rounded-full">Romance</span>
                        <span
                                class="bg-green-100 text-green-600 text-xs font-semibold px-2 py-0.5 rounded-full">Completed</span>
                    </div>
                    <div class="flex items-center justify-between text-sm text-gray-500">
                            <span class="flex gap-2 items-center"><svg xmlns="http://www.w3.org/2000/svg" width="16"
                                                                       height="16" viewBox="0 0 24 24" fill="yellow" stroke-width="2"
                                                                       stroke-linecap="round" stroke-linejoin="round"
                                                                       class="lucide lucide-star-icon lucide-star">
                                    <path
                                            d="M11.525 2.295a.53.53 0 0 1 .95 0l2.31 4.679a2.123 2.123 0 0 0 1.595 1.16l5.166.756a.53.53 0 0 1 .294.904l-3.736 3.638a2.123 2.123 0 0 0-.611 1.878l.882 5.14a.53.53 0 0 1-.771.56l-4.618-2.428a2.122 2.122 0 0 0-1.973 0L6.396 21.01a.53.53 0 0 1-.77-.56l.881-5.139a2.122 2.122 0 0 0-.611-1.879L2.16 9.795a.53.53 0 0 1 .294-.906l5.165-.755a2.122 2.122 0 0 0 1.597-1.16z" />
                                </svg> 5.0 (1247)</span>
                        <span>25 Chapters</span>
                        <span>Updated: 25/09/2025</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- CỘT PHẢI: BẢNG XẾP HẠNG -->
    <div class="col-span-3 space-y-6">
        <!-- Top Weekly Series -->
        <h3 class="font-semibold text-gray-800 mb-3">Top Weekly Series</h3>

        <div class="bg-white rounded-xl shadow p-5">
            <ol class="space-y-4 text-sm text-gray-500">
                <li class="text-[#E23636] font-semibold">1. The Shattered Vows
                    <span class="float-right">543</span>
                </li>
                <li class="text-[#F5A83D] font-semibold">2. The U I I A <span class="float-right">400</span></li>
                <li class="text-[#195DA9] font-semibold">3. Momomo
                    <span class="float-right">123</span>
                </li>
                <li>4. AAAAAA <span class="float-right">90</span></li>
                <li>5. 1234 <span class="float-right text-gray-500">59</span></li>
                <li>6. KT Phieu Luu Ky <span class="float-right text-gray-500">42</span></li>
                <li>7. An nhong a se o <span class="float-right text-gray-500">32</span></li>
                <li>8. Ff <span class="float-right text-gray-500">32</span></li>
            </ol>
        </div>

        <!-- Top Reader Points -->
        <h3 class="font-semibold text-gray-800 mb-3">Top Reader Points</h3>

        <div class="bg-white rounded-xl shadow p-5">
            <ol class="space-y-4 text-sm text-gray-500">
                <li class="text-[#E23636] font-semibold">1. BonNguyen
                    <span class="float-right">543</span>
                </li>
                <li class="text-[#F5A83D] font-semibold">2. KhaiToan<span class="float-right">400</span></li>
                <li class="text-[#195DA9] font-semibold">3. DuyP<span class="float-right">123</span>
                </li>
                <li>4. HaiDD <span class="float-right">90</span></li>
                <li>5. Nguyen <span class="float-right text-gray-500">59</span></li>
                <li>6. Mark <span class="float-right text-gray-500">42</span></li>
                <li>7. An nhong a se o <span class="float-right text-gray-500">32</span></li>
                <li>8. Ff <span class="float-right text-gray-500">32</span></li>
            </ol>
        </div>
    </div>
</main>

</body>
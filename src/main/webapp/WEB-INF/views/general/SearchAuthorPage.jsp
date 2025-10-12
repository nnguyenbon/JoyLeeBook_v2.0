<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/12/2025
  Time: 1:13 AM
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


<!-- Content -->
<main class="main max-w-[1290px] mx-auto mt-10 grid grid-cols-12 gap-x-[30px]">
    <div class="col-span-12 border-b-1 border-gray-300 flex items-center">
        <button class="text-gray-500 font-bold text-2xl px-4 hover:text-[#195DA9]">Series</button>
        <button class="font-bold text-2xl border-b-5 border-[#195DA9] px-4">Author</button>

    </div>
    <div class="col-span-7 col-start-2 my-3 ">
        <p class="text-sm text-gray-500">30 results</p>
    </div>


    <!-- CỘT GIỮA: DANH SÁCH Author -->
    <div class="col-span-7 col-start-2">
        <div class="space-y-6">
            <div
                    class="flex justify-between items-center bg-white p-4 rounded-xl shadow shadow-[#195DA9] hover:shadow-md transition">
                <div class="flex items-center gap-4">
                    <img src="https://placehold.co/120x120" alt="cover"
                         class="w-[80px] h-[80px] object-cover rounded-full">
                    <div>
                        <h3 class="font-semibold text-lg text-gray-800">The Shattered Vows</h3>
                        <p class="text-sm text-gray-500 mb-1">3 series</p>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/author-profile?authorId=4">
                    <button
                            class="bg-[#ECF1FE] text-[#195DA9] font-medium px-4 py-2 rounded-lg hover:bg-[#195DA9] hover:text-white transition flex items-center justify-center">
                        View Profile
                    </button>
                </a>

            </div>


            <div
                    class="flex justify-between items-center bg-white p-4 rounded-xl shadow shadow-[#195DA9] hover:shadow-md transition">
                <div class="flex items-center gap-4">
                    <img src="https://placehold.co/120x120" alt="cover"
                         class="w-[80px] h-[80px] object-cover rounded-full">
                    <div>
                        <h3 class="font-semibold text-lg text-gray-800">The Shattered Vows</h3>
                        <p class="text-sm text-gray-500 mb-1">3 series</p>
                    </div>
                </div>

                <button
                        class="bg-[#ECF1FE] text-[#195DA9] font-medium px-4 py-2 rounded-lg hover:bg-[#195DA9] hover:text-white transition flex items-center justify-center">
                    View Profile
                </button>
            </div>
        </div>
    </div>

    <!-- CỘT PHẢI: BẢNG XẾP HẠNG -->
    <div class="col-span-3 col-start-10 space-y-6">
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

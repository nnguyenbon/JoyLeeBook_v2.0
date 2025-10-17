<%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 10/17/2025
  Time: 8:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Author Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
    </style>
</head>
<body class="bg-gray-50 min-h-screen text-gray-800">
<div class="max-w-7xl mx-auto px-6 py-8">
    <!-- Title and Action -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">Author Dashboard</h1>
    </div>

    <!-- Filter -->
    <div class="flex flex-wrap items-center gap-4 mb-6">
        <p class="text-gray-600 text-sm">Total: 3</p>
        <div class="flex-1">
            <input type="text" placeholder="Search series, author"
                   class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-[#195DA9] focus:outline-none">
        </div>
        <select class="border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-[#195DA9]">
            <option>All status</option>
            <option>Ongoing</option>
            <option>Completed</option>
        </select>
        <button class="bg-[#195DA9] text-white px-5 py-2 rounded-lg hover:bg-[#154b89] transition flex items-center gap-2">
            <span class="text-xl leading-none">＋</span> Create
        </button>
    </div>

    <!-- Table Header -->
    <div class="grid grid-cols-12 items-center text-sm font-semibold text-gray-600 border-b border-gray-300 pb-2 mb-3">
        <div class="col-span-4">Series</div>
        <div class="col-span-1 text-center">Rating</div>
        <div class="col-span-2 text-center">Chapters</div>
        <div class="col-span-2 text-center">Status</div>
        <div class="col-span-1 text-center">Date</div>
        <div class="col-span-2 text-center">Actions</div>
    </div>

    <!-- Series Row -->
    <div class="space-y-3">
        <!-- Item 1 -->
        <div class="grid grid-cols-12 items-center bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition">
            <!-- Series Info -->
            <div class="col-span-4 flex items-center gap-4">
                <img src="https://via.placeholder.com/60x80" alt="cover" class="w-16 h-20 object-cover rounded-md">
                <div>
                    <h2 class="font-semibold text-base">The Shattred Vows</h2>
                    <p class="text-gray-500 text-sm">@colab</p>
                </div>
            </div>

            <div class="col-span-1 text-center text-yellow-500 text-sm font-medium">★ 5.0 (1247)</div>
            <div class="col-span-2 text-center text-gray-700 text-sm">25 Chapters</div>

            <!-- Status -->
            <div class="col-span-2 text-center">
                <span class="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-semibold">Completed</span>
            </div>

            <!-- Date -->
            <div class="col-span-1 text-center text-gray-500 text-sm">25/09/2025</div>

            <!-- Actions -->
            <div class="col-span-2 flex justify-center gap-2">
                <button class="border border-gray-300 px-3 py-1.5 rounded-md text-sm hover:bg-gray-100">Detail</button>
                <button class="border border-[#195DA9] text-[#195DA9] bg-blue-50 px-3 py-1.5 rounded-md text-sm hover:bg-blue-100">Edit</button>
            </div>
        </div>

        <!-- Item 2 -->
        <div class="grid grid-cols-12 items-center bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition">
            <div class="col-span-4 flex items-center gap-4">
                <img src="https://via.placeholder.com/60x80" alt="cover" class="w-16 h-20 object-cover rounded-md">
                <div>
                    <h2 class="font-semibold text-base">The Shattred Vows</h2>
                    <p class="text-gray-500 text-sm">@author</p>
                </div>
            </div>

            <div class="col-span-1 text-center text-yellow-500 text-sm font-medium">★ 5.0 (1247)</div>
            <div class="col-span-2 text-center text-gray-700 text-sm">25 Chapters</div>

            <div class="col-span-2 text-center">
                <span class="bg-yellow-100 text-yellow-700 px-3 py-1 rounded-full text-xs font-semibold">Ongoing</span>
            </div>

            <div class="col-span-1 text-center text-gray-500 text-sm">25/09/2025</div>

            <div class="col-span-2 flex justify-center gap-2">
                <button class="border border-gray-300 px-3 py-1.5 rounded-md text-sm hover:bg-gray-100">Detail</button>
                <button class="border border-[#195DA9] text-[#195DA9] bg-blue-50 px-3 py-1.5 rounded-md text-sm hover:bg-blue-100">Edit</button>
            </div>
        </div>

        <!-- Item 3 -->
        <div class="grid grid-cols-12 items-center bg-white border border-gray-200 rounded-xl p-4 shadow-sm hover:shadow-md transition">
            <div class="col-span-4 flex items-center gap-4">
                <img src="https://via.placeholder.com/60x80" alt="cover" class="w-16 h-20 object-cover rounded-md">
                <div>
                    <h2 class="font-semibold text-base">The Shattred Vows</h2>
                    <p class="text-gray-500 text-sm">@writer</p>
                </div>
            </div>

            <div class="col-span-1 text-center text-yellow-500 text-sm font-medium">★ 5.0 (1247)</div>
            <div class="col-span-2 text-center text-gray-700 text-sm">25 Chapters</div>

            <div class="col-span-2 text-center">
                <span class="bg-yellow-100 text-yellow-700 px-3 py-1 rounded-full text-xs font-semibold">Ongoing</span>
            </div>

            <div class="col-span-1 text-center text-gray-500 text-sm">25/09/2025</div>

            <div class="col-span-2 flex justify-center gap-2">
                <button class="border border-gray-300 px-3 py-1.5 rounded-md text-sm hover:bg-gray-100">Detail</button>
                <button class="border border-[#195DA9] text-[#195DA9] bg-blue-50 px-3 py-1.5 rounded-md text-sm hover:bg-blue-100">Edit</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>


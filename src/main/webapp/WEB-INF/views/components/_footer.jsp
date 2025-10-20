<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/13/2025
  Time: 2:38 PM
  To change this template use File | Settings | File Templates.
--%>
<footer class="border-t border-neutral-300 mt-8 bg-white">
    <div class="max-w-6xl mx-auto px-4 grid grid-cols-1 md:grid-cols-4 items-center text-center md:text-left gap-4">
        <!-- Cột 1: Logo -->
        <div class="flex justify-center md:justify-start">
            <img
                    src="${pageContext.request.contextPath}/img/shared/logo.png"
                    alt="JoyLeeBook Logo"
                    class="w-40 h-auto object-contain"
            />
        </div>

        <!-- Cột 2: Mô tả -->
        <div class="col-span-2">
            <p class="text-neutral-500 text-sm leading-snug">
                Your gateway to endless stories. Discover, read, and connect with millions of readers worldwide in the ultimate reading experience.
            </p>
        </div>

        <!-- Cột 3: Mạng xã hội -->
        <div class="flex justify-center md:justify-end gap-4 text-neutral-500 text-lg">
            <a href="#" class="hover:text-blue-400"><i class="fa-brands fa-twitter"></i></a>
            <a href="#" class="hover:text-blue-600"><i class="fa-brands fa-facebook"></i></a>
            <a href="#" class="hover:text-pink-500"><i class="fa-brands fa-instagram"></i></a>
            <a href="#" class="hover:text-red-500"><i class="fa-solid fa-envelope"></i></a>
        </div>
    </div>

    <!-- Dòng bản quyền -->
    <div class="border-t border-neutral-200 text-center py-2 text-neutral-600 text-xs">
        &copy; 2025 JoyLeeBook. All rights reserved.
    </div>
</footer>

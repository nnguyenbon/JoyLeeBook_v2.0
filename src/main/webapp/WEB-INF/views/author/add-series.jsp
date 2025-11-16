<%--
  Created by IntelliJ IDEA.
  User: Trunguyen
  Date: 10/20/2025
  Time: 8:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<main class="mt-10">
    <!-- Create Series Section -->
    <div class="">
        <h1 class="text-3xl font-bold text-center text-gray-800 mb-1">Create Series</h1>
        <p class="text-center text-gray-500 mb-8">Add a new series to your collection</p>
    </div>
    <form action="add-series" method="post" class="col-span-12 grid grid-cols-12 gap-x-5 gap-y-3 mt-10 relative"
          enctype="multipart/form-data">
        <div class="col-span-3 h-96">
            <div id="upload-box" class="h-110 flex flex-col justify-between items-center">
                <div
                        class="h-full border-2 border-dashed border-gray-300 rounded-lg flex flex-col justify-center items-center p-6 hover:border-blue-400 transition cursor-pointer relative overflow-hidden">
                    <input required name="coverImgUrl" id="coverImgUrl" type="file" accept="image/*" class="hidden" />
                    <i class="fa-solid fa-upload text-gray-400 text-3xl mb-3"></i>
                    <p class="text-gray-500 text-sm mb-3">Click to upload or drag and drop</p>


                    <!-- Ảnh xem trước -->
                    <img id="preview-image" src="" alt="Preview"
                         class="hidden absolute inset-0 w-full h-full object-cover rounded-lg" />
                </div>
                <button type="button"
                        class="mt-5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium px-4 py-1.5 rounded border">
                    Choose File Image
                </button>
            </div>
        </div>

        <div class="col-span-8 bg-white p-6 rounded-lg shadow-md border space-y-5">
            <!-- Title -->
            <div>
                <label class="block font-medium text-gray-700 mb-1">
                    Series Title <span class="text-red-500">*</span>
                    <span class="text-gray-400 text-sm">(Maximum 50 characters)</span>
                    <input name="title" type="text" placeholder="Enter Title" required
                           class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400">
                </label>

            </div>

            <!-- Genre & Status -->
            <div class="grid grid-cols-4 gap-5">
                <!-- Genre (Multiple Select with ticked dropdown) -->
                <div class="col-span-3 relative">
                    <label class="block font-medium text-gray-700 mb-1">Genre <span
                            class="text-red-500">*</span></label>

                    <button id="genre-btn" type="button"
                            class="max-w-128 w-full text-left border border-gray-300 rounded-md px-3 py-2 bg-white flex justify-between items-center focus:outline-none focus:ring-2 focus:ring-blue-400">
                        <span id="selected-genres-text" class="text-gray-700">Select genres</span>
                        <i class="fa-solid fa-chevron-down text-gray-500"></i>
                    </button>

                    <!-- Dropdown danh sách thể loại -->
                    <!-- Dropdown -->
                    <div id="genre-dropdown"
                         class="hidden mt-1 bg-white border border-gray-300 rounded-md shadow-md absolute z-10 w-full max-h-72 overflow-y-auto">
                        <ul id="genre-list" class="grid grid-cols-3 divide-y divide-gray-100 text-gray-700">
                            <c:forEach var="category" items="${categoryList}">
                                <li class="px-3 py-2 flex items-center gap-3 hover:bg-blue-50 cursor-pointer">
                                    <input type="checkbox" class="genre-checkbox" name="selectedGenres" value="${category.categoryId}" id="genre-${category.categoryId}" />
                                    <label for="genre-${category.categoryId}" class="cursor-pointer select-none">${category.name}</label>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <div class="col-span-1">
                    <label class="block font-medium text-gray-700 mb-1">Status <span
                            class="text-red-500">*</span></label>
                    <select name="status"
                            class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400">
                        <option value="Ongoing">Ongoing</option>
                        <option value="Completed">Completed</option>
                    </select>
                </div>
            </div>

            <!-- Description -->
            <div>
                <label class="block font-medium text-gray-700 mb-1">Description</label>
                <textarea name="description" rows="5" placeholder="Enter a description of the series" required
                          class="w-full border border-gray-300 rounded-md px-3 py-2 resize-none focus:outline-none focus:ring-2 focus:ring-blue-400"></textarea>
            </div>

            <!-- Buttons -->
            <div class="flex justify-end gap-3">
                <button type="submit"
                        class="bg-[#195DA9] hover:bg-blue-700 text-white text-lg font-medium px-5 py-2 rounded-md shadow-sm transition">
                    Create Series
                </button>
                <a type="button"
                   class="border border-gray-400 text-gray-600 hover:bg-gray-100 px-5 py-2 rounded-md transition">
                    Cancel
                </a>
            </div>
        </div>
    </form>
</main>


<script>
    document.addEventListener("DOMContentLoaded", function () {
        const genreBtn = document.getElementById("genre-btn");
        const genreDropdown = document.getElementById("genre-dropdown");
        const selectedText = document.getElementById("selected-genres-text");
        const checkboxes = document.querySelectorAll(".genre-checkbox");

        // Mở / đóng dropdown
        genreBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            genreDropdown.classList.toggle("hidden");
        });

        // Click ngoài -> đóng dropdown
        document.addEventListener("click", (e) => {
            if (!genreDropdown.contains(e.target) && !genreBtn.contains(e.target)) {
                genreDropdown.classList.add("hidden");
            }
        });

        // Khi tick checkbox
        checkboxes.forEach((checkbox) => {
            checkbox.addEventListener("change", () => {
                const selected = Array.from(checkboxes)
                    .filter((c) => c.checked)
                    .map(c => document.querySelector('label[for="genre-' + c.value + '"]').textContent)


                selectedText.textContent =
                    selected.length > 0 ? selected.join(", ") : "Select genres";
            });
        });
    });
    document.addEventListener("DOMContentLoaded", function () {
        const uploadBox = document.getElementById("upload-box");
        const fileInput = document.getElementById("coverImgUrl");
        const previewImage = document.getElementById("preview-image");

        // Khi click vào box -> mở chọn file
        uploadBox.addEventListener("click", () => fileInput.click());

        // Khi chọn file từ hộp thoại
        fileInput.addEventListener("change", handleFileSelect);

        // Kéo file vào
        uploadBox.addEventListener("dragover", (e) => {
            e.preventDefault();
            uploadBox.classList.add("border-blue-400", "bg-blue-50");
        });

        uploadBox.addEventListener("dragleave", () => {
            uploadBox.classList.remove("border-blue-400", "bg-blue-50");
        });

        // Thả file vào
        uploadBox.addEventListener("drop", (e) => {
            e.preventDefault();
            uploadBox.classList.remove("border-blue-400", "bg-blue-50");

            const file = e.dataTransfer.files[0];
            if (file && file.type.startsWith("image/")) {
                previewFile(file);
                fileInput.files = e.dataTransfer.files; // đồng bộ lại input
            } else {
                alert("Please upload an image file!");
            }
        });

        function handleFileSelect() {
            const file = fileInput.files[0];
            if (file && file.type.startsWith("image/")) {
                previewFile(file);
            } else {
                alert("Please upload an image file!");
            }
        }

        function previewFile(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImage.src = e.target.result;
                previewImage.classList.remove("hidden");
            };
            reader.readAsDataURL(file);
        }
    });

</script>

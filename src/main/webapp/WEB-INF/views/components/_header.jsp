<%-- Created by IntelliJ IDEA. User: DPhuc Date: 10/10/25 Time: 12:48 AM To
change this template use File | Settings | File Templates. --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dao.CategoryDAO" %>
<%@ page import="model.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="db.DBConnection" %>
<%@ page import="java.sql.SQLException" %>

<header class="relative top-0 left-0 right-0 shadow-md bg-white left-1/2 right-1/2 -mx-[50vw] w-screen">
    <div class="max-w-7xl mx-auto grid grid-cols-12 gap-8 items-center">
        <div class="col-span-2 flex items-center gap-2 h-20">
            <a href="/">
                <img src="${pageContext.request.contextPath}/img/shared/logo.png" alt="logo"/>
            </a>
        </div>
        <div class="col-span-1 relative">
            <!-- Nút Genre -->
            <button
                    id="genreButton"
                    class="px-4 py-2 bg-white rounded-md hover:bg-gray-100 font-medium flex items-center gap-1"
            >
                Genre
                <i class="fa-solid fa-caret-down"></i>
            </button>

            <!-- Dropdown -->
            <div
                    id="genreMenu"
                    class="hidden absolute left-0 top-full mt-2 w-[600px] bg-white border border-gray-200 rounded-lg shadow-lg p-4 z-50"
            >
                <div class="grid grid-cols-5 gap-3 text-sm">
                    <c:forEach var="category" items="${categories}">
                        <button class="hover:bg-blue-100 rounded px-2 py-1 text-left">
                                ${category.name}
                        </button>
                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="col-span-6 ">
            <form action="search?" class="w-full">
                <input
                        type="text"
                        placeholder="Search series, author"
                        class="py-2 px-3 border border-gray-300 rounded-md focus:outline-none w-full"
                        name="keyword"
                        id="keyword"
                />
            </form>
        </div>
        <c:if test="${loginedUser != null}">
            <div class="col-span-2 col-start-10  ">
                <button
                        class="bg-gradient-to-r from-[#341661] via-[#491894] to-[#195DA9] font-black text-lg px-3 py-1 rounded-3xl border-2 border-[#E3E346]"
                >
          <span
                  class="bg-gradient-to-r from-[#D2D200] via-[#F8F881] to-[#999400] bg-clip-text text-transparent"
          >
            Write Now
          </span>
                </button>
            </div>
            <div class="col-span-1 relative">
                <button id="BtnAvatar" class="px-4 py-2 flex items-center gap-1">
                    <div class="w-10 h-10 bg-gray-300 rounded-full">
                        <img
                                class="w-10 h-10 rounded-full"
                                src="${pageContext.request.contextPath}/img/thenewkidinschool.png"
                        />
                    </div>
                    <i class="fa-solid fa-caret-down"></i>
                </button>

                <div
                        id="MenuAvatar"
                        class="hidden absolute right-0 top-full mt-2 w-50 h-65 bg-white border border-gray-200 rounded-lg shadow-lg p-3 z-50"
                >
                    <div class="text-lg">
                        <div class="mb-2">
                            <h4
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent font-bold"
                            >
                                Trunguyen
                            </h4>
                            <p class="text-[#195DA9] text-xs">Reader</p>
                        </div>
                        <hr class="mb-2 border-gray-300"/>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <i class="fa-solid fa-user"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Profile</span
                            >
                        </button>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <i class="fa-solid fa-pen"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Author</span
                            >
                        </button>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <i class="fa-solid fa-bookmark"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Library</span
                            >
                        </button>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 text-lg"
                        >
                            <i class="fa-solid fa-right-from-bracket"></i>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Logout</span
                            >
                        </button>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${loginedUser == null}">
            <div class="col-span-3 text-right">
                <button>
                    <a
                            href="./login.html"
                            class="inline-block bg-sky-700 text-white py-1 px-3 rounded-lg hover:bg-sky-900"
                    >Login</a
                    >
                </button>
                <button>
                    <a
                            href="./signup.html"
                            class="inline-block py-1 px-3 rounded-lg hover:bg-neutral-200 mx-4"
                    >Sign Up</a
                    >
                </button>
            </div>
        </c:if>
    </div>
</header>

<script>
    const genreButton = document.getElementById('genreButton');
    const genreMenu = document.getElementById('genreMenu');

    genreButton.addEventListener('click', () => {
        genreMenu.classList.toggle('hidden');
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener('click', (e) => {
        if (!genreButton.contains(e.target) && !genreMenu.contains(e.target)) {
            genreMenu.classList.add('hidden');
        }
    });

    const BtnAvatar = document.getElementById('BtnAvatar');
    const MenuAvatar = document.getElementById('MenuAvatar');

    BtnAvatar.addEventListener('click', () => {
        MenuAvatar.classList.toggle('hidden');
    });

    // Ẩn menu khi click ra ngoài
    document.addEventListener('click', (e) => {
        if (!BtnAvatar.contains(e.target) && !MenuAvatar.contains(e.target)) {
            MenuAvatar.classList.add('hidden');
        }
    });
</script>
<%-- Created by IntelliJ IDEA. User: DPhuc Date: 10/10/25 Time: 12:48 AM To
change this template use File | Settings | File Templates. --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dao.CategoryDAO" %>
<%@ page import="model.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="db.DBConnection" %>
<%@ page import="java.sql.SQLException" %>

<%
    CategoryDAO categoryDAO = null;
    try {
        categoryDAO = new CategoryDAO(DBConnection.getConnection());
        List<Category> categories = categoryDAO.getAll();
        request.setAttribute("categories", categories);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>
<header class="header">
    <div class="max-w-[1290px] mx-auto grid grid-cols-12 gap-8 items-center">

        <div class="col-span-2 flex items-center gap-2 h-20">
            <a href="homepage" class="cols-span-2 block">
                <img src="./img/shared/logo.png" alt="logo"/>
            </a>

        </div>
        <div class="col-span-1 relative">
            <!-- Nút Genre -->
            <button
                    id="genreButton"
                    class="px-4 py-2 bg-white rounded-md hover:bg-gray-100 font-medium flex items-center gap-1"
            >
                Genre
                <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="w-4 h-4"
                        viewBox="0 0 20 20"
                        fill="currentColor"
                >
                    <path
                            fill-rule="evenodd"
                            d="M5.23 7.21a.75.75 0 011.06.02L10 10.94l3.71-3.71a.75.75 0 111.06 1.06l-4.24 4.25a.75.75 0 01-1.06 0L5.25 8.29a.75.75 0 01-.02-1.08z"
                            clip-rule="evenodd"
                    />
                </svg>
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
                                src="../img/thenewkidinschool.png"
                        />
                    </div>
                    <svg
                            xmlns="http://www.w3.org/2000/svg"
                            class="w-4 h-4"
                            viewBox="0 0 20 20"
                            fill="currentColor"
                    >
                        <path
                                fill-rule="evenodd"
                                d="M5.23 7.21a.75.75 0 011.06.02L10 10.94l3.71-3.71a.75.75 0 111.06 1.06l-4.24 4.25a.75.75 0 01-1.06 0L5.25 8.29a.75.75 0 01-.02-1.08z"
                                clip-rule="evenodd"
                        />
                    </svg>
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
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="22"
                                    height="22"
                                    class="bi bi-person"
                                    viewBox="0 0 16 16"
                            >
                                <defs>
                                    <linearGradient id="gradient" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stop-color="#341661"/>
                                        <stop offset="50%" stop-color="#4C1D95"/>
                                        <stop offset="100%" stop-color="#195BA7"/>
                                    </linearGradient>
                                </defs>
                                <path
                                        fill="url(#gradient)"
                                        d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"
                                />
                            </svg>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Profile</span
                            >
                        </button>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="22"
                                    height="22"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke-width="2"
                                    stroke-linecap="round"
                                    stroke-linejoin="round"
                                    class="lucide lucide-pencil-line-icon lucide-pencil-line"
                            >
                                <defs>
                                    <linearGradient id="gradient" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stop-color="#341661"/>
                                        <stop offset="50%" stop-color="#4C1D95"/>
                                        <stop offset="100%" stop-color="#195BA7"/>
                                    </linearGradient>
                                </defs>
                                <path stroke="url(#gradient)" d="M13 21h8"/>
                                <path stroke="url(#gradient)" d="m15 5 4 4"/>
                                <path
                                        stroke="url(#gradient)"
                                        d="M21.174 6.812a1 1 0 0 0-3.986-3.987L3.842 16.174a2 2 0 0 0-.5.83l-1.321 4.352a.5.5 0 0 0 .623.622l4.353-1.32a2 2 0 0 0 .83-.497z"
                                />
                            </svg>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Author</span
                            >
                        </button>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 mb-2 text-lg"
                        >
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="22"
                                    height="22"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke-width="2"
                                    stroke-linecap="round"
                                    stroke-linejoin="round"
                                    class="lucide lucide-library-icon lucide-library"
                            >
                                <defs>
                                    <linearGradient id="gradient" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stop-color="#341661"/>
                                        <stop offset="50%" stop-color="#4C1D95"/>
                                        <stop offset="100%" stop-color="#195BA7"/>
                                    </linearGradient>
                                </defs>
                                <path
                                        stroke="url(#gradient)"
                                        d=" m16 6 4 14 M12 6v14 M8 8v12 M4 4v16"
                                />
                            </svg>
                            <span
                                    class="bg-gradient-to-r from-[#341661] via-[#4C1D95] to-[#195BA7] bg-clip-text text-transparent text-bold"
                            >Library</span
                            >
                        </button>
                        <button
                                class="block flex gap-2 w-full text-left hover:bg-blue-100 rounded px-2 py-1 text-lg"
                        >
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="22"
                                    height="22"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke-width="2"
                                    stroke-linecap="round"
                                    stroke-linejoin="round"
                                    class="lucide lucide-log-out-icon lucide-log-out"
                            >
                                <defs>
                                    <linearGradient id="gradient" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stop-color="#341661"/>
                                        <stop offset="50%" stop-color="#4C1D95"/>
                                        <stop offset="100%" stop-color="#195BA7"/>
                                    </linearGradient>
                                </defs>
                                <path
                                        stroke="url(#gradient)"
                                        d="m16 17 5-5-5-5 M21 12H9 M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"
                                />
                            </svg>
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
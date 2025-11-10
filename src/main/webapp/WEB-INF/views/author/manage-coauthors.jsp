<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/15/25
  Time: 12:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<main class="mt-10 grid grid-cols-12 gap-8 items-center">
    <div class="col-span-12">
        <h1 class="font-bold text-4xl tracking-wide">
            Collaboration Management
        </h1>
        <p class="text-gray-400">
            Manage co-authors and collaborators for your stories
        </p>
    </div>

    <div class="border border-gray-700 mt-6 rounded-2xl col-span-12">
        <div class="p-6">
            <div>
                <input
                        type="text"
                        class="border border-gray-400 rounded-xl w-md py-1 px-3"
                        placeholder="search"
                />
            </div>
            <ul class="">
                <c:forEach var="serie" items="${series}">
                    <li class="border border-gray-700 p-4 rounded-2xl mt-6">
                        <div class="flex">
                            <div
                                    class="size-12 rounded-xl bg-sky-200 flex justify-center items-center mr-4"
                            >
                                <i class="fa-solid fa-book-open text-2xl block"></i>
                            </div>
                            <div class="w-full">
                                <div class="flex justify-between items-center">
                                    <p class="text-2xl font-semibold">${serie.title}</p>
                                    <p
                                            class="py-1 px-3 text-xs rounded-full bg-sky-200 text-sky-700 font-semibold"
                                    >
                                        in-progress
                                    </p>
                                </div>
                                <div class="flex text-gray-500 items-center mt-2">
                                    <span>12 chapters</span>
                                    <div class="size-1 bg-gray-400 mx-5 rounded-full"></div>
                                    <span>update 1/15/2025</span>
                                </div>
                            </div>
                        </div>

                        <div class="border-t border-gray-400 mt-4 pt-4">
                            <div class="flex justify-between items-center">
                                <div>
                                    <i class="fa-solid fa-users mx-2"></i>
                                    Co-Authors (<span>2</span>)
                                </div>
                                <div>
                                    <button
                                            onclick="showModal('${serie.title}')"
                                            class="border border-gray-500 rounded-md py-1 px-3 hover:bg-gray-100 cursor-pointer"
                                    >
                                        <i class="fa-solid fa-user-plus mr-2"></i>
                                        Add Co-Author
                                    </button>
                                </div>
                            </div>

                            <div class="grid grid-cols-2 gap-4 mt-4">
                                <div
                                        class="flex justify-between items-center px-4 py-2 bg-gray-100 rounded-lg"
                                >
                          <span class="flex-1 truncate mr-4"
                          >${loginedUser.username}</span
                          >
                                    <span class="flex-1 truncate mr-4">author@gmail.com</span>
                                    <a href="${pageContext.request.contextPath}/author"
                                       class="border border-gray-300 py-1 px-3 rounded-lg text-sm mr-2 cursor-pointer hover:bg-gray-200"
                                    >
                                        Detail
                                    </a>
                                    <p class="inline-block bg-sky-300 py-1 px-3 rounded-full text-xs">Owner</p>
                                </div>

                                <div
                                        class="flex justify-between items-center px-4 py-2 bg-gray-100 rounded-lg"
                                >
                          <span class="flex-1 truncate mr-4"
                          >Lorem ipsum, dolor sit amet consectetur adipisicing
                            elit. Odio, repellendus similique! Officiis autem quasi
                            veniam cumque repudiandae, tenetur quia asperiores, amet
                            eos commodi labore illo, earum dolore neque repellat
                            doloremque.</span
                          >
                                    <span class="flex-1 truncate mr-4">author@gmail.com</span>
                                    <a href=""
                                       class="border border-gray-300 py-1 px-3 rounded-lg text-sm mr-2 cursor-pointer hover:bg-gray-200"
                                    >
                                        Detail
                                    </a>
                                    <a href=""
                                    ><i
                                            class="fa-solid fa-trash-can text-red-400 hover:text-red-300"
                                    ></i
                                    ></a>
                                </div>

                                <div
                                        class="flex justify-between items-center px-4 py-2 bg-gray-100 rounded-lg"
                                >
                          <span class="flex-1 truncate mr-4"
                          >Lorem ipsum, dolor sit amet consectetur adipisicing
                            elit. Odio, repellendus similique! Officiis autem quasi
                            veniam cumque repudiandae, tenetur quia asperiores, amet
                            eos commodi labore illo, earum dolore neque repellat
                            doloremque.</span
                          >
                                    <span class="flex-1 truncate mr-4">author@gmail.com</span>
                                    <a href=""
                                       class="border border-gray-300 py-1 px-3 rounded-lg text-sm mr-2 cursor-pointer hover:bg-gray-200"
                                    >
                                        Detail
                                    </a>
                                    <a href=""
                                    ><i
                                            class="fa-solid fa-trash-can text-red-400 hover:text-red-300"
                                    ></i
                                    ></a>
                                </div>

                                <div
                                        class="flex justify-between items-center px-4 py-2 bg-gray-100 rounded-lg"
                                >
                          <span class="flex-1 truncate mr-4"
                          >Lorem ipsum, dolor sit amet consectetur adipisicing
                            elit. Odio, repellendus similique! Officiis autem quasi
                            veniam cumque repudiandae, tenetur quia asperiores, amet
                            eos commodi labore illo, earum dolore neque repellat
                            doloremque.</span
                          >
                                    <span class="flex-1 truncate mr-4">author@gmail.com</span>
                                    <a href=""
                                       class="border border-gray-300 py-1 px-3 rounded-lg text-sm mr-2 cursor-pointer hover:bg-gray-200"
                                    >Detail</a
                                    >
                                    <a href=""
                                    ><i
                                            class="fa-solid fa-trash-can text-red-400 hover:text-red-300"
                                    ></i
                                    ></a>
                                </div>
                            </div>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <dialog closedby="any" id="modalCoauthor" class="md:w-lg w-sm">
        <div class="p-4">
            <div>
                <p class="text-2xl font-semibold">Add Co-Author</p>
                <p id="inviteTitle" class="text-gray-500 font-light">
                </p>
            </div>

            <form action="" method="POST" class="mt-6">
                <label for="email" class="block text-xl">Email Address</label>
                <input
                        type="email"
                        class="py-2 px-3 mt-2 mb-6 border border-gray-400 rounded-xl w-full"
                        id="email"
                        name="email"
                        placeholder="coauthor@example.com"
                />

                <div class="flex w-full justify-between gap-6">
                    <button
                            type="submit"
                            class="flex-1 border border-gray-400 bg-sky-200 py-2 px-3 rounded-xl cursor-pointer hover:bg-sky-300"
                    >
                        Send Invitation
                    </button>
                    <button onclick="closeModal()" type="reset"
                            class="flex-1 border border-gray-400 rounded-xl cursor-pointer hover:bg-gray-400">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    </dialog>
</main>

<script>
    const modalCoauthor = document.getElementById('modalCoauthor');
    const titleElement = document.getElementById('inviteTitle');

    function showModal(title) {
        modalCoauthor.showModal();
        titleElement.textContent = 'Invite a co-author to collaborate on "' + title + '"';
    }

    function closeModal() {
        modalCoauthor.close();
    }

</script>
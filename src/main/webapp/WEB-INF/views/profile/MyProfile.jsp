<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/13/2025
  Time: 2:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<main
        class="my-12 grid grid-cols-12 gap-x-12 justify-center"
>
    <div
            class="col-span-4 border border-sky-600/50 rounded-xl text-center h-fit"
    >
        <div
                class="w-1/2 rounded-full overflow-hidden aspect-square mx-auto m-8"
        >
            <img
                    src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                    class="w-full h-full object-center"
                    alt=""
            />
        </div>

        <div class="px-3">
            <div>
                <p class="font-semibold text-3xl">${user.username}</p>
                <div class="flex justify-between py-4 border-b border-black">
                    <p>${user.role}</p>
                    <p>
                        <span class="text-sky-600 inline-block pr-2">${user.points}</span>Points
                    </p>
                </div>
            </div>

            <ul class="py-4 text-left">
                <li
                        class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4"
                >
                    <i class="fa-solid fa-user"></i>
                    <div>
                        <p class="text-sm">Username</p>
                        <p class="font-semibold">${user.username}</p>
                    </div>
                </li>

                <li
                        class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4"
                >
                    <i class="fa-solid fa-user"></i>
                    <div>
                        <p class="text-sm">Full name</p>
                        <p class="font-semibold">${user.fullName}</p>
                    </div>
                </li>

                <li
                        class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4"
                >
                    <i class="fa-solid fa-envelope"></i>
                    <div>
                        <p class="text-sm">Email</p>
                        <p class="font-semibold">${user.email}</p>
                    </div>
                </li>
            </ul>

            <div class="flex justify-center gap-8 pb-4">
                <button
                        id="editProfile"
                        class="py-2 px-4 border-2 border-sky-100 bg-sky-100 text-sky-500 hover:bg-sky-500 hover:text-white rounded-md"
                >
                    Edit Profile
                </button>
                <button
                        id="changePassword"
                        class="py-2 px-4 border-2 border-sky-100 text-sky-600 hover:bg-sky-100 rounded-md"
                >
                    Change Password
                </button>
            </div>
        </div>
    </div>

    <div class="col-span-8">
        <div class="border border-sky-600/50 rounded-xl p-2">
            <p class="text-primary font-semibold text-sky-600/50 text-3xl">Bio</p>

            <p class="whitespace-pre-wrap text-neutral-400">${user.bio}</p>
        </div>

        <div class="border border-sky-600/50 rounded-xl px-4 py-8 mt-8">
            <ul class="w-full flex flex-wrap gap-y-8">
                <c:forEach var="badge" items="${badges}">
                    <li class="w-1/3 text-center">
                        <div class="size-32 mx-auto mb-1 rounded-full overflow-hidden bg-transparent">
                            <img
                                    src="${badge.iconUrl}"
                                    class="w-full h-full object-center"
                                    alt="${user.username}"
                            />
                        </div>
                        <p>${badge.name}</p>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</main>

<!-- dialog for edit profile -->
<dialog closedby="any" id="editProfileDialog">
    <form action="${pageContext.request.contextPath}/profile" method="post">
        <div class="min-w-3xl px-4">
            <p class="font-semibold text-xl">Edit Profile</p>
            <div class="flex py-6">
                <div class="w-1/3 mx-auto text-center">
                    <div
                            class="size-28 mx-auto aspect-square rounded-full overflow-hidden"
                    >
                        <img
                                src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                                class="w-full h-full object-center"
                                alt="${user.username}"
                        />
                    </div>
                </div>

                <div class="w-2/3">
                    <div>
                        <label class="block text-primary text-sm" for="username"
                        >Username</label
                        >
                        <input
                                type="text"
                                class="border border-neutral-400 w-full py-1 px-2 mt-1 mb-4 rounded-lg"
                                name="username"
                                id="username"
                                value="${user.username}"
                        />
                    </div>

                    <div>
                        <label class="block text-primary text-sm" for="fullName"
                        >Full name</label
                        >
                        <input
                                type="text"
                                class="border border-neutral-400 w-full py-1 px-2 mt-1 mb-4 rounded-lg"
                                name="fullName"
                                id="fullName"
                                value="${user.fullName}"
                        />
                    </div>
                </div>
            </div>

            <div>
                <label for="bio" class="block text-primary font-semibold">Bio</label>
                <textarea
                        class="w-full min-h-64 border border-neutral-400 rounded-lg p-2"
                        name="bio"
                        id="bio"
                >${user.bio}</textarea
                >
                <p class="text-xs text-red-700/70">Maximum 160 characters length</p>
            </div>

            <div class="flex justify-center gap-4">
                <button
                        class="py-2 px-4 bg-sky-100 border border-sky-100 hover:bg-sky-600 hover:text-white rounded-md"
                        type="submit"
                >
                    Save
                </button>
                <button
                        class="cancelBtn py-2 px-4 border border-neutral-300 hover:bg-sky-600 hover:text-white rounded-md"
                        type="reset"
                >
                    Cancel
                </button>
            </div>
        </div>
    </form>
</dialog>

<dialog closedby="any" id="changePasswordDialog">
    <form action="${pageContext.request.contextPath}/change-password"  method="post">
        <div class="min-w-md">
            <p class="font-semibold text-xl">Edit Profile</p>

            <div class="my-4">
                <label class="block mb-1" for="currentPass">Current Password</label>
                <div class="relative">
                    <input
                            class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                            type="password"
                            id="currentPass"
                            name="currentPass"
                            placeholder="Enter your old password"
                    />
                    <i
                            class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                            onclick="togglePassword(this, 'currentPass')"
                    ></i>
                </div>
            </div>

            <div class="my-4">
                <label class="block mb-1" for="newPassword">New Password</label>
                <div class="relative">
                    <input
                            class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                            type="password"
                            id="newPassword"
                            name="newPassword"
                            placeholder="Enter your new password"
                    />
                    <i
                            class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                            onclick="togglePassword(this, 'newPassword')"
                    ></i>
                </div>
            </div>

            <div class="my-4">
                <label class="block mb-1" for="confirmPassword">Re-enter Password</label>
                <div class="relative">
                    <input
                            class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            placeholder="Re-Enter your new password"
                    />
                    <i
                            class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                            onclick="togglePassword(this, 'confirmPassword')"
                    ></i>
                </div>
            </div>

            <div class="flex justify-center gap-4">
                <button
                        class="py-2 px-4 bg-sky-100 border border-sky-100 hover:bg-sky-600 hover:text-white rounded-md"
                        type="submit"
                >
                    Save
                </button>
                <button
                        class="cancelBtn py-2 px-4 border border-neutral-300 hover:bg-sky-600 hover:text-white rounded-md"
                        type="reset"
                >
                    Cancel
                </button>
            </div>
        </div>
    </form>
</dialog>

<c:if test="${not empty message}">
    <script>
        alert("${message}")
        <c:remove var="message" scope="session"/>
    </script>
</c:if>

<script>
    const editProfileButton = document.getElementById('editProfile');
    const changePasswordButton = document.getElementById('changePassword');
    const cancelButtons = document.querySelectorAll('.cancelBtn');

    const editProfileDialog = document.getElementById('editProfileDialog');
    const changePasswordDialog = document.getElementById(
        'changePasswordDialog'
    );
    editProfileButton.addEventListener('click', () => {
        editProfileDialog.showModal();
    });

    changePasswordButton.addEventListener('click', () => {
        changePasswordDialog.showModal();
    });

    cancelButtons.forEach((cancelButton) => {
        cancelButton.addEventListener('click', () => {
            editProfileDialog.close();
            changePasswordDialog.close();
        });
    });

    function togglePassword(iconElement, id) {
        const inputElement = document.getElementById(id);
        iconElement.classList.toggle('fa-eye-slash');
        iconElement.classList.toggle('fa-eye');

        if (inputElement.type === 'password') {
            inputElement.type = 'text';
        } else {
            inputElement.type = 'password';
        }
    }


</script>

<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/13/2025
  Time: 2:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>My Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
    <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/fontawesome/css/all.min.css"
    />
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet"
    />
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/_header.jsp" />

    <main
            class="max-w-7xl mx-auto my-12 grid grid-cols-12 gap-x-12 justify-center"
    >
        <div
                class="col-span-4 border border-sky-600/50 rounded-xl text-center h-fit"
        >
            <div
                    class="w-1/2 rounded-full overflow-hidden aspect-square mx-auto m-8"
            >
                <img
                        src="../img/thenewkidinschool.png"
                        class="w-full h-full object-center"
                        alt=""
                />
            </div>

            <div class="px-3">
                <div>
                    <p class="font-semibold text-3xl">Truong Duy Phuc</p>
                    <div class="flex justify-between py-4 border-b border-black">
                        <p>Author</p>
                        <p>
                            <span class="text-sky-600 inline-block pr-2">1000</span>Points
                        </p>
                    </div>
                </div>

                <ul class="py-4 text-left">
                    <li
                            class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4"
                    >
                        <div>
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="22"
                                    height="22"
                                    class="bi bi-person"
                                    viewBox="0 0 16 16"
                            >
                                <defs>
                                    <linearGradient id="gradient" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stop-color="#341661" />
                                        <stop offset="50%" stop-color="#4C1D95" />
                                        <stop offset="100%" stop-color="#195BA7" />
                                    </linearGradient>
                                </defs>
                                <path
                                        fill="url(#gradient)"
                                        d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"
                                />
                            </svg>
                        </div>
                        <div>
                            <p class="text-sm">Username</p>
                            <p class="font-semibold">DuyPhuc</p>
                        </div>
                    </li>

                    <li
                            class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4"
                    >
                        <div>
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="22"
                                    height="22"
                                    class="bi bi-person"
                                    viewBox="0 0 16 16"
                            >
                                <defs>
                                    <linearGradient id="gradient" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stop-color="#341661" />
                                        <stop offset="50%" stop-color="#4C1D95" />
                                        <stop offset="100%" stop-color="#195BA7" />
                                    </linearGradient>
                                </defs>
                                <path
                                        fill="url(#gradient)"
                                        d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"
                                />
                            </svg>
                        </div>
                        <div>
                            <p class="text-sm">Full name</p>
                            <p class="font-semibold">Truong Duy Phuc</p>
                        </div>
                    </li>

                    <li
                            class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4"
                    >
                        <div class="size-5.5">
                            <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 640 640"
                                    class="w-full h-full"
                            >
                                <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                                <path
                                        d="M112 128C85.5 128 64 149.5 64 176C64 191.1 71.1 205.3 83.2 214.4L291.2 370.4C308.3 383.2 331.7 383.2 348.8 370.4L556.8 214.4C568.9 205.3 576 191.1 576 176C576 149.5 554.5 128 528 128L112 128zM64 260L64 448C64 483.3 92.7 512 128 512L512 512C547.3 512 576 483.3 576 448L576 260L377.6 408.8C343.5 434.4 296.5 434.4 262.4 408.8L64 260z"
                                />
                            </svg>
                        </div>
                        <div>
                            <p class="text-sm">Email</p>
                            <p class="font-semibold">phuctd.ce190116</p>
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

                <p class="whitespace-pre-wrap text-neutral-400">
                    In a world where magic and technology coexist, young Aria Starweaver
                    discovers she possesses a rare gift that could either save her realm
                    or destroy it completely. As ancient prophecies unfold and dark
                    forces gather, she must navigate treacherous alliances, master her
                    newfound powers, and uncover the truth about her mysterious
                    heritage.
                </p>
            </div>

            <div class="border border-sky-600/50 rounded-xl px-4 py-8 mt-8">
                <ul class="w-full flex flex-wrap gap-y-8">
                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>

                    <li class="w-1/3 text-center">
                        <div class="mb-1">
                            <img class="mx-auto" src="../img/Rectangle 397.svg" alt="" />
                        </div>
                        <p>Become an Author</p>
                    </li>
                </ul>
            </div>
        </div>
    </main>

    <!-- dialog for edit profile  -->
    <dialog closedby="any" id="editProfileDialog">
        <form action="" method="post">
            <div class="min-w-3xl px-4">
                <p class="font-semibold text-xl">Edit Profile</p>
                <div class="flex py-6">
                    <div class="w-1/3 mx-auto text-center">
                        <div
                                class="size-28 mx-auto aspect-square rounded-full overflow-hidden"
                        >
                            <img
                                    id="avatar-preview"
                                    src="../img/thenewkidinschool.png"
                                    class="object-cover w-full h-full"
                                    alt="Avatar preview"
                            />
                        </div>
                        <label
                                class="py-1 px-3 border border-neutral-400 rounded-lg text-sm mt-4 inline-block cursor-pointer"
                                for="avatar"
                        >Choose file image</label
                        >
                        <input
                                type="file"
                                name="avatar"
                                id="avatar"
                                accept="image/*"
                                hidden
                        />
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
                                    value="DuyPhuc"
                            />
                        </div>

                        <div>
                            <label class="block text-primary text-sm" for="fullname"
                            >Full name</label
                            >
                            <input
                                    type="text"
                                    class="border border-neutral-400 w-full py-1 px-2 mt-1 mb-4 rounded-lg"
                                    name="fullname"
                                    id="fullname"
                                    value="Truong Duy Phuc"
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
                    >
              value</textarea
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
        <form action="" method="post">
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

    <jsp:include page="/WEB-INF/views/components/_footer.jsp" />

    <script>
        const editProfileButton = document.getElementById('editProfile');
        const changePasswordButton = document.getElementById('changePassword');
        const cancelButtons = document.querySelectorAll('.cancelBtn');

        const editProfileDialog = document.getElementById('editProfileDialog');
        const changePasswordDialog = document.getElementById(
            'changePasswordDialog'
        );

        const avatarInput = document.getElementById('avatar');
        const avatarPreview = document.getElementById('avatar-preview');

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

        avatarInput.addEventListener('change', function (event) {
            if (event.target.files && event.target.files[0]) {
                const file = event.target.files[0];

                const objectURL = URL.createObjectURL(file);

                avatarPreview.src = objectURL;

                avatarPreview.onload = function () {
                    URL.revokeObjectURL(objectURL);
                };
            }
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

</body>
</html>

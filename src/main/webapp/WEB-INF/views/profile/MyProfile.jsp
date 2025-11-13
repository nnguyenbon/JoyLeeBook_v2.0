<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<main class="my-12 grid grid-cols-12 gap-x-12 justify-center">
    <div class="col-span-4 border border-sky-600/50 rounded-xl text-center h-fit">
        <div class="w-1/2 rounded-full overflow-hidden aspect-square mx-auto m-8">
            <img src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                 class="w-full h-full object-center" alt=""/>
        </div>

        <div class="px-3">
            <div>
                <p class="font-semibold text-3xl">${user.username}</p>
                <div class="flex justify-between py-4 border-b border-black">
                    <p>${user.role}</p>
                    <p><span class="text-sky-600 inline-block pr-2">${user.points}</span>Points</p>
                </div>
            </div>

            <ul class="py-4 text-left">
                <li class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4">
                    <i class="fa-solid fa-user"></i>
                    <div>
                        <p class="text-sm">Username</p>
                        <p class="font-semibold">${user.username}</p>
                    </div>
                </li>

                <li class="flex items-center gap-2 bg-sky-100 py-2 px-4 rounded-md my-4">
                    <i class="fa-solid fa-user"></i>
                    <div>
                        <p class="text-sm">Full name</p>
                        <p class="font-semibold">${user.fullName}</p>
                    </div>
                </li>

                <li class="flex items-center justify-between bg-sky-100 py-2 px-4 rounded-md my-4">
                    <div class="flex items-center gap-2">
                        <i class="fa-solid fa-envelope"></i>
                        <div>
                            <p class="text-sm">Email</p>
                            <p class="font-semibold">${user.email}</p>
                        </div>
                    </div>

                    <!-- Only show edit button if NOT Google login -->
                    <c:if test="${empty user.googleId}">
                        <button id="editEmail" type="button"
                                class="inline-flex items-center gap-2 px-3 py-1.5 text-sm border-2 border-sky-100 text-sky-600 hover:bg-sky-500 hover:text-white rounded-md transition"
                                title="Edit email">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </button>
                    </c:if>
                </li>
            </ul>

            <div class="flex justify-center gap-8 pb-4">
                <button id="editProfile"
                        class="py-2 px-4 border-2 border-sky-100 bg-sky-100 text-sky-500 hover:bg-sky-500 hover:text-white rounded-md">
                    Edit Profile
                </button>
                <!-- Only show change password if NOT Google login -->
                <c:if test="${empty user.googleId}">
                    <button id="changePassword"
                            class="py-2 px-4 border-2 border-sky-100 text-sky-600 hover:bg-sky-100 rounded-md">
                        Change Password
                    </button>
                </c:if>
            </div>
        </div>
    </div>

    <div class="col-span-8">
        <div class="border border-sky-600/50 rounded-xl p-2">
            <p class="text-primary font-semibold text-sky-600/50 text-3xl">Bio</p>
            <c:choose>
                <c:when test="${user.bio == null}">
                    <p class="text-neutral-400">What is your bio?</p>
                </c:when>
                <c:otherwise>
                    <p class="whitespace-pre-wrap text-neutral-400">${user.bio}</p>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="border border-sky-600/50 rounded-xl px-4 py-8 mt-8">
            <ul class="grid grid-cols-3 gap-6">
                <c:forEach var="badge" items="${badgeList}">
                    <li class="flex flex-col items-center">
                        <div class="w-32 h-32 mb-2 rounded-full overflow-hidden bg-gray-200 relative flex items-center justify-center flex-shrink-0">
                            <c:choose>
                                <c:when test="${badge.unlocked}">
                                    <img src="${badge.iconUrl}" class="w-full h-full object-cover" alt="${badge.name}"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="${badge.iconUrl}" class="w-full h-full object-cover grayscale opacity-50" alt="${badge.name}"/>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${!badge.unlocked}">
                                <div class="absolute inset-0 flex items-center justify-center bg-opacity-20"></div>
                            </c:if>
                        </div>
                        <c:choose>
                            <c:when test="${badge.unlocked}">
                                <p class="text-sm text-gray-900 font-medium text-center">${badge.name}</p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-sm text-gray-400 text-center">${badge.name}</p>
                                <p class="text-xs text-gray-400 mt-0.5">Locked</p>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</main>

<!-- Dialog for Edit Profile -->
<dialog closedby="any" id="editProfileDialog">
    <form action="${pageContext.request.contextPath}/profile/edit" method="post">
        <div class="min-w-3xl px-4">
            <p class="font-semibold text-xl">Edit Profile</p>
            <div class="flex py-6">
                <div class="w-1/3 mx-auto text-center">
                    <div class="w-2/3 mx-auto aspect-square rounded-full overflow-hidden">
                        <img src="${pageContext.request.contextPath}/img/shared/imgUser.png"
                             class="w-full h-full object-center" alt="${user.username}"/>
                    </div>
                </div>

                <div class="w-2/3">
                    <div>
                        <label class="block text-primary text-sm" for="username">Username</label>
                        <input type="text" class="border border-neutral-400 w-full py-1 px-2 mt-1 rounded-lg"
                               name="username" id="username" value="${user.username}"/>
                        <p id="username-error" class="text-xs text-red-700"></p>
                    </div>

                    <div>
                        <label class="block text-primary text-sm mt-4" for="fullName">Full name</label>
                        <input type="text" class="border border-neutral-400 w-full py-1 px-2 mt-1 rounded-lg"
                               name="fullName" id="fullName" value="${user.fullName}"/>
                        <p id="fullname-error" class="text-xs text-red-700"></p>
                    </div>
                </div>
            </div>

            <div>
                <label for="bio" class="block text-primary font-semibold mt-4">Bio</label>
                <textarea class="w-full min-h-64 border border-neutral-400 rounded-lg p-2"
                          name="bio" id="bio">${user.bio}</textarea>
                <p class="text-xs text-red-700/70">Maximum 160 character length</p>
            </div>

            <div class="flex justify-center gap-4">
                <button class="py-2 px-4 bg-sky-100 border border-sky-100 hover:bg-sky-600 hover:text-white rounded-md"
                        type="submit">Save</button>
                <button class="cancelBtn py-2 px-4 border border-neutral-300 hover:bg-sky-600 hover:text-white rounded-md"
                        type="reset">Cancel</button>
            </div>
        </div>
    </form>
</dialog>

<!-- Dialog for Change Password -->
<dialog closedby="any" id="changePasswordDialog">
    <form action="${pageContext.request.contextPath}/profile/changePassword" method="post">
        <div class="min-w-md">
            <p class="font-semibold text-xl">Change Password</p>

            <div class="my-4">
                <label class="block mb-1" for="currentPass">Current Password</label>
                <div class="relative">
                    <input class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                           type="password" id="currentPass" name="currentPass"
                           placeholder="Enter your current password"/>
                    <i class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                       onclick="togglePassword(this, 'currentPass')"></i>
                </div>
            </div>

            <div class="my-4">
                <label class="block mb-1" for="newPassword">New Password</label>
                <div class="relative">
                    <input class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                           type="password" id="newPassword" name="newPassword"
                           placeholder="Enter your new password"/>
                    <i class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                       onclick="togglePassword(this, 'newPassword')"></i>
                    <p id="password-error" class="text-xs text-red-700"></p>
                </div>
            </div>

            <div class="my-4">
                <label class="block mb-1" for="confirmPassword">Re-enter Password</label>
                <div class="relative">
                    <input class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                           type="password" id="confirmPassword" name="confirmPassword"
                           placeholder="Re-Enter your new password"/>
                    <i class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                       onclick="togglePassword(this, 'confirmPassword')"></i>
                </div>
            </div>

            <div class="flex justify-center gap-4">
                <button class="py-2 px-4 bg-sky-100 border border-sky-100 hover:bg-sky-600 hover:text-white rounded-md"
                        type="submit">Save</button>
                <button class="cancelBtn py-2 px-4 border border-neutral-300 hover:bg-sky-600 hover:text-white rounded-md"
                        type="reset">Cancel</button>
            </div>
        </div>
    </form>
</dialog>

<!-- Dialog for Change Email -->
<dialog closedby="any" id="changeEmailDialog">
    <div class="min-w-md px-4">
        <p class="font-semibold text-xl mb-4">Change Email</p>

        <div id="emailStep">
            <div class="my-4">
                <label class="block mb-1" for="newEmail">New Email</label>
                <input class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                       type="email" id="newEmail" name="newEmail"
                       placeholder="Enter your new email"/>
                <p id="email-error" class="text-xs text-red-700"></p>
            </div>

            <div class="flex justify-center gap-4">
                <button id="sendOtpBtn"
                        class="py-2 px-4 bg-sky-100 border border-sky-100 hover:bg-sky-600 hover:text-white rounded-md"
                        type="button">Send OTP</button>
                <button class="cancelBtn py-2 px-4 border border-neutral-300 hover:bg-sky-600 hover:text-white rounded-md"
                        type="button">Cancel</button>
            </div>
        </div>

        <div id="otpStep" style="display: none;">
            <p class="text-sm text-gray-600 mb-4">We've sent a verification code to <span id="displayEmail" class="font-semibold"></span></p>

            <div class="my-4">
                <label class="block mb-1" for="emailOtp">Enter OTP</label>
                <input class="border border-neutral-400 w-full py-2 px-4 rounded-lg"
                       type="text" id="emailOtp" name="emailOtp"
                       placeholder="Enter 6-digit OTP" maxlength="6"/>
                <p id="otp-error" class="text-xs text-red-700"></p>
            </div>

            <div class="flex justify-center gap-4">
                <button id="verifyOtpBtn"
                        class="py-2 px-4 bg-sky-100 border border-sky-100 hover:bg-sky-600 hover:text-white rounded-md"
                        type="button">Verify OTP</button>
                <button id="resendOtpBtn"
                        class="py-2 px-4 border border-sky-200 text-sky-600 hover:bg-sky-100 rounded-md"
                        type="button">Resend OTP</button>
            </div>
        </div>
    </div>
</dialog>

<c:if test="${not empty message}">
    <script>
        toastr["success"]("${message}")
        <c:remove var="message" scope="session"/>
    </script>
</c:if>

<script>
    const editProfileButton = document.getElementById('editProfile');
    const changePasswordButton = document.getElementById('changePassword');
    const editEmailButton = document.getElementById('editEmail');
    const cancelButtons = document.querySelectorAll('.cancelBtn');

    const editProfileDialog = document.getElementById('editProfileDialog');
    const changePasswordDialog = document.getElementById('changePasswordDialog');
    const changeEmailDialog = document.getElementById('changeEmailDialog');

    editProfileButton.addEventListener('click', () => {
        editProfileDialog.showModal();
    });

    if (changePasswordButton) {
        changePasswordButton.addEventListener('click', () => {
            changePasswordDialog.showModal();
        });
    }

    if (editEmailButton) {
        editEmailButton.addEventListener('click', () => {
            changeEmailDialog.showModal();
            document.getElementById('emailStep').style.display = 'block';
            document.getElementById('otpStep').style.display = 'none';
            document.getElementById('newEmail').value = '';
            document.getElementById('email-error').textContent = '';
        });
    }

    cancelButtons.forEach((cancelButton) => {
        cancelButton.addEventListener('click', () => {
            editProfileDialog.close();
            if (changePasswordDialog) changePasswordDialog.close();
            if (changeEmailDialog) changeEmailDialog.close();
        });
    });

    function togglePassword(iconElement, id) {
        const inputElement = document.getElementById(id);
        iconElement.classList.toggle('fa-eye-slash');
        iconElement.classList.toggle('fa-eye');
        inputElement.type = inputElement.type === 'password' ? 'text' : 'password';
    }

    let debounceTimer;
    function debounce(func, delay) {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(func, delay);
    }

    // Send OTP for email change
    document.getElementById('sendOtpBtn')?.addEventListener('click', function() {
        const newEmail = document.getElementById('newEmail').value.trim();
        const errorElement = document.getElementById('email-error');

        if (!newEmail) {
            errorElement.textContent = 'Please enter an email';
            return;
        }

        this.disabled = true;
        this.textContent = 'Sending...';

        fetch('${pageContext.request.contextPath}/profile/sendEmailOtp', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'newEmail=' + encodeURIComponent(newEmail)
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    toastr.success(data.message);
                    document.getElementById('emailStep').style.display = 'none';
                    document.getElementById('otpStep').style.display = 'block';
                    document.getElementById('displayEmail').textContent = newEmail;
                } else {
                    toastr.error(data.message);
                    errorElement.textContent = data.message;
                }
            })
            .catch(() => {
                toastr.error('Something went wrong. Please try again.');
            })
            .finally(() => {
                this.disabled = false;
                this.textContent = 'Send OTP';
            });
    });

    // Verify OTP
    document.getElementById('verifyOtpBtn')?.addEventListener('click', function() {
        const otp = document.getElementById('emailOtp').value.trim();
        const errorElement = document.getElementById('otp-error');

        if (!otp || otp.length !== 6) {
            errorElement.textContent = 'Please enter a 6-digit OTP';
            return;
        }

        this.disabled = true;
        this.textContent = 'Verifying...';

        fetch('${pageContext.request.contextPath}/profile/verifyEmailOtp', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'otp=' + encodeURIComponent(otp)
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    toastr.success(data.message);
                    changeEmailDialog.close();
                    setTimeout(() => location.reload(), 1500);
                } else {
                    toastr.error(data.message);
                    errorElement.textContent = data.message;
                }
            })
            .catch(() => {
                toastr.error('Something went wrong. Please try again.');
            })
            .finally(() => {
                this.disabled = false;
                this.textContent = 'Verify OTP';
            });
    });

    // Resend OTP
    document.getElementById('resendOtpBtn')?.addEventListener('click', function() {
        const newEmail = document.getElementById('newEmail').value.trim();

        this.disabled = true;
        this.textContent = 'Sending...';

        fetch('${pageContext.request.contextPath}/profile/sendEmailOtp', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'newEmail=' + encodeURIComponent(newEmail)
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    toastr.success('OTP resent successfully');
                } else {
                    toastr.error(data.message);
                }
            })
            .finally(() => {
                this.disabled = false;
                this.textContent = 'Resend OTP';
            });
    });

    // Validation functions
    document.getElementById("username").addEventListener("input", function () {
        const username = this.value.trim();
        const errorElement = document.getElementById("username-error");
        debounce(() => {
            fetch("/register?type=username&value=" + encodeURIComponent(username))
                .then(res => res.json())
                .then(data => {
                    errorElement.textContent = data.valid ? "" : data.message;
                    validateEdit();
                })
                .catch(() => {
                    errorElement.textContent = "";
                    validateEdit();
                });
        }, 400);
    });

    document.getElementById("fullName").addEventListener("input", function () {
        const fullName = this.value.trim();
        const errorElement = document.getElementById("fullname-error");
        debounce(() => {
            fetch("/register?type=fullname&value=" + encodeURIComponent(fullName))
                .then(res => res.json())
                .then(data => {
                    errorElement.textContent = data.valid ? "" : data.message;
                    validateEdit();
                })
                .catch(() => {
                    errorElement.textContent = "";
                    validateEdit();
                });
        }, 400);
    });

    document.getElementById("newPassword")?.addEventListener("input", function () {
        const password = this.value.trim();
        const errorElement = document.getElementById("password-error");
        debounce(() => {
            fetch("/register?type=password&value=" + encodeURIComponent(password))
                .then(res => res.json())
                .then(data => {
                    errorElement.textContent = data.valid ? "" : data.message;
                    validateChange();
                })
                .catch(() => {
                    errorElement.textContent = "";
                    validateChange();
                });
        }, 400);
    });

    function validateEdit() {
        const username = document.getElementById("username").value.trim();
        const fullName = document.getElementById("fullName").value.trim();
        const usernameError = document.getElementById("username-error").textContent.trim();
        const fullnameError = document.getElementById("fullname-error").textContent.trim();
        const editBtn = document.querySelector("#editProfileDialog button[type=submit]");
        editBtn.disabled = !(username && fullName && !usernameError && !fullnameError);
    }

    function validateChange() {
        const password = document.getElementById("newPassword").value.trim();
        const confirmPassword = document.getElementById("confirmPassword").value.trim();
        const passwordError = document.getElementById("password-error").textContent.trim();
        const changeBtn = document.querySelector("#changePasswordDialog button[type=submit]");
        changeBtn.disabled = !(password && confirmPassword && password === confirmPassword && !passwordError);
    }

    document.getElementById("confirmPassword")?.addEventListener("input", validateChange);
    document.addEventListener("DOMContentLoaded", () => {
        validateEdit();
        if (document.getElementById("newPassword")) validateChange();
    });
</script>
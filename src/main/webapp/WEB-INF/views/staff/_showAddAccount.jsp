<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="max-w-4xl mx-auto flex flex-col h-full items-center justify-center">
    <!-- Create Account Card -->
    <div class="bg-white shadow-lg shadow-gray-400 rounded-2xl border border-[#195DA9] overflow-hidden mb-6 p-8">
        <main class="overflow-y-auto max-h-[80vh] px-4 custom-scrollbar">
            <div class="text-center mb-6">
                <h1 class="font-bold text-4xl text-[#0A2A66]">Edit Account</h1>
                <c:if test="${not empty message}">
                    <p class="text-red-600 font-medium mt-2">${message}</p>
                </c:if>
            </div>

            <form action="${pageContext.request.contextPath}/account/insert" method="post" class="max-w-2xl mx-auto">
                <!-- Role Selection -->
                <div class="flex justify-center mb-5 border-b pb-2 gap-5">
                    <span class="text-lg text-[#195DA9] font-bold">With role:</span>
                    <label class="flex items-center gap-2">
                        <input type="radio" name="type" value="staff" class="text-[#0A2A66] focus:ring-[#0A2A66]"
                               onchange="toggleEmailField()" ${type == 'staff' ? 'checked' : ''}>
                        <span class="font-medium text-gray-700">Staff</span>
                    </label>
                    <label class="flex items-center gap-2">
                        <input type="radio" name="type" value="reader" class="text-[#0A2A66] focus:ring-[#0A2A66]"
                               onchange="toggleEmailField()" ${type != 'staff' ? 'checked' : ''}>
                        <span class="font-medium text-gray-700">Reader</span>
                    </label>
                </div>

                <!-- Username -->
                <div>
                    <label class="block mt-2 mb-1 font-medium text-gray-700" for="username">Username*</label>
                    <input
                            type="text"
                            id="username"
                            name="username"
                            class="border border-gray-400 w-full rounded-md px-4 py-2 focus:ring-2 focus:ring-[#0A2A66] outline-none"
                            placeholder="Enter a unique username"
                            value="${username == null ? "" : username}"
                            required
                    />
                    <p id="username-error" class="text-xs text-red-700"></p>
                </div>

                <!-- Full Name -->
                <div>
                    <label class="block mt-2 mb-1 font-medium text-gray-700" for="fullName">Full Name*</label>
                    <input
                            type="text"
                            id="fullName"
                            name="fullName"
                            class="border border-gray-400 w-full rounded-md px-4 py-2 focus:ring-2 focus:ring-[#0A2A66] outline-none"
                            placeholder="Enter full name"
                            value="${fullName == null ? "" : fullName}"
                            required
                    />
                    <p id="fullname-error" class="text-xs text-red-700"></p>
                </div>

                <!-- Email - Chỉ hiện khi là Reader -->
                <div id="email-container" style="display: none;">
                    <label class="block mt-2 mb-1 font-medium text-gray-700" for="email">Email*</label>
                    <input type="email" id="email" name="email"
                           class="border border-gray-400 w-full rounded-md px-4 py-2 focus:ring-2 focus:ring-[#0A2A66] outline-none"
                           placeholder="Enter email address"
                           value="${email != null ? email : ''}" />
                    <p id="email-error" class="text-xs text-red-700"></p>
                </div>

                <!-- Password + Confirm Password -->
                <div class="flex gap-4">
                    <div class="flex-1">
                        <label class="block mt-2 mb-1 font-medium text-gray-700" for="password">Password*</label>
                        <div class="relative">
                            <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    class="border border-gray-400 w-full rounded-md pl-4 pr-8 py-2 focus:ring-2 focus:ring-[#0A2A66] outline-none"
                                    placeholder="Enter password"
                                    required
                            />
                            <i
                                    class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                                    onclick="togglePassword(this, 'password')"
                            ></i>
                        </div>
                        <p id="password-error" class="text-xs text-red-700"></p>
                    </div>

                    <div class="flex-1">
                        <label class="block mt-2 mb-1 font-medium text-gray-700" for="confirmPassword">Confirm Password*</label>
                        <div class="relative">
                            <input
                                    type="password"
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    class="border border-gray-400 w-full rounded-md pl-4 pr-8 py-2 focus:ring-2 focus:ring-[#0A2A66] outline-none"
                                    placeholder="Confirm password"
                                    required
                            />
                            <i
                                    class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                                    onclick="togglePassword(this, 'confirmPassword')"
                            ></i>
                        </div>
                    </div>
                </div>

                <!-- Submit -->
                <button
                        id="submit-btn"
                        class="block text-xl text-white font-bold py-2 mt-4 w-full rounded-md bg-[#0A2A66] hover:bg-[#153B8A] transition duration-300 cursor-pointer"
                        type="submit"
                >
                    Create Account
                </button>
            </form>

            <div class="mt-6 text-center text-sm text-gray-500">
                <p>Back to <a href="${pageContext.request.contextPath}/admin/accounts" class="text-[#0A2A66] hover:underline">Accounts Management</a></p>
            </div>
        </main>
    </div>
</div>
<script>

    // Ẩn/Hiện Email theo Role
    function toggleEmailField() {
        const isStaff = document.querySelector('input[name="type"]:checked').value === 'staff';
        const emailContainer = document.getElementById('email-container');
        const emailInput = document.getElementById('email');

        if (isStaff) {
            emailContainer.style.display = 'none';
            emailInput.removeAttribute('required');
            emailInput.value = ''; // Xóa giá trị
            document.getElementById('email-error').textContent = '';
        } else {
            emailContainer.style.display = 'block';
            emailInput.setAttribute('required', '');
        }

        validateForm(); // Cập nhật nút submit
    }
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

    let debounceTimer;

    function debounce(func, delay) {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(func, delay);
    }


    document.getElementById("username").addEventListener("input", function () {
        const username = this.value.trim();
        const errorElement = document.getElementById("username-error");

        debounce(() => {

            fetch("/register?type=username&value=" + encodeURIComponent(username))
                .then(res => res.json())
                .then(data => {
                    if (data.valid) {
                        errorElement.textContent = "";
                    } else {
                        errorElement.textContent = data.message;
                    }
                    validateForm();
                })
                .catch(() => {
                    errorElement.textContent = "";
                    validateForm();
                });
        }, 400);
    });


    document.getElementById("fullName").addEventListener("input", function () {
        const username = this.value.trim();
        const errorElement = document.getElementById("fullname-error");

        debounce(() => {

            fetch("/register?type=fullname&value=" + encodeURIComponent(username))
                .then(res => res.json())
                .then(data => {
                    if (data.valid) {
                        errorElement.textContent = "";
                    } else {
                        errorElement.textContent = data.message;
                    }
                    validateForm();
                })
                .catch(() => {
                    errorElement.textContent = "";
                    validateForm();
                });
        }, 400);
    });

    const emailInput = document.getElementById("email");
    if (emailInput) {
        emailInput.addEventListener("input", function () {
            const isStaff = document.querySelector('input[name="type"]:checked')?.value === 'staff';
            if (isStaff) {
                document.getElementById("email-error").textContent = "";
                validateForm();
                return; // Stop processing if Staff
            }
            const email = this.value.trim();
            const errorElement = document.getElementById("email-error");

            debounce(() => {
                fetch("/register?type=email&value=" + encodeURIComponent(email))
                    .then(res => res.json())
                    .then(data => {
                        if (data.valid) {
                            errorElement.textContent = "";
                        } else {
                            errorElement.textContent = data.message;
                        }
                        validateForm();
                    })
                    .catch(() => {
                        errorElement.textContent = "";
                        validateForm();
                    });
            }, 400);
        });
    }

    document.getElementById("password").addEventListener("input", function () {
        const email = this.value.trim();
        const errorElement = document.getElementById("password-error");

        debounce(() => {
            fetch("/register?type=password&value=" + encodeURIComponent(email))
                .then(res => res.json())
                .then(data => {
                    if (data.valid) {
                        errorElement.textContent = "";
                    } else {
                        errorElement.textContent = data.message;
                    }
                    validateForm();
                })
                .catch(() => {
                    errorElement.textContent = "";
                    validateForm();
                });
        }, 400);
    });

    function validateForm() {
        const usernameEl = document.getElementById("username");
        const fullNameEl = document.getElementById("fullName");
        const emailEl = document.getElementById("email");
        const passwordEl = document.getElementById("password");
        const confirmEl = document.getElementById("confirmPassword");

        const username = usernameEl?.value.trim() || "";
        const fullName = fullNameEl?.value.trim() || "";
        const email = emailEl?.value.trim() || "";
        const password = passwordEl?.value.trim() || "";
        const confirmPassword = confirmEl?.value.trim() || "";

        const usernameError = document.getElementById("username-error")?.textContent.trim() || "";
        const fullnameError = document.getElementById("fullname-error")?.textContent.trim() || "";
        const emailError = document.getElementById("email-error")?.textContent.trim() || "";
        const passwordError = document.getElementById("password-error")?.textContent.trim() || "";

        const submitBtn = document.getElementById("submit-btn");
        if (!submitBtn) return;

        // Kiểm tra role hiện tại
        const isStaff = document.querySelector('input[name="type"]:checked')?.value === 'staff';

        // Nếu là staff -> bỏ qua email hoàn toàn
        const isValid =
            username &&
            fullName &&
            password &&
            confirmPassword &&
            password === confirmPassword &&
            !usernameError &&
            !fullnameError &&
            !passwordError &&
            (isStaff || (email && !emailError)); // chỉ check email nếu không phải staff

        submitBtn.disabled = !isValid;
    }



    document.getElementById("confirmPassword").addEventListener("input", validateForm);
    document.addEventListener("DOMContentLoaded", validateForm);

</script>
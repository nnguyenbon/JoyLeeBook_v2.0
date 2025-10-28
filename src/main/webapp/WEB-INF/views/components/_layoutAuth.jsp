<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/19/2025
  Time: 6:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page buffer="32kb" autoFlush="true" %>
<html>
<head>
    <title>
        ${pageTitle}
    </title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/styles.css?v=<%= System.currentTimeMillis() %>"/>
    <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/fontawesome/css/all.min.css"
    />
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
    <link
            href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
            rel="stylesheet"
    />
    <style>
        /* Hiệu ứng glow xanh mờ */
        .blue-glow {
            background-image: radial-gradient(
                    circle at 30% 70%,
                    rgba(173, 216, 230, 0.35),
                    transparent 60%
            ),
            radial-gradient(
                    circle at 70% 30%,
                    rgba(255, 182, 193, 0.4),
                    transparent 60%
            );
        }
    </style>
</head>
<body class="min-h-screen w-full relative">
<!-- Radial Gradient Background from Bottom -->
<div class="absolute inset-0 z-0 blue-glow"></div>
<!-- Your Content/Components -->
<div
        class="relative z-10 flex flex-col items-center justify-center h-screen w-full max-w-lg mx-auto"
>
    <div class="w-1/4 mx-auto">
        <a href="${pageContext.request.contextPath}/homepage" class="block">
            <img src="${pageContext.request.contextPath}/img/shared/logo.png" alt="logo"/>
        </a>
    </div>
    <div class="rounded-lg ring px-6 py-4 w-full">
        <c:if test="${not empty contentPage}">
            <c:import url="${contentPage}"/>
        </c:if>
    </div>
</div>

<script>
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

                fetch("${pageContext.request.contextPath}/register?type=username&value=" + encodeURIComponent(username))
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

                fetch("${pageContext.request.contextPath}/register?type=fullname&value=" + encodeURIComponent(username))
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

        document.getElementById("email").addEventListener("input", function () {
            const email = this.value.trim();
            const errorElement = document.getElementById("email-error");

            debounce(() => {
                fetch("${pageContext.request.contextPath}/register?type=email&value=" + encodeURIComponent(email))
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

        document.getElementById("password").addEventListener("input", function () {
            const email = this.value.trim();
            const errorElement = document.getElementById("password-error");

            debounce(() => {
                fetch("${pageContext.request.contextPath}/register?type=password&value=" + encodeURIComponent(email))
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
            const username = document.getElementById("username").value.trim();
            const fullName = document.getElementById("fullName").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();
            const confirmPassword = document.getElementById("confirmPassword").value.trim();

            const usernameError = document.getElementById("username-error").textContent.trim();
            const fullnameError = document.getElementById("fullname-error").textContent.trim();
            const emailError = document.getElementById("email-error").textContent.trim();
            const passwordError = document.getElementById("password-error").textContent.trim();

            const submitBtn = document.getElementById("submit-btn");

            const isValid =
                username &&
                fullName &&
                email &&
                password &&
                confirmPassword &&
                password === confirmPassword &&
                !usernameError &&
                !fullnameError &&
                !emailError &&
                !passwordError;
            submitBtn.disabled = !isValid;
        }
        document.getElementById("confirmPassword").addEventListener("input", validateForm);
        document.addEventListener("DOMContentLoaded", validateForm);
</script>
</body>
</html>

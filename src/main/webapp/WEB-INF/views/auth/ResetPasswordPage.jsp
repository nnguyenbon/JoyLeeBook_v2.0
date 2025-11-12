<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 12-Nov-25
  Time: 08:51
  To change this template use File | Settings | File Templates.
--%>
<main>
    <div class="text-center mb-6">
        <h1 class="font-bold text-4xl">Reset Password</h1>
        <p class="text-gray-500">
            Enter your new password
        </p>
        <c:if test="${not empty message}">
            <c:choose>
                <c:when test="${messageType == 'error'}">
                    <p class="text-red-600 font-medium mt-2">${message}</p>
                </c:when>
                <c:otherwise>
                    <p class="text-green-600 font-medium mt-2">${message}</p>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>

    <form action="${pageContext.request.contextPath}/reset-password" method="post">
        <label class="block mb-1" for="password">New Password*</label>
        <div class="relative">
            <input
                    type="password"
                    id="password"
                    name="password"
                    class="border border-gray-400 w-full rounded-md pl-4 pr-10 py-2"
                    placeholder="Enter new password"
                    required
            />
            <i
                    class="fa-solid fa-eye absolute right-3 bottom-1/2 translate-y-1/2 cursor-pointer"
                    onclick="togglePassword(this, 'password')"
            ></i>
        </div>
        <p class="text-xs text-gray-500 mt-1">
            Password must be at least 8 characters with uppercase, lowercase, number and special character
        </p>

        <label class="block mt-4 mb-1" for="confirmPassword">Confirm Password*</label>
        <div class="relative">
            <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    class="border border-gray-400 w-full rounded-md pl-4 pr-10 py-2"
                    placeholder="Re-enter new password"
                    required
            />
            <i
                    class="fa-solid fa-eye absolute right-3 bottom-1/2 translate-y-1/2 cursor-pointer"
                    onclick="togglePassword(this, 'confirmPassword')"
            ></i>
        </div>

        <button
                class="block text-xl text-white font-bold py-1 mt-6 ring w-full rounded-md bg-sky-800 hover:bg-sky-800/80 transition duration-300 cursor-pointer"
                type="submit"
        >
            Reset Password
        </button>
    </form>
</main>

<script>
    function togglePassword(icon, inputId) {
        const input = document.getElementById(inputId);
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    }
</script>
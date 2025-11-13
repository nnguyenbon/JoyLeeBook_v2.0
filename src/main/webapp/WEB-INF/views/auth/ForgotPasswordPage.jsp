<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 12-Nov-25
  Time: 08:50
  To change this template use File | Settings | File Templates.
--%>
<main>
    <div class="text-center mb-6">
        <h1 class="font-bold text-4xl">Forgot Password?</h1>
        <p class="text-gray-500">
            Enter your email address and we'll send you an OTP to reset your password
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

    <form action="${pageContext.request.contextPath}/forgot?action=sendOtp" method="post">
        <label class="block mb-1" for="email">Email Address</label>
        <input
                type="email"
                id="email"
                name="email"
                class="border border-gray-400 w-full rounded-md px-4 py-2"
                placeholder="Enter your registered email"
                required
        />

        <button
                class="block text-xl text-white font-bold py-1 mt-4 ring w-full rounded-md bg-sky-800 hover:bg-sky-800/80 transition duration-300 cursor-pointer"
                type="submit"
        >
            Send OTP
        </button>
    </form>

    <p class="mt-4 text-center">
        Remember your password?
        <a href="${pageContext.request.contextPath}/login" class="text-sky-800 font-medium">Back to Login</a>
    </p>
</main>
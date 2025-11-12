<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 12-Nov-25
  Time: 08:50
  To change this template use File | Settings | File Templates.
--%>
<main class="px-2 py-4">
    <div class="text-center mb-6">
        <h1 class="font-bold text-4xl">Enter OTP</h1>
        <p class="text-gray-500 mt-2">
            We've sent a verification code to your email
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

    <form action="${pageContext.request.contextPath}/forgot?action=verifyOtp" method="post">
        <div>
            <label class="block mt-2 mb-1" for="otp">OTP Code*</label>
            <input
                    type="text"
                    id="otp"
                    name="otp"
                    class="border border-gray-300 w-full rounded-md px-4 py-2"
                    placeholder="Enter 6-digit OTP"
                    maxlength="6"
                    required
            />
        </div>

        <div class="flex gap-4 mt-4">
            <a
                    href="${pageContext.request.contextPath}/forgot?action=sendOtp"
                    class="block text-center text-xl font-bold py-1 ring ring-gray-700 w-full rounded-md hover:bg-gray-700 hover:text-white transition duration-300 cursor-pointer"
            >
                Resend OTP
            </a>
            <button
                    class="block text-xl text-white font-bold py-1 w-full rounded-md bg-sky-800 hover:bg-sky-800/80 transition duration-300 cursor-pointer"
                    type="submit"
            >
                Verify
            </button>
        </div>
    </form>

    <p class="mt-4 text-center">
        <a href="${pageContext.request.contextPath}/forgot" class="text-sky-800">Change Email</a>
    </p>
</main>
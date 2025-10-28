<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/17/2025
  Time: 9:27 PM
  To change this template use File | Settings | File Templates.
--%>
<main class="px-2 py-4">
    <div class="text-center mb-6">
        <h1 class="font-bold text-4xl">Enter OTP</h1>
    </div>

    <form action="${pageContext.request.contextPath}/register?action=verifyOtp" method="post">
        <div>
            <label class="block mt-2 mb-1" for="otp">OTP*</label>
            <input
                    type="text"
                    id="otp"
                    name="otp"
                    class="border border-gray-300 w-full rounded-md px-4 py-2"
                    placeholder="Enter OTP"
                    required
            />
            <p class="text-xs text-red-700">
            </p>
        </div>

        <div class="flex gap-4 mt-2">
            <a
                    href="${pageContext.request.contextPath}/register?action=sendOtp"
                    class="block text-center text-xl font-bold py-1 mt-2 ring ring-gray-700 w-full rounded-md hover:bg-gray-700 hover:text-white transition duration-300 cursor-pointer"
            >
                Send Again
            </a>
            <button
                    class="block text-xl text-white font-bold py-1 mt-2 w-full rounded-md bg-sky-800 hover:bg-sky-800/30 hover:text-black transition duration-300 cursor-pointer"
                    type="submit"
            >
                Verify
            </button>
        </div>
    </form>
</main>

<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/17/2025
  Time: 5:25 PM
  To change this template use File | Settings | File Templates.
--%>
<main class="overflow-y-auto max-h-[80vh] px-2 custom-scrollbar">
    <div class="text-center mb-4">
        <h1 class="font-bold text-4xl">Create your account</h1>
        <p class="text-gray-500">
            Join thousands of readers and discover amazing stories
        </p>
        <c:if test="${not empty message}">
            <p class="text-red-600 font-medium mt-2">${message}</p>
        </c:if>
    </div>

    <form action="${pageContext.request.contextPath}/register?action=sendOtp" method="post">
        <div>
            <label class="block mt-2 mb-1" for="username">Username*</label>
            <input
                    type="text"
                    id="username"
                    name="username"
                    class="border border-gray-400 w-full rounded-md px-4 py-2"
                    placeholder="Enter a unique username"
                    value="${username == null ? "" : username}"
                    required
            />
            <p id="username-error" class="text-xs text-red-700">
            </p>
        </div>

        <div>
            <label class="block mt-2 mb-1" for="fullName">Full name*</label>
            <input
                    type="text"
                    id="fullName"
                    name="fullName"
                    class="border border-gray-400 w-full rounded-md px-4 py-2"
                    placeholder="Enter your full name"
                    value="${fullName == null ? "" : fullName}"
                    required
            />
            <p id="fullname-error" class="text-xs text-red-700"></p>
        </div>

        <div>
            <label class="block mt-2 mb-1" for="email">Email*</label>
            <input
                    type="email"
                    id="email"
                    name="email"
                    class="border border-gray-400 w-full rounded-md px-4 py-2"
                    placeholder="Enter your email"
                    value="${email == null ? "" : email}"
                    required
            />
            <p id="email-error" class="text-xs text-red-700">
            </p>
        </div>

        <div class="flex gap-4">
            <div>
                <label class="block mt-2 mb-1" for="password">Password*</label>
                <div class="relative">
                    <input
                            type="password"
                            id="password"
                            name="password"
                            class="border border-gray-400 w-full rounded-md pl-4 pr-8 py-2"
                            placeholder="Enter your password"
                            value="${password == null ? "" : password}"
                            required
                    />
                    <i
                            class="fa-solid fa-eye absolute right-2 bottom-1/2 translate-y-1/2 cursor-pointer"
                            onclick="togglePassword(this, 'password')"
                    ></i>
                </div>
                <p id="password-error" class="text-xs text-red-700">
                </p>
            </div>

            <div>
                <label class="block mt-2 mb-1" for="confirmPassword"
                >Confirm Password*</label
                >
                <div class="relative">
                    <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            class="border border-gray-300 w-full rounded-md pl-4 pr-8 py-2"
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


        <button
                id="submit-btn"
                class="block text-xl text-white font-bold py-1 mt-2 ring w-full rounded-md bg-sky-800 hover:bg-sky-800/80 transition duration-300 cursor-pointer"
                type="submit"
                disabled
        >
            Create Account
        </button>
    </form>
    <div class="flex items-center gap-4 my-4 text-sm text-gray-500">
        <span class="block h-px w-full bg-black"></span>
        <span class="block text-nowrap">OR CONTINUE WITH</span>
        <span class="block h-px w-full bg-black"></span>
    </div>

    <a
            href="${pageContext.request.contextPath}/auth/google"
            class="flex items-center justify-center gap-4 ring py-2 rounded-md hover:bg-gray-200 transition duration-300"
    >
        <i class="fa-brands fa-google"></i>
        <p class="font-semibold">Continue with Google</p>
    </a>

    <p class="mt-3 text-center">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login" class="text-sky-800">Sign In</a>
    </p>
</main>

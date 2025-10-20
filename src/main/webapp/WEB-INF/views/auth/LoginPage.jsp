<%--
  Created by IntelliJ IDEA.
  User: tvphu
  Date: 10/17/2025
  Time: 5:18 PM
  To change this template use File | Settings | File Templates.
--%>
<main>
    <div class="text-center mb-6">
        <h1 class="font-bold text-4xl">Welcome back</h1>
        <p class="text-gray-500">
            Sign in to your account to continue reading
        </p>
    </div>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <label class="block mb-1" for="username">Username or Email</label>
        <input
                type="text"
                id="username"
                name="username"
                class="border border-gray-400 w-full rounded-md px-4 py-2"
                placeholder="Enter your username or email"
                required
        />

        <label class="block mt-4 mb-1" for="password">Password</label>
        <div class="relative">
            <input
                    type="password"
                    id="password"
                    name="password"
                    class="border border-gray-400 w-full rounded-md pl-4 pr-10 py-2"
                    placeholder="Enter your password"
                    required
            />
            <i
                    class="fa-solid fa-eye absolute right-3 bottom-1/2 translate-y-1/2 cursor-pointer"
                    onclick="togglePassword(this, 'password')"
            ></i>
        </div>

        <a class="inline-block text-sky-800 mt-2 mb-4" href="./forgot"
        >Forgot password?</a
        >

        <button
                class="block text-xl text-white font-bold py-1 ring w-full rounded-md bg-sky-800 hover:bg-sky-800/80 transition duration-300 cursor-pointer"
                type="submit"
        >
            Sign In
        </button>
    </form>

    <div class="flex items-center gap-4 my-4 text-sm text-gray-500">
        <span class="block h-px w-full bg-black"></span>
        <span class="block text-nowrap">OR CONTINUE WITH</span>
        <span class="block h-px w-full bg-black"></span>
    </div>

    <a href="./login.html"
       class="flex items-center justify-center gap-4 ring py-2 rounded-md hover:bg-gray-200 transition duration-300">
        <i class="fa-brands fa-google"></i>
        <p class="font-semibold">Continue with Google</p>
    </a>

    <p class="mt-3 text-center">Don't have an account? <a href="${pageContext.request.contextPath}/register" class="text-sky-800">Sign up</a>
    </p>
</main>

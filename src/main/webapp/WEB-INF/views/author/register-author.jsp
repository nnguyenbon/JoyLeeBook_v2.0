<%-- register-author-modal.jsp --%>
<div id="registerAuthorModal"
     class="fixed inset-0 z-50 flex items-center justify-center bg-opacity-50 hidden">
    <div class="bg-white rounded-xl shadow-xl w-full max-w-lg h-[80vh] flex flex-col relative animate-fade-in">

        <!-- Header -->
        <div class="p-6 border-b border-gray-300 flex flex-col items-center">
            <h1 class="text-2xl font-bold mb-2">Register As Author</h1>
            <p class="text-gray-700 text-center">
                Please review and accept the following terms to become an author on our platform
            </p>
        </div>

        <!-- Nội dung có scroll -->
        <div id="termsContent" class="flex-1 overflow-y-auto px-8 py-4 text-gray-800">
            <ul class="space-y-3">
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>You must have a verified user account before submitting your author registration request.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>You are required to provide a short biography that accurately represents you as an author.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>All information and uploaded content must follow our community guidelines and must not include any offensive, harmful, or copyrighted materials.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>Your content submissions request will be reviewed by our staff or admin team before approval. You will receive a notification regarding the result.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>You must take full responsibility for all works, stories, and materials you publish on this platform.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>As an author, you are responsible for maintaining the quality and originality of your works.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>The platform and its administrators are not liable for any legal or copyright issues resulting from your published content.</span>
                </li>
                <li class="flex items-start">
                    <i class="fas fa-check-circle text-blue-500 mr-3 mt-1"></i>
                    <span>The platform reserves the right to suspend or revoke author privileges if any rules or content policies are violated.</span>
                </li>
            </ul>
        </div>

        <!-- Footer -->
        <div class="border-t border-gray-300 p-6">
            <p class="font-semibold mb-3">Do you agree to these terms?</p>
            <div class="flex justify-end space-x-3">
                <form action="${pageContext.request.contextPath}/author/register" method="get" class="m-0">
                    <button id="agreeButton" type="submit"
                            class="bg-blue-400 text-white px-6 py-2 rounded-lg font-medium transition cursor-not-allowed"
                            disabled>
                        I agree
                    </button>
                </form>
                <button type="button"
                        class="bg-gray-200 hover:bg-gray-300 text-gray-800 px-6 py-2 rounded-lg font-medium transition"
                        onclick="closeRegisterAuthorModal()">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<script>
    async function openRegisterAuthorModal() {
        try {
            const res = await fetch('<%= request.getContextPath() %>' + '/author/check');
            const data = await res.json();

            if (data.isAuthor) {
                window.location.href = '<%= request.getContextPath() %>' + '/author/register';
            } else {
                const modal = document.getElementById('registerAuthorModal');
                modal.classList.remove('hidden');
                document.body.style.overflow = 'hidden';
            }
        } catch (err) {
            console.error('Failed to check author status', err);
        }
    }

    function closeRegisterAuthorModal() {
        document.getElementById('registerAuthorModal').classList.add('hidden');
        document.body.style.overflow = 'auto';
    }

    const termsContent = document.getElementById('termsContent');
    const agreeButton = document.getElementById('agreeButton');

    termsContent.addEventListener('scroll', function () {
        const isScrolledToBottom =
            termsContent.scrollTop + termsContent.clientHeight >= termsContent.scrollHeight - 5;

        if (isScrolledToBottom) {
            agreeButton.disabled = false;
            agreeButton.classList.remove('bg-blue-400', 'cursor-not-allowed');
            agreeButton.classList.add('bg-blue-600', 'hover:bg-blue-700');
        }
    });
</script>

<style>
    @keyframes fade-in {
        from { opacity: 0; transform: translateY(-10px); }
        to { opacity: 1; transform: translateY(0); }
    }
    .animate-fade-in {
        animation: fade-in 0.25s ease-out;
    }

    #termsContent::-webkit-scrollbar {
        width: 8px;
    }
    #termsContent::-webkit-scrollbar-thumb {
        background-color: #c1c1c1;
        border-radius: 10px;
    }
</style>

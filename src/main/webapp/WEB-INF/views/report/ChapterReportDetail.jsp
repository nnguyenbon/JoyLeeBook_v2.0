<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/20/2025
  Time: 10:32 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<!-- Main content -->
<main class="flex-1 bg-gray-100 overflow-y-auto max-h-[90vh] custom-scrollbar">
    <!-- Header -->
    <div class="h-28 flex items-center border-b bg-white p-9 mb-1">
        <p class="font-semibold text-lg">Staff: <span class="text-[#195DA9]">JOHNDON</span></p>
    </div>

    <!-- Content -->
    <div class="p-9">
        <div class="border rounded-lg bg-white px-4 py-3 mb-3 hover:shadow-sm transition">
            <!-- Hàng trên -->
            <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                    <div class="px-4">
                        <p class="text-xl text-[#195DA9] font-bold">Chapter 43: The Awakening</p>
                        <p class="text-gray-500">Series: Chronicles of the Mystic Realm</p>
                    </div>
                </div>
                <div class="flex items-center gap-10 text-sm">
                    <p>Reported by User123</p>
                    <p>
                        Status:
                        <span
                                class="w-20 text-center px-3 py-0.5 rounded-full bg-blue-100 text-blue-700 text-xs">Resolved</span>
                    </p>
                    <div class="flex items-center gap-2 text-sm">
                        <p class="text-gray-500 mr-3">Report date: 25/09/2025</p>
                    </div>
                </div>
            </div>

            <!-- Hàng dưới -->
            <div class="pt-3 px-4 text-gray-700">
                <p><span class="font-semibold">Reason:</span> Contains inappropriate language in dialogue.</p>
            </div>
        </div>

        <div class="mb-5">
            <div class="border-2 rounded-lg bg-white mb-3 px-4">
                <h2 class="px-4 text-xl font-semibold mt-2">Content</h2>
                <p class="px-4 py-2 text-gray-700">
                    Lorem ipsum dolor sit amet consectetur adipisicing elit. Ad eveniet culpa ducimus dicta
                    delectus similique neque enim quod nihil dolore necessitatibus id nemo dolor magnam
                    voluptatem, sunt earum odio totam! Lorem ipsum dolor sit amet consectetur, adipisicing elit.
                    Dolor vero iure nostrum doloribus eveniet sit assumenda ducimus, nemo voluptatibus autem
                    amet error quaerat, corrupti explicabo officia sapiente nihil. Iusto, perferendis?
                    In a world where magic and mystery intertwine, "Chronicles of the
                    Mystic
                    Realm" follows the journey of Aria, a young sorceress destined to unlock ancient secrets and
                    confront dark forces threatening her homeland. As she navigates through enchanted forests,
                    treacherous mountains, and forgotten ruins, Aria forms unlikely alliances with mythical
                    creatures
                    and brave warriors. Together, they embark on a quest filled with peril, self-discovery, and
                    the
                    enduring power of friendship. Will Aria rise to her destiny and restore balance to the
                    Mystic
                    Realm?</p>
            </div>
        </div>
        <div class="flex gap-2">
            <button id="approveBtn"
                    class="block text-[#42CC75] bg-white border rounded-lg flex gap-2 items-center px-4 py-2 hover:bg-gray-100">
                <svg
                        xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                        class="lucide lucide-circle-check-big-icon lucide-circle-check-big">
                    <path d="M21.801 10A10 10 0 1 1 17 3.335"/>
                    <path d="m9 11 3 3L22 4"/>
                </svg>
                Approve
            </button>
            <button
                    class="block text-[#E23636] bg-white border rounded-lg flex gap-2 items-center px-4 py-2 hover:bg-gray-100">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                     class="lucide lucide-circle-x-icon lucide-circle-x">
                    <circle cx="12" cy="12" r="10"/>
                    <path d="m15 9-6 6"/>
                    <path d="m9 9 6 6"/>
                </svg>
                Reject
            </button>

        </div>
        <!-- MODAL CONFIRM APPROVAL -->
        <div id="approveModal"
             class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 hidden z-50">
            <div class="bg-white rounded-xl w-[450px] p-6 shadow-lg">
                <h2 class="text-xl font-semibold text-[#195DA9] mb-4 text-center">Confirm Approval Report</h2>

                <div class="space-y-2 text-sm text-gray-700">
                    <p><span class="font-semibold">Chapter:</span> Chapter 43: The Awakening</p>
                    <p><span class="font-semibold">Series:</span> Chronicles of the Mystic Realm</p>
                    <p><span class="font-semibold">Reported by:</span> User123</p>
                    <p><span class="font-semibold">Reason:</span> Contains inappropriate language in dialogue.
                    </p>
                </div>

                <div class="mt-4">
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Staff’s Message to
                        Author:</label>
                    <textarea id="staffMessage"
                              class="w-full border rounded-lg p-2 text-sm focus:ring-[#195DA9] focus:border-[#195DA9]"
                              rows="3" placeholder="Write your message here..."></textarea>
                </div>

                <div class="flex justify-end gap-3 mt-6">
                    <button id="cancelApprove"
                            class="px-4 py-2 rounded-lg border hover:bg-gray-100">Cancel
                    </button>
                    <button id="confirmApprove"
                            class="px-4 py-2 rounded-lg bg-[#195DA9] text-white hover:bg-[#13477F]">Confirm
                    </button>
                </div>
            </div>
        </div>
    </div>
</main>


<script>
    // Lấy các phần tử
    const approveBtn = document.getElementById('approveBtn');
    const approveModal = document.getElementById('approveModal');
    const cancelApprove = document.getElementById('cancelApprove');
    const confirmApprove = document.getElementById('confirmApprove');

    // Mở modal khi nhấn "Approve"
    approveBtn.addEventListener('click', () => {
        approveModal.classList.remove('hidden');
    });

    // Đóng modal khi nhấn "Cancel" hoặc click ra ngoài
    cancelApprove.addEventListener('click', () => {
        approveModal.classList.add('hidden');
    });
    approveModal.addEventListener('click', (e) => {
        if (e.target === approveModal) approveModal.classList.add('hidden');
    });

    // Khi xác nhận duyệt
    confirmApprove.addEventListener('click', async () => {
        const message = document.getElementById('staffMessage').value.trim();
        if (message === "") {
            alert("Please write a message to the author before confirming.");
            return;
        }

        // Ẩn modal
        approveModal.classList.add('hidden');
    });
</script>


<script>
    document.querySelectorAll('.dropdown-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation(); // Ngăn việc click lan ra ngoài
            const menu = btn.nextElementSibling; // Tìm menu liền sau button
            document.querySelectorAll('.dropdown-menu').forEach(m => {
                if (m !== menu) m.classList.add('hidden'); // ẩn các menu khác
            });
            menu.classList.toggle('hidden');
        });
    });

    window.addEventListener('click', () => {
        document.querySelectorAll('.dropdown-menu').forEach(m => m.classList.add('hidden'));
    });
</script>
</body>

</html>

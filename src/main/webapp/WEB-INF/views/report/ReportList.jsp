<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 10/14/2025
  Time: 11:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <div class="border-b border-gray-200 flex items-center px-9">
        <button id="tab-chapter" data-type="chapter" class="text-xl font-bold border-b- py-1 px-4 border-[#195DA9] tab-btn">Chapter Report</button>
        <button id="tab-comment" data-type="comment" class="text-xl text-gray-500 py-1 px-4 hover:text-[#195DA9] tab-btn">Comment Report</button>

        <div class="ml-auto flex gap-2">
            <label>
                <select class="border border-gray-400 rounded-md text-sm px-2 py-1 focus:outline-[#195DA9]">
                    <option>All status</option>
                    <option>Resolved</option>
                    <option>Pending</option>
                </select>
            </label>
            <label>
                <input type="text" placeholder="Search series, chapter..."
                       class="border border-gray-400 rounded-md text-sm px-3 py-1 w-60 focus:outline-[#195DA9]"/>
            </label>
        </div>

    </div>

<script>
    const contextPath = "${pageContext.request.contextPath}";
    const tabs = document.querySelectorAll(".tab-btn");
    const container = document.getElementById("tab-content");

    let currentType = "chapter";

    function highlightActiveTab(type) {
        tabs.forEach(tab => {
            if (tab.dataset.type === type) {
                tab.classList.add("font-extrabold", "border-b-4", "border-[#195DA9]", "text-[#195DA9]");
                tab.classList.remove("text-gray-500");
            } else {
                tab.classList.remove("font-extrabold", "border-b-4", "border-[#195DA9]", "text-[#195DA9]");
                tab.classList.add("text-gray-500");
            }
        });
    }

    function getSizeByType(type) {
        switch (type) {
            case "chapter":
                return 8;
            case "comment":
                return 10;
            default:
                return 8;
        }
    }

    function fetchContent(type, page = 1) {
        currentType = type;
        highlightActiveTab(type);

        const sizePage = getSizeByType(type);
        const url = contextPath + '/report?type=' + encodeURIComponent(type) + '&sizePage=' + sizePage + '&currentPage=' + page;

        fetch(url)
            .then(res => res.text())
            .then(html => {
                container.innerHTML = html;

                if (window.tailwind?.refresh) window.tailwind.refresh();
                bindPaginationEvents();
                if (typeof bindFilterEvents === "function") bindFilterEvents();
            })
            .catch(err => {
                console.error("Fetch failed:", err);
                container.innerHTML = `<p class="text-red-500 p-4">Failed to load ${type} content.</p>`;
            });
    }

    function bindPaginationEvents() {
        const paginationLinks = container.querySelectorAll(".page-link, .pagination a");
        paginationLinks.forEach(link => {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                const url = new URL(link.href);
                const page = url.searchParams.get("currentPage") || url.searchParams.get("page") || 1;
                fetchContent(currentType, page);
            });
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        tabs.forEach(tab => {
            tab.addEventListener("click", () => {
                const type = tab.dataset.type;
                fetchContent(type);
            });
        });
        fetchContent(currentType);
    });
    document.querySelectorAll('.dropdown-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const menu = btn.nextElementSibling;
            document.querySelectorAll('.dropdown-menu').forEach(m => {
                if (m !== menu) m.classList.add('hidden');
            });
            menu.classList.toggle('hidden');
        });
    });

    window.addEventListener('click', () => {
        document.querySelectorAll('.dropdown-menu').forEach(m => m.classList.add('hidden'));
    });

</script>

<%--
  Created by IntelliJ IDEA.
  User: KHAI TOAN
  Date: 11/10/2025
  Time: 12:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<p class="pt-6 pb-4 font-bold text-3xl">Top reader points</p>
<div class="border border-gray-300 rounded-xl pr-3 flex-1 shadow-xl">
    <ul class="">
        <c:forEach var="user" items="${userList}" varStatus="loop">
            <li class="flex justify-between items-center py-2
                   ${loop.index == 0 ? 'text-[#E23636] font-semibold text-lg mt-2' :
                     loop.index == 1 ? 'text-[#F5A83D] font-semibold' :
                     loop.index == 2 ? 'text-[#195DA9] font-semibold' : 'text-gray-700'}">
                <div class="flex items-center gap-1">
                    <span class="font-semibold w-6 text-right">${loop.index + 1}.</span>
                    <p class="truncate w-48" title="${user.username}">${user.username}</p>
                </div>
                <span class="float-right">${user.points}</span>
            </li>
        </c:forEach>
    </ul>
</div>

<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/13/25
  Time: 8:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User List</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-styles.css">
</head>
<body class="bg-gray-100">
<div class="container mx-auto mt-10">
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-3xl font-bold mb-6 text-gray-800">User Management</h1>

        <div class="overflow-x-auto">
            <table class="min-w-full bg-white">
                <thead class="bg-gray-800 text-white">
                <tr>
                    <th class="w-1/12 py-3 px-4 uppercase font-semibold text-sm text-left">User ID</th>
                    <th class="w-2/12 py-3 px-4 uppercase font-semibold text-sm text-left">Username</th>
                    <th class="w-3/12 py-3 px-4 uppercase font-semibold text-sm text-left">Full Name</th>
                    <th class="w-3/12 py-3 px-4 uppercase font-semibold text-sm text-left">Email</th>
                    <th class="w-1/12 py-3 px-4 uppercase font-semibold text-sm text-center">Role</th>
                    <th class="w-1/12 py-3 px-4 uppercase font-semibold text-sm text-center">Status</th>
                    <th class="w-1/12 py-3 px-4 uppercase font-semibold text-sm text-center">Actions</th>
                </tr>
                </thead>
                <tbody class="text-gray-700">
                <c:forEach var="user" items="${requestScope.users}">
                    <tr>
                        <td class="py-3 px-4">${user.userId}</td>
                        <td class="py-3 px-4 font-medium">${user.username}</td>
                        <td class="py-3 px-4">${user.fullName}</td>
                        <td class="py-3 px-4">${user.email}</td>
                        <td class="py-3 px-4 text-center">
                            <span class="role ${user.role}">${user.role}</span>
                        </td>
                        <td class="py-3 px-4 text-center">
                            <span class="status ${user.status}">${user.status}</span>
                        </td>
                        <td class="py-3 px-4 text-center">
                            <a href="#" class="text-blue-500 hover:text-blue-700">Edit</a>
                            <a href="#" class="text-red-500 hover:text-red-700 ml-2">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
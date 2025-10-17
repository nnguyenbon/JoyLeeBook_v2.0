<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/15/25
  Time: 12:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Manage Co-Authors</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/components/_header.jsp"/>

<div class="container">
    <h2>Manage Co-Authors for: <c:out value="${series.title}"/></h2>

    <%-- Display Messages --%>
    <c:if test="${not empty param.message}">
        <div class="alert alert-success">
            <c:choose>
                <c:when test="${param.message == 'authorAddedSuccess'}">Author added successfully.</c:when>
                <c:when test="${param.message == 'authorRemovedSuccess'}">Author removed successfully.</c:when>
            </c:choose>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger">
            <c:choose>
                <c:when test="${param.error == 'userNotFoundOrNotAuthor'}">User not found or is not an author.</c:when>
                <c:when test="${param.error == 'userIsAlreadyAuthor'}">This user is already an author for this series.</c:when>
                <c:when test="${param.error == 'cannotRemoveSelf'}">You cannot remove yourself from a series.</c:when>
            </c:choose>
        </div>
    </c:if>

    <%-- Add Co-Author Form --%>
    <div class="form-section">
        <h4>Add New Co-Author</h4>
        <form action="${pageContext.request.contextPath}/manage-coauthors" method="post">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="seriesId" value="${series.seriesId}">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary">Add Author</button>
        </form>
    </div>

    <hr>

    <%-- Current Authors List --%>
    <div class="authors-list">
        <h4>Current Authors</h4>
        <table class="table">
            <thead>
            <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="author" items="${authors}">
                <tr>
                    <td><c:out value="${author.username}"/></td>
                    <td><c:out value="${author.email}"/></td>
                    <td>
                        <c:if test="${sessionScope.user.userId != author.userId}">
                            <form action="${pageContext.request.contextPath}/manage-coauthors" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="seriesId" value="${series.seriesId}">
                                <input type="hidden" name="userId" value="${author.userId}">
                                <button type="submit" class="btn btn-danger btn-sm">Remove</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/components/_footer.jsp"/>
</body>
</html>
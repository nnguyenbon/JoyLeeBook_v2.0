<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/9/25
  Time: 5:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*, dto.ChapterItemDTO, services.MyChapterService.PagedResult" %>
<%
    String mode = (String) request.getAttribute("mode");
    List<ChapterItemDTO> items = (List<ChapterItemDTO>) request.getAttribute("items");
    Integer currentPage = (Integer) request.getAttribute("page");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
%>
<h2><%= "author".equalsIgnoreCase(mode) ? "My Authored Chapters" : "My Reading History" %>
</h2>

<form method="get">
    <input type="hidden" name="mode" value="<%= mode %>">
    <input type="text" name="q" placeholder="Search..." value="<%= request.getAttribute("q") %>"/>
    <% if ("author".equalsIgnoreCase(mode)) { %>
    <select name="status">
        <option value="">All status</option>
        <option value="pending">pending</option>
        <option value="approved">approved</option>
        <option value="rejected">rejected</option>
    </select>
    <% } %>
    <button type="submit">Filter</button>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>Series</th>
        <th>Chapter #</th>
        <th>Title</th>
        <th>Status</th>
        <th>Updated</th>
        <% if (!"author".equalsIgnoreCase(mode)) { %>
        <th>Last read</th>
        <% } %>
    </tr>
    <%
        if (items != null) for (ChapterItemDTO it : items) {
    %>
    <tr>
        <td><%= it.getSeriesTitle() %>
        </td>
        <td><%= it.getChapterNumber() %>
        </td>
        <td>
            <a href="<%= request.getContextPath() %>/chapter?chapterId=<%= it.getChapterId() %>"><%= it.getChapterTitle() %>
            </a></td>
        <td><%= it.getStatus() %>
        </td>
        <td><%= it.getUpdatedAt() %>
        </td>
        <% if (!"author".equalsIgnoreCase(mode)) { %>
        <td><%= it.getLastReadAt() %>
        </td>
        <% } %>
    </tr>
    <% } %>
</table>

<div>
    <% for (int p = 1; p <= totalPages; p++) { %>
    <a href="?mode=<%= mode %>&page=<%= p %>&q=<%= request.getAttribute("q") %>&status=<%= request.getAttribute("status") %>"><%= p %>
    </a>
    <% } %>
</div>

package dto;

public class PaginationRequest {
    private int page;
    private int pageSize;
    private String orderBy;
    private String sortDir;

    public PaginationRequest(int page, int pageSize, String orderBy, String sortDir) {
        this.page = Math.max(page, 1);
        this.pageSize = Math.max(pageSize, 1);
        this.orderBy = (orderBy == null || orderBy.isEmpty()) ? "id" : orderBy;
        this.sortDir = (sortDir == null || sortDir.isEmpty()) ? "ASC" : sortDir.toUpperCase();
    }

    public int getPageSize() { return pageSize; }
    public String getOrderBy() { return orderBy; }
    public String getSortDir() { return sortDir; }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}

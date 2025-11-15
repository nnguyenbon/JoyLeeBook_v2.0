package dao.helper;

import model.PaginationRequest;

public class PaginationDAOHelper {

    private final PaginationRequest pagination;

    public PaginationDAOHelper(PaginationRequest pagination) {
        this.pagination = pagination;
    }

    public String buildPaginationClause() {
        int offset = (pagination.getPage() - 1) * pagination.getPageSize();
        return String.format(
                " ORDER BY %s %s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY ",
                pagination.getOrderBy(),
                pagination.getSortDir(),
                offset,
                pagination.getPageSize()
        );
    }
}

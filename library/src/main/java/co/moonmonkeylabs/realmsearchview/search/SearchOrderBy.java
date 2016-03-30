package co.moonmonkeylabs.realmsearchview.search;

import io.realm.Sort;

public class SearchOrderBy {
    private Sort sortOrder;
    private String sortKey;

    public SearchOrderBy(String sortKey, Sort sortOrder) {
        this.sortOrder = sortOrder;
        this.sortKey = sortKey;
    }

    public Sort getSortOrder() {
        return sortOrder;
    }

    public String getSortKey() {
        return sortKey;
    }
}
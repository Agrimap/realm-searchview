package co.moonmonkeylabs.realmsearchview.search;

import io.realm.Case;

public class SearchCriteria {
    private String field;
    private boolean useContains;
    private Case casing;

    public SearchCriteria(String field, boolean useContains, Case casing) {
        this.field = field;
        this.useContains = useContains;
        this.casing = casing;
    }

    public String getField() {
        return field;
    }

    public boolean isUseContains() {
        return useContains;
    }

    public Case getCasing() {
        return casing;
    }
}
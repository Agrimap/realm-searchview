package co.moonmonkeylabs.realmsearchview.search;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class SearchFilter<T extends RealmModel> {

    private RealmResults<T> realmResults;
    private Realm realm;
    private Class<T> realmClass;
    private Collection<SearchCriteria> orFields;
    private Collection<SearchOrderBy> orderBys;
    private String defaultSearchInput;

    public SearchFilter(Realm realm, Class<T> realmClass, SearchCriteria orFields, SearchOrderBy orderBys, String defaultSearchInput) {
        this(realm, realmClass, Arrays.asList(orFields), Arrays.asList(orderBys), defaultSearchInput);
    }

    public SearchFilter(RealmResults<T> realmResults, SearchCriteria orFields, SearchOrderBy orderBys, String defaultSearchInput) {
        this(realmResults, Arrays.asList(orFields), Arrays.asList(orderBys), defaultSearchInput);
    }

    public SearchFilter(Realm realm, Class<T> realmClass, Collection<SearchCriteria> orFields, Collection<SearchOrderBy> orderBys, String defaultSearchInput) {
        this.realm = realm;
        this.realmClass = realmClass;
        this.orFields = orFields;
        this.orderBys = orderBys;
        this.defaultSearchInput = defaultSearchInput;
    }

    public SearchFilter(RealmResults<T> realmResults, Collection<SearchCriteria> orFields, Collection<SearchOrderBy> orderBys, String defaultSearchInput) {
        this.realmResults = realmResults;
        this.orFields = orFields;
        this.orderBys = orderBys;
        this.defaultSearchInput = defaultSearchInput;
    }

    @NonNull
    public RealmResults<T> filter(String input) {
        RealmQuery<T> where = getWhere();

        String searchText = input;
        if (input == null || input.isEmpty() && defaultSearchInput != null) {
            searchText = defaultSearchInput;
        }

        if (!searchText.isEmpty() && !orFields.isEmpty()) {
            where.beginGroup();
            boolean notFirst = false;
            for (SearchCriteria field : orFields) {
                if (notFirst) {
                    where = where.or();
                } else {
                    notFirst = true;
                }
                if (field.isUseContains()) {
                    where = where.contains(field.getField(), searchText, field.getCasing());
                } else {
                    where = where.beginsWith(field.getField(), searchText, field.getCasing());
                }
            }
            where.endGroup();
        }

        RealmResults<T> results = where.findAllSorted(getSortKeys(), getSortOrders());
        results.load();

        return results;
    }

    @NonNull
    public RealmQuery<T> getWhere() {
        if (realmResults != null) {
            return realmResults.where();
        }
        return realm.where(realmClass);
    }

    private String[] getSortKeys() {
        int i = 0;
        String[] keys = new String[orderBys.size()];
        for (SearchOrderBy orderBy : orderBys) {
            keys[i++] = orderBy.getSortKey();
        }

        return keys;
    }

    private Sort[] getSortOrders() {
        int i = 0;
        Sort[] keys = new Sort[orderBys.size()];
        for (SearchOrderBy orderBy : orderBys) {
            keys[i++] = orderBy.getSortOrder();
        }

        return keys;
    }
}
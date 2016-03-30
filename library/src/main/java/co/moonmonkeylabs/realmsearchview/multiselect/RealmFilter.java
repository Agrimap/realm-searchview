package co.moonmonkeylabs.realmsearchview.multiselect;

import android.support.annotation.NonNull;
import android.widget.Filter;

import co.moonmonkeylabs.realmsearchview.search.SearchFilter;
import io.realm.RealmObject;
import io.realm.RealmResults;

public abstract class RealmFilter<T extends RealmObject> extends Filter {

    private SearchFilter searchFilter;
    private boolean filterInUIThread;

    public RealmFilter(SearchFilter searchFilter, boolean filterInUIThread) {
        this.searchFilter = searchFilter;
        this.filterInUIThread = filterInUIThread;
    }

    public abstract void updateRealmResults(RealmResults<T> filteredResults);

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();

        if (!filterInUIThread) {
            filter(filterResults, constraint == null ? null : constraint.toString());
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults filteredResults) {
        if (filterInUIThread) {
            filter(filteredResults, constraint == null ? null : constraint.toString());
        }

        updateRealmResults((RealmResults<T>) filteredResults.values);
    }

    private RealmResults<T> filter(FilterResults filterResults, String input) {
        RealmResults<T> realmResults = filterInput(input);

        filterResults.values = realmResults;
        filterResults.count = realmResults.size();

        return realmResults;
    }

    @NonNull
    private RealmResults<T> filterInput(String input) {
        return searchFilter.filter(input);
    }

}
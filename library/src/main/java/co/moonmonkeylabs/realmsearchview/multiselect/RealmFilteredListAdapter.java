package co.moonmonkeylabs.realmsearchview.multiselect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.Arrays;
import java.util.Collection;

import co.moonmonkeylabs.realmsearchview.search.SearchCriteria;
import co.moonmonkeylabs.realmsearchview.search.SearchFilter;
import co.moonmonkeylabs.realmsearchview.search.SearchOrderBy;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Note: Filter bypasses retrieving results using the Filter background thread because
 * of Realm's thread limitation, it instead processes the query in the UI thread.
 *
 * TODO: implement ViewHolder pattern instead of populateView()
 */
public abstract class RealmFilteredListAdapter<T extends RealmObject/*, VH extends RealmViewHolder*/>
        extends RealmBaseAdapter<T> implements Filterable {

    private RealmResults<T> realmResults;
    private RealmFilter<T> realmFilter;

    public RealmFilteredListAdapter(
            @NonNull Context context,
            @NonNull SearchFilter<T> searchFilter,
            @NonNull boolean autoRefresh) {
        super(context, null, autoRefresh);

        this.realmFilter = new RealmFilter<T>(searchFilter, true) {
            @Override
            public void updateRealmResults(RealmResults<T> filteredResults) {
                RealmFilteredListAdapter.this.updateRealmResults(filteredResults);
            }
        };
    }

    @Override
    public Filter getFilter() {
        return realmFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T object = getItem(position);

        View view = populateView(object, convertView, parent);

        return view;
    }

    protected abstract View populateView(T object, View view, ViewGroup parent);

//    public abstract VH onCreateRealmViewHolder(ViewGroup var1, int var2);
//
//    public abstract void onBindRealmViewHolder(VH var1, int var2);

}

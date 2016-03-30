package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.moonmonkeylabs.realmsearchview.multiselect.RealmFilter;
import co.moonmonkeylabs.realmsearchview.search.SearchCriteria;
import co.moonmonkeylabs.realmsearchview.search.SearchFilter;
import co.moonmonkeylabs.realmsearchview.search.SearchOrderBy;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A custom adapter for the {@link RealmSearchView}. It has options to customize the filtering.
 */
public abstract class RealmSearchAdapter<T extends RealmObject, VH extends RealmSearchViewHolder>
        extends RealmBasedRecyclerViewAdapter<T, VH> {

    private SearchFilter searchFilter;

    public RealmSearchAdapter(
            @NonNull Context context,
            @NonNull Realm realm,
            @NonNull SearchFilter<T> searchFilter) {
        super(context, null, false, false);

        this.searchFilter = searchFilter;
    }

    public RealmSearchAdapter(
            @NonNull Context context,
            @NonNull RealmResults<T> realmResults,
            @NonNull Collection<SearchCriteria> orFields,
            @NonNull Collection<SearchOrderBy> orderBys,
            @NonNull String defaultSearchInput,
            @NonNull boolean autoRefresh) {
        super(context, null, false, false);

        this.searchFilter = new SearchFilter<T>(realmResults, orFields, orderBys, defaultSearchInput);
    }

    public RealmSearchAdapter(
            @NonNull Context context,
            @NonNull Realm realm,
            @NonNull String filterKey) {
        this(context, realm, filterKey, true, Case.INSENSITIVE, Sort.ASCENDING, filterKey, null);
    }

    public RealmSearchAdapter(
            @NonNull Context context,
            @NonNull Realm realm,
            @NonNull String filterKey,
            boolean useContains,
            Case casing,
            Sort sortOrder,
            String sortKey,
            String defaultSearchInput) {
        super(context, null, false, false);

        Class<T> realmClass = RealmSearchAdapter.getRealmClass(getClass());

        this.searchFilter = new SearchFilter<T>(realm, realmClass,
                Arrays.asList(new SearchCriteria(filterKey, useContains, casing)),
                Arrays.asList(new SearchOrderBy(sortKey, sortOrder)), defaultSearchInput);
    }

    public abstract void bindToViewHolder(VH viewHolder, T user);

    @Override
    public void onBindRealmViewHolder(VH viewHolder, int position) {
        T selected = realmResults.get(position);

        bindToViewHolder(viewHolder, selected);
    }

    @Override
    public void onBindFooterViewHolder(VH holder, int position) {
        holder.footerTextView.setText("I'm a footer");
    }

    @Override
    @SuppressWarnings("unchecked")
    public VH onCreateFooterViewHolder(ViewGroup viewGroup) {
        FrameLayout v = (FrameLayout) inflater.inflate(R.layout.footer_view, viewGroup, false);
        TextView footer = (TextView) v.findViewById(R.id.footer_text_view);
        RealmSearchViewHolder vh = new RealmSearchViewHolder(v, footer);

        return (VH) vh;
    }

    public void filter(String input) {
        RealmResults results = this.searchFilter.filter(input);

        updateRealmResults(results);
    }

    //
    // The code below is copied from StackOverflow in order to avoid having to pass in the T as a
    // Class for the Realm query/filtering.
    // http://stackoverflow.com/a/15008017
    //
    /**
     * Get the underlying class for a type, or null if the type is a variable
     * type.
     *
     * @param type the type
     * @return the underlying class
     */
    public static Class<?> getClass(Type type)
    {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic
     * base class.
     *
     * @param baseClass the base class
     * @param childClass the child class
     * @return a list of the raw classes for the actual type arguments.
     */
    public static <T> List<Class<?>> getTypeArguments(
            Class<T> baseClass, Class<? extends T> childClass)
    {
        Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (!getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just keep going.
                type = ((Class) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class) parameterizedType.getRawType();

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass, determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments;
        if (type instanceof Class) {
            actualTypeArguments = ((Class) type).getTypeParameters();
        } else {
            actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        }
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }

    private static <T extends RealmObject> Class<T> getRealmClass(Class<? extends RealmSearchAdapter> clazz) {
        return (Class<T>) getTypeArguments(RealmSearchAdapter.class, clazz).get(0);
    }
    //
    // End StackOverflow code
    //
}

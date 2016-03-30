package co.moonmonkeylabs.realmsearchview.multiselect;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import co.moonmonkeylabs.realmsearchview.R;
import io.realm.RealmObject;

public abstract class RealmMultiSelectView<T extends RealmObject> extends TokenCompleteTextView<T> {

    public RealmMultiSelectView(Context context) {
        super(context);
    }

    public RealmMultiSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RealmMultiSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(T object) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.token_default, (ViewGroup) RealmMultiSelectView.this.getParent(), false);
        TextView textView = (TextView) view.findViewById(R.id.token_value);

        textView.setText(getTokenText(object));

        return view;
    }

    protected abstract String getTokenText(T object);

    @Override
    protected T defaultObject(String completionText) {
        return null;
    }
}
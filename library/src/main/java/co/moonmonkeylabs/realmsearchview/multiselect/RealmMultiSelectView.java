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

import java.io.Serializable;
import java.util.ArrayList;

import co.moonmonkeylabs.realmsearchview.R;
import io.realm.RealmModel;
import io.realm.RealmObject;

public abstract class RealmMultiSelectView<T extends RealmModel> extends TokenCompleteTextView<T> {

    public RealmMultiSelectView(Context context) {
        super(context);
        setDeletionStyle(TokenCompleteTextView.TokenDeleteStyle.Clear);
    }

    public RealmMultiSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDeletionStyle(TokenCompleteTextView.TokenDeleteStyle.Clear);
    }

    public RealmMultiSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDeletionStyle(TokenCompleteTextView.TokenDeleteStyle.Clear);
    }

    @Override
    protected View getViewForObject(T object) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.token_default, (ViewGroup) RealmMultiSelectView.this.getParent(), false);
        TextView textView = (TextView) view.findViewById(R.id.token_value);

        if (RealmObject.isValid(object)) {
            textView.setText(getTokenText(object));
        }

        return view;
    }

    @Override
    abstract protected ArrayList<T> convertSerializableArrayToObjectArray(ArrayList<Serializable> s);

    @Override
    abstract protected ArrayList<Serializable> getSerializableObjects();

    protected abstract String getTokenText(T object);

    @Override
    protected T defaultObject(String completionText) {
        return null;
    }
}
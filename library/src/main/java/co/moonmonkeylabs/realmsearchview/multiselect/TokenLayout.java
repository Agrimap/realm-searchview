package co.moonmonkeylabs.realmsearchview.multiselect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.moonmonkeylabs.realmsearchview.R;

public class TokenLayout extends LinearLayout {

    public TokenLayout(Context context) {
        super(context);
    }

    public TokenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        TextView v = (TextView)findViewById(R.id.token_value);
        if (selected) {
            v.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close_x, 0);
        } else {
            v.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

}
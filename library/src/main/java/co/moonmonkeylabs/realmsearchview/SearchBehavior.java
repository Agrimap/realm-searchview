package co.moonmonkeylabs.realmsearchview;

import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.view.View;

public interface SearchBehavior extends View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    public void setClearDrawable(Drawable clearDrawable);

}

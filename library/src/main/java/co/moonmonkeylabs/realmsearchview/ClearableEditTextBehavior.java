package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import static android.view.View.OnFocusChangeListener;

/**
 * Adds a clear input text drawable to {@link EditText}.
 */
public class ClearableEditTextBehavior implements SearchBehavior {

    private EditText editText;
    private Drawable clearDrawable;

    public ClearableEditTextBehavior(EditText editText) {
        this.editText = editText;
        this.clearDrawable = editText.getCompoundDrawables()[2];
        if (this.clearDrawable == null) {
            this.clearDrawable = editText.getResources().getDrawable(R.drawable.ic_cancel_black_18dp);
        }
        setClearDrawable(this.clearDrawable);
        setClearIconVisible(false);
        editText.setOnTouchListener(this);
        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);
    }

    public static void addTo(EditText editText) {
        new ClearableEditTextBehavior(editText);
    }

    public EditText getEditText() {
        return editText;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Drawable drawable = editText.getCompoundDrawables()[2];
        if (drawable != null) {
            int clearWidth = clearDrawable.getIntrinsicWidth();
            int textWidth = editText.getWidth();
            int textPadding = editText.getPaddingRight();
            boolean tappedX = event.getX() > (textWidth - textPadding - clearWidth);
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    editText.setText("");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(isNotEmpty(editText.getText().toString()));
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (editText.isFocused()) {
            setClearIconVisible(isNotEmpty(s.toString()));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    private boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    private void setClearIconVisible(boolean visible) {
        Drawable[] compoundDrawables = editText.getCompoundDrawables();
        boolean wasVisible = (compoundDrawables[2] != null);
        if (visible != wasVisible) {
            Drawable x = visible ? clearDrawable : null;
            editText.setCompoundDrawables(
                    compoundDrawables[0],
                    compoundDrawables[1],
                    x,
                    compoundDrawables[3]);
        }
    }

    @Override
    public void setClearDrawable(Drawable clearDrawable) {
        this.clearDrawable = clearDrawable;
        this.clearDrawable.setBounds(
                0,
                0,
                clearDrawable.getIntrinsicWidth(),
                clearDrawable.getIntrinsicHeight());
    }
}
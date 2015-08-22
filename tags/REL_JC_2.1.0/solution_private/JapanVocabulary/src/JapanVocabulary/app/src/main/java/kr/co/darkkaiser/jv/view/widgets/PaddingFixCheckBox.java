package kr.co.darkkaiser.jv.view.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CheckBox;

import kr.co.darkkaiser.jv.R;

public class PaddingFixCheckBox extends CheckBox {

    public PaddingFixCheckBox(Context context) {
        super(context);
    }

    public PaddingFixCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PaddingFixCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getCompoundPaddingBottom() {
        int compoundPaddingBottom = super.getCompoundPaddingBottom();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return compoundPaddingBottom;
        } else {
            return compoundPaddingBottom + (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public int getCompoundPaddingLeft() {
        int compoundPaddingLeft = super.getCompoundPaddingLeft();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Drawable drawable = getResources().getDrawable(R.drawable.custom_checkbox);
            return compoundPaddingLeft + (drawable != null ? drawable.getIntrinsicWidth() : 0);
        } else {
            return compoundPaddingLeft;
        }
    }

}

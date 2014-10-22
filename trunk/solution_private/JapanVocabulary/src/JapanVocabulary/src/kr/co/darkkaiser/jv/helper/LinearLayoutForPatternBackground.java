package kr.co.darkkaiser.jv.helper;

import kr.co.darkkaiser.jv.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinearLayoutForPatternBackground extends LinearLayout {
	private Drawable background = null;
 
	public LinearLayoutForPatternBackground(Context context, AttributeSet attrs) {
		super(context, attrs);

		background = context.getResources().getDrawable(R.drawable.search_bar_bg);
	}
 
	@Override
	protected void dispatchDraw(Canvas canvas) {
		Rect bounds = new Rect();
		canvas.getClipBounds(bounds);

		for(int top = 0; top < bounds.bottom; top += background.getIntrinsicHeight()) {
			for(int left = 0;left<bounds.right; left += background.getIntrinsicWidth()) {
				background.setBounds(left, top, left + background.getIntrinsicWidth(), top + background.getIntrinsicHeight());
				background.draw(canvas);
			}
		}

		super.dispatchDraw(canvas);
	}
}

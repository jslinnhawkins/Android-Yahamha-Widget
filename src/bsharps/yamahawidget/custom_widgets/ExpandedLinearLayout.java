package bsharps.yamahawidget.custom_widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

class ExpandedLinearLayout extends LinearLayout
{
	public ExpandedLinearLayout (Context context, AttributeSet attrs)
	{
		super (context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		//final int height = MeasureSpec.getSize(heightMeasureSpec);

		//Log.v("", String.format("w %d h %d", width, height));
		
		// TODO determine children height
		this.setMeasuredDimension(width, 60);
	}
}
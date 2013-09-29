package bsharps.yamahawidget.custom_widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScalableImageView extends ImageView
{
	public ScalableImageView (Context context, AttributeSet attr)
	{
		super (context, attr);
		setScaleType (ScaleType.FIT_XY);
	}

	private float _scale = 1.0f;
	public void SetScaleFactor(float scale)
	{
		_scale =scale;
		this.requestLayout ();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		
		// TODO change fixed height to something better
		//final int height = MeasureSpec.getSize(heightMeasureSpec);

		int w= (int)(width * _scale);

		//Log.v("", String.format("w %d h %d w %d", width, height, w));

		this.setMeasuredDimension(w, 50 );
	}
}

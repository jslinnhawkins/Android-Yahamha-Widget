/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package bsharps.yamahawidget.custom_widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends CustomRangePreference implements SeekBar.OnSeekBarChangeListener
{
	private SeekBar mSeekBar;
	private TextView mSplashText/* , mValueText */;

	public SeekBarPreference (Context context, AttributeSet attrs)
	{
		super (context, attrs);
	}

	@Override
	protected int convertToPreferenceValue (int sliderVal)
	{
		return (sliderVal * _step) + _min;
	}

	@Override
	protected int convertFromPreferenceValue (int realVal)
	{
		return (int) Math.ceil ((double) (realVal - _min) / (double) _step);
	}

	private void SetSliderValue (int val)
	{
		mSeekBar.setProgress (val);
	}

	private void SetSliderValue ()
	{
		SetSliderValue (convertFromPreferenceValue (getRangeValue ()));
	}

	private void SetSliderMaxValue ()
	{
		int x = 0;
		if (mSeekBar == null) x++;

		mSeekBar.setMax (convertFromPreferenceValue (_max));
	}

	@Override
	protected View onCreateView (ViewGroup parent)
	{
		if (shouldPersist ()) setRangeValue (getPersistedInt (_default));

		//LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout (_context);
		layout.setOrientation (LinearLayout.VERTICAL);
		layout.setPadding (6, 6, 6, 6);

		mSplashText = new TextView (_context);
		mSplashText.setTextSize (24);

		layout.addView (mSplashText);

		mSeekBar = new SeekBar (_context);
		mSeekBar.setOnSeekBarChangeListener (this);
		layout.addView (mSeekBar, new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		SetSliderMaxValue ();
		SetSliderValue ();

		onProgressChanged (mSeekBar, mSeekBar.getProgress (), false);
		UpdateSplashText ();

		return layout;
	}

	@Override
	protected void onBindView (View v)
	{
		super.onBindView (v);
		// SetSliderMaxValue();
		// SetSliderValue();
	}

	@Override
	public void setTitle (CharSequence title)
	{
		super.setTitle (title);
		UpdateSplashText ();
	}

	public void UpdateSplashText ()
	{
		String title = (String) getTitle ();

		if (title == null) title = "";

		// Possible that splashtext has not yet been created
		if (mSplashText != null)
		{
			// TODO dirty
			if (_exp != 0)
			{
				// TODO make the number of decimal places dependant on exp
				mSplashText.setText (String.format ("%s: %.1f %s", title, (double) getRangeValue () / Math.pow (10, _exp), _unit));
			}
			else
			{
				mSplashText.setText (String.format ("%s: %d %s", title, getRangeValue (), _unit));
			}
		}
	}

	// @Override
	public void onProgressChanged (SeekBar seek, int value, boolean fromTouch)
	{
		value = convertToPreferenceValue (value);
		setRangeValue (value);
		UpdateSplashText ();
	}

	public void onStartTrackingTouch (SeekBar seek)
	{
	}

	public void onStopTrackingTouch (SeekBar seek)
	{
	}
}

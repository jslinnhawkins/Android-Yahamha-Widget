/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package bsharps.yamahawidget.custom_widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import bsharps.yamahawidget.main.R;

public class CustomRangePreference extends Preference
{
	protected static final String androidns = "http://schemas.android.com/apk/res/android";

	protected Context _context;

	// The _value refers to the preference's value
	protected int _default, _max, _min, _exp, _step;
	protected String _unit;

	private int _value;
	protected AttributeSet _attributes;

	public CustomRangePreference (Context context, AttributeSet attrs)
	{
		super (context, attrs);
		_context = context;

		_attributes = attrs;
		init (attrs);
	}

	private void init (AttributeSet attrs)
	{
		TypedArray a = getContext ().obtainStyledAttributes (attrs, R.styleable.PreferenceCustomAttrs);

		_min = a.getInteger (R.styleable.PreferenceCustomAttrs_min, 0);
		_max = a.getInteger (R.styleable.PreferenceCustomAttrs_max, 0);

		_unit = a.getString (R.styleable.PreferenceCustomAttrs_unit);
		if (_unit == null) _unit = "";

		_default = a.getInteger (R.styleable.PreferenceCustomAttrs_defaultVal, 0);
		_step = a.getInteger (R.styleable.PreferenceCustomAttrs_step, 0);
		_exp = a.getInteger (R.styleable.PreferenceCustomAttrs_exp, 0);
	}

	protected int convertToPreferenceValue (int value)
	{
		return value;
	}

	protected int convertFromPreferenceValue (int value)
	{
		return value;
	}

	@Override
	protected void onSetInitialValue (boolean restore, Object defaultValue)
	{
		super.onSetInitialValue (restore, defaultValue);
		//android.os.Debug.waitForDebugger ();
		if (restore) 
			_value = shouldPersist () ? getPersistedInt (_default) : _default;
		else 
			_value = _default;
	}

	public float GetRangeProportion ()
	{
		if ((_max - _min) == 0) 
			throw new RuntimeException ("(_max - _min) == 0");

		return (float) (_value - _min) / (float) GetAbsRange ();
	}

	public void setRangeValue (int value)
	{
		_value = value;

		if (shouldPersist ()) persistInt (value);
		callChangeListener (new Integer (value));
	}

	public int GetAbsRange ()
	{
		return (_max - _min);
	}

	public int GetNumSteps ()
	{
		return Math.abs (GetAbsRange () / _step);
	}

	public void IncreaseValue ()
	{
		int val = getRangeValue ();
		if (val + _step <= _max) setRangeValue (val + _step);
	}

	public void DecreaseValue ()
	{
		int val = _value;
		if (val - _step >= _min) setRangeValue (val - _step);
	}

	public void IncreaseXSteps (int steps)
	{
		int newVal = _value + (_step * steps);
		if (newVal > _max) newVal = _max;
		setRangeValue (newVal);
	}

	public void DecreaseXSteps (int steps)
	{
		int newVal = _value - (_step * steps);
		if (newVal < _min) newVal = _min;
		setRangeValue (newVal);
	}

	public int getRangeValue ()
	{
		return _value;
	}

}

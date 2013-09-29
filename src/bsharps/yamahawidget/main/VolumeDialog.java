package bsharps.yamahawidget.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import bsharps.yamahawidget.custom_widgets.ScalableImageView;
import bsharps.yamahawidget.receiver.ReceiverService;

public class VolumeDialog extends Activity implements DialogInterface.OnKeyListener, OnTouchListener
{
	private static final String TAG = "VolumeDialog";

	private static int VolumeStep = 5;
	private static int MaxVolumeStep = 30;
	private static int MaxCommandSendTimeMs = 200;

	private GestureDetector gestureDetector;
	// private GestureDetector buttonGestureDetector;

	private int volume;
	int _maxZoneVolume = ReceiverService.MinVolume;
	Dialog dialog = null;
	ProgressDialog pd;	
	
	// started up at beginning to read the vol
	private class ReadVolumeTask extends AsyncTask<Void, Void, Void>
	{
		boolean _result = false;

		@Override
		protected void onPreExecute ()
		{
			pd = ProgressDialog.show (VolumeDialog.this, "Please Wait", "Please Wait", true, false);
		}

		@Override
		protected void onPostExecute (Void result)
		{
			pd.dismiss ();

			// showDialog (0);
			if (_result)
			{
				showDialog (0);
			}
			else
			{
				// TODO show an error msg
				Log.e(TAG, "Error in ReadVolumeTask!");
				CloseWindow ();
			}
		}

		@Override
		protected Void doInBackground (Void... params)
		{
			_result = ReceiverService.LoadCurrentZoneVolume (VolumeDialog.this);
			volume = ReceiverService.GetVolume (VolumeDialog.this);			
			return null;
		}
	}

	private class SetVolumeTask extends AsyncTask<Void, Void, Void>
	{
		int _volume;

		SetVolumeTask (int volume)
		{
			_volume = volume;
		}

		@Override
		protected Void doInBackground (Void... params)
		{
			try
			{
				ReceiverService.SetVolume (_volume, VolumeDialog.this);
			}
			catch (Exception e)
			{
				Log.e ("SetVolumeTask", "Error " + e.getMessage ());
			}
			return null;
		}
	}

	public void CloseWindow()
	{
		Log.v(TAG, "Close Window");
		if (dialog != null)
			dialog.dismiss ();
		
		finish ();		
	}

	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);

		new ReadVolumeTask ().execute ((Void[])null);
				
		Log.d (TAG, "Finished onCreate");
	}

	private void ScaleVolumeBar ()
	{
		ScalableImageView imgView = (ScalableImageView) dialog.findViewById (R.id.volume_image);
		float range = (ReceiverService.MaxVolume - ReceiverService.MinVolume);
		
		float offs =range / 20; 
		range += offs;
				
		float scale = Math.abs ((volume - ReceiverService.MinVolume + offs) /range);
		
		//Log.v(TAG,"SCALE " + scale);
		imgView.SetScaleFactor (scale);
	}

	public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event)
	{
		Log.v (TAG, "Key Pressed " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
		{
			CloseWindow();
			return true;
		}

		// TODO space out the volume commands
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction () == KeyEvent.ACTION_DOWN)
		{
			Log.v (TAG, "Volume Up " + volume);

			int newVol = volume + VolumeStep;
			if (ReceiverService.CanSetToVolume (newVol, this))
			{
				volume = newVol;
				ScaleVolumeBar ();
				ReceiverService.VolumeUp (this);
			}

			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction () == KeyEvent.ACTION_DOWN)
		{
			Log.v (TAG, "Volume Down " + volume);

			int newVol = volume - VolumeStep;
			if (ReceiverService.CanSetToVolume (newVol, this))
			{
				volume = newVol;
				ScaleVolumeBar ();
				ReceiverService.VolumeDown (this);
			}
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog (int id)
	{
		super.onCreateDialog (id);

		_maxZoneVolume = ReceiverService.GetZoneMaxVolume (VolumeDialog.this);
		Log.v(TAG, "Zone Max Volume " + _maxZoneVolume);
		
		if (volume > _maxZoneVolume)
		{
			volume = _maxZoneVolume;	
			SendSetVolumeAsync(volume);
		}
		
		dialog = new Dialog (this);
		dialog.setContentView (R.layout.volume_layout);
		dialog.setOnKeyListener (this);
		// TODO - also show which zone and the db value as the volume is changed
		dialog.setTitle ("Slide to change the volume:");

		ScalableImageView imgView = (ScalableImageView) dialog.findViewById (R.id.volume_image);

		Resources r = getResources ();
		Drawable [] layers = new Drawable [1];
		layers[0] = r.getDrawable (R.drawable.volume_slider_gradient);
		// TODO 
		//layers[1] = r.getDrawable (R.drawable.volume_slider_alpha);
		LayerDrawable layerDrawable = new LayerDrawable (layers);

		// imgView.setImageResource (R.drawable.volume_slider_gradient);
		imgView.setImageDrawable (layerDrawable);
		imgView.setOnTouchListener (this);
		ScaleVolumeBar ();

		View v = dialog.findViewById (R.id.volume_area);
		v.setOnTouchListener (this);
		
		updateVolumeText (volume);
		
		return dialog;
	}

	public boolean onTouch (View v, MotionEvent event)
	{
		if (gestureDetector == null)
		{
			gestureDetector = new GestureDetector (new MyGestureDetector ());
		}

		gestureDetector.onTouchEvent (event);
		
		return true; // TODO
	}

	public int GetRoundedVolume ()
	{
		int rem = (Math.abs (volume) % 5);
		return volume + ((volume > 0) ? rem * (-1) : rem);
	}
	
	public void SendSetVolumeAsync(int vol)
	{
		new SetVolumeTask (vol).execute ((Void[])null);
	}

	private void updateVolumeText (int newVol)
	{
		TextView tv = (TextView)VolumeDialog.this.dialog.findViewById (R.id.volume_text);
		if (ReceiverService.MaxVolumeReached (newVol, VolumeDialog.this))
		{
			Log.v (TAG, "Max volume reached at " + newVol);
			tv.setVisibility(View.VISIBLE);
		}
		else if (tv.getVisibility () == View.VISIBLE)
		{
			Log.v (TAG, " " + newVol);
			tv.setVisibility(View.INVISIBLE);
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener
	{
		//private static final String TAG = "MyGestureDetector";
		private int lastVolumeSent = -805;
		private long lastMessage = 0;
		private boolean init = false;

		public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			int inc = ((int) distanceX / 2);
			if (Math.abs (inc) > MaxVolumeStep)
			{
				inc = (inc > 0) ? MaxVolumeStep : -MaxVolumeStep;
			}

			int newVol = volume - inc;
			
			// Can happen if the max zone volume is set lower than the current volume
			if (_maxZoneVolume <= newVol)
			{
				newVol = _maxZoneVolume;
			}

			updateVolumeText (newVol);			
			
			if (!ReceiverService.CanSetToVolume (newVol, VolumeDialog.this))
			{				
				return false;
			}

			volume = newVol;

			long now = SystemClock.elapsedRealtime ();
			long elapsed = (now > lastMessage) ? (now - lastMessage) : (Long.MAX_VALUE - now + lastMessage);

			int volumetosend = GetRoundedVolume ();
			if ((!init) || (elapsed > MaxCommandSendTimeMs) && (lastVolumeSent != volumetosend))
			{
				init = true;
				lastVolumeSent = volumetosend;
				
				SendSetVolumeAsync (volumetosend);
				lastMessage = now;
			}
			
			ScaleVolumeBar ();
			
			return false; // TODO
		}
	}
}

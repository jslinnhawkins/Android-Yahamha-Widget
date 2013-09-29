package bsharps.yamahawidget.settings;

import java.util.List;

import org.acra.ErrorReporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import bsharps.yamahawidget.custom_widgets.SeekBarPreference;
import bsharps.yamahawidget.main.R;
import bsharps.yamahawidget.main.TestWidget;
import bsharps.yamahawidget.main.TestWidget.UpdateService;
import bsharps.yamahawidget.receiver.ReceiverService;
import bsharps.yamahawidget.util.HttpClientUtil;
import bsharps.yamahawidget.util.SharedPreferenceHelper;

public class AppSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, View.OnClickListener, View.OnKeyListener
{
	private final static String TAG = "AppSettingsActivity";
	private ProgressDialog pd;

	class AsyncReceiverScan extends AsyncTask<String, Void, Boolean>
	{
		private Context _ctx;
		private Handler _handler;

		public AsyncReceiverScan (Context ctx, Handler handler)
		{
			_ctx = ctx;
			_handler = handler;
		}

		@Override
		protected Boolean doInBackground (String... params)
		{
			return ReceiverService.LoadReceiverInfo (_ctx);
		}

		
		@Override
		protected void onPostExecute (Boolean result)
		{
			super.onPostExecute (result);

			if (_handler != null)
			{
				Message msg = new Message ();
				Log.v (TAG, "Result AsyncReceiverScan " + result);
				msg.obj = result;
				_handler.sendMessage (msg);
			}
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		Log.v(TAG,"FOCUS CHANGE " + hasFocus);
		if  (!hasFocus)
		TestWidget.StartTheService (this);
	}

	public void onBackPressed ()
	{
		finish ();
		return;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		addPreferencesFromResource (R.xml.app_settings);
	
		PreferenceManager.setDefaultValues (this, R.xml.app_settings, false);

		ReceiverService.GetInputs (this).AddItemsAsCheckBoxListToMenu (this, getString (R.string.as_inputsel_menu));
		ReceiverService.GetDsp (this).AddItemsAsCheckBoxListToMenu (this, getString (R.string.as_dspsel_menu));

		String model = ReceiverService.GetModel (this);
		
		if (model != null)
		{
			SetModelPreference (model);
		}
		else
		{
			SetModelPreference ("Unknown, please configure the IP Address");
		}
	
		// market intent
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=bsharps.yamahawidget.main"));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	
		PreferenceScreen p = (PreferenceScreen) findPreference (getString(R.string.pref_rate));
	
		List<ResolveInfo> acts = this.getPackageManager().queryIntentActivities(intent, Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		
		if (acts.size()>0)
		{
			p.setIntent(intent);	
		}
		else
		{
			p.setEnabled(false);			
		}
		
		SetZoneNames ();
	}
	
	private void SetZoneNames ()
	{
		if (ReceiverService.Valid (this))
		{	
			for (int i = 0; i < ReceiverService.ZoneNames.length; i++)
			{
				SeekBarPreference p = (SeekBarPreference)findPreference ("zonemaxvol" + i);
				
				if (p != null)
				{
					String zoneDisplayName = ReceiverService.GetZoneDisplayName (i, this);
					if (zoneDisplayName == null)
					{
						zoneDisplayName = "Not Available";
						p.setEnabled (false);
					}
					p.setTitle (zoneDisplayName);
				}
			}			
		}
	}

	@Override
	protected void onResume ()
	{
		super.onResume ();
		getPreferenceScreen ().getSharedPreferences ().registerOnSharedPreferenceChangeListener (this);
	}

	@Override
	protected void onPause ()
	{
		super.onPause ();
		getPreferenceScreen ().getSharedPreferences ().unregisterOnSharedPreferenceChangeListener (this);
	}

	// @Override
	public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
	{
		Preference pref = findPreference (key);

		if (pref != null && (pref.getKey ().compareTo (getString (R.string.as_k_receiver_ip)) == 0))
		{
			String ip = SharedPreferenceHelper.GetStringPreference (key, "", this);
			// TODO valid check
			Log.v (TAG, "IP Set " + ip);
			HttpClientUtil.setIp (ip);

			ScanReceiver ();
		}
	}

	public void SetModelPreference (String model)
	{
		Preference p = findPreference (getString (R.string.as_receiver_type));
		p.setSummary (model);
	}

	public void ScanReceiver ()
	{
		pd = ProgressDialog.show (this, "Please Wait..", "Determining Receiver Type", true, false);

		new AsyncReceiverScan (this, new Handler ()
		{
			@Override
			public void handleMessage (Message msg)
			{
				super.handleMessage (msg);
				
				Context ctx = AppSettingsActivity.this;

				pd.dismiss ();
				
				Log.v (TAG, "Receiver Scan done");

				boolean found = false;

				// android.os.Debug.waitForDebugger();
				if (msg != null && msg.obj != null)
				{
					Boolean response = (Boolean) msg.obj;

					if (response != null && response == true)
					{
						SetModelPreference (ReceiverService.GetModel (ctx));						
						SetZoneNames();
						ReceiverService.SetReceiverAvailability (true, ctx);
						found = true;
					}
				}

				if (!found)
				{
					SetModelPreference ("Receiver not found, please check the Ip Address again, or consult the Help");

					// clear model as this is our indication for a valid setup
					ReceiverService.SetModel (ctx, null);					
				}
				
				ReceiverService.SetInputs (ctx);
				ReceiverService.SetDsp (ctx);
				
				ReceiverService.GetInputs (ctx).AddItemsAsCheckBoxListToMenu (AppSettingsActivity.this, getString (R.string.as_inputsel_menu));
				ReceiverService.GetDsp (ctx).AddItemsAsCheckBoxListToMenu (AppSettingsActivity.this, getString (R.string.as_dspsel_menu));

				// send an intent to the service to update the view
				Intent sint = new Intent (ctx, UpdateService.class);
				ctx.startService (sint);
			}
		}).execute();			
	}

	public void onClick (View v)
	{
		ScanReceiver ();
	}

	public boolean onKey (View v, int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
		{
			TestWidget.StartTheService (this);			
			finish ();
			return true;
		}
		return false;
	}
}

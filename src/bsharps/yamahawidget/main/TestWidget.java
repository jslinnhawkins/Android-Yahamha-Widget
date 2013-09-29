package bsharps.yamahawidget.main;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import bsharps.yamahawidget.receiver.ReceiverService;
import bsharps.yamahawidget.settings.AppSettingsActivity;
import bsharps.yamahawidget.util.HttpClientUtil;
import bsharps.yamahawidget.util.SharedPreferenceHelper;

public class TestWidget extends AppWidgetProvider
{
	public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";

	public static int ACTION_BUTTON_POWER = 1;
	public static int ACTION_RESCAN = 2;

	public static String TAG = "AppWidgetProvider";
	public static String WIDGET_IDS = "WidgetIds";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Log.v(TAG,"OnUpdate()");
		if (!ReceiverService.Valid (context))
		{
			for (int i : appWidgetIds)
			{
				Log.v(TAG, "Show no setup view for appwidget " + i);
				ShowNoSetupView (context, AppWidgetManager.getInstance (context), i);
			}
		}
		else
		{
			StartTheService (context);			
		}
	}
	
	@Override
	public void onReceive (Context context, Intent intent)
	{
		Log.v (TAG, "onReceive()");
		super.onReceive (context, intent);
		
		String action = intent.getAction ();

		if (action != null)
		{
			Log.v (TAG, "onReceive() ACTION is " + action);

			if (action.equals (AppWidgetManager.ACTION_APPWIDGET_UPDATE) || action.equals (AppWidgetManager.ACTION_APPWIDGET_ENABLED))
			{
				Log.v (TAG, "onUpdate");

				if (!ReceiverService.Valid (context))
				{
					Bundle extras = intent.getExtras ();
					if (extras != null)
					{
						int ids[] = extras.getIntArray (AppWidgetManager.EXTRA_APPWIDGET_IDS);
						AppWidgetManager manager = AppWidgetManager.getInstance (context);

						if (ids != null && manager != null)
						{
							for (int i : ids)
							{
								Log.v(TAG, "Show no setup view for appwidget " + i);
								ShowNoSetupView (context, AppWidgetManager.getInstance (context), i);
							}
								
							Log.v (TAG, "CASE 1");
						}
						else
						{
							ShowNoSetupView (context);
							Log.v (TAG, "CASE 2");
						}
					}
					else
					{
						ShowNoSetupView (context);
						Log.v (TAG, "CASE 3");
					}
				}
				else
				{
					StartTheService (context);
				}
			}
			else if (action.equals (ConnectivityManager.CONNECTIVITY_ACTION))
			{
				Log.v (TAG, "Rescan Receiver");
				Intent serviceIntent = new Intent (context, UpdateService.class);
				serviceIntent.putExtra ("Button", ACTION_RESCAN);
				context.startService (serviceIntent);
			}
			else if (action.equals (AppWidgetManager.ACTION_APPWIDGET_DISABLED))
			{
				Log.v (TAG, "Delete all user data");
				SharedPreferenceHelper.ClearAllPreferences (context);
			}
			else// TODO listen for specific action??			
			{
				StartTheService (context);
			}
		}
		Log.v (TAG, "onUpdate done");
	}

	public static void StartTheService (Context context)
	{
		Log.v (TAG, "Start the Service!");
		Intent serviceIntent = new Intent (context, UpdateService.class);
		context.startService (serviceIntent);
	}

	public static class UpdateService extends Service
	{
		public static final String TAG = "Service";

		@Override
		public void onCreate ()
		{
			super.onCreate ();
			Log.v (TAG, "onCreate!!");

			HttpClientUtil.setIp (SharedPreferenceHelper.GetStringPreference (getString (R.string.as_k_receiver_ip), "", this));
		}

		@Override
		public void onStart (Intent intent, int startId)
		{
			Log.d (TAG, "onStart()");

			int action = 0;
			Bundle extras = intent.getExtras ();
			// check if a button was pressed
			if (extras != null)
			{
				action = extras.getInt ("Button");
			}

			Log.v (TAG, "ACTION " + action);

			if (action == ACTION_RESCAN)
			{
				Log.v (TAG, "Rescan Receiver");
				CreateNoConnectionView(this, true);
				ReceiverService.CheckReceiverAvailability (this);				
			}
			else if (!ReceiverService.Valid (this))// Was setup ever run?
			{
				Log.v (TAG, "Receiver NOT valid");
				ShowNoSetupView (this);
			}
			else if (!ReceiverService.IsReceiverAvailable (this))
			{
				Log.v (TAG, "Receiver NOT Available");
				CreateNoConnectionView (this,false);
			}
			else if (action == ACTION_BUTTON_POWER)
			{
				Log.v (TAG, "Toggle Power");
				ReceiverService.ToggleZonePower (this);
			}
			else
			{
				// no button supplied => create view
				ConnectivityManager con = (ConnectivityManager) this.getSystemService (Context.CONNECTIVITY_SERVICE);

				if (con != null)
				{
					NetworkInfo info = con.getActiveNetworkInfo ();

					if (info == null || !info.isConnected ())
					{
						CreateNoConnectionView (this, false);
					}
					else
					{
						CreateView (this);
					}
				}
				 
			}
			Log.d (TAG, "onStart Finished");
		}

		@Override
		public IBinder onBind (Intent arg0)
		{
			return null;
		}

	}
	

	public static RemoteViews CreateView (Context context)
	{
		Log.v (TAG, "Show Normal View");
		RemoteViews remoteViews = new RemoteViews (context.getPackageName (), R.layout.main);

		Intent intent = new Intent (context, AppSettingsActivity.class);
		PendingIntent pi = PendingIntent.getActivity (context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent (R.id.open_settings_button, pi);

		intent = new Intent (context, ZoneSelectionDialog.class);
		pi = PendingIntent.getActivity (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent (R.id.zone_button, pi);

		intent = new Intent (context, InputSelectionDialog.class);
		pi = PendingIntent.getActivity (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent (R.id.input_selection_button, pi);

		intent = new Intent (context, VolumeDialog.class);
		pi = PendingIntent.getActivity (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent (R.id.sound_button, pi);

		intent = new Intent (context, DspSelectionDialog.class);
		pi = PendingIntent.getActivity (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent (R.id.dsp_selection_button, pi);

		intent = new Intent (context, UpdateService.class);
		intent.putExtra ("Button", ACTION_BUTTON_POWER);
		pi = PendingIntent.getService (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent (R.id.power_button, pi);

		ShowRemoteView (context, remoteViews);

		return remoteViews;
	}

	public static RemoteViews CreateNoConnectionView (Context context, boolean progressWidget)
	{
		Log.v (TAG, "Show No Connection View");
		RemoteViews remoteViews = null;
		
		if (progressWidget)
		{
			remoteViews = new RemoteViews (context.getPackageName (), R.layout.main_no_connection_progress);
		}				
		else
		{
			remoteViews = new RemoteViews (context.getPackageName (), R.layout.main_no_connection);
		}

		Intent intent = new Intent (context, AppSettingsActivity.class);
		PendingIntent pi = PendingIntent.getActivity (context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent (R.id.open_settings_button, pi);

		intent = new Intent (context, UpdateService.class);
		intent.putExtra ("Button", ACTION_RESCAN);
		pi = PendingIntent.getService (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent (R.id.button_rescan, pi);

		ShowRemoteView (context, remoteViews);

		return remoteViews;
	}

	public static RemoteViews CreateNoSetupView (Context context)
	{
		RemoteViews view = new RemoteViews (context.getPackageName (), R.layout.main_no_setup);
		Intent intent = new Intent (context, AppSettingsActivity.class);
		PendingIntent pi = PendingIntent.getActivity (context, 0, intent, 0);
		view.setOnClickPendingIntent (R.id.open_settings_button, pi);
		view.setOnClickPendingIntent (R.id.main_nosetup_frame, pi);
		view.setOnClickPendingIntent (R.id.main_nosetup_text, pi);

		return view;
	}

	public static void ShowNoSetupView (Context context)
	{
		RemoteViews remoteViews = CreateNoSetupView (context);
		ShowRemoteView (context, remoteViews);
	}

	public static void ShowNoSetupView (Context context, AppWidgetManager appWidgetManager, int id)
	{
		RemoteViews remoteViews = CreateNoSetupView (context);
		appWidgetManager.updateAppWidget (id, remoteViews);
		Log.v (TAG, "Show setup View because of this awful bug!");
	}

	public static void ShowRemoteView (Context context, RemoteViews remoteViews)
	{
		// Push update for this widget to the home screen
		ComponentName component = new ComponentName (context, TestWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance (context);
		manager.updateAppWidget (component, remoteViews);
	}
}

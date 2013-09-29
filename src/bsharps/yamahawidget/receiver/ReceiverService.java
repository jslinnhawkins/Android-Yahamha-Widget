package bsharps.yamahawidget.receiver;

import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import bsharps.yamahawidget.main.BugReporter;
import bsharps.yamahawidget.main.R;
import bsharps.yamahawidget.main.TestWidget;
import bsharps.yamahawidget.settings.ListContent;
import bsharps.yamahawidget.settings.ListContentItem;
import bsharps.yamahawidget.util.SharedPreferenceHelper;

public class ReceiverService
{
	private static final String TAG = "Receiver";
	private static final String zoneNumberKey = "zone_number_idx";
	private static final String receiverValidKey = "receiver_valid";
	private static final String receiverAvailabilityKey = "receiver_available";

	private static final String receiverModelKey = "System.Config.Model_Name";

	public static final String [] ZoneNames = new String []
	{
	"Main_Zone", "Zone_2", "Zone_3", "Zone_4"
	};

	public static final int MinVolume = -805;
	public static final int DefaultZoneMaxVolume = -30;
	
	public static final int MaxVolume = 0;
	public static final int DivisorVolume = 10;
	
	public static ListContent Inputs = null;
	public static ListContent Dsps = null;

	public static Handler GetHandler (final Context ctx)
	{
		return new Handler ()
		{
			@Override
			public void handleMessage (Message msg)
			{
				HttpResponse response = (HttpResponse) msg.obj;
				if (response != null)
				{
					int status = response.getStatusLine ().getStatusCode ();
					Log.v (TAG, "Status: " + status);
					if (status != 200)
					{
						CheckReceiverAvailability (ctx);
					}
				}
				else
				{
					Log.e (TAG, "response == null");
					CheckReceiverAvailability (ctx);
				}
			}
		};
	}
	
	public static void SetDsp(Context ctx)
	{
		String model = GetModel (ctx);
		Dsps = new ListContent ();
		Log.d("SetDsp", "setting the dsp contents");
		
		if (model != null)
		{
			if (model.contains ("67") || model.contains("RX-A"))
			{
				Dsps.add(new ListContentItem("Hall in Munich","Hall in Munich",R.string.dspselkey_hall_in_munich));
				Dsps.add(new ListContentItem("Hall in Vienna","Hall in Vienna",R.string.dspselkey_hall_in_vienna));
				Dsps.add(new ListContentItem("Chamber","Chamber",R.string.dspselkey_chamber));
				Dsps.add(new ListContentItem("Cellar Club","Cellar Club",R.string.dspselkey_cellar_club));
				Dsps.add(new ListContentItem("The Roxy Theatre","The Roxy Theatre",R.string.dspselkey_the_roxy_theatre));
				Dsps.add(new ListContentItem("The Bottom Line","The Bottom Line",R.string.dspselkey_the_bottom_line));
				Dsps.add(new ListContentItem("Sports","Sports",R.string.dspselkey_sports));
				Dsps.add(new ListContentItem("Action Game","Action Game",R.string.dspselkey_action_game));
				Dsps.add(new ListContentItem("Roleplaying Game","Roleplaying Game",R.string.dspselkey_roleplaying_game));
				Dsps.add(new ListContentItem("Music Video","Music Video",R.string.dspselkey_music_video));
				Dsps.add(new ListContentItem("Standard","Standard",R.string.dspselkey_standard));
				Dsps.add(new ListContentItem("Spectacle","Spectacle",R.string.dspselkey_spectacle));
				Dsps.add(new ListContentItem("Sci-Fi","Sci-Fi",R.string.dspselkey_scifi));
				Dsps.add(new ListContentItem("Adventure","Adventure",R.string.dspselkey_adventure));
				Dsps.add(new ListContentItem("Drama","Drama",R.string.dspselkey_drama));
				Dsps.add(new ListContentItem("Mono Movie","Mono Movie",R.string.dspselkey_mono_movie));
				Dsps.add(new ListContentItem("2ch Stereo","2ch Stereo",R.string.dspselkey_2ch_stereo));
				Dsps.add(new ListContentItem("7ch Stereo","7ch Stereo",R.string.dspselkey_7ch_stereo));
				Dsps.add(new ListContentItem("STRAIGHT","STRAIGHT",R.string.dspselkey_straight_enhancer));
				Dsps.add(new ListContentItem("Surround Decoder","Surround Decoder",R.string.dspselkey_surround_decode));
			}
			else
			{
				Dsps.add(new ListContentItem("Hall in Munich","Hall in Munich",R.string.dspselkey_hall_in_munich));
				Dsps.add(new ListContentItem("Hall in Vienna","Hall in Vienna",R.string.dspselkey_hall_in_vienna));
				Dsps.add(new ListContentItem("Hall in Amsterdam","Hall in Amsterdam",R.string.dspselkey_hall_in_amsterdam));
				Dsps.add(new ListContentItem("Church in Freiburg","Church in Freiburg",R.string.dspselkey_church_in_freiburg));
				Dsps.add(new ListContentItem("Chamber","Chamber",R.string.dspselkey_chamber));
				Dsps.add(new ListContentItem("Village Vanguard","Village Vanguard",R.string.dspselkey_village_vanguard));
				Dsps.add(new ListContentItem("Warehouse Loft","Warehouse Loft",R.string.dspselkey_warehouse_loft));
				Dsps.add(new ListContentItem("Cellar Club","Cellar Club",R.string.dspselkey_cellar_club));
				Dsps.add(new ListContentItem("The Roxy Theatre","The Roxy Theatre",R.string.dspselkey_the_roxy_theatre));
				Dsps.add(new ListContentItem("The Bottom Line","The Bottom Line",R.string.dspselkey_the_bottom_line));
				Dsps.add(new ListContentItem("Sports","Sports",R.string.dspselkey_sports));
				Dsps.add(new ListContentItem("Action Game","Action Game",R.string.dspselkey_action_game));
				Dsps.add(new ListContentItem("Roleplaying Game","Roleplaying Game",R.string.dspselkey_roleplaying_game));
				Dsps.add(new ListContentItem("Music Video","Music Video",R.string.dspselkey_music_video));
				Dsps.add(new ListContentItem("Recital/Opera","Recital/Opera",R.string.dspselkey_recitalopera));
				Dsps.add(new ListContentItem("Standard","Standard",R.string.dspselkey_standard));
				Dsps.add(new ListContentItem("Spectacle","Spectacle",R.string.dspselkey_spectacle));
				Dsps.add(new ListContentItem("Sci-Fi","Sci-Fi",R.string.dspselkey_scifi));
				Dsps.add(new ListContentItem("Adventure","Adventure",R.string.dspselkey_adventure));
				Dsps.add(new ListContentItem("Drama","Drama",R.string.dspselkey_drama));
				Dsps.add(new ListContentItem("Mono Movie","Mono Movie",R.string.dspselkey_mono_movie));
				Dsps.add(new ListContentItem("2ch Stereo","2ch Stereo",R.string.dspselkey_2ch_stereo));
				Dsps.add(new ListContentItem("7ch Stereo","7ch Stereo",R.string.dspselkey_7ch_stereo));
				Dsps.add(new ListContentItem("Straight Enhancer","Straight Enhancer",R.string.dspselkey_straight_enhancer));
				Dsps.add(new ListContentItem("7ch Enhancer","7ch Enhancer",R.string.dspselkey_7ch_enhancer));
				Dsps.add(new ListContentItem("Surround Decode","Surround Decode",R.string.dspselkey_surround_decode));
			}
		}
	}
	
	public static void SetInputs(Context ctx)
	{
		String model = GetModel (ctx);
		Inputs = new ListContent ();
		
		if (model != null)
		{
			if (model.contains ("2065") || model.contains ("67") || model.contains("RX-A") || model.contains("6295"))
			{
				Inputs.add(new ListContentItem("HDMI1"     ,"HDMI 1"	,R.string.inputsel_hdmi1));
				Inputs.add(new ListContentItem("HDMI2"     ,"HDMI 2"	,R.string.inputsel_hdmi2));
				Inputs.add(new ListContentItem("HDMI3"     ,"HDMI 3"	,R.string.inputsel_hdmi3));
				Inputs.add(new ListContentItem("HDMI4"     ,"HDMI 4"	,R.string.inputsel_hdmi4));
				Inputs.add(new ListContentItem("AV1"       ,"AV 1"   	,R.string.inputsel_av1));
				Inputs.add(new ListContentItem("AV2"       ,"AV 2"   	,R.string.inputsel_av2));
				Inputs.add(new ListContentItem("AV3"       ,"AV 3"   	,R.string.inputsel_av3));
				Inputs.add(new ListContentItem("AV4"       ,"AV 4"   	,R.string.inputsel_av4));
				Inputs.add(new ListContentItem("AV5"       ,"AV 5"   	,R.string.inputsel_av5));
				Inputs.add(new ListContentItem("AV6"       ,"AV 6"   	,R.string.inputsel_av6));
				Inputs.add(new ListContentItem("Audio1"    ,"Audio 1" 	,R.string.inputsel_audio1));
				Inputs.add(new ListContentItem("Audio2"    ,"Audio 2" 	,R.string.inputsel_audio2));
				Inputs.add(new ListContentItem("NET RADIO" ,"NET RADIO" ,R.string.inputsel_netradio));    
				Inputs.add(new ListContentItem("TUNER"     ,"Tuner"		,R.string.inputsel_tuner));
				Inputs.add(new ListContentItem("V-AUX"     ,"V-AUX"		,R.string.inputsel_vaux));        
				Inputs.add(new ListContentItem("PHONO"     ,"Phono"		,R.string.inputsel_phono));
				Inputs.add(new ListContentItem("MULTI CH"  ,"MULTI CH"	,R.string.inputsel_multicha));		
				Inputs.add(new ListContentItem("DOCK"      ,"DOCK"		,R.string.inputsel_dock));       
				Inputs.add(new ListContentItem("USB"       ,"USB"		,R.string.inputsel_usb));
				Inputs.add(new ListContentItem("XM"        ,"XM"		,R.string.inputsel_xm));
				Inputs.add(new ListContentItem("PC"        ,"PC"     	,R.string.inputsel_pc));
			}
			else
			{
				Inputs.add(new ListContentItem("BD/HD DVD" ,"BD/HD DVD"	,R.string.inputsel_bdhddvd));     
				Inputs.add(new ListContentItem("VCR"       ,"VCR"		,R.string.inputsel_vcr));        
				Inputs.add(new ListContentItem("CBL/SAT"   ,"CBL/SAT"	,R.string.inputsel_cbl_sat));    
				Inputs.add(new ListContentItem("TUNER"     ,"Tuner"		,R.string.inputsel_tuner));       
				Inputs.add(new ListContentItem("TV"        ,"Tv"		,R.string.inputsel_tv));          
				Inputs.add(new ListContentItem("V-AUX"     ,"V-AUX"		,R.string.inputsel_vaux));        
				Inputs.add(new ListContentItem("CD"        ,"CD"		,R.string.inputsel_cd));         
				Inputs.add(new ListContentItem("DVR"       ,"DVR"		,R.string.inputsel_dvr));         
				Inputs.add(new ListContentItem("DOCK"      ,"DOCK"		,R.string.inputsel_dock));       
				Inputs.add(new ListContentItem("DVD"       ,"DVD"		,R.string.inputsel_dvd));        
				Inputs.add(new ListContentItem("MD/CD-R"   ,"MD/CD-R"	,R.string.inputsel_md_cdr));      
				Inputs.add(new ListContentItem("PHONO"     ,"Phono"		,R.string.inputsel_phono));
				Inputs.add(new ListContentItem("MULTI CH"  ,"MULTI CH"	,R.string.inputsel_multicha));		
				Inputs.add(new ListContentItem("PC/MCX"    ,"PC/MCX"	,R.string.inputsel_pc_mcx));
				Inputs.add(new ListContentItem("NET RADIO" ,"NET RADIO" ,R.string.inputsel_netradio));    
				Inputs.add(new ListContentItem("USB"       ,"USB"		,R.string.inputsel_usb));
				Inputs.add(new ListContentItem("Bluetooth" ,"Bluetooth"	,R.string.inputsel_bluetooth));   
				Inputs.add(new ListContentItem("XM"        ,"XM"		,R.string.inputsel_xm));
				Inputs.add(new ListContentItem("iPod"      ,"iPod"		,R.string.inputsel_ipod));        
				Inputs.add(new ListContentItem("Rhapsody"  ,"Rhapsody"	,R.string.inputsel_rhapsody));    
				Inputs.add(new ListContentItem("SIRIUS InternetRadio","Sirius", R.string.inputsel_sirius_internet));			
			}
		}
	}
	
	public static ListContent GetInputs(Context ctx)
	{
		if (Inputs == null)
			SetInputs (ctx);
		return Inputs;			
	}
	
	public static ListContent GetDsp(Context ctx)
	{
		if (Dsps == null)
			SetDsp (ctx);
		return Dsps;			
	}

	public static void SetReceiverAvailability (boolean avail, Context ctx)
	{
		boolean previous = IsReceiverAvailable (ctx);

		Log.v (TAG, "SetReceiverAvailability " + avail + " previous " + previous);
		SharedPreferenceHelper.EditBoolPreference (receiverAvailabilityKey, avail, ctx);

		TestWidget.StartTheService (ctx);
	}

	public static boolean IsReceiverAvailable (Context ctx)
	{
		return SharedPreferenceHelper.GetBoolPreference (receiverAvailabilityKey, false, ctx);
	}

	public static String ZoneInputSelectKey (Context ctx)
	{
		return GetCurZoneName (ctx) + ".Input.Input_Sel";
	}

	public static void SetInput (Context ctx, String input)
	{
		SharedPreferenceHelper.EditStringPreference (ZoneInputSelectKey (ctx), input, "", ctx);
		new OnMethodsWrapper (GetCurZoneName (ctx), CommandEnum.WriteZone_Input_Sel, GetHandler (ctx), ctx).execute ();
	}

	public static String ZoneDspSelectKey (Context ctx)
	{
		return GetCurZoneName (ctx) + ".Basic_Status.Surr.Pgm_Sel.Pgm";
	}

	public static void SetDsp (Context ctx, String dsp)
	{
		SharedPreferenceHelper.EditStringPreference (ZoneDspSelectKey (ctx), dsp, "", ctx);
		new OnMethodsWrapper (GetCurZoneName (ctx), CommandEnum.WriteZone_Dsp_Sel, GetHandler (ctx), ctx).execute ();
	}

	public static void CheckReceiverAvailability (final Context ctx)
	{
		Log.v (TAG, "Checking Receiver Availability");
		new AsyncTask<Void, Void, Void> ()
		{
			@Override
			protected Void doInBackground (Void... params)
			{				
				try
				{
					// android.os.Debug.waitForDebugger ();
					boolean res = OnMethods.ReadTestMessage (ctx);
					SetReceiverAvailability (res, ctx);
				}
				catch (Exception e)
				{
					SetReceiverAvailability (false, ctx);
				}
				return null;
			}
		}.execute ((Void []) null);
	}

	public static void ReadSystemBasicStatus (Context ctx, Handler handler)
	{
		new OnMethodsWrapper (null, CommandEnum.Read_SystemPowerControl, handler, ctx).execute ();
	}

	public static String ZoneVolumeKey (Context ctx)
	{
		return GetCurZoneName (ctx) + ".Basic_Status.Vol.Lvl.Val";
	}

	public static int GetVolume (Context ctx)
	{
		String vol = SharedPreferenceHelper.GetStringPreference (ZoneVolumeKey (ctx), "-800", ctx);

		Log.v (TAG, "Volume is " + vol);

		int volume = 0;
		try
		{
			volume = Integer.valueOf (vol);
		}
		catch (NumberFormatException e)
		{
			Log.e (TAG, "Error parsing volume " + e.getMessage ());
			volume = MinVolume;
		}

		return volume;
	}

	public static void LoadMemory (Context ctx, String mem)
	{
		SharedPreferenceHelper.EditStringPreference (GetCurZoneName (ctx) + ".Sys_Mem.Load", mem, "", ctx);
		new OnMethodsWrapper (GetCurZoneName (ctx), CommandEnum.WriteZone_Load_Memory, GetHandler (ctx), ctx).execute ();
	}

	public static void SaveMemory (Context ctx, String mem)
	{
		SharedPreferenceHelper.EditStringPreference (GetCurZoneName (ctx) + ".Sys_Mem.Save", mem, "", ctx);
		new OnMethodsWrapper (GetCurZoneName (ctx), CommandEnum.WriteZone_Save_Memory, GetHandler (ctx), ctx).execute ();
	}

	// null if not avail
	public static String GetZoneDisplayName (int zoneIdx, Context ctx)
	{
		return SharedPreferenceHelper.GetStringPreference (ZoneNames[zoneIdx], null, ctx);
	}

	public static boolean LoadReceiverInfo (Context ctx)
	{
		// invalidate receiver
		SharedPreferenceHelper.EditBoolPreference (receiverValidKey, false, ctx);

		// invalidate zones
		for (int i = 0; i < ZoneNames.length; i++)
		{
			SharedPreferenceHelper.EditStringPreference (ZoneNames[i], null, ctx);
		}

		HttpResponse response;
		try
		{
			response = OnMethods.ReadSystemConfig (ctx);

			int code = response.getStatusLine ().getStatusCode ();
			if (code != 200)
			{
				BugReporter.addCustomData("response code", Integer.toString(code), ctx);
				BugReporter.handleSilentException(new Exception("Failed to read system config"), ctx);
				
				Log.d("GetZoneDisplayName", "Failed  to read system config");
				return false;
			}
		}
		catch (Exception e)
		{		
			BugReporter.addCustomData("LoadReceiverInfo ex", e.getMessage(), ctx);
			BugReporter.handleSilentException(e, ctx);
			Log.v (TAG, "Error loading receiver info: " + e.getMessage ());
			return false;
		}

		for (int i = 0; i < ZoneNames.length; i++)
		{
			LoadReceiverZoneInfo (i, ctx);
		}

		List<Zone> zones = GetZonesFromPreferences (ctx);
		if (zones.size () > 0)
		{
			SharedPreferenceHelper.EditBoolPreference (receiverValidKey, true, ctx);
			return true;
		}
		else
		{
			Log.d("LoadReceiverInfo", "Receiver is not valid as there are no available zones");
		}
		return false;
	}

	public static void ReportError(boolean error, int code, String msg, String zoneName, Context ctx)
	{
		if (error)
		{
			BugReporter.addCustomData("zone", zoneName, ctx);
			BugReporter.addCustomData("code", Integer.toString(code), ctx);
			BugReporter.handleSilentException(new Exception(msg), ctx);
		}		
	}
	
	// synchronous call to read zone info
	public static void LoadReceiverZoneInfo (int zoneIdx, Context ctx)
	{
		String zoneName = ZoneNames[zoneIdx];
		Zone tmp = new Zone (zoneName, zoneName);

		// Execute the command to see if the Zone is available on the receiver
		boolean error = true;
		int code = 0;
	
		try
		{
			HttpResponse r;
			// Main_Zone is obligatory
			if (zoneIdx != 0)
			{
				r = OnMethods.ReadZone_Config (zoneName, ctx);
				code = r.getStatusLine ().getStatusCode ();
				
				error = (code != 200 || SharedPreferenceHelper.GetStringPreference (tmp.Name + ".Config.Device", "", ctx).compareTo ("Ready") != 0);
				// TODO what about a standby zone?
			}
			else
			{
				error = false;
			}
			
			ReportError(error, code, SharedPreferenceHelper.GetStringPreference (tmp.Name + ".Config.Device", "", ctx),zoneName, ctx);

			if (!error)
			{
				r = OnMethods.Read_ZoneName (zoneName, ctx);
				code = r.getStatusLine ().getStatusCode ();
				error =  code != 200;
				
				ReportError(error, code, "Read zone name", zoneName, ctx); 
			}

			if (!error)
			{
				r = OnMethods.Read_ZoneBasic_Status (zoneName, ctx);
				code = r.getStatusLine ().getStatusCode ();
				error =  code != 200;

				ReportError(error, code, "Read zone status", zoneName, ctx);
			}
			else
			{
				ReportError(error, -1, "Zone reading failed", zoneName, ctx);
				Log.d("LoadReceiverZoneInfo", "Failed to read zone name");
			}

			if (!error)
			{
				String zoneDisplayName = SharedPreferenceHelper.GetStringPreference (zoneName + ".ZoneName", null, ctx);

				if (zoneDisplayName != null)
				{
					Log.v (TAG, "New Zone added=>" + zoneName + " Display Name => " + zoneDisplayName);
					SharedPreferenceHelper.EditStringPreference (zoneName, zoneDisplayName, ctx);
				}
				else
				{
					error = true;
					ReportError(error, -2, "couldnt read custom zone Name", zoneName, ctx);
					Log.e (TAG, "Error: couldnt read custom zone Name!");
				}
			}
			else
			{
				ReportError(true, -3, "Failed to get zone basic status", zoneName, ctx);
				Log.d("LoadReceiverZoneInfo", "Failed to get zone basic status");
			}
		}
		catch (Exception e)
		{
			BugReporter.handleSilentException(e,ctx);
			Log.e (TAG, "Error reading zone info of " + zoneName + ": " + e.getMessage ());
			error = true;
		}

		if (error)
		{
			Log.v (TAG, "Zone not available, clearing zone: " + zoneName);
			ReportError(true, -4, "Zone not available, clearing zone:", zoneName, ctx);
			SharedPreferenceHelper.EditStringPreference (zoneName, null, ctx);
		}
	}

	public static List<Zone> GetZonesFromPreferences (Context ctx)
	{
		List<Zone> zones = new ArrayList<Zone> ();

		for (int i = 0; i < ZoneNames.length; i++)
		{
			String zoneName = ZoneNames[i];
			String zoneDisplayName = GetZoneDisplayName (i, ctx);

			if (zoneDisplayName != null)
			{
				Zone z = new Zone (zoneDisplayName, zoneName);
				z.ZoneId = i;
				zones.add (z);
			}
		}
		return zones;
	}

	public static boolean LoadCurrentZoneVolume (Context ctx)
	{
		try
		{
			HttpResponse r;
			// Main_Zone is obligatory
			r = OnMethods.Read_ZoneVolume (GetCurZoneName (ctx), ctx);
			return (r.getStatusLine ().getStatusCode () == 200);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean Valid (Context ctx)
	{
		boolean valid = SharedPreferenceHelper.GetBoolPreference (receiverValidKey, false, ctx);

		Log.v (TAG, "Receiver valid value " + valid);

		return (GetModel (ctx) != null) && valid;
	}

	public static String GetModel (Context ctx)
	{
		return SharedPreferenceHelper.GetStringPreference (receiverModelKey, null, ctx);
	}

	public static void SetModel (Context ctx, String model)
	{
		Log.d("TAG", "Setting receiver model to: " + model);
		SharedPreferenceHelper.EditStringPreference (receiverModelKey, model, ctx);
		SetInputs(ctx);
	}

	public static String GetCurZoneName (Context ctx)
	{
		return ZoneNames[GetZoneIdx (ctx)];
	}

	public static int GetZoneIdx (Context ctx)
	{
		return SharedPreferenceHelper.GetIntPreference (zoneNumberKey, 0, ctx);
	}

	// 0 based
	public static void SetZoneIdx (Context ctx, int zoneIdx)
	{
		SharedPreferenceHelper.EditIntPreference (zoneNumberKey, zoneIdx, ctx);
		Log.v (TAG, "Zone " + zoneIdx);
	}

	public static void ReadSystemConfig (Context ctx)
	{
		ExecuteCommandAsync (CommandEnum.Read_SystemConfig, ctx);
	}

	public static void ExecuteCommandAsync (CommandEnum cmd, Context ctx)
	{
		try
		{
			new OnMethodsWrapper (GetCurZoneName (ctx), cmd, GetHandler (ctx), ctx).execute ();
		}
		catch (Exception e)
		{
			Log.e (TAG, "ExecuteCommandAsync Exception: " + e.getMessage ());
		}
	}

	public static void VolumeUp (Context ctx)
	{
		new OnMethodsWrapper (GetCurZoneName (ctx), CommandEnum.WriteZone_VolUp, GetHandler (ctx), ctx).execute ();
	}

	public static void VolumeDown (Context ctx)
	{
		new OnMethodsWrapper (GetCurZoneName (ctx), CommandEnum.WriteZone_VolDown, GetHandler (ctx), ctx).execute ();
	}

	public static void VolumeUpSynchronous (Context ctx) throws Exception
	{
		OnMethods.Write_ZoneVolUp (GetCurZoneName (ctx), ctx);
	}

	// synchronous
	public static void VolumeDownSynchronous (Context ctx) throws Exception
	{
		OnMethods.Write_ZoneVolDown (GetCurZoneName (ctx), ctx);
	}

	public static int GetZoneMaxVolume (Context ctx)
	{
		String key = "zonemaxvol" + GetZoneIdx (ctx);
		return SharedPreferenceHelper.GetIntPreference (key, DefaultZoneMaxVolume, ctx);
	}

	public static boolean CanSetToVolume (int vol, Context ctx)
	{
		return (vol >= MinVolume && vol <= MaxVolume && vol <= GetZoneMaxVolume (ctx));
	}

	public static boolean MaxVolumeReached (int vol, Context ctx)
	{
		return (vol >= MaxVolume || vol >= GetZoneMaxVolume (ctx));
	}

	// Syncronous
	public static void SetVolume (int vol, Context ctx) throws Exception
	{
		if (CanSetToVolume (vol, ctx))
		{
			OnMethods.Write_ZoneVolLvl (vol, GetCurZoneName (ctx), ctx);
		}
	}
	
	public static void ToggleZonePower (Context ctx)
	{
		String zonePowerKey = GetZonePowerKey (ctx);
		String power = SharedPreferenceHelper.GetStringPreference (zonePowerKey, null, ctx);
		if (power == null)
		{
			Log.e (TAG, "ToggleZonePower null");
			return;
		}

		if (power.compareTo ("On") == 0)
		{
			power = "Standby";
		}
		else if (power.compareTo ("Standby") == 0) 
		{
			power = "On";
		}
		else
		{
			Log.e (TAG, "ToggleZonePower value");
			return;
		}
		
		Log.v (TAG, "ZONE_POWER " + power);

		SharedPreferenceHelper.EditStringPreference (zonePowerKey, power, ctx);
		ExecuteCommandAsync (CommandEnum.WriteZone_PowerControl, ctx);
	}

	private static String GetZonePowerKey (Context ctx)
	{
		return GetCurZoneName (ctx) + ".Basic_Status.Power_Control.Power";
	}
}

package bsharps.yamahawidget.receiver;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class OnMethodsWrapper extends AsyncTask<String, Void, HttpResponse>
{
	String zoneName;
	CommandEnum command;
	Context ctx;
	Handler handler;
	Exception error;
	static String TAG = "OnMethodsWrapper";

	public OnMethodsWrapper (String zoneName, CommandEnum cmd, Handler handler, Context ctx)
	{
		this.zoneName = zoneName;
		this.command = cmd;
		this.ctx = ctx;
		this.handler = handler;
	}

	public void execute ()
	{
		execute ((String []) null);
	}

	@Override
	protected HttpResponse doInBackground (String... nix)
	{
		try
		{
			return doIt ();
		}
		catch (Exception e)
		{
			error = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute (HttpResponse result)
	{
		super.onPostExecute (result);

		if (handler != null)
		{
			Message msg = new Message ();
			msg.obj = result;
			handler.sendMessage (msg);
		}
	}

	private HttpResponse doIt () throws Exception
	{
		switch (command)
		{
			case Read_SystemConfig:
				return OnMethods.ReadSystemConfig (ctx);

			case Read_SystemNetworkInfo:
				return OnMethods.ReadSystemNetworkInfo (ctx);

			case Read_SystemNetworkSetting:
				return OnMethods.ReadSystemNetworkSetting (ctx);

			case Read_SystemPowerControl:
				return OnMethods.ReadSystemPower_ControlPower (ctx);

			case Read_ZoneBasicStatus:
				return OnMethods.Read_ZoneBasic_Status (zoneName, ctx);

			case Read_ZoneConfig:
				return OnMethods.ReadZone_Config (zoneName, ctx);

			case Read_ZoneName:
				return OnMethods.Read_ZoneName (zoneName, ctx);

			case Read_ZoneVolume:
				return OnMethods.Read_ZoneVolume (zoneName, ctx);
				
			case Read_ZoneInputInput_Sel:
				return OnMethods.Read_ZoneInputInput_Sel (zoneName, ctx);

			case Read_ZonePowerControl:
				return OnMethods.Read_ZonePower_ControlPower (zoneName, ctx);

			case WriteZone_PowerControl:
				return OnMethods.WriteZone_Power_ControlPower (zoneName, ctx);

			case WriteZone_VolUp:
				return OnMethods.Write_ZoneVolUp (zoneName, ctx);

			case WriteZone_VolDown:
				return OnMethods.Write_ZoneVolDown (zoneName, ctx);

			case WriteZone_Input_Sel:
				return OnMethods.WriteZone_Input_Sel (zoneName, ctx);

			case WriteZone_Dsp_Sel:
				return OnMethods.WriteZone_Dsp_Sel (zoneName, ctx);

			case WriteZone_Load_Memory:
				return OnMethods.WriteZone_Load_Memory (zoneName, ctx);

			case WriteZone_Save_Memory:
				return OnMethods.WriteZone_Save_Memory (zoneName, ctx);
				
			default:
				Log.e (TAG, "Invalid command");
				throw new Exception ("Invalid command");
		}
	}
}

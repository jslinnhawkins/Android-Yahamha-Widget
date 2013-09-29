package bsharps.yamahawidget.receiver;

import org.acra.ErrorReporter;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.util.Log;
import bsharps.yamahawidget.main.BugReporter;
import bsharps.yamahawidget.util.HttpClientUtil;
import bsharps.yamahawidget.util.SharedPreferenceHelper;
import bsharps.yamahawidget.util.XmlResponseDecoder;

// TODO put in receiver ns
public class OnMethods
{
	static int decodecount = 0;
	
	public static void LogResponse (HttpResponse Response, String Command, Context ctx)
	{
		if (BugReporter.Enabled (ctx))
		{
			Header [] responses = Response.getHeaders ("Response");
			String responseStr = null;
			if (responses.length > 0) responseStr = responses[0].getValue ();
	
			if (responseStr != null && responseStr.length () > 0)
			{
				BugReporter.addCustomData( Integer.toString(decodecount) , Command + " response: " + responseStr, ctx);
				decodecount++;
			}
		}
	}
	
	public static HttpResponse ReadSystemConfig (Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><System><Config>GetParam</Config></System></YAMAHA_AV>"));
		SharedPreferenceHelper.EditStringPreference ("System.Config.Model_Name", SharedPreferenceHelper.DecodeStringResponse (response, "System.Config.Model_Name"), "RX-V3900", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Config.System_ID", SharedPreferenceHelper.DecodeStringResponse (response, "System.Config.System_ID"), "00000000", ctx);
		LogResponse (response, "ReadSystemConfig", ctx);
		return response;
	}

	public static HttpResponse ReadSystemPower_ControlPower (Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><System><Power_Control><Power>GetParam</Power></Power_Control></System></YAMAHA_AV>"));
		SharedPreferenceHelper.EditStringPreference ("System.Power_Control.Power", SharedPreferenceHelper.DecodeStringResponse (response, "System.Power_Control.Power"), "On", ctx);
		LogResponse (response, "ReadSystemPower", ctx);
		return response;
	}

	public static boolean ReadTestMessage (Context ctx) throws Exception
	{
		HttpResponse r =  HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><System><Power_Control><Power>GetParam</Power></Power_Control></System></YAMAHA_AV>"));
		return (r.getStatusLine ().getStatusCode () == 200);
	}
	
	public static HttpResponse ReadSystemNetworkSetting (Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><System><Network><Setting>GetParam</Setting></Network></System></YAMAHA_AV>"));
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.Network_Standby", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.Network_Standby"), "No", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.DHCP", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.DHCP"), "On", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.IP_Address", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.IP_Address"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.Subnet_Mask", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.Subnet_Mask"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.Default_Gateway", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.Default_Gateway"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.DNS_Server_1", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.DNS_Server_1"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Setting.DNS_Server_2", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Setting.DNS_Server_2"), "0.0.0.0", ctx);
		return response;
	}

	public static HttpResponse ReadSystemNetworkInfo (Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><System><Network><Info>GetParam</Info></Network></System></YAMAHA_AV>"));
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.IP_Address", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.IP_Address"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.Subnet_Mask", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.Subnet_Mask"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.Default_Gateway", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.Default_Gateway"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.DNS_Server_1", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.DNS_Server_1"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.DNS_Server_2", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.DNS_Server_2"), "0.0.0.0", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.MAC_Address", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.MAC_Address"), "000000000000", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.Status", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.Status"), "100 Base-TX Full Duplex", ctx);
		SharedPreferenceHelper.EditStringPreference ("System.Network.Info.System_ID", SharedPreferenceHelper.DecodeStringResponse (response, "System.Network.Info.System_ID"), "00000000", ctx);
		return response;
	}

	// *****************************************************************************

	public static HttpResponse Read_ZoneBasic_Status (String zoneName, Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Basic_Status>GetParam</Basic_Status></%s></YAMAHA_AV>", zoneName, zoneName));
		
		SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Power_Control.Power", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Power_Control.Power"), "On", ctx);
		
		String model = SharedPreferenceHelper.GetStringPreference ("System.Config.Model_Name", "", ctx);
		//SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Power_Control.Sleep", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Power_Control.Sleep"), "Off", ctx);
		if (!model.contains ("671"))
		{
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Vol.Lvl.Val", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Vol.Lvl.Val"), "-800", ctx);
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Vol.Mute", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Vol.Mute"), "Off", ctx);
		}
		else
		{
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Vol.Lvl.Val", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Volume.Lvl.Val"), "-800", ctx);
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Vol.Mute", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Volume.Mute"), "Off", ctx);
		}
		//SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Input.Input_Sel", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Input.Input_Sel"), "TUNER", ctx);
		//SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Input.Input_Sel_Title", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Input.Input_Sel_Title"), "TUNER", ctx);
	
		LogResponse (response, "ReadZoneBasicStatus", ctx);
		return response;
	}

	public static HttpResponse Read_ZoneVolume (String zoneName, Context ctx) throws Exception
	{
		String model = SharedPreferenceHelper.GetStringPreference ("System.Config.Model_Name", "", ctx);
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Basic_Status>GetParam</Basic_Status></%s></YAMAHA_AV>", zoneName, zoneName));

		//SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Power_Control.Sleep", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Power_Control.Sleep"), "Off", ctx);
		if (!model.contains ("671"))
		{
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Vol.Lvl.Val", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Vol.Lvl.Val"), "-800", ctx);
		}
		else
		{
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Basic_Status.Vol.Lvl.Val", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Basic_Status.Volume.Lvl.Val"), "-800", ctx);
		}
		return response;
	}
	
	public static HttpResponse Read_ZonePower_ControlPower (String zoneName, Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Power_Control><Power>GetParam</Power></Power_Control></%s></YAMAHA_AV>", zoneName, zoneName));
		SharedPreferenceHelper.EditStringPreference (zoneName + ".Power_Control.Power", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Power_Control.Power"), "On", ctx);
		//SharedPreferenceHelper.EditStringPreference (zoneName + ".Power_Control.Sleep", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Power_Control.Sleep"), "Off", ctx);
		return response;
	}

	public static HttpResponse Read_ZoneInputInput_Sel (String zoneName, Context ctx) throws Exception
	{
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Input><Input_Sel>GetParam</Input_Sel></Input></%s></YAMAHA_AV>", zoneName, zoneName));
		SharedPreferenceHelper.EditStringPreference (zoneName + ".Input.Input_Sel", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Input.Input_Sel"), "TUNER", ctx);
		return response;
	}

	public static HttpResponse ReadZone_Config (String zoneName, Context ctx) throws Exception
	{
		// Device can be set to either "Not Exist" or "Ready"
		HttpResponse response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Config>GetParam</Config></%s></YAMAHA_AV>", zoneName, zoneName));
		
		String model = SharedPreferenceHelper.GetStringPreference ("System.Config.Model_Name", "", ctx);
		
		BugReporter.addCustomData("Model", model,ctx);
		
		if (!model.contains ("67") && !model.contains("RX-A"))
		{
			BugReporter.addCustomData("Checking zone config - not 67 or RX model",zoneName,ctx);
			SharedPreferenceHelper.EditStringPreference (zoneName + ".Config.Device", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Config.Device"), "Not Exist", ctx);
		}
		else
		{
			BugReporter.addCustomData("67 or RX model",zoneName,ctx);
			
			if (!model.contains ("671"))
			{
				String s  = SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Feature_Existence");
				if (s.contentEquals ("1"))
				{
					SharedPreferenceHelper.EditStringPreference (zoneName + ".Config.Device","Ready" , "Not Exist", ctx);
				}
				else
				{
					BugReporter.addCustomData("Does not exist",zoneName,ctx);
					SharedPreferenceHelper.EditStringPreference (zoneName + ".Config.Device","Not Exist" , "Not Exist", ctx);
				}
			}
			else
			{
				String s  = SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Feature_Availability");
				if (s.contentEquals ("Not Ready") || s.contentEquals("Ready"))
				{
					SharedPreferenceHelper.EditStringPreference (zoneName + ".Config.Device","Ready" , "Not Exist", ctx);
				}
				else
				{
					BugReporter.addCustomData("Does not exist",zoneName,ctx);
					SharedPreferenceHelper.EditStringPreference (zoneName + ".Config.Device","Not Exist" , "Not Exist", ctx);
				}
			}
		}
		return response;
	}

	public static HttpResponse Read_ZoneName (String zoneName, Context ctx) throws Exception
	{
		HttpResponse response;
		String model = SharedPreferenceHelper.GetStringPreference ("System.Config.Model_Name", "", ctx);
		
		if (model.contains ("67") || model.contains("RX-A"))
		{
			response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Config>GetParam</Config></%s></YAMAHA_AV>", zoneName, zoneName));
			SharedPreferenceHelper.EditStringPreference (zoneName + ".ZoneName", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Config.Name.Zone"), zoneName, ctx);
		}
		else if (!model.contains ("2065") && !model.contains("6295"))
		{
			response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Rename><Rename_Latin_1>GetParam</Rename_Latin_1></Rename></%s></YAMAHA_AV>", zoneName, zoneName));
			SharedPreferenceHelper.EditStringPreference (zoneName + ".ZoneName", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Rename.Rename_Latin_1"), zoneName, ctx);
		}
		else
		{
			response = HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"GET\"><%s><Rename>GetParam</Rename></%s></YAMAHA_AV>", zoneName, zoneName));
			SharedPreferenceHelper.EditStringPreference (zoneName + ".ZoneName", SharedPreferenceHelper.DecodeStringResponse (response, zoneName + ".Rename"), zoneName, ctx);
		}
		LogResponse (response, "ReadZoneName", ctx);
		return response;
	}

	// **************************************************************************************
	public static HttpResponse WriteZone_Power_ControlPower (String zoneName, Context ctx) throws Exception
	{
		String key = zoneName + ".Basic_Status.Power_Control.Power";

		String Zone__Power_Control_Power = SharedPreferenceHelper.GetStringPreference (key, "Standby", ctx);
		return HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"PUT\"><%s><Power_Control><Power>%s</Power></Power_Control></%s></YAMAHA_AV>", zoneName, Zone__Power_Control_Power, zoneName));
	}

	public static HttpResponse WriteZone_Dsp_Sel (String zoneName, Context ctx) throws Exception
	{
		String key = zoneName + ".Basic_Status.Surr.Pgm_Sel.Pgm";
		String Main_Zone_Surr_Pgm_Sel_Pgm = SharedPreferenceHelper.GetStringPreference(key,"Surround Decode",ctx);
		String model = SharedPreferenceHelper.GetStringPreference ("System.Config.Model_Name", "", ctx);
		
		if (model.contains ("67") || model.contains("RX-A"))
		{
			HttpResponse response = HttpClientUtil.SendCommand ("<YAMAHA_AV cmd=\"GET\"><Main_Zone><Surround><Program_Sel><Current>GetParam</Current></Program_Sel></Surround></Main_Zone></YAMAHA_AV>");
			String straight = SharedPreferenceHelper.DecodeStringResponse (response, "Main_Zone.Surround.Program_Sel.Current.Straight");
			String enhancer = SharedPreferenceHelper.DecodeStringResponse (response, "Main_Zone.Surround.Program_Sel.Current.Enhancer");
			
			return HttpClientUtil.SendCommand(String.format("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Surround><Program_Sel><Current><Straight>%s</Straight><Enhancer>%s</Enhancer><Sound_Program>%s</Sound_Program></Current></Program_Sel></Surround></Main_Zone></YAMAHA_AV>",straight,enhancer,Main_Zone_Surr_Pgm_Sel_Pgm));
		}
		else
		{
			return HttpClientUtil.SendCommand(String.format("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Surr><Pgm_Sel><Straight>%s</Straight><Pgm>%s</Pgm></Pgm_Sel></Surr></Main_Zone></YAMAHA_AV>","Off",Main_Zone_Surr_Pgm_Sel_Pgm));
		}
	}
	
	public static HttpResponse Write_ZoneVolUp (String zoneName, Context ctx) throws Exception
	{
		return HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"PUT\"><%s><Vol><Up_Down>Up</Up_Down></Vol></%s></YAMAHA_AV>", zoneName, zoneName));
	}

	public static HttpResponse Write_ZoneVolDown (String zoneName, Context ctx) throws Exception
	{
		return HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"PUT\"><%s><Vol><Up_Down>Down</Up_Down></Vol></%s></YAMAHA_AV>", zoneName, zoneName));
	}

	public static HttpResponse Write_ZoneVolLvl (int vol, String zoneName, Context ctx) throws Exception
	{
		String model = SharedPreferenceHelper.GetStringPreference ("System.Config.Model_Name", "", ctx);
		
		if (model.contains ("67") || model.contains("RX-A"))
		{
			return HttpClientUtil.SendCommand(String.format("<YAMAHA_AV cmd=\"PUT\"><%s><Volume><Lvl><Val>%d</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Volume></%s></YAMAHA_AV>",zoneName, vol, zoneName));
		}
		else
		{
			return HttpClientUtil.SendCommand(String.format("<YAMAHA_AV cmd=\"PUT\"><%s><Vol><Lvl><Val>%d</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Vol></%s></YAMAHA_AV>",zoneName, vol, zoneName));
		}
	}
	
	public static HttpResponse WriteZone_Input_Sel (String zoneName, Context ctx) throws Exception
	{
		String Zone_Input_Sel = SharedPreferenceHelper.GetStringPreference (zoneName + ".Input.Input_Sel", "NET RADIO", ctx);
		return HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"PUT\"><%s><Input><Input_Sel>%s</Input_Sel></Input></%s></YAMAHA_AV>", zoneName, Zone_Input_Sel, zoneName));
	}
	
	public static HttpResponse WriteZone_Load_Memory (String zoneName, Context ctx) throws Exception
	{
		String mem = SharedPreferenceHelper.GetStringPreference (zoneName + ".Sys_Mem.Load", "Mem1", ctx);
		return HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"PUT\"><%s><Sys_Mem><Load>%s</Load></Sys_Mem></%s></YAMAHA_AV>", zoneName, mem, zoneName));
	}
	
	public static HttpResponse WriteZone_Save_Memory (String zoneName, Context ctx) throws Exception
	{
		String mem = SharedPreferenceHelper.GetStringPreference (zoneName + ".Sys_Mem.Save", "Mem1", ctx);
		return HttpClientUtil.SendCommand (String.format ("<YAMAHA_AV cmd=\"PUT\"><%s><Sys_Mem><Save>%s</Save></Sys_Mem></%s></YAMAHA_AV>", zoneName, mem, zoneName));
	}
}

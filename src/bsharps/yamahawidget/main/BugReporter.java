package bsharps.yamahawidget.main;

import org.acra.ErrorReporter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class BugReporter extends org.acra.CrashReportingApplication
{
	public static boolean Enabled(Context ctx)
	{
		boolean en = PreferenceManager.getDefaultSharedPreferences (ctx).getBoolean("enableErrorReporting", false);
		return en;
	}
	
	public static void addCustomData(String key, String value, Context ctx)
	{
		if (ctx != null)
		{
			if (Enabled(ctx))
				ErrorReporter.getInstance().addCustomData(key, value);	
		}
		else
		{
			ErrorReporter.getInstance().addCustomData(key, value);
		}
	}
	
	public static void handleSilentException(Exception e, Context ctx)
	{
		if (ctx != null)
		{
			if (Enabled(ctx))
				ErrorReporter.getInstance().handleSilentException(e);
		}
		else
		{
			Log.v("BUGREpORTTEr", ""+ErrorReporter.getInstance()+" " + e);
			ErrorReporter.getInstance().handleSilentException(e);			
		}
	}
	
	@Override
	public String getFormId() 
	{
		// TODO Auto-generated method stub
		return "dEUyTVZTWWFhUDBsNFhXSEI1NDRaYUE6MQ";
	}

}

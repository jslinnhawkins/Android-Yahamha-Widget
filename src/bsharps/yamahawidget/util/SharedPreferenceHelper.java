package bsharps.yamahawidget.util;

import org.acra.ErrorReporter;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import bsharps.yamahawidget.main.BugReporter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreferenceHelper
{
	// public static SharedPreferences PREFS;
	static String TAG = "SharedPreferenceHelper";

	public static void EditStringPreference (String key, String value, String defVal, Context ctx, Preference pref)
	{
		EditStringPreference (key, value, ctx);
	}

	public static String GetStringPreference (String key, String defVal, Context ctx, Preference pref)
	{
		return GetStringPreference (key, defVal, ctx);
	}

	public static SharedPreferences GetPrefs (Context ctx)
	{
		return PreferenceManager.getDefaultSharedPreferences (ctx);
	}

	public static SharedPreferences.Editor GetEditor (Context ctx)
	{
		return GetPrefs (ctx).edit ();
	}

	public static void EditBoolPreference (String key, boolean value, Context ctx)
	{
		GetEditor (ctx).putBoolean (key, value).commit ();
	}

	public static void EditStringPreference (String key, String value, Context ctx)
	{
		GetEditor (ctx).putString (key, value).commit ();
	}

	public static void EditIntPreference (String key, int value, Context ctx)
	{
		GetEditor (ctx).putInt (key, value).commit ();
	}

	// TODO remove and fx generator
	public static void EditStringPreference (String key, String value, String defVal, Context ctx)
	{
		GetEditor (ctx).putString (key, value).commit ();

	}

	public static String GetStringPreference (String key, String defVal, Context ctx)
	{
		return GetPrefs (ctx).getString (key, defVal);
	}

	public static byte GetBytePreference (String key, String defVal, Context ctx)
	{
		byte b = 0;
		try
		{
			b = (byte) Integer.parseInt (GetPrefs (ctx).getString (key, defVal));
		}
		catch (NumberFormatException e)
		{
			Log.e (TAG, e.getStackTrace ().toString ());
		}
		return b;
	}

	public static int GetIntPreference (String key, int defVal, Context ctx)
	{
		int b = GetPrefs (ctx).getInt (key, defVal);
		return b;
	}

	public static boolean GetBoolPreference (String key, boolean defVal, Context ctx)
	{
		return GetPrefs (ctx).getBoolean (key, defVal);
	}

	public static String DecodeStringResponse (HttpResponse response, String key) throws RuntimeException
	{
		Header [] responses = response.getHeaders ("Response");
		String responseStr = null;
		if (responses.length > 0) responseStr = responses[0].getValue ();

		if (responseStr != null && responseStr.length () > 0)
		{
			Log.d(TAG, responseStr);
			return XmlResponseDecoder.DecodeStringResponse (responseStr, key);
		}				
		return null;
	}

	public static void ClearAllPreferences (Context context)
	{
		SharedPreferences.Editor prefsEdit = GetEditor(context);
		prefsEdit.clear ();
		prefsEdit.commit ();
	}
}

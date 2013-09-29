package bsharps.yamahawidget.settings;

import java.util.ArrayList;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class ListContent extends ArrayList<ListContentItem>
{
	private static final long serialVersionUID = 1L;

	public void AddItemsAsCheckBoxListToMenu (PreferenceActivity a, String menu_key)
	{
		PreferenceScreen p = (PreferenceScreen) a.findPreference (menu_key);
		if (p== null)
		{
			Log.d("AddItemsAsCheckBoxListToMenu", "failed to find perference from key: " + menu_key);
			return;
		}
		p.removeAll ();
		Log.d("AddItemsAsCheckBoxListToMenu", p.toString ());
		
		if (size() == 0)
		{
			Preference p1 = new Preference(a);
			p1.setTitle ("Run Setup");
			p1.setSummary ("Please set your Receiver's IP address first");
			p1.setSelectable (false);
			p.addPreference (p1);			
		}
		else
		for (int i = 0; i < size (); i++)
		{
			CheckBoxPreference chk = new CheckBoxPreference (a);

			ListContentItem input = get (i);
			Log.d("AddItemsAsCheckBoxListToMenu", input.DisplayName);
			chk.setKey (a.getString (input.Key));
			chk.setTitle (input.DisplayName);
			chk.setDefaultValue (true);

			p.addPreference (chk);
		}
	}

	public ArrayList<ListContentItem> GetActiveItems (Context ctx)
	{
		ArrayList<ListContentItem> result = new ArrayList<ListContentItem> ();
		for (ListContentItem item : this)
		{
			if (PreferenceManager.getDefaultSharedPreferences (ctx).getBoolean (ctx.getString (item.Key), true)) 
				result.add (item);
		}
		return result;
	}
}

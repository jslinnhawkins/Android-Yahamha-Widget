package bsharps.yamahawidget.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import bsharps.yamahawidget.receiver.ReceiverService;
import bsharps.yamahawidget.settings.Globals;
import bsharps.yamahawidget.settings.ListContentItem;

public class InputSelectionDialog extends Activity implements DialogInterface.OnKeyListener, DialogInterface.OnClickListener
{
	private static final String TAG = "InputSelectionDialog";

	ArrayList<ListContentItem> items;

	AlertDialog alert = null;

	public void CloseWindow ()
	{
		Log.v (TAG, "Close Window");
		if (alert != null) alert.dismiss ();

		finish ();
	}

	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		// android.os.Debug.waitForDebugger();

		items = ReceiverService.GetInputs (this).GetActiveItems (this);

		AlertDialog.Builder builder = new AlertDialog.Builder (this);
		builder.setTitle ("Select Source");

		final LayoutInflater inflater = (LayoutInflater) this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

		builder.setAdapter (new ArrayAdapter<Object> (this, android.R.layout.select_dialog_item, items.toArray ())
		{
			@Override
			public View getView (int position, View convertView, ViewGroup parent)
			{
				View row;
				if (null == convertView)
				{
					row = inflater.inflate (android.R.layout.select_dialog_item, null);
				}
				else
				{
					row = convertView;
				}
				TextView tv = (TextView) row.findViewById (android.R.id.text1);
				tv.setText (getItem (position).toString ());

				return row;
			}

		}, this);

		alert = builder.create ();
		alert.show ();
		alert.setOnKeyListener (this);
		Log.d (TAG, "Finished in onCreate");
	}

	public void onClick (DialogInterface dialog, int item)
	{
		ReceiverService.SetInput (this, items.get (item).Name);
		Log.v (TAG, "Selected Input " + items.get (item));
		Toast.makeText (getApplicationContext (), items.get (item).toString (), Toast.LENGTH_SHORT).show ();

		CloseWindow ();
	}

	public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
		{
			CloseWindow ();
			return true;
		}
		return false;
	}
}

package bsharps.yamahawidget.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class SupportActivity extends Activity
{
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.support);
		
		findViewById(R.id.ButtonSendFeedback).setOnClickListener(new View.OnClickListener() 
		{
		    public void onClick(View v) 
		    {
				final EditText feedbackField = (EditText) findViewById (R.id.EditTextFeedbackBody);
				String feedback = feedbackField.getText ().toString ();

				final Spinner feedbackSpinner = (Spinner) findViewById (R.id.SpinnerFeedbackType);
				String feedbackType = feedbackSpinner.getSelectedItem ().toString ();

				// Take the fields and format the message contents
				String subject = formatFeedbackSubject (feedbackType);
				
				String message = formatFeedbackMessage (feedbackType, feedback);
				Log.v("email", "Subject " + message);

				sendFeedbackMessage (subject, message);
		    }
		});
	}

	protected String formatFeedbackSubject (String feedbackType)
	{
		String strFeedbackSubjectFormat = getResources ().getString (R.string.feedbackmessagesubject_format);
		String strFeedbackSubject = String.format (strFeedbackSubjectFormat, feedbackType);
		return strFeedbackSubject;
	}
	
	protected String formatFeedbackMessage (String feedbackType, String feedback)
	{
	    ComponentName comp = new ComponentName(this, ".TestWidget");
	    PackageInfo pinfo = null;
	    
		try
		{
			pinfo = this.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
		}
		catch (NameNotFoundException e)
		{
		}

		String strFeedbackFormatMsg = getResources ().getString (R.string.feedbackmessagebody_format);
		String strFeedbackMsg = String.format (strFeedbackFormatMsg,
											   getResources ().getString (R.string.app_name) + ":V" + pinfo.versionName,
											   feedbackType, 
											   feedback 
											   );
		return strFeedbackMsg;
	}

	public void sendFeedbackMessage (String subject, String message)
	{
		Intent messageIntent = new Intent (android.content.Intent.ACTION_SEND);

		String aEmailList[] =
		{
			"support@thumbmunkeys.com"
		};
		
		messageIntent.putExtra (android.content.Intent.EXTRA_EMAIL, aEmailList);
		messageIntent.putExtra (android.content.Intent.EXTRA_SUBJECT, subject);
		messageIntent.putExtra (android.content.Intent.EXTRA_TEXT, message);
		messageIntent.setType ("plain/text");

		startActivity(Intent.createChooser(messageIntent, "Send message using:"));
	}
}

package bsharps.yamahawidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.acra.ErrorReporter;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import bsharps.yamahawidget.main.BugReporter;

import android.util.Log;

public class HttpClientUtil
{
	private static String Ip;
	private static String Url;
	private static String TAG = "HttpClientUtil";

	public static String setIp (String ip)
	{
		Ip = ip;
		Url = "http://" + ip + "/YamahaRemoteControl/ctrl";
		return Url;
	}

	public static String getUrl ()
	{
		return Url;
	}

	public static String getIp ()
	{
		return Ip;
	}

	public static int GetIpAddressAsInt ()
	{
		InetAddress inetAddress;
		try
		{
			inetAddress = InetAddress.getByName (Ip);
		}
		catch (UnknownHostException e)
		{
			return -1;
		}
		byte [] addrBytes;
		int addr;
		addrBytes = inetAddress.getAddress ();
		addr = ((addrBytes[3] & 0xff) << 24) | ((addrBytes[2] & 0xff) << 16) | ((addrBytes[1] & 0xff) << 8) | (addrBytes[0] & 0xff);
		return addr;
	}

	public static String getResponseString (HttpResponse response)
	{
		// only decode accepted requests
		if (response.getStatusLine ().getStatusCode () != 200) return null;

		StringBuffer buffer = new StringBuffer ();
		InputStream inputStream = null;

		try
		{
			inputStream = response.getEntity ().getContent (); // get content to
			// a stream�..
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
		}
		int contentLength = (int) response.getEntity ().getContentLength (); // getting
		// content
		// length�..
		if (contentLength < 0)
		{
		}
		byte [] data = new byte [512];
		int len = 0;
		try
		{
			while (-1 != (len = inputStream.read (data)))
			{
				buffer.append (new String (data, 0, len)); // converting to
				// string and
				// appending to
				// stringbuffer�..
			}
		}
		catch (IOException e)
		{
			Log.e (TAG, e.getMessage ());
		}
		finally
		{
			try
			{
				inputStream.close (); // closing the stream�..
			}
			catch (IOException e)
			{
				Log.e (TAG, e.getMessage ());
			}
		}
		return buffer.toString ();

	}

	public static HttpResponse SendCommand (String url, String xml) throws Exception
	{
		Log.v (TAG, "SendCommand " + xml + " to " + url);

		HttpParams params = new BasicHttpParams ();
		HttpConnectionParams.setConnectionTimeout (params, 2000);
		HttpConnectionParams.setSoTimeout (params, 2000);
		params.setParameter (CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient (params);
		HttpPost httppost = new HttpPost (url);

		httppost.addHeader ("ContentType", "text/xml; charset=UTF-8");
		httppost.addHeader ("ContentLength", Integer.toString (xml.length ()));

		try
		{
			httppost.setEntity (new StringEntity (xml));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute (httppost);

			if (response.getStatusLine ().getStatusCode () == 200)
			{
				response.setHeader ("Response", getResponseString (response));
				Log.d (TAG, "Read: " + response.getStatusLine ().toString ());
			}
			else Log.e (TAG, "Error: " + response.getStatusLine ().toString ());

			return response;
		}
		catch (Exception e)
		{		
			Log.e (TAG, "Error Sending: " + e.getMessage ());
		}
		return null;
	}

	public static HttpResponse SendCommand (String xml) throws Exception
	{
		return SendCommand (Url, xml);
	}
}

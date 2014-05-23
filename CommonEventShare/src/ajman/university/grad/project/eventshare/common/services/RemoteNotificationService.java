package ajman.university.grad.project.eventshare.common.services;

import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import ajman.university.grad.project.eventshare.common.contracts.IRemoteNotificationService;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class RemoteNotificationService implements IRemoteNotificationService, Handler.Callback {
	private static final String LOG_TAG = "Remote Notification Service"; 
    private final static String HANDLER_THREAD_NAME = "ajman.university.grad.project.eventshare.common.services.RemoteNotificationServiceThread";

    HandlerThread _backgroundThread = null;
    Looper _looper;
    Handler _handler;

	@Override
	public void register(String appId, String appKey) {
        Parse.initialize(getApplicationContext(), appId, appKey);
		//PushService.setDefaultPushCallback(this, launchActivity.getClass());
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

	@Override
	public void unRegister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> retrieveSubscriptions() {
		return PushService.getSubscriptions(getApplicationContext());
	}

	@Override
	public void subscribe(List<String> channels, Activity launchActivity) {
		for (String channel : channels) {
			PushService.subscribe(getApplicationContext(), channel, launchActivity.getClass());
		}
	}

	@Override
	public void unSubscribe(String channel) {
		PushService.unsubscribe(getApplicationContext(), channel);
	}

	@Override
	public void sendPushNotification(String appId, String appKey, String channel, String message) {
		Log.d(LOG_TAG, "sendPushNotification - channel: " + channel + " - message: " + message);

		// Do not instantiate a background thread until we need it!!!
		if (_backgroundThread == null) {
			_backgroundThread = new HandlerThread(HANDLER_THREAD_NAME);
	        _backgroundThread.start();
	        _looper = _backgroundThread.getLooper();
	        _handler = new Handler(_looper, this);
        }

        // Queue up a message to the thread handler
		Message msgObj = _handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("command", "postParseAPI");
        b.putString("appId", appId);
        b.putString("appKey", appKey);
        b.putString("channel", channel);
        b.putString("notification", message);
        msgObj.setData(b);
        _handler.sendMessage(msgObj);
	}

    @Override
	public boolean handleMessage(Message message) {
		Log.d(LOG_TAG, "Remote Service handleMessage entry");
	
		if (message != null && message.getData() != null) {
	        String command = message.getData().getString("command");
			Log.d(LOG_TAG, "Remote Service handleMessage command: " + command);
	
			if (command != null && command.equals("postParseAPI")) {
				Log.d(LOG_TAG, "Procesing remote service handleMessage command: " + command);
	        	String appId = message.getData().getString("appId");
	        	String appKey = message.getData().getString("appKey");
	        	String channel = message.getData().getString("channel");
	        	String notification = message.getData().getString("notification");
	
	        	try {
	    			HttpClient client = new DefaultHttpClient();
	    		    HttpPost post = new HttpPost("https://api.parse.com/1/push");
	    	    	post.setHeader("X-Parse-Application-Id", appId);
	    	    	post.setHeader("X-Parse-REST-API-Key", appKey);
	    	    	post.setHeader("Content-Type", "application/json");
	    			    
	    	    	String json = "{\"channels\": [\"" + channel + "\"],\"data\": {\"alert\": \"" + notification + "\"}}";
	    			Log.d(LOG_TAG, "sendPushNotification json payload: " + json);
	    	    	StringEntity input = new StringEntity(json);
	    			input.setContentType("application/json");
	    			post.setEntity(input);
	
	    		    HttpResponse response = client.execute(post);
	    			Log.d(LOG_TAG, "sendPushNotification status code: " + response.getStatusLine().getStatusCode());
	
	    	    } catch (Exception e) {
	    	    	e.printStackTrace();
	    	    }
	        } else {
				Log.d(LOG_TAG, "Remote Service handleMessage command: " + command + " not recognized!");
	        }
		} else {
			Log.d(LOG_TAG, "Remote Service handleMessage message is null!");
		}
	
	    return true;
    }

	private Context getApplicationContext() {
		return ApplicationContextProvider.getContext();		
	}
}

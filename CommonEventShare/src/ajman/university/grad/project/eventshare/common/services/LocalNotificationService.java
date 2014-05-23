package ajman.university.grad.project.eventshare.common.services;


import ajman.university.grad.project.eventshare.common.contracts.ILocalNotificationService;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.models.Event;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


public class LocalNotificationService implements ILocalNotificationService {
	private static final String LOG_TAG = "LocalNotificationService";
	
	@Override
	public void showNotification(Event event) {
			// Get the launch activity so we can place it in the pending intent
	        PackageManager pm = getApplicationContext().getPackageManager();
	        Intent launchIntent = pm.getLaunchIntentForPackage(Constants.LAUNCH_PACKAGE_NAME);
	
	        // Let the notification get into the activity but make sure that the activity behaves well in back-pressed
	        // situation
	        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
	        taskStackBuilder.addNextIntentWithParentStack(launchIntent);
	        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	
	        String title = getEventTitle(event);
	        String subtitle = getEventSubtitle(event);
	        String message = getEventMessage(event);
	
	        // Add the Big Text Style
	        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
	        bigTextStyle.setBigContentTitle(title)
	            		.setSummaryText(message)
	            		.bigText(subtitle);
	        
	        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
	        builder.setContentTitle(title)
	               .setContentText(message)
	               .setContentIntent(pendingIntent)
	               .setAutoCancel(true)
	               .setSmallIcon(ajman.university.grad.project.eventshare.common.R.drawable.hdpi_75)
	               .setStyle(bigTextStyle);

	        Log.d(LOG_TAG, "Inside Notification");
	        Notification notification = builder.build();
	        NotificationManager mgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	        Log.d(LOG_TAG, "Inside Notification - mgr: " + mgr);
	        mgr.notify(this.getClass().getName(), event.getId(), notification);	
    }

    public void removeNotification(Event event) {
        NotificationManager mgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(this.getClass().getName(), event.getId());
    }

    private String getEventTitle(Event event) {
    	return event.getTitle().toString();
    }

    private String getEventSubtitle(Event event) {
    	return event.getLocation().toString();
    }

    private String getEventMessage(Event event) {
    	String time = event.getFromDayHour() + " : " + event.getFromMinute();
    	return time;
    }

    private Context getApplicationContext() {
		return ApplicationContextProvider.getContext();		
	}
}

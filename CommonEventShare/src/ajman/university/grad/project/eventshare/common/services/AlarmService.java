package ajman.university.grad.project.eventshare.common.services;

import java.util.Calendar;

import ajman.university.grad.project.eventshare.common.contracts.IAlarmService;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.system.services.CronService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmService implements IAlarmService{
	private static final String LOG_TAG = "AlarmService";
	
	@Override
	public void start() {
		Context context = getApplicationContext();
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(context, CronService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
        
        Log.d(LOG_TAG, "Inside AlarmService, start");
        // Start an alarm service to wake up our service every n seconds
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Constants.CRON_SERVICE_INTERVAL * 60 * 1000, pintent);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
	
	private Context getApplicationContext() {
		return ApplicationContextProvider.getContext();		
	}
}

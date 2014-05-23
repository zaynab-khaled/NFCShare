package ajman.university.grad.project.eventshare.common.system.services;

import java.util.List;

import ajman.university.grad.project.eventshare.common.contracts.ILocalNotificationService;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.models.Event;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CronService extends IntentService {
	private static final String LOG_TAG = "Cron Service"; 

	public CronService() {
		super("CronService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "Inside cron service");
		
		try {
			ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();
			ILocalNotificationService localNotificationService = ServicesFactory.getLocalNotificationService();
			
			List<Event> alarmableEvents = localStorageService.getExpiringAlarmableEvents(Constants.CRON_SERVICE_INTERVAL);
			for(Event event : alarmableEvents) {
				localNotificationService.showNotification(event);
			}
		} catch (Exception e) {
			
		} finally {
			//super.onHandleIntent(intent);
		}
	}
}

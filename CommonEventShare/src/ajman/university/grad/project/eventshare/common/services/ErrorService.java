package ajman.university.grad.project.eventshare.common.services;

import ajman.university.grad.project.eventshare.common.contracts.IErrorService;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import android.content.Context;
import android.util.Log;

public class ErrorService implements IErrorService {
	
	private static String LOG_TAG = "Error Service";

	@Override
	public void log(String message) {
		Log.d(LOG_TAG, message);
	}

	@Override
	public void log(Exception e) {
		Log.d(LOG_TAG, e.getMessage());
	}
	
	private Context getApplicationContext() {
		return ApplicationContextProvider.getContext();		
	}
}

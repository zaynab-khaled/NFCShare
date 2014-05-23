package ajman.university.grad.project.eventshare.user;

import ajman.university.grad.project.eventshare.common.contracts.IAlarmService;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.contracts.IRemoteNotificationService;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import ajman.university.grad.project.eventshare.helpers.Constants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static String LOG_TAG = "Main Activity";
	
	private ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();
	private IAlarmService alarmService = ServicesFactory.getAlarmService();
	
	Boolean registered;
	String userDept = localStorageService.getUserDepartment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
		
		alarmService.start();
		Log.d(LOG_TAG, "In the main activity splash screen: " + localStorageService.isRegistered());
		
		registered = localStorageService.isRegistered();
		
		Log.d(LOG_TAG, "Registered? " + localStorageService.isRegistered());
		if(registered){
			Intent intent = new Intent(MainActivity.this, ListActivity.class);
			startActivity(intent);
		} 
		else {
			Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
			startActivity(intent);
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override 
	public void onSaveInstanceState(Bundle savedInstanceState) { 
	  super.onSaveInstanceState(savedInstanceState); 
	  // Save UI state changes to the savedInstanceState. 
	  // This bundle will be passed to onCreate if the process is 
	  // killed and restarted. 

	  savedInstanceState.putBoolean("registered", true);
	  savedInstanceState.putString("userDepartment", localStorageService.getUserDepartment());
	  // etc. 
	}
	
	@Override 
	public void onRestoreInstanceState(Bundle savedInstanceState) { 
	  super.onRestoreInstanceState(savedInstanceState); 
	  // Restore UI state from the savedInstanceState. 
	  // This bundle has also been passed to onCreate. 

	  registered = savedInstanceState.getBoolean("registered");
	  userDept = savedInstanceState.getString("userDepartment");
	} 
	
	public void onResume() {
		super.onRestart();
		localStorageService.setRegistered(true);
	}
}

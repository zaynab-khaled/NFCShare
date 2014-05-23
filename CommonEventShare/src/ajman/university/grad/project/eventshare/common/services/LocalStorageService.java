package ajman.university.grad.project.eventshare.common.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.models.Event;
import ajman.university.grad.project.eventshare.common.db.*;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.helpers.Utils;

public class LocalStorageService implements ILocalStorageService {
	private static String LOG_TAG = "LocalStorageService";

	@Override
	public void addEvent(Event event) throws Exception {
		EventsDataSource ds = null;

		try {
			ds = new EventsDataSource(getApplicationContext());
			ds.open();
			ds.insert(event);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}
	}

	@Override
	public void updateEvent(Event event) throws Exception {
		EventsDataSource ds = null;

		try {
			ds = new EventsDataSource(getApplicationContext());
			ds.open();
			ds.delete(event);
			ds.insert(event);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}
	}

	@Override
	public void removeEvent(Event event) throws Exception {
		EventsDataSource ds = null;

		try {
			ds = new EventsDataSource(getApplicationContext());
			ds.open();
			ds.delete(event);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}
	}

	@Override
	public List<Event> getAllEvents() throws Exception {
		List<Event> events = new ArrayList<Event>();

		EventsDataSource ds = null;

		try {
			ds = new EventsDataSource(getApplicationContext());
			ds.open();
			events = ds.retrieveAll(1000);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}

		return events;
	}

	public int deleteDeclinedEvents() throws Exception {
		List<Event> events = getAllEvents();
		int count = 0;
		// Compare each event, compare the event's from date to current
		for (Event event : events) {
			if (isDeclined(event)) {
				Log.d(LOG_TAG, "Event name: " + event.getTitle() + " will be deleted!");
				removeEvent(event);
				count++;
			}
		}
		return count;
	}
	
	public boolean isDeclined(Event event) {

		Calendar toCal = Calendar.getInstance();
		toCal.set(Calendar.HOUR_OF_DAY, event.getToDayHour());
		toCal.set(Calendar.MINUTE, event.getToMinute());
		toCal.set(Calendar.YEAR, event.getFromYear());
		toCal.set(Calendar.MONTH, event.getFromMonth());
		toCal.set(Calendar.DAY_OF_MONTH, event.getFromDay());

		Calendar c = Calendar.getInstance();

		if (toCal.getTimeInMillis() < c.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	public int deleteAllEvents() throws Exception {
		List<Event> events = getAllEvents();
		int count = 0;
		// Compare each event, compare the event's from date to current
		for (Event event : events) {
				Log.d(LOG_TAG, "Event name: " + event.getTitle() + " will be deleted!");
				removeEvent(event);
				count++;
		}
		return count;
	}
	
	@Override
	public List<Event> filterByDoctorName(String docName) throws Exception {
		List<Event> events = new ArrayList<Event>();

		EventsDataSource ds = null;

		try {
			ds = new EventsDataSource(getApplicationContext());
			ds.open();
			events = ds.retrieveByDoctorName(docName);
			System.out.println("Local Storage filterByDoctorName [" + docName + "] " + events);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}

		return events;
	}

	@Override
	public List<Event> getExpiringAlarmableEvents(int minutes) throws Exception {	
		List<Event> alarmableEvents = new ArrayList<Event> ();
		List<Event> events = getAllEvents();
		for (Event event : events) {
			int minutesToEvent = Utils.getTimeDifferenceFromNow(event.getFromYear(), event.getFromMonth(), event.getFromDay(), event.getFromDayHour(), event.getFromMinute());
			Log.d(LOG_TAG, "Event alarmable flag: " + event.isAlarmable());
			Log.d(LOG_TAG, "Event minutes from now: " + minutesToEvent);
			if (event.isAlarmable() && minutesToEvent >= 0 && minutesToEvent <= minutes)	
				alarmableEvents.add(event);
		}
		return alarmableEvents;
	}

	@Override
	public void setUserPassword(String pwd) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USER_PASSWORD, pwd);
        editor.commit();
	}

	@Override
	public String getUserPassword() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(Constants.USER_PASSWORD, "");
	}

	@Override
	public void setAdminPassword(String pwd) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.ADMIN_PASSWORD, pwd);
        editor.commit();
	}

	@Override
	public String getAdminPassword() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(Constants.ADMIN_PASSWORD, "");
	}
	
	@Override
	public void setRegistered(boolean reg) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.REGISTERED, reg);
        editor.commit();
	}

	@Override
	public boolean isRegistered() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean(Constants.REGISTERED, false);
	}
	
	@Override
	public void setUserDepartment(String dept) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USER_DEPARTMENT, dept);
        editor.commit();
	}

	@Override
	public String getUserDepartment() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(Constants.USER_DEPARTMENT, "");
	}
	
	@Override
	public void setAdminDepartment(String dept) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.ADMIN_DEPARTMENT, dept);
        editor.commit();	
	}

	@Override
	public String getAdminDepartment() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(Constants.ADMIN_DEPARTMENT, "");
	}

	@Override
	public void setPushMessage(String msg) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PUSH_MESSAGE, msg);
        editor.commit();
	}

	@Override
	public String getPushMessage() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(Constants.PUSH_MESSAGE, "");
	}

	@Override
	public void setPushChannel(String chnl) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PUSH_CHANNEL, chnl);
        editor.commit();
	}

	@Override
	public String getPushChannel() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(Constants.PUSH_CHANNEL, "");
	}
	
	/*** PRIVATE METHODS */
	private Context getApplicationContext() {
		return ApplicationContextProvider.getContext();
	}
}

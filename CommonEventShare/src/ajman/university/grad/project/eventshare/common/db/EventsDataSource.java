package ajman.university.grad.project.eventshare.common.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ajman.university.grad.project.eventshare.common.models.Event;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class EventsDataSource {
	private final static String LOG_TAG = "Events Data Source";
	
	private SQLiteDatabase database;
	private EventsSqlLiteHelper dbHelper;
	private String [] allColumns = {
			EventsSqlLiteHelper.COLUMN_ID,
			EventsSqlLiteHelper.COLUMN_TITLE,
			EventsSqlLiteHelper.COLUMN_DEPT,
			EventsSqlLiteHelper.COLUMN_DESC,
			EventsSqlLiteHelper.COLUMN_LOCATION,
			EventsSqlLiteHelper.COLUMN_NAME_DOCTOR,
			EventsSqlLiteHelper.COLUMN_NAME_PATIENT,
			EventsSqlLiteHelper.COLUMN_FROM_DAY_HOUR,
			EventsSqlLiteHelper.COLUMN_FROM_MINUTE,
			EventsSqlLiteHelper.COLUMN_FROM_YEAR,
			EventsSqlLiteHelper.COLUMN_FROM_MONTH,
			EventsSqlLiteHelper.COLUMN_FROM_DAY,
			EventsSqlLiteHelper.COLUMN_TO_DAY_HOUR,
			EventsSqlLiteHelper.COLUMN_TO_MINUTE,
			EventsSqlLiteHelper.COLUMN_EXPIRED,
			EventsSqlLiteHelper.COLUMN_ALARMABLE
	};
	
	public EventsDataSource(Context context) {
		dbHelper = new EventsSqlLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	// Expects the database to be open!!
	public Event insert (Event event) {
		Log.d(LOG_TAG, "insert");
		Event newEvent = null;
		Cursor cursor = null;

		try {
			ContentValues  values = new ContentValues();
			values.put(EventsSqlLiteHelper.COLUMN_TITLE, event.getTitle());
			values.put(EventsSqlLiteHelper.COLUMN_DEPT, event.getDepartment());
			values.put(EventsSqlLiteHelper.COLUMN_DESC, event.getDescription());
			values.put(EventsSqlLiteHelper.COLUMN_LOCATION, event.getLocation());
			values.put(EventsSqlLiteHelper.COLUMN_NAME_DOCTOR, event.getNameDoc());
			values.put(EventsSqlLiteHelper.COLUMN_NAME_PATIENT, event.getNamePat());
			values.put(EventsSqlLiteHelper.COLUMN_FROM_DAY_HOUR, event.getFromDayHour());
			values.put(EventsSqlLiteHelper.COLUMN_FROM_MINUTE, event.getFromMinute());
			values.put(EventsSqlLiteHelper.COLUMN_FROM_YEAR, event.getFromYear());
			values.put(EventsSqlLiteHelper.COLUMN_FROM_MONTH, event.getFromMonth());
			values.put(EventsSqlLiteHelper.COLUMN_FROM_DAY, event.getFromDay());
			values.put(EventsSqlLiteHelper.COLUMN_TO_DAY_HOUR, event.getToDayHour());
			values.put(EventsSqlLiteHelper.COLUMN_TO_MINUTE, event.getToMinute());
			values.put(EventsSqlLiteHelper.COLUMN_EXPIRED, (event.isExpired() ? 1 : 0));
			values.put(EventsSqlLiteHelper.COLUMN_ALARMABLE, (event.isAlarmable() ? 1 : 0));
			
			long insertId = database.insert(EventsSqlLiteHelper.TABLE_EVENTS, null, values);
			Log.d(LOG_TAG, "inserted id: " + insertId);
			cursor = database.query(EventsSqlLiteHelper.TABLE_EVENTS, allColumns, EventsSqlLiteHelper.COLUMN_ID + " = " + insertId, null, null, null , null);
			cursor.moveToFirst();
			newEvent = cursorToEvent(cursor);
			Log.d(LOG_TAG, "inserted name: " + newEvent.getTitle());
		} catch (Exception e) {
			Log.d(LOG_TAG, "insert exception: " + e.getMessage());
		} finally {
			try {if (cursor != null) cursor.close();} catch (Exception e) {/*Ignore */}
		}

		return newEvent;
	}
	 
	// Expects the database to be open!!
	public void delete(Event event) {
		try {
			long id = event.getId();
			Log.d(LOG_TAG, "Deleting event ID: " + id);
			database.delete(EventsSqlLiteHelper.TABLE_EVENTS, EventsSqlLiteHelper.COLUMN_ID + " = " + id, null);
		} catch (Exception e) {
			Log.d(LOG_TAG, "delete exception: " + e.getMessage());
		} finally {
			
		}
	}
	
	// Expects the database to be open!!
	public List<Event> retrieveAll(int records) {
		Log.d(LOG_TAG, "retrieveAll: " + Environment.getDataDirectory());
		List<Event> events = new ArrayList<Event> ();
		Cursor cursor = null;
		String orderBy = EventsSqlLiteHelper.COLUMN_FROM_YEAR + "," + EventsSqlLiteHelper.COLUMN_FROM_MONTH + "," +  EventsSqlLiteHelper.COLUMN_FROM_DAY + "," +  EventsSqlLiteHelper.COLUMN_TO_DAY_HOUR + "," +  EventsSqlLiteHelper.COLUMN_TO_MINUTE + "," + EventsSqlLiteHelper.COLUMN_FROM_DAY_HOUR + "," + EventsSqlLiteHelper.COLUMN_TO_MINUTE;
		
		try {
			//cursor = database.query(EventsSqlLiteHelper.TABLE_EVENTS, allColumns, null, null, orderBy, null, EventsSqlLiteHelper.COLUMN_TITLE, "" + records);
			cursor = database.query(EventsSqlLiteHelper.TABLE_EVENTS, allColumns, null, null, null, null, orderBy, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				events.add(cursorToEvent(cursor));
				cursor.moveToNext();
			} 
		} catch (Exception e) {
			Log.d(LOG_TAG, "retrieveAll exception: " + e.getMessage());
		} finally {
			try {if (cursor != null) cursor.close();} catch (Exception e) {/*Ignore */}
		}

		return events;
	}
	
	// Expects the database to be open!!
		public List<Event> retrieveByDoctorName(String docName) {
			Log.d(LOG_TAG, "retrieveByDoctorName: " + Environment.getDataDirectory());
			List<Event> events = new ArrayList<Event> ();
			Cursor cursor = null;
			String orderBy = EventsSqlLiteHelper.COLUMN_FROM_YEAR + "," + EventsSqlLiteHelper.COLUMN_FROM_MONTH + "," +  EventsSqlLiteHelper.COLUMN_FROM_DAY + "," +  EventsSqlLiteHelper.COLUMN_FROM_DAY_HOUR + "," +  EventsSqlLiteHelper.COLUMN_FROM_MINUTE;
			
			try {
				//cursor = database.query(EventsSqlLiteHelper.TABLE_EVENTS, allColumns, null, null, orderBy, null, EventsSqlLiteHelper.COLUMN_TITLE, "" + records);
				cursor = database.query(EventsSqlLiteHelper.TABLE_EVENTS, allColumns, EventsSqlLiteHelper.COLUMN_NAME_DOCTOR +" = '"+docName+"'", null, orderBy, null, null);
				System.out.println("The database: " + Environment.getDataDirectory());
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					events.add(cursorToEvent(cursor));
					cursor.moveToNext();
				} 
			} catch (Exception e) {
				Log.d(LOG_TAG, "retrieveAll exception: " + e.getMessage());
			} finally {
				try {if (cursor != null) cursor.close();} catch (Exception e) {/*Ignore */}
			}

			return events;
		}

	private Event cursorToEvent(Cursor cursor) {
		Log.d(LOG_TAG, "cursorToEvent id: " + cursor.getInt(0));
		Event event = new Event();
		event.setId(cursor.getInt(0));
		event.setTitle(cursor.getString(1));
		event.setDepartment(cursor.getString(2));
		event.setDescription(cursor.getString(3));
		event.setLocation(cursor.getString(4));
		event.setNameDoc(cursor.getString(5));
		event.setNamePat(cursor.getString(6));
		event.setFromDayHour(cursor.getInt(7));
		event.setFromMinute(cursor.getInt(8));
		event.setFromYear(cursor.getInt(9));
		event.setFromMonth(cursor.getInt(10));
		event.setFromDay(cursor.getInt(11));
		event.setToDayHour(cursor.getInt(12));
		event.setToMinute(cursor.getInt(13));
		event.setExpired((cursor.getInt(14) == 0 ? false : true));
		event.setAlarmable((cursor.getInt(15) == 0 ? false : true));
		
		String st = "";
		for (int i = 0; i < 16; i++) {
			st += cursor.getString(i) + " | ";
		}
		System.out.println(st);
		return event;
	}
}

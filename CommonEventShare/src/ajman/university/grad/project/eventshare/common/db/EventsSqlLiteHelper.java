package ajman.university.grad.project.eventshare.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventsSqlLiteHelper extends SQLiteOpenHelper {
	public static final String LOG_TAG = "EventsSqlLiteHelper";
	public static final String TABLE_EVENTS = "events";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DEPT = "department";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_NAME_DOCTOR = "name_doc";
	public static final String COLUMN_NAME_PATIENT = "name_pat";
	public static final String COLUMN_FROM_DAY_HOUR = "from_day_hour";
	public static final String COLUMN_FROM_MINUTE = "from_minute";
	public static final String COLUMN_FROM_YEAR = "from_year";
	public static final String COLUMN_FROM_MONTH = "from_month";
	public static final String COLUMN_FROM_DAY = "from_day";
	public static final String COLUMN_TO_DAY_HOUR = "to_day_hour";
	public static final String COLUMN_TO_MINUTE = "to_minute";
	public static final String COLUMN_EXPIRED = "is_expired";
	public static final String COLUMN_ALARMABLE = "is_alarmable";
	
	public static final String DATABASE_NAME = "events.db";
	public static final int DATABASE_VERSION = 9;
	
	private static final String DATABSASE_CREATE_SQL = "create table " 
	+ TABLE_EVENTS 
	+ "(" 
	+ COLUMN_ID + " integer primary key autoincrement, " 
	+ COLUMN_TITLE + " text not null, "
	+ COLUMN_DEPT + " text not null, "
	+ COLUMN_DESC + " text not null, "
	+ COLUMN_LOCATION + " text not null, "
	+ COLUMN_NAME_DOCTOR + " text not null, "
	+ COLUMN_NAME_PATIENT + " text not null, "
	+ COLUMN_FROM_DAY_HOUR + " integer, "
	+ COLUMN_FROM_MINUTE + " integer, "
	+ COLUMN_FROM_YEAR + " integer, "
	+ COLUMN_FROM_MONTH + " integer, "
	+ COLUMN_FROM_DAY + " integer, "
	+ COLUMN_TO_DAY_HOUR + " integer, "
	+ COLUMN_TO_MINUTE + " integer, "
	+ COLUMN_EXPIRED + " integer, "
	+ COLUMN_ALARMABLE + " integer "
	+ ");";
	
	public EventsSqlLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(LOG_TAG, "onCreate");
		database.execSQL(DATABSASE_CREATE_SQL);
	}
	
	 @Override
	 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.d(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		 if (oldVersion != newVersion) {
			 db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
			 onCreate(db);
		 }
	 }
}

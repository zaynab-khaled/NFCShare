package ajman.university.grad.project.eventshare.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.models.Event;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DetailEventActivity extends Activity {
	
	private static String LOG_TAG = "DetailEventActivity User";
	private Event event;
	
	private TextView tvEventTitle;
	private TextView tvEventDoc;
	private TextView tvEventPat;
	private TextView tvEventLoc;
	private TextView tvEventDesc;
	private TextView tvEventDate;
	private TextView tvEventTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
		
		tvEventTitle = (TextView) findViewById(R.id.edit_tvTitle);
		tvEventLoc = (TextView) findViewById(R.id.edit_tvLoc);
		tvEventDoc = (TextView) findViewById(R.id.edit_tvNameDoc);
		tvEventPat = (TextView) findViewById(R.id.edit_tvNamePat);
		tvEventDesc = (TextView) findViewById(R.id.edit_tvDescription);
		tvEventDate = (TextView) findViewById(R.id.edit_tvDate);
		tvEventTime = (TextView) findViewById(R.id.edit_tvTime);
		
		// Get bundled data
		event = (Event) getIntent().getSerializableExtra(Constants.CLICKED_EVENT);
		
		// Bind the UI
		populateFields();
	}

	private void populateFields() {
		Calendar fromCal = Calendar.getInstance();
		fromCal.set(Calendar.YEAR, event.getFromYear());
		fromCal.set(Calendar.MONTH, event.getFromMonth());
		fromCal.set(Calendar.DAY_OF_MONTH, event.getFromDay());
		fromCal.set(Calendar.HOUR_OF_DAY, event.getFromDayHour());
		fromCal.set(Calendar.MINUTE, event.getFromMinute());
		
		Calendar toCal = Calendar.getInstance();
		toCal.set(Calendar.HOUR_OF_DAY, event.getToDayHour());
		toCal.set(Calendar.MINUTE, event.getToMinute());
		
		tvEventTitle.setText(event.getTitle());
		tvEventDoc.setText(event.getNameDoc());
		tvEventPat.setText(event.getNamePat());
		tvEventLoc.setText(event.getLocation());
		tvEventDesc.setText(event.getDescription());
		tvEventDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(fromCal.getTime()));
		tvEventTime.setText(new SimpleDateFormat("HH:mm").format(fromCal.getTime())  + " - " + new SimpleDateFormat("HH:mm").format(toCal.getTime()));
		
		// Setting them wrong here
		Log.d(LOG_TAG, "from cal: " + fromCal.getTime() + " to cal: " +  toCal.getTime());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

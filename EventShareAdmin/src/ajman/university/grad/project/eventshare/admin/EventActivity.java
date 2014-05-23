package ajman.university.grad.project.eventshare.admin;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ajman.university.grad.project.eventshare.common.contracts.IErrorService;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.models.Event;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class EventActivity extends SherlockActivity implements
		OnItemSelectedListener {
	private static final String LOG_TAG = "EventActivity";

	private ILocalStorageService localStorageService = ServicesFactory
			.getLocalStorageService();

	private EditText etEventTitle;
	private EditText etEventDesc;
	private EditText etEventNamePat;

	private Button btnLocation;
	private Button btnDoctor;
	private Button btnD1;
	private Button btnT1;
	private Button btnT2;

	private Event event;
	private int mode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		setUpViews();

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);

		if (getIntent().getSerializableExtra(Constants.EDIT_EVENT) == null) {
			event = new Event();
			mode = 0;
		} else {
			event = (Event) getIntent().getSerializableExtra(
					Constants.EDIT_EVENT);
			mode = 1;
			populateFields();
		}

		// BEGIN_INCLUDE (inflate_set_custom_view)
		// Inflate a "Done/Cancel" custom action bar view.
		final LayoutInflater inflater = (LayoutInflater) getSupportActionBar()
				.getThemedContext().getSystemService(
						Activity.LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						actionDone();
					}
				});
		customActionBarView.findViewById(R.id.actionbar_discard)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						actionCancel();
					}
				});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar bar = getSupportActionBar();
		;
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		bar.setCustomView(customActionBarView, new ActionBar.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		// END_INCLUDE (inflate_set_custom_view)
	}

	public void showStartTimePickerDialog(View v) {
		DialogFragment newFragment = new EventStartTimePickerFragment(event,
				mode, btnT1);
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void showEndTimePickerDialog(View v) {
		DialogFragment newFragment = new EventEndTimePickerFragment(event,
				mode, btnT2);
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new EventDatePickerFragment(event, mode,
				btnD1);
		newFragment.show(getFragmentManager(), "StartDatePicker");
	}

	private void setUpViews() {
		final ArrayAdapter<?> adapterLocation = ArrayAdapter
				.createFromResource(this, R.array.arrayLocation,
						android.R.layout.simple_spinner_dropdown_item);
		final ArrayAdapter<?> adapterLocation2 = ArrayAdapter
				.createFromResource(this, R.array.arrayLocation2,
						android.R.layout.simple_spinner_dropdown_item);
		final ArrayAdapter<?> adapterDoctor = ArrayAdapter.createFromResource(
				this, R.array.arrayDoctors,
				android.R.layout.simple_spinner_dropdown_item);
		final ArrayAdapter<?> adapterDoctor2 = ArrayAdapter.createFromResource(
				this, R.array.arrayDoctors2,
				android.R.layout.simple_spinner_dropdown_item);

		etEventTitle = (EditText) findViewById(R.id.editText_operationTitle);
		etEventNamePat = (EditText) findViewById(R.id.editText_patientName);
		etEventDesc = (EditText) findViewById(R.id.editText_operationDesc);

		btnLocation = (Button) findViewById(R.id.btn_location);
		btnLocation.setTextColor(0xff888888);
		btnDoctor = (Button) findViewById(R.id.btn_doctor);
		btnDoctor.setTextColor(0xff888888);
		btnD1 = (Button) findViewById(R.id.btnDate1);
		btnD1.setTextColor(0xff888888);
		btnT1 = (Button) findViewById(R.id.btnTime1);
		btnT1.setTextColor(0xff888888);
		btnT2 = (Button) findViewById(R.id.btnTime2);
		btnT2.setTextColor(0xff888888);

		btnD1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});

		btnT1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showStartTimePickerDialog(v);
			}
		});

		btnT2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showEndTimePickerDialog(v);
			}
		});

		btnLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] locations = null;

				if (localStorageService.getAdminDepartment()
						.equals("Neurology")) {
					locations = getResources().getStringArray(
							R.array.arrayLocation);
					final String[] items = locations;

					new AlertDialog.Builder(EventActivity.this)
							.setTitle("Select Operation Room")
							.setAdapter(adapterLocation,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String locName = items[which];
											event.setLocation(locName);
											btnLocation.setText(locName);
											btnLocation.setTextColor(0xff000000);
											dialog.dismiss();
										}
									}).create().show();
				} else {
					locations = getResources().getStringArray(
							R.array.arrayLocation2);
					final String[] items = locations;

					new AlertDialog.Builder(EventActivity.this)
							.setTitle("Select Operation Room")
							.setAdapter(adapterLocation2,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											String locName = items[which];
											event.setLocation(locName);
											btnLocation.setText(locName);
											btnLocation.setTextColor(0xff000000);
											dialog.dismiss();

											dialog.dismiss();
										}
									}).create().show();
				}
			}
		});

		btnDoctor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] doctors = null;

				if (localStorageService.getAdminDepartment()
						.equals("Neurology")) {
					doctors = getResources().getStringArray(
							R.array.arrayDoctors);
					final String[] items = doctors;

					new AlertDialog.Builder(EventActivity.this)
							.setTitle("Select Doctor")
							.setAdapter(adapterDoctor,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String docName = items[which];
											event.setNameDoc(docName);
											System.out.println("Doctor Name in db: "
													+ event.getNameDoc());
											btnDoctor.setText(docName);
											btnDoctor.setTextColor(0xff000000);
											dialog.dismiss();
										}
									}).create().show();
				} else {
					doctors = getResources().getStringArray(
							R.array.arrayDoctors2);
					final String[] items = doctors;

					new AlertDialog.Builder(EventActivity.this)
							.setTitle("Select Doctor")
							.setAdapter(adapterDoctor2,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											String docName = items[which];
											event.setNameDoc(docName);
											btnDoctor.setText(docName);
											btnDoctor.setTextColor(0xff000000);
											dialog.dismiss();
										}
									}).create().show();
				}
			}
		});
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

		etEventTitle.setText(event.getTitle());
		etEventDesc.setText(event.getDescription());
		etEventNamePat.setText(event.getNamePat());
		btnDoctor.setText(event.getNameDoc());
		btnDoctor.setTextColor(0xff000000);
		btnLocation.setText(event.getLocation());
		btnLocation.setTextColor(0xff000000);

		btnD1.setText(new SimpleDateFormat("yyyy-MM-dd").format(fromCal
				.getTime()));
		btnD1.setTextColor(0xff000000);
		btnT1.setText(new SimpleDateFormat("HH:mm").format(fromCal.getTime()));
		btnT1.setTextColor(0xff000000);
		btnT2.setText(new SimpleDateFormat("HH:mm").format(toCal.getTime()));
		btnT2.setTextColor(0xff000000);
	}

	private void actionCancel() {
		finish();
	}

	private void actionDone() {
		IErrorService errorService = ServicesFactory.getErrorService();

		if (etEventTitle.getText().length() > 0
				&& etEventNamePat.getText().length() > 0
				&& !btnDoctor.getText().toString().trim().equals("Select Doctor")
				&& !btnLocation.getText().toString().trim().equals("Select Operation Room")
				&& event.getNameDoc() != null && event.getNamePat() != null
				&& event.getFromDay() != -1 && event.getFromDayHour() != -1
				&& event.getFromMinute() != -1 && event.getFromMonth() != -1
				&& event.getFromYear() != -1 && event.getToDayHour() != -1
				&& event.getToMinute() != -1) {

			// Get the events from the events repository
			event.setTitle(etEventTitle.getText().toString().trim());
			event.setNamePat(etEventNamePat.getText().toString().trim());
			event.setDescription(etEventDesc.getText().toString().trim());
			event.setDepartment(localStorageService.getAdminDepartment());
			event.setAlarmable(false);
			if(event.getDescription().toString().equals(""))
				event.setDescription("-");

			try {
				if (mode == 0) {
					localStorageService.addEvent(event);
					Toast.makeText(getApplicationContext(), "Event Created",
							Toast.LENGTH_SHORT).show();
				} else {
					localStorageService.updateEvent(event);
					Toast.makeText(getApplicationContext(), "Event Saved",
							Toast.LENGTH_SHORT).show();
				}
				Log.d(LOG_TAG,
						"Event details: " + event.getDescription()
								+ " location: " + event.getLocation()
								+ " Doctor: " + event.getNameDoc()
								+ " Patient: " + event.getNamePat()
								+ " Title: " + event.getTitle());
				Intent intent = new Intent(this, ListActivity.class);
				startActivity(intent);

				finish();
			} catch (Exception e) {
				errorService.log(e);
			}
		} else {
			errorService
					.log("You cannot keep some fields empty, please fill them out!");
			Toast.makeText(this, "Some fields cannot be empty!",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
}

// Start code for TimePickerDialog
class EventStartTimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	private Event _innerEvent;
	private int mode;
	private Button btnT1;
	private int hour;
	private int minute;

	public EventStartTimePickerFragment(Event e, int m, Button b) {
		_innerEvent = e;
		mode = m;
		btnT1 = b;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		if (mode == 1) {
			hour = _innerEvent.getFromDayHour();
			minute = _innerEvent.getFromMinute();
		}

		else {
			final Calendar c = Calendar.getInstance();
			hour = (_innerEvent.getFromDayHour() != -1 ? _innerEvent
					.getFromDayHour() : c.get(Calendar.HOUR_OF_DAY));
			minute = (_innerEvent.getFromMinute() != -1 ? _innerEvent
					.getFromMinute() : c.get(Calendar.MINUTE));
		}

		// Create a new instance of TimePickerDialog and return it
		TimePickerDialog dialogTime = new TimePickerDialog(getActivity(), this,
				hour, minute, DateFormat.is24HourFormat(getActivity()));
		dialogTime.setTitle("Set Time");

		return dialogTime;
	}

	public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {

		// btnT1.setText(String.valueOf(hourOfDay) + ":" +
		// String.valueOf(minute));

		_innerEvent.setFromDayHour(hourOfDay);
		_innerEvent.setFromMinute(minute);

		Calendar fromCal = Calendar.getInstance();
		fromCal.set(Calendar.HOUR_OF_DAY, _innerEvent.getFromDayHour());
		fromCal.set(Calendar.MINUTE, _innerEvent.getFromMinute());
		btnT1.setText(new SimpleDateFormat("HH:mm").format(fromCal.getTime()));
		btnT1.setTextColor(0xff000000);

		System.out.println("hour:" + hourOfDay + " minute: " + minute);
	}

}

class EventEndTimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	private Event _innerEvent;
	private int mode;
	private Button btnT2;
	private int hour;
	private int minute;

	public EventEndTimePickerFragment(Event e, int m, Button b) {
		_innerEvent = e;
		mode = m;
		btnT2 = b;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (mode == 1) {
			hour = _innerEvent.getToDayHour();
			minute = _innerEvent.getToMinute();
		} else {
			final Calendar c = Calendar.getInstance();
			hour = (_innerEvent.getToDayHour() != -1 ? _innerEvent
					.getToDayHour() : c.get(Calendar.HOUR_OF_DAY));
			minute = (_innerEvent.getToMinute() != -1 ? _innerEvent
					.getToMinute() : c.get(Calendar.MINUTE));
		}

		// Create a new instance of TimePickerDialog and return it
		TimePickerDialog dialogTime = new TimePickerDialog(getActivity(), this,
				hour, minute, DateFormat.is24HourFormat(getActivity()));
		dialogTime.setTitle("Set Time");

		return dialogTime;
	}

	public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {

		// btnT2.setText(String.valueOf(hourOfDay) + ":" +
		// String.valueOf(minute));

		_innerEvent.setToDayHour(hourOfDay);
		_innerEvent.setToMinute(minute);

		Calendar toCal = Calendar.getInstance();
		toCal.set(Calendar.HOUR_OF_DAY, _innerEvent.getToDayHour());
		toCal.set(Calendar.MINUTE, _innerEvent.getToMinute());
		btnT2.setText(new SimpleDateFormat("HH:mm").format(toCal.getTime()));
		btnT2.setTextColor(0xff000000);

		System.out.println("hour:" + hourOfDay + " minute: " + minute);
	}

}

class EventDatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private Event _innerEvent;
	private int mode;
	private Button btnD1;
	private int year;
	private int month;
	private int day;

	public EventDatePickerFragment(Event e, int m, Button b) {
		_innerEvent = e;
		mode = m;
		btnD1 = b;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker

		if (mode == 1) {
			year = _innerEvent.getFromYear();
			month = _innerEvent.getFromMonth();
			day = _innerEvent.getFromDay();
		} else {
			final Calendar c = Calendar.getInstance();
			year = (_innerEvent.getFromYear() != -1 ? _innerEvent.getFromYear()
					: c.get(Calendar.YEAR));
			month = (_innerEvent.getFromMonth() != -1 ? _innerEvent
					.getFromMonth() : c.get(Calendar.MONTH));
			day = (_innerEvent.getFromDay() != -1 ? _innerEvent.getFromDay()
					: c.get(Calendar.DAY_OF_MONTH));
		}

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialogDate = new DatePickerDialog(getActivity(), this,
				year, month, day);
		dialogDate.setTitle("Set Date");
		return dialogDate;

	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// btnD1.setText(String.valueOf(day) + String.valueOf(month + 1) +
		// String.valueOf(year));

		_innerEvent.setFromDay(day);
		_innerEvent.setFromMonth(month);
		_innerEvent.setFromYear(year);

		Calendar fromCal = Calendar.getInstance();
		fromCal.set(Calendar.YEAR, _innerEvent.getFromYear());
		fromCal.set(Calendar.MONTH, _innerEvent.getFromMonth());
		fromCal.set(Calendar.DAY_OF_MONTH, _innerEvent.getFromDay());

		btnD1.setText(new SimpleDateFormat("yyyy-MM-dd").format(fromCal
				.getTime()));
		btnD1.setTextColor(0xff000000);

		System.out.println("day:" + day + " month: " + month + " year:" + year);
	}
}

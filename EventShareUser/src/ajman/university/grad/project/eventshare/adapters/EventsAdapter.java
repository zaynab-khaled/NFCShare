package ajman.university.grad.project.eventshare.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ajman.university.grad.project.eventshare.common.contracts.IErrorService;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.models.Event;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import ajman.university.grad.project.eventshare.user.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventsAdapter extends BaseAdapter implements OnClickListener {
	private static final String LOG_TAG = "EventsAdapter User";

	private List<Event> events = new ArrayList<Event>();
	private Context context;
	private int nrOfValidEvents;
	private final static int tagSize = (256 - 40) * 16;

	// (nr of block - trailer blocks) * block size = 3456 bytes;
	private ILocalStorageService service = ServicesFactory.getLocalStorageService();
	private IEventsAdapterManager myManager;
	private String docName = null;
	
	public EventsAdapter(Context c, IEventsAdapterManager manager) {
		context = c;
		myManager = manager;
		refreshAdapter();
	}

	public EventsAdapter(Context c, IEventsAdapterManager manager, String doc) {
		context = c;
		myManager = manager;
		docName = doc;
		refreshAdapter();
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int i) {
		return events.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Event event = events.get(i);

		Calendar fromCal = Calendar.getInstance();
		fromCal.set(Calendar.YEAR, event.getFromYear());
		fromCal.set(Calendar.MONTH, event.getFromMonth());
		fromCal.set(Calendar.DAY_OF_MONTH, event.getFromDay());
		fromCal.set(Calendar.HOUR_OF_DAY, event.getFromDayHour());
		fromCal.set(Calendar.MINUTE, event.getFromMinute());

		Calendar toCal = Calendar.getInstance();
		toCal.set(Calendar.HOUR_OF_DAY, event.getToDayHour());
		toCal.set(Calendar.MINUTE, event.getToMinute());
		View row;

		if (isDeclined(event)) {
			// contains a reference to the Linear layout
			Log.d(LOG_TAG, "Event: " + event);
			row = inflater.inflate(R.layout.expired_row_list, viewGroup, false);
			event.setExpired(true);
		}
		else {
			row = inflater.inflate(R.layout.single_row_list, viewGroup, false);
			event.setExpired(false);

			ImageView ivAddClock = (ImageView) row.findViewById(R.id.btnAddClock);

			// TRICKY: Attach an object to the button view so we an retrieve
			// later
			ivAddClock.setTag(event);
			ivAddClock.setOnClickListener(this);
		}

		TextView title = (TextView) row.findViewById(R.id.textView1);
		TextView docname = (TextView) row.findViewById(R.id.textView2);
		TextView location = (TextView) row.findViewById(R.id.textView3);
		TextView dateTime = (TextView) row.findViewById(R.id.textView4);

		title.setText(event.getTitle());
		docname.setText(event.getNameDoc());
		location.setText(event.getLocation());
		dateTime.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(fromCal.getTime()) + "  /  "
				+ new SimpleDateFormat("HH:mm").format(fromCal.getTime()) + " - "
				+ new SimpleDateFormat("HH:mm").format(toCal.getTime()));

		ImageView ivAddClock = (ImageView) row.findViewById(R.id.btnAddClock);
		if (event.isAlarmable()) {
			ivAddClock.setImageDrawable(null);
			ivAddClock.setBackgroundResource(R.drawable.ic_action_alarms);
		}

		return row; // return the rootView of the single_row_list.xml
	}

	@Override
	public void onClick(View view) {
		ILocalStorageService service = ServicesFactory.getLocalStorageService();
		// TRICKY: Detach a previously attached object to the image view
		Event taggedEvent = (Event) view.getTag();
		if (taggedEvent != null) {
			if (taggedEvent.isAlarmable()) {
				taggedEvent.setAlarmable(false);
				((ImageView) view).setImageDrawable(null);
				view.setBackgroundResource(R.drawable.ic_action_add_alarm);
				Toast.makeText(context, "Notification has been removed", Toast.LENGTH_SHORT).show();
			} else {
				taggedEvent.setAlarmable(true);
				((ImageView) view).setImageDrawable(null);
				view.setBackgroundResource(R.drawable.ic_action_alarms);
				Toast.makeText(context, "Notification set 30 minutes before time of event", Toast.LENGTH_SHORT).show();
			}
			
			try {
				Log.d(LOG_TAG, "Number of local storage events Before : " + service.getAllEvents().size() );
				service.updateEvent(taggedEvent);
				Log.d(LOG_TAG, "Number of local storage events After : " + service.getAllEvents().size() );
			} catch (Exception e) {
				Log.d(LOG_TAG, "Exception ocurred during event update: " + e.getMessage());
			}
			
			if (myManager != null)
				myManager.eventsChanged();
		}
	}

	// Final format should be 20140924185545
	private String formatToDate(Event event) {
		String date = "";
		date += event.getFromYear();
		date += (event.getFromMonth() < 10 ? "0" + (event.getFromMonth() + 1) : event.getFromMonth() + 1);
		date += (event.getFromDay() < 10 ? "0" + event.getFromDay() : event.getFromDay());
		date += (event.getToDayHour() < 10 ? "0" + event.getToDayHour() : event.getToDayHour());
		date += (event.getToMinute() < 10 ? "0" + event.getToMinute() : event.getToMinute());
		date += "00";
		return date;
	}

	private String formatFromDate(Event event) {
		String date = "";
		date += event.getFromYear();
		date += (event.getFromMonth() < 10 ? "0" + (event.getFromMonth() + 1) : event.getFromMonth() + 1);
		date += (event.getFromDay() < 10 ? "0" + event.getFromDay() : event.getFromDay());
		date += (event.getFromDayHour() < 10 ? "0" + event.getFromDayHour() : event.getFromDayHour());
		date += (event.getFromMinute() < 10 ? "0" + event.getFromMinute() : event.getFromMinute());
		date += "00";
		return date;
	}

	@Override
	public String toString() {
		String vCal = "";
		String vCalPrev = "";
		String vEvent = "";
		nrOfValidEvents = 0;

		vCal += "<c>";

		for (int i = 0; i < events.size(); i++) {
			if (!isDeclined(events.get(i))) {
				vEvent = "<e>";
				vEvent += "<s>" + formatFromDate(events.get(i)) + "</s>";
				vEvent += "<f>" + formatToDate(events.get(i)) + "</f>";
				vEvent += "<t>" + events.get(i).getTitle() + "</t>";
				vEvent += "<a>" + events.get(i).getDepartment() + "</a>";
				vEvent += "<d>" + events.get(i).getDescription() + "</d>";
				vEvent += "<o>" + events.get(i).getNameDoc() + "</o>";
				vEvent += "<p>" + events.get(i).getNamePat() + "</p>";
				vEvent += "<i>" + events.get(i).getId() + "</i>";
				vEvent += "<l>" + events.get(i).getLocation() + "</l>";
				vEvent += "</e>";

				vCal += vEvent;

				if (vCal.getBytes().length < (tagSize - "</c>".getBytes().length)) {
					vCalPrev = vCal;
					nrOfValidEvents++;
					System.out.println("nrofevents = " + nrOfValidEvents);
					System.out.println("size = " + vCal.getBytes().length + " bytes");
				}
				else {
					vCal = vCalPrev;
					break;
				}
			}
		}

		vCal += "</c>";
		return vCal;
	}

	private boolean isDeclined(Event event) {

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

	public int getValidCount() {
		return nrOfValidEvents;
	}
	
	private void refreshAdapter () {
		events = new ArrayList<Event>();

		try {
			List<Event> storedEvents = new ArrayList<Event>();
			if (docName != null)
				storedEvents = service.filterByDoctorName(docName);
			else
				storedEvents = service.getAllEvents();
			
			for (Event event : storedEvents) {
				if (event.getDepartment().toString().equals(service.getUserDepartment().toString())) {
					events.add(event);
				}
			}
		} catch (Exception e) {
			// TODO: Error service
			IErrorService eService = ServicesFactory.getErrorService();
			eService.log(e);
		}
	}
}

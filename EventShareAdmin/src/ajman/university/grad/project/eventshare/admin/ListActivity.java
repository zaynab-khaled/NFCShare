package ajman.university.grad.project.eventshare.admin;

import ajman.university.grad.project.eventshare.adapters.EventsAdapter;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.models.Event;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity implements OnItemClickListener {
	//private static final String LOG_TAG = "LIst Activity";
	
	private ILocalStorageService service = ServicesFactory.getLocalStorageService();
	
	private TextView tvDepartment;
	private TextView tvSchedule;
	private ListView list;
	private EventsAdapter adapter;
	private ImageView startingImage;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setupActionBar();
        setUpViews();

        list = (ListView) findViewById(android.R.id.list);
        adapter = new EventsAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setEmptyView(startingImage);
		
    }

    private void setupActionBar() {
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
	}
    
    private void setUpViews() {
    	tvSchedule = (TextView) findViewById(R.id.tv_schedule);
        tvDepartment = (TextView) findViewById(R.id.tv_department);
        startingImage = (ImageView) findViewById(android.R.id.empty);

        tvSchedule.setGravity(Gravity.CENTER);
        tvDepartment.setGravity(Gravity.CENTER);
        
        String dept = service.getAdminDepartment();
        tvDepartment.setText(dept == null ? "Unknown" : dept + " Department");
        tvSchedule.setText("Operating Schedule"); 
	}
    
    @Override
    public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);	  
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(ListActivity.this, DetailEventActivity.class);
		Event event = (Event) adapter.getItem(arg2);
		intent.putExtra(Constants.CLICKED_EVENT, event);
		startActivity(intent);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.first_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//return super.onOptionsItemSelected(item);
    	switch (item.getItemId()) {
		case R.id.addNewEvent:
			addNewEvent();
			return true;
			
		case R.id.writeToTag:
			writeToTag();
			return true;
			
		case R.id.action_sendMsg:
			actionSendMsg();
			return true;
			
		case R.id.action_deleteExpired:
			actionDeleteExpired();
			return true;
			
		case R.id.action_erase:
			actionErase();
			return true;
			
		case R.id.action_about:
			actionAbout();
			return true;

		default:
			return super.onOptionsItemSelected(item);
    	}
    }


	private void actionSendMsg() {
		Intent intent = new Intent(ListActivity.this, MessageActivity.class);
		startActivity(intent);
		
	}

	private void actionAbout() {
		Intent intent = new Intent(ListActivity.this, AboutActivity.class);
		startActivity(intent);
	}

	private void actionErase() {
		Intent intent = new Intent(ListActivity.this, EraseTagActivity.class);
		startActivity(intent);
	}

	private void actionDeleteExpired() {
		new AlertDialog.Builder(this)
		.setTitle("Delete Event")
		.setMessage("Are you sure you want to delete all declined events?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
	               //Do nothing
	           }
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				ILocalStorageService service = ServicesFactory.getLocalStorageService();
				try {
					int count = service.deleteDeclinedEvents();
					onCreate(null);
					Toast.makeText(getApplicationContext(), count + " declined " +  ((count == 1) ? "event" : "events") + " have been deleted!",
							   Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
	           }
		}).show();
	}

	private void addNewEvent() {
		Intent intent = new Intent(ListActivity.this, EventActivity.class);
		startActivity(intent);
	}

	private void writeToTag() {
		Intent intent = new Intent(ListActivity.this, WriteToTagActivity.class);
		intent.putExtra(Constants.ICALENDAR, adapter.toString());
		intent.putExtra(Constants.EVENTCOUNT, adapter.getValidCount());
		startActivity(intent);
	}
}
package ajman.university.grad.project.eventshare.admin;

import ajman.university.grad.project.eventshare.admin.helpers.Constants;
import ajman.university.grad.project.eventshare.common.contracts.IErrorService;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.contracts.IRemoteNotificationService;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class MessageActivity extends SherlockActivity {
	private ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();
	private IRemoteNotificationService remoteNotifciationService = ServicesFactory.getRemoteNotificationService();
	private IErrorService errorService = ServicesFactory.getErrorService();

	private Button btnDepartment;
	private EditText etMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		setUpViews();

		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);

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

	private void setUpViews() {
		final ArrayAdapter<?> adapterDepartment = ArrayAdapter.createFromResource(this, R.array.arrayDepartments,
				android.R.layout.simple_spinner_dropdown_item);

		btnDepartment = (Button) findViewById(R.id.btn_dept);
		etMessage = (EditText) findViewById(R.id.et_message);
		btnDepartment.setTextColor(0xff888888);

		btnDepartment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String[] departments = null;

				departments = getResources().getStringArray(R.array.arrayDepartments);
				final String[] items = departments;

				new AlertDialog.Builder(MessageActivity.this)
						.setTitle("Select Department")
						.setAdapter(adapterDepartment, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								String deptName = items[which];
								btnDepartment.setText(deptName);
								localStorageService.setPushChannel(deptName);
								btnDepartment.setTextColor(0xff000000);
								dialog.dismiss();
							}
						}).create().show();
			}
		});
	}

	private void actionDone() {
		try {
			if (etMessage.getText().length() > 0 && !btnDepartment.getText().toString().trim().equals("Select Department")) {

				String channel = btnDepartment.getText().toString().trim();
				String message = etMessage.getText().toString().trim();
				localStorageService.setPushMessage(message);
				remoteNotifciationService.sendPushNotification(Constants.PARSE_APP_ID, Constants.PARSE_APP_REST_KEY, channel, message);
				Intent intent = new Intent(this, ListActivity.class);
				Toast.makeText(this, "Notification sent!", Toast.LENGTH_SHORT).show();
				startActivity(intent);

			} else {
				errorService.log("You cannot keep some fields empty, please fill them out!");
				Toast.makeText(this, "Some fields cannot be empty!", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Notification could not be sent, check your internet connection!", Toast.LENGTH_SHORT).show();
		}
	}

	private void actionCancel() {
		finish();
	}
}

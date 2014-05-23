package ajman.university.grad.project.eventshare.user;

import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private static final String LOG_TAG = "Register Activity";
	
	private ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();
	
	private Button btnDepartments;
	private Button btnRegister;
	private EditText etPass;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setUpViews();

		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
	
	}

	private void setUpViews() {
    	final ArrayAdapter<?> adapterDepartment = ArrayAdapter.createFromResource(this,R.array.arrayDepartments, android.R.layout.simple_spinner_dropdown_item);

		etPass = (EditText) findViewById(R.id.editText_password);
		btnDepartments = (Button) findViewById(R.id.btnDepartments);
		btnRegister = (Button) findViewById(R.id.btn_register);
		etPass.setGravity(Gravity.CENTER);
		btnDepartments.setGravity(Gravity.CENTER);
	    btnRegister.setGravity(Gravity.CENTER);
	    
	    btnDepartments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] departments = null;

				departments = getResources().getStringArray(
						R.array.arrayDepartments);
				final String[] items = departments;

				new AlertDialog.Builder(RegisterActivity.this)
						.setTitle("Select Department")
						.setAdapter(adapterDepartment, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								String deptName = items[which];
								btnDepartments.setText(deptName);
								localStorageService.setUserDepartment(btnDepartments.getText().toString());
								dialog.dismiss();
							}
						}).create().show();
			}
		});

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				password = localStorageService.getAdminPassword();

				if (btnDepartments.getText().equals("Select Department")) {
					Toast.makeText(getApplicationContext(), "Choose a department", Toast.LENGTH_SHORT).show();
				}
				else if (btnDepartments.getText().equals("Neurology")) {
					Log.d(LOG_TAG, "Password: " + password);

					if (etPass.getText().length() == 0) {
						Log.d(LOG_TAG, "Password: " + password);
						Toast.makeText(getApplicationContext(), "No password has been entered!", Toast.LENGTH_SHORT).show();
					}
					else if (etPass.getText().toString().equals("neuro")) {
						localStorageService.setRegistered(true);
						Intent intent = new Intent(RegisterActivity.this, ListActivity.class);
						startActivity(intent);
						finish();
					}
					else
						Toast.makeText(getApplicationContext(), "Sorry, invalid password", Toast.LENGTH_SHORT).show();
				}
				else if (btnDepartments.getText().equals("Cardiology")) {
					Log.d(LOG_TAG, "Password: " + password);

					if (etPass.getText().length() == 0) {
						Log.d(LOG_TAG, "Password: " + password);
						Toast.makeText(getApplicationContext(), "No password has been entered!", Toast.LENGTH_SHORT).show();
					}
					else if (etPass.getText().toString().equals("card")) {
						localStorageService.setRegistered(true);
						Intent intent = new Intent(RegisterActivity.this, ListActivity.class);
						startActivity(intent);
						finish();
					}
					else
						Toast.makeText(getApplicationContext(), "Sorry, invalid password", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getApplicationContext(), "This department is coming soon!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;	
	}
	
    @Override
    public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);	  
    }
}

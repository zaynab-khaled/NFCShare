package ajman.university.grad.project.eventshare.admin;

import java.io.IOException;

import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.contracts.IRemoteNotificationService;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.helpers.Utils;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class WriteToTagActivity extends Activity {

	static String separator = System.getProperty("line.separator");
	public static String calString;

	private TextView tvCalSize;
	private int nrOfEvents;
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;
	private static boolean write = false;

	private ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();
	private IRemoteNotificationService remoteService = ServicesFactory.getRemoteNotificationService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_to_tag);
		setupActionBar();

		// Get bundled data
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			nrOfEvents = extras.getInt(Constants.EVENTCOUNT);
		}

		calString = (String) getIntent().getSerializableExtra(Constants.ICALENDAR);

		System.out.println(calString);

		tvCalSize = (TextView) findViewById(R.id.tv_calendar_size);
		tvCalSize.setText("Calendar size: " + calString.getBytes().length + " bytes");

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// create an intent with tag data and deliver to this activity
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// set an intent filter for all MIME data
		IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] { ndefIntent };
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}

		mNFCTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		// Ensure that the device supports NFC
		ensureNfcIsAvailable(mNfcAdapter);
		ensureSensorIsOn(mNfcAdapter);

		mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		if (!write) {
			processIntent(intent);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void processIntent(Intent intent) {
		if ((int) Math.ceil(calString.getBytes().length / (double) MifareClassic.BLOCK_SIZE) > 216) {
			System.out.println("Message is too big to be written to tag!");
			return;
		}
		byte[][] msg = new byte[216][MifareClassic.BLOCK_SIZE];
		int start = 0;
		int calBlocks = (int) Math.ceil(calString.getBytes().length / (double) MifareClassic.BLOCK_SIZE);

		for (int i = 0; i < calBlocks; i++) {
			if (start + MifareClassic.BLOCK_SIZE > calString.getBytes().length) {
				System.arraycopy(calString.getBytes(), start, msg[i], 0, calString.getBytes().length - start);
			} else {
				System.arraycopy(calString.getBytes(), start, msg[i], 0, MifareClassic.BLOCK_SIZE);
			}
			start += MifareClassic.BLOCK_SIZE;
		}

		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		boolean auth = false;
		MifareClassic mfc = MifareClassic.get(tagFromIntent);

		try {
			String metaInfo = "";
			int msgCount = 0;
			mfc.connect();
			outer: for (int j = 0; j < mfc.getSectorCount(); j++) {

				/*
				 * if (msgCount >= nfcPreferences.getInt("prevCalendarBlocks",
				 * 216) && msgCount > calBlocks) { System.out.println(
				 * "Stopped writing because prev calendar size was: " +
				 * nfcPreferences.getInt("prevCalendarBlocks", 216)); break; }
				 */

				// Authenticate a sector with key.
				auth = mfc.authenticateSectorWithKeyA(j, getKey(localStorageService.getAdminDepartment()));

				int bCount;
				int bIndex;
				if (auth) {
					System.out.println("Sector " + j + " authenticated successfully");
					bCount = mfc.getBlockCountInSector(j);
					bIndex = mfc.sectorToBlock(j);

					for (int i = 0; i < bCount; i++) {

						try {
							byte[] data = mfc.readBlock(bIndex);
							if (Utils.byteArrayToHexString(data).equals("00000000000000000000000000000000") && msgCount > calBlocks) {
								System.out.println("stopped writing");
								break outer;
							}

						} catch (Exception e) {
							System.out.println("Read error at: " + bIndex);
						}

						// Write to data blocks with key A
						if ((bIndex + 1) % bCount != 0 && bIndex != 0) {
							try {
								mfc.writeBlock(bIndex, msg[msgCount]);
								msgCount++;
								System.out.println("Written to block: " + bIndex);

							} catch (IOException e) {
								System.out.println("Could not write to block: " + bIndex);
							}
						}

						try {
							byte[] data = mfc.readBlock(bIndex);
							metaInfo += "Block " + bIndex + " : " + Utils.byteArrayToHexString(data) + "\n";
						} catch (Exception e) {
							System.out.println("Cound not read block nr: " + bIndex);
						}

						bIndex++;
					}
				} else {
					System.out.println("Sector " + j + " could not be authenticated");
				}
			}
			System.out.println(metaInfo);
			System.out.println("Write count = " + msgCount);

			mfc.close();
			write = true;

			if (msgCount > 0 && write) {
				remoteService.sendPushNotification(ajman.university.grad.project.eventshare.admin.helpers.Constants.PARSE_APP_ID,
						ajman.university.grad.project.eventshare.admin.helpers.Constants.PARSE_APP_REST_KEY,
						localStorageService.getAdminDepartment(),
						"New events have been updated for your department");
			}

			new AlertDialog.Builder(this)
					.setMessage(
							(msgCount == 0) ? "Could not write to tag!"
									: (nrOfEvents + (nrOfEvents == 1 ? " event " : " events ") + "successfully written to tag!"))
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							write = false;
							finish();
						}
					}).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ***** ****
	private void ensureSensorIsOn(NfcAdapter mNfcAdapter) {
		if (mNfcAdapter == null) {
			// Stop here, we definitely need NFC
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	private void ensureNfcIsAvailable(NfcAdapter mNfcAdapter) {
		if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
			// Alert the user that NFC is off
			new AlertDialog.Builder(getApplicationContext())
					.setTitle("NFC Sensor Turned Off")
					.setMessage(
							"In order to use this application, the NFC sensor must be turned on. Do you wish to turn it on?")
					.setPositiveButton("Go to Settings",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									// Send the user to the settings page and
									// hope they turn it on
									if (android.os.Build.VERSION.SDK_INT >= 16) {
										startActivity(new Intent(
												android.provider.Settings.ACTION_NFC_SETTINGS));
									} else {
										startActivity(new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS));
									}
								}
							})
					.setNegativeButton("Do Nothing",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									// Do nothing
								}
							}).show();
		}
	}

	private byte[] getKey(String department) {
		if (department.equals("Neurology")) {
			return Constants.KEYA_NEURO;
		}
		else {
			return Constants.KEYA_FAKE;
		}
	}
}
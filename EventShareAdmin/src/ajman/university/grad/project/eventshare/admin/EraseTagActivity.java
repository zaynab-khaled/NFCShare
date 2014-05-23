package ajman.university.grad.project.eventshare.admin;

import java.io.IOException;

import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
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
import android.widget.Toast;

public class EraseTagActivity extends Activity {

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;
	private static boolean erase = false;

	private ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();

	private static final byte[] CLEANDATACONTENT = new byte[MifareClassic.BLOCK_SIZE];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erase);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// create an intent with tag data and deliver to this activity
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// set an intent filter for all MIME data
		IntentFilter ndefIntent = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] { ndefIntent };
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}

		mNFCTechLists = new String[][] { new String[] { MifareClassic.class
				.getName() } };
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
		if (!erase) {
			processIntent(intent);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@SuppressWarnings("unused")
	private void processIntent(Intent intent) {

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
				 * 216)) { System.out.println(
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
						/*
						 * try { byte[] data = mfc.readBlock(bIndex); if
						 * (byteArrayToHexString
						 * (data).equals("00000000000000000000000000000000")) {
						 * System.out.println("stopped writing"); break outer; }
						 * 
						 * } catch (Exception e) {
						 * System.out.println("Read error at: " + bIndex); }
						 */

						// Write to data blocks with key A
						if ((bIndex + 1) % bCount != 0 && bIndex != 0) {
							try {
								mfc.writeBlock(bIndex, CLEANDATACONTENT);
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

			System.out.println(metaInfo);
			mfc.close();
			erase = true;
			new AlertDialog.Builder(this)
					.setMessage((msgCount == 0) ? "Could not erase tag!" : "Tag successfully erased!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									erase = false;
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
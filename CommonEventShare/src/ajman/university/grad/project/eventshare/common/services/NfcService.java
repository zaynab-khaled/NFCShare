package ajman.university.grad.project.eventshare.common.services;


import ajman.university.grad.project.eventshare.common.contracts.INfcService;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

public class NfcService extends Activity implements INfcService {

	@Override
	public void ensureSensorIsOn(NfcAdapter mNfcAdapter) {
		if (mNfcAdapter == null) {
			// Stop here, we definitely need NFC
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public void ensureNfcIsAvailable(NfcAdapter mNfcAdapter) {
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

	@Override
	public String hexToASCII(String hex) {
		if (hex.length() % 2 != 0) {
			System.err.println("requires EVEN number of chars");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		// Convert Hex 0232343536AB into two characters stream.
		for (int i = 0; i < hex.length() - 1; i += 2) {
			/*
			 * Grab the hex in pairs
			 */
			String output = hex.substring(i, (i + 2));
			/*
			 * Convert Hex to Decimal
			 */
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
		}
		return sb.toString();
	}

	@Override
	public String byteArrayToHexString(byte[] raw) {
		final String HEXES = "0123456789ABCDEF";
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	@Override
	public byte[] getKey(String department) {
		if (department.equals("Neurology")) {
			return Constants.KEYA_NEURO;
		}
		else {
			return Constants.KEYA_FAKE;
		}
	}
	
	public Context getApplicationContext() {
		return ApplicationContextProvider.getContext();
	}
}

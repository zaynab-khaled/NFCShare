package ajman.university.grad.project.eventshare.common.contracts;

import android.nfc.NfcAdapter;

public interface INfcService {
	public void ensureSensorIsOn(NfcAdapter mNfcAdapter);
	public void ensureNfcIsAvailable(NfcAdapter mNfcAdapter);
	public String hexToASCII(String hex);
	public String byteArrayToHexString(byte[] raw);
	public byte[] getKey(String department);
}

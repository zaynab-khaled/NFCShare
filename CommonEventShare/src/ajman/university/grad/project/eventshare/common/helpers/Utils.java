package ajman.university.grad.project.eventshare.common.helpers;

import java.util.Calendar;

public class Utils {	
	public static int getTimeDifferenceFromNow(int year, int month, int day, int hour, int minutes) {
		Calendar eventCal = Calendar.getInstance();
		eventCal.set(Calendar.YEAR, year);
		eventCal.set(Calendar.MONTH, month);
		eventCal.set(Calendar.DAY_OF_MONTH, day);
		eventCal.set(Calendar.HOUR_OF_DAY, hour);
		eventCal.set(Calendar.MINUTE, minutes);
		
		Calendar now = Calendar.getInstance();
		
		return (int)((eventCal.getTimeInMillis() - now.getTimeInMillis()) / 60000);
	}
	
	public static String byteArrayToHexString(byte[] raw) {
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
	
	public static String hexToASCII(String hex) {
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
}

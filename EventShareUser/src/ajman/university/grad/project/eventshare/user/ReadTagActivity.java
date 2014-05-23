package ajman.university.grad.project.eventshare.user;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ajman.university.grad.project.eventshare.common.contracts.IErrorService;
import ajman.university.grad.project.eventshare.common.contracts.ILocalStorageService;
import ajman.university.grad.project.eventshare.common.contracts.ITagService;
import ajman.university.grad.project.eventshare.common.helpers.Constants;
import ajman.university.grad.project.eventshare.common.helpers.Utils;
import ajman.university.grad.project.eventshare.common.models.Event;
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

public class ReadTagActivity extends Activity {

	private ILocalStorageService localStorageService = ServicesFactory.getLocalStorageService();
	private ITagService fakeTagService = ServicesFactory.getFakeNfcTagService();
	private IErrorService errorService = ServicesFactory.getErrorService();

	static String separator = System.getProperty("line.separator");
	public static String calString;

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;
	private static boolean read = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_tag);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xff33b5e5));
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);

		// Get Fake events to test the functionality without actual NFC tags
		List<Event> events = fakeTagService.readEvents();
		for (Event event : events) {
			try {
				localStorageService.addEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			if (!read) {
				processIntent(getIntent());
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		if (!read) {
			processIntent(intent);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void processIntent(Intent intent) {
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		boolean auth = false;
		MifareClassic mfc = MifareClassic.get(tagFromIntent);

		try {
			String metaInfo = "";
			mfc.connect();

			outer: for (int j = 0; j < mfc.getSectorCount(); j++) {

				// Authenticate a sector with key.
				auth = mfc.authenticateSectorWithKeyA(j, getKey(localStorageService.getUserDepartment()));
				int bCount;
				int bIndex;
				if (auth) {
					System.out.println("Sector " + j + " authenticated successfully");
					bCount = mfc.getBlockCountInSector(j);
					bIndex = mfc.sectorToBlock(j);

					for (int i = 0; i < bCount; i++) {

						try {
							byte[] data = mfc.readBlock(bIndex);
							if (Utils.byteArrayToHexString(data).equals("00000000000000000000000000000000")) {
								System.out.println("stopped reading");
								break outer;
							}

						} catch (Exception e) {
							System.out.println("Read error at: " + bIndex);
						}

						// Read data blocks with key A
						if ((bIndex + 1) % bCount != 0 && bIndex != 0) {
							try {
								byte[] data = mfc.readBlock(bIndex);
								metaInfo += Utils.byteArrayToHexString(data);
							} catch (Exception e) {
								System.out.println("Cound not read block nr: "
										+ bIndex);
							}
						}

						bIndex++;
					}
				} else {
					System.out.println("Sector " + j + " could not be authenticated");
				}
			}

			mfc.close();
			new AlertDialog.Builder(this).setMessage("Authentication failed!").setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							read = false;
							Intent intent = new Intent(ReadTagActivity.this, ListActivity.class);
							startActivity(intent);
						}
					}).show();

			Event event;
			try {

				String xmlCalendar = Utils.hexToASCII(metaInfo);
				System.out.println(xmlCalendar);

				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xmlCalendar));
				Document doc = db.parse(is);

				NodeList nodes = doc.getElementsByTagName("e");

				localStorageService.deleteAllEvents();

				for (int i = 0; i < nodes.getLength(); i++) {
					event = new Event();

					Element attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("t").item(0);
					event.setTitle(getCharacterDataFromElement(attr));
					System.out.println("Title: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("d").item(0);
					event.setDescription(getCharacterDataFromElement(attr));
					System.out.println("Description: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("l").item(0);
					event.setLocation(getCharacterDataFromElement(attr));
					System.out.println("Location: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("i").item(0);
					event.setId(Integer.parseInt(getCharacterDataFromElement(attr)));
					System.out.println("Uid: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("o").item(0);
					event.setNameDoc(getCharacterDataFromElement(attr));
					System.out.println("Doctor: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("p").item(0);
					event.setNamePat(getCharacterDataFromElement(attr));
					System.out.println("Patient: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("a").item(0);
					event.setDepartment(getCharacterDataFromElement(attr));
					System.out.println("Department: " + getCharacterDataFromElement(attr));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("s").item(0);
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
					cal.setTime(sdf.parse(getCharacterDataFromElement(attr)));

					event.setFromYear(cal.get(Calendar.YEAR));
					System.out.println("Fromyear: " + cal.get(Calendar.YEAR));

					event.setFromMonth(cal.get(Calendar.MONTH));
					System.out.println("FromMonth: " + cal.get(Calendar.MONTH));

					event.setFromDay(cal.get(Calendar.DAY_OF_MONTH));
					System.out.println("FromDay: " + cal.get(Calendar.DAY_OF_MONTH));

					event.setFromDayHour(cal.get(Calendar.HOUR_OF_DAY));
					System.out.println("FromHour: " + cal.get(Calendar.HOUR_OF_DAY));

					event.setFromDayHour(cal.get(Calendar.MINUTE));
					System.out.println("Fromminute: " + cal.get(Calendar.MINUTE));

					attr = (Element) ((Element) nodes.item(i)).getElementsByTagName("f").item(0);
					cal.setTime(sdf.parse(getCharacterDataFromElement(attr)));

					event.setToDayHour(cal.get(Calendar.HOUR_OF_DAY));
					// System.out.println("ToHour: " +
					// cal.get(Calendar.HOUR_OF_DAY));

					event.setToDayHour(cal.get(Calendar.MINUTE));
					// System.out.println("Tominute: " +
					// cal.get(Calendar.MINUTE));

					localStorageService.addEvent(event);
				}
				read = true;
			} catch (Exception e) {
				System.out.println("Parse error occured!!");
				errorService.log(e);
			}

			new AlertDialog.Builder(this).setMessage((metaInfo.length() == 0) ? "Tag is empty!" : "Tag successfully read!").setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							read = false;
							Intent intent = new Intent(ReadTagActivity.this, ListActivity.class);
							startActivity(intent);
						}
					}).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
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

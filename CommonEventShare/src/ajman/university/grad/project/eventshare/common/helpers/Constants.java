package ajman.university.grad.project.eventshare.common.helpers;

public class Constants {
	// Intent Bundle Keys
	public static String CLICKED_EVENT = "clickedEvent";
	public static String EDIT_EVENT = "editEvent";
	public static String ICALENDAR = "iCalendar";
	public static String EVENTCOUNT = "eventCount";
	
	//Local storage keys
	public static String ADMIN_DEPARTMENT = "adminDepartment";
	public static String USER_DEPARTMENT = "userDepartment";
	public static String PASSWORD = "password";
	public static String REGISTERED = "registered";
	public static String USER_PASSWORD = "userPassword";
	public static String ADMIN_PASSWORD = "adminPassword";
	public static String FILTER_BY = "filterBy";
	public static String PUSH_MESSAGE = "pushMessage";
	public static String PUSH_CHANNEL = "pushChannel";
	
	public static String LAUNCH_PACKAGE_NAME = "ajman.university.grad.project.eventshare.user";

	// Cron Service in minutes
	public static int CRON_SERVICE_INTERVAL = 30;
	
	// Nfc tag keys	
	public static final byte[] KEYA_NEURO = {
			(byte) 0xc1, (byte) 0xf9, (byte) 0xd3, (byte) 0x45, (byte) 0x23, (byte) 0x11
	};

	public static final byte[] KEYA_FAKE = {
			(byte) 0xd3, (byte) 0xf7, (byte) 0xd3, (byte) 0xf7, (byte) 0xd3, (byte) 0xf7
	};
}

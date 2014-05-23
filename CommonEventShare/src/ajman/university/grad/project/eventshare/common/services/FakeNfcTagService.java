package ajman.university.grad.project.eventshare.common.services;

import java.util.ArrayList;
import java.util.List;

import ajman.university.grad.project.eventshare.common.contracts.ITagService;
import ajman.university.grad.project.eventshare.common.models.Event;
import android.util.Log;

public class FakeNfcTagService implements ITagService{
	private static final String LOG_TAG = "FakeNfcTagService";
	
	@Override
	public List<Event> readEvents() {
		List<Event> readEvents = new ArrayList<Event>();
		Event event = new Event();
		event.setId(2);
		event.setTitle("Heart Transplant");
		event.setDepartment("Neurology");
		event.setDescription("Patient has low blood count");
		event.setNameDoc("Dr. Suhail Al Rukn");
		event.setNamePat("John Carter");
		event.setLocation("OR 3");
		event.setFromDay(14);
		event.setFromMonth(4);
		event.setFromYear(2015);
		event.setFromDayHour(9);
		event.setFromMinute(40);
		event.setToDayHour(10);
		event.setToMinute(26);
		event.setAlarmable(false);
		event.setExpired(false);
		readEvents.add(event);
		
		event = new Event();
		event.setId(3);
		event.setTitle("Biopsy");
		event.setDepartment("Neurology");
		event.setDescription("Patient has requested anesthesia");
		event.setNameDoc("Dr. Suhail Al Rukn");
		event.setNamePat("Kelse Milllers");
		event.setLocation("OR 4");
		event.setFromDay(14);
		event.setFromMonth(4);
		event.setFromYear(2015);
		event.setFromDayHour(9);
		event.setFromMinute(55);
		event.setToDayHour(10);
		event.setToMinute(56);
		event.setAlarmable(false);
		event.setExpired(false);
		readEvents.add(event);
		
		event = new Event();
		event.setId(4);
		event.setTitle("Brain Tumor Removal");
		event.setDepartment("Neurology");
		event.setDescription("No info");
		event.setNameDoc("Dr. Mohammed Saadah");
		event.setNamePat("Tim Parker");
		event.setLocation("OR 1");
		event.setFromDay(14);
		event.setFromMonth(4);
		event.setFromYear(2014);
		event.setFromDayHour(10);
		event.setFromMinute(35);
		event.setToDayHour(15);
		event.setToMinute(56);
		event.setAlarmable(false);
		event.setExpired(false);
		readEvents.add(event);
		
		event = new Event();
		event.setId(4);
		event.setTitle("Biopsy");
		event.setDepartment("Cardiology");
		event.setDescription("No info");
		event.setNameDoc("Dr. Mais M Mauwfak");
		event.setNamePat("Clark Parker");
		event.setLocation("OR 1");
		event.setFromDay(16);
		event.setFromMonth(4);
		event.setFromYear(2015);
		event.setFromDayHour(18);
		event.setFromMinute(35);
		event.setToDayHour(18);
		event.setToMinute(56);
		event.setAlarmable(false);
		event.setExpired(false);
		readEvents.add(event);
		

		event = new Event();
		event.setId(5);
		event.setTitle("Heart Transplant");
		event.setDepartment("Cardiology");
		event.setDescription("No info");
		event.setNameDoc("Dr. Mais M Mauwfak");
		event.setNamePat("Allen Parker");
		event.setLocation("OR 2");
		event.setFromDay(16);
		event.setFromMonth(4);
		event.setFromYear(2015);
		event.setFromDayHour(16);
		event.setFromMinute(35);
		event.setToDayHour(15);
		event.setToMinute(56);
		event.setAlarmable(false);
		event.setExpired(false);
		readEvents.add(event);
		
		event = new Event();
		event.setId(6);
		event.setTitle("Cardio-Something");
		event.setDepartment("Cardiology");
		event.setDescription("No info");
		event.setNameDoc("Dr. Moosa Kunhi");
		event.setNamePat("Richard Stone");
		event.setLocation("OR 2");
		event.setFromDay(16);
		event.setFromMonth(4);
		event.setFromYear(2015);
		event.setFromDayHour(16);
		event.setFromMinute(40);
		event.setToDayHour(15);
		event.setToMinute(56);
		event.setAlarmable(false);
		event.setExpired(false);
		readEvents.add(event);


		Log.d(LOG_TAG, "Read events .. FakeNFCRead");
		return readEvents;
	}

	@Override
	public void writeEvents(List<Event> events) {
		//... blah blah
	}

	public void eraseEvents() {
	}
}

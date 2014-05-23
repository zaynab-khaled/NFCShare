package ajman.university.grad.project.eventshare.common.models;

import java.io.Serializable;

public class Event implements Serializable {
	private static final long serialVersionUID = 4861948165540136263L;

	private int id;
	private String title;
	private String department;
	private String description;
	private String location;
	private String nameDoc;
	private String namePat;
	private int fromDayHour;
	private int fromMinute;
	private int fromYear;
	private int fromMonth;
	private int fromDay;
	private int toDayHour;
	private int toMinute;
	private boolean expired;
	private boolean alarmable;
	
	public Event () {
		this.setId(-1);
		this.setTitle("");
		this.setDepartment("");
		this.setDescription("");
		this.setLocation("");
		this.setNameDoc("");
		this.setNamePat("");
		this.setFromDayHour(-1);
		this.setFromDay(-1);
		this.setFromMinute(-1);
		this.setFromMonth(-1);
		this.setFromYear(-1);
		this.setToDayHour(-1);
		this.setToMinute(-1);
		this.setExpired(false);
		this.setAlarmable(false);
	}

	public Event(int id, String title, String department, String description, String location,
			String nameDoc, String namePat, int fromDayHour, int fromMinute, int fromYear, 
			int fromMonth, int fromDay, int toDayHour, int toMinute, boolean isExpired, boolean isAlarmable) {
		super();
		this.id = id;
		this.title = title;
		this.department = department;
		this.description = description;
		this.location = location;
		this.nameDoc = nameDoc;
		this.namePat = namePat;
		this.fromDayHour = fromDayHour;
		this.fromMinute = fromMinute;
		this.fromYear = fromYear;
		this.fromMonth = fromMonth;
		this.fromDay = fromDay;
		this.toDayHour = toDayHour;
		this.toMinute = toMinute;
		this.expired = isExpired;
		this.alarmable = isAlarmable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getNameDoc() {
		return nameDoc;
	}

	public void setNameDoc(String nameDoc) {
		this.nameDoc = nameDoc;
	}

	public String getNamePat() {
		return namePat;
	}

	public void setNamePat(String namePat) {
		this.namePat = namePat;
	}

	public int getFromDayHour() {
		return fromDayHour;
	}

	public void setFromDayHour(int fromDayHour) {
		this.fromDayHour = fromDayHour;
	}

	public int getFromMinute() {
		return fromMinute;
	}

	public void setFromMinute(int fromMinute) {
		this.fromMinute = fromMinute;
	}

	public int getFromYear() {
		return fromYear;
	}

	public void setFromYear(int fromYear) {
		this.fromYear = fromYear;
	}

	public int getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(int fromMonth) {
		this.fromMonth = fromMonth;
	}

	public int getFromDay() {
		return fromDay;
	}

	public void setFromDay(int fromDay) {
		this.fromDay = fromDay;
	}

	public int getToDayHour() {
		return toDayHour;
	}

	public void setToDayHour(int toDayHour) {
		this.toDayHour = toDayHour;
	}

	public int getToMinute() {
		return toMinute;
	}

	public void setToMinute(int toMinute) {
		this.toMinute = toMinute;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	
	public boolean isAlarmable() {
		return alarmable;
	}

	public void setAlarmable(boolean alarmable) {
		this.alarmable = alarmable;
	}
}

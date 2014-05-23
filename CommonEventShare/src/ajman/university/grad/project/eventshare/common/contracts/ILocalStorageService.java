package ajman.university.grad.project.eventshare.common.contracts;

import java.util.List;

import ajman.university.grad.project.eventshare.common.models.Event;

public interface ILocalStorageService {
	//Database Event
	public void addEvent(Event event) throws Exception;
	public void updateEvent(Event event) throws Exception;
	public void removeEvent(Event event) throws Exception;
	public List<Event> getAllEvents() throws Exception;
	public int deleteDeclinedEvents() throws Exception;
	public int deleteAllEvents() throws Exception;
	public List<Event> filterByDoctorName(String docName) throws Exception;
	public List<Event> getExpiringAlarmableEvents(int minutes) throws Exception;
	
	//Application
	public void setUserPassword(String pwd);
	public String getUserPassword();
	public void setAdminPassword(String pwd);
	public String getAdminPassword();
	public void setRegistered(boolean reg);
	public boolean isRegistered();
	public void setUserDepartment(String dept);
	public String getUserDepartment();
	public void setAdminDepartment(String dept);
	public String getAdminDepartment();
	public void setPushMessage(String msg);
	public String getPushMessage();
	public void setPushChannel(String chnl);
	public String getPushChannel();
}

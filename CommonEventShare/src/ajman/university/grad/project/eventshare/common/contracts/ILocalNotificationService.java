package ajman.university.grad.project.eventshare.common.contracts;

import ajman.university.grad.project.eventshare.common.models.Event;

public interface ILocalNotificationService {
	public void showNotification(Event event);
	public void removeNotification(Event event);
}

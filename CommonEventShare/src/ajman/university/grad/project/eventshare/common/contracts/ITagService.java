package ajman.university.grad.project.eventshare.common.contracts;

import java.util.List;

import ajman.university.grad.project.eventshare.common.models.Event;

public interface ITagService {
	public List<Event> readEvents();
	public void writeEvents(List<Event> events);
	public void eraseEvents();
}

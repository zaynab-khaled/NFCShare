package ajman.university.grad.project.eventshare.common.contracts;

public interface IErrorService {
	public void log(String message);
	public void log(Exception e);
}

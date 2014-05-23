package ajman.university.grad.project.eventshare.user;

import ajman.university.grad.project.eventshare.common.contracts.IRemoteNotificationService;
import ajman.university.grad.project.eventshare.common.helpers.ApplicationContextProvider;
import ajman.university.grad.project.eventshare.common.services.ServicesFactory;
import ajman.university.grad.project.eventshare.helpers.Constants;

public class UserApp extends ApplicationContextProvider {

	private IRemoteNotificationService remoteNotificationService = ServicesFactory.getRemoteNotificationService();

	@Override
	public void onCreate() {
		super.onCreate();
		
		remoteNotificationService.register(Constants.PARSE_APP_ID, Constants.PARSE_APP_CLIENT_KEY);
	}
}
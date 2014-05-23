package ajman.university.grad.project.eventshare.common.contracts;

import java.util.List;
import java.util.Set;

import android.app.Activity;

public interface IRemoteNotificationService {
	public void register(String appId, String appKey);
	public void unRegister();
	public Set<String> retrieveSubscriptions();
	public void subscribe(List<String> channels, Activity launchActivity);
	public void unSubscribe(String channel);
	public void sendPushNotification(String appId, String appKey, String channel, String message);
}

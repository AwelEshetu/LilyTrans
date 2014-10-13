package lily.homecare;

import java.util.Date;

public interface TaskObsorver {
	/**
	 * Task update notifier 
	 */
	public void updateData();
	/**
	 * Task completed notifier 
	 */
	public void taskCompleted(Date date);
	
	/**
	 * Task start notifier 
	 */
	public void taskStarted(Date date);
	
	/**
	 * download completed 
	 */
	public void downloadCompleted();
	
	/**
	 * error notifier 
	 */
	public void error(String text);
}

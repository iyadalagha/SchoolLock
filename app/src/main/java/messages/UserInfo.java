package messages;

public class UserInfo{

	public static enum Status{ACTIVE, BLOCKED}

	String userId;
	String userName;
	int sessionStart;
	int sessionEnd;
	Status currentStatus = Status.ACTIVE;
	
	
	public UserInfo(){}
	
	public UserInfo(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSessionStart() {
		return sessionStart;
	}

	public void setSessionStart(int sessionStart) {
		this.sessionStart = sessionStart;
	}

	public int getSessionEnd() {
		return sessionEnd;
	}

	public void setSessionEnd(int sessionEnd) {
		this.sessionEnd = sessionEnd;
	}
	
	@Override
	public String toString() {
		return this.getUserName();
	}

	public Status getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

}

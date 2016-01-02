package user;

public class UserInfo {
	String mName;

	public UserInfo(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return "UserInfo [mName=" + mName + "]";
	}

}

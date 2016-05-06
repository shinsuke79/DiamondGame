package user;

public abstract class UserInfo {
	String mName;

	public UserInfo(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public abstract Class<? extends User> getUserClass();

	public abstract String getImageUrl();

	public abstract String getDescription();

	@Override
	public String toString() {
		return "UserInfo [mName=" + mName + "]";
	}

}

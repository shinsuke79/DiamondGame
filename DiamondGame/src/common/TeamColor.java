package common;

public enum TeamColor {
	RED("赤チーム"), GREEN("緑チーム"), YELLOW("黃チーム");
	String mName;
	private TeamColor(String name) {
		mName = name;
	}
	public String getName(){
		return mName;
	}

	public TeamColor getNext(){
		switch(this){
		case RED:    return GREEN;
		case GREEN:  return YELLOW;
		case YELLOW: return RED;
		}
		return null;
	}
}

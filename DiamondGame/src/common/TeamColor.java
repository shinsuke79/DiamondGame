package common;

import game.Board.Cordinate;

public enum TeamColor {
	RED("赤チーム",    new Cordinate(0,9,9)),
	GREEN("緑チーム",  new Cordinate(9,9,0)),
	YELLOW("黃チーム", new Cordinate(9,0,9));

	static DGLog Log = new DGLog(TeamColor.class.getSimpleName());

	String mName;
	Cordinate rootCordinate;
	private TeamColor(String name, Cordinate root) {
		mName = name;
		rootCordinate = root;
	}
	public String getName(){
		return mName;
	}

	public TeamColor getNext(){
		TeamColor result = null;
		switch(this){
		case RED:    result = GREEN;   break;
		case GREEN:  result = YELLOW;  break;
		case YELLOW: result = RED;     break;
		}
		Log.fine("getNext this:%s -> next:%s", this.getName(), result);
		return result;
	}
	public Cordinate getRootCordinate() {
		Log.fine("getRootCordinate this:%s -> next:%s", this.getName(), rootCordinate);
		return rootCordinate;
	}
}

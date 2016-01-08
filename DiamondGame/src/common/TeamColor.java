package common;

import game.Board.Cordinate;

public enum TeamColor {
	RED("赤チーム",    new Cordinate(0,9,9)),
	GREEN("緑チーム",  new Cordinate(9,9,0)),
	YELLOW("黃チーム", new Cordinate(9,0,9));

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
		switch(this){
		case RED:    return GREEN;
		case GREEN:  return YELLOW;
		case YELLOW: return RED;
		}
		return null;
	}
	public Cordinate getRootCordinate() {
		return rootCordinate;
	}
}

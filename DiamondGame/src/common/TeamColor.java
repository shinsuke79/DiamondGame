package common;

import game.Board;
import game.Board.Cordinate;

/**
 * チームを表す列挙体
 * @author 0000140105
 *
 */
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

	/**
	 * このチームの日本語名を返却します
	 * @return ○○チーム
	 */
	public String getName(){
		return mName;
	}

	/**
	 * このチームの次のチームを返却します
	 * @return 次の番のチーム
	 */
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

	/**
	 * このチームの{@link Board}におけるルート座標(自陣の一番深い座標)を返却します
	 * @return 自陣の一番深い座標(x, y, z)
	 */
	public Cordinate getRootCordinate() {
		Log.fine("getRootCordinate this:%s -> next:%s", this.getName(), rootCordinate);
		return rootCordinate;
	}
}

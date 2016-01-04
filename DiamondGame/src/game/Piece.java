package game;

import common.TeamColor;

/**
 * ボード上のコマを表すクラス<br>
 * このクラスはUserに見える可能性があるため、setterは設けない
 * @author 0000140105
 *
 */
public class Piece {
	private TeamColor mTeamColor;
	private int       mNum;

	public Piece(int i, TeamColor color) {
		mTeamColor = color;
		mNum       = i;
	}

	public TeamColor getmTeamColor() {
		return mTeamColor;
	}

	public int getmNum() {
		return mNum;
	}

}

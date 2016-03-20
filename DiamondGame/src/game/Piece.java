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

	@Override
	public String toString() {
		return "Piece [mTeamColor=" + mTeamColor + ", mNum=" + mNum + "]";
	}

	/**
	 * わかりやすい文字列で返します
	 * @return 例) 緑3
	 */
	public String getNameStr(){
		return String.format("%s%d", mTeamColor.getSimpleName(), mNum);
	}

}

package view;

import java.util.ArrayList;
import java.util.List;

import game.GameManager;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import user.UserManager;
import user.humanUser.HumanUser;

public interface UserInterface {

	/**
	 * UIスレッドを開始するトリガーとなるAPI。
	 */
	void start();

	/**
	 * ゲームとユーザーの記録を管理するManagerを設定する。
	 * これらをメンバ変数に覚えておく必要がある
	 * @param gameMgr
	 * @param userMgr
	 */
	void setManager(GameManager gameMgr, UserManager userMgr);

	UIHand askHand(HumanUser humanUser, UserBoard userBoard);

	/**
	 * ユーザーの手を表す
	 * @author yone
	 *
	 */
	public class UIHand {
		public UIBoardSpot from;
		public List<UIBoardSpot> to;
		public UIHand() {
			from = null;
			to   = new ArrayList<>();
		}
	}

	/**
	 * ユーザーに見せるボード盤のマスの位置を表す
	 * @author yone
	 *
	 */
	public class UIBoardSpot {
		private char line;
		private int  column;
		private boolean isValid;

		public UIBoardSpot() {
			line  = 0;
			column = 0;
			isValid = false;
		}

		public boolean set(char line, int column){
			this.isValid = false;

			if( !(('A'<=line)&&(line<='M')) ){
				return false;
			}
			if( !((1<=column)&&(column<=18)) ){
				return false;
			}

			this.line    = line;
			this.column  = column;
			this.isValid = true;
			return true;
		}

		public char getLine() {
			return line;
		}

		public int getColumn() {
			return column;
		}

		public boolean isValid() {
			return isValid;
		}

		public UserCordinate toUserCordinate(){
			return null; // TODO
		}

		public static UIBoardSpot parseUserCordinate(UserCordinate uCordinate){
			return null; // TODO
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + column;
			result = prime * result + line;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UIBoardSpot other = (UIBoardSpot) obj;
			if (column != other.column)
				return false;
			if (line != other.line)
				return false;
			return true;
		}

		public String getValueStr() {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		public static UIBoardSpot parseString(String inputStrSpot) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}
	}
}

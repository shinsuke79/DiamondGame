package view;

import game.GameManager;
import user.UserManager;

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

	boolean askHand(UIBoardSpot userHand);

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


	}
}

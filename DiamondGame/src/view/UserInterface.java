package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import game.GameManager;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import user.UserManager;
import user.humanUser.HumanUser;
import view.UserInterface.UIBoardSpot.UiUserSpotConvTable.UiUserSpotKey;

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
		static UiUserSpotConvTable spotConvTable = new UiUserSpotConvTable();

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
			if( !((1<=column)&&(column<=19)) ){
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

		/**
		 * UserCordinateを返却する
		 * @return
		 */
		public UserCordinate toUserCordinate(){
			return spotConvTable.getUserCordinateFromUiCordinate(line, column).clone();
		}

		/**
		 * 指定されたUserCordinateをUIBoardSpotに変換する
		 * @param uCordinate
		 * @return
		 */
		public static UIBoardSpot parseUserCordinate(UserCordinate uCordinate){
			Map<UiUserSpotKey, Integer> searchResult = spotConvTable.getUiCordinateFromUserCordinate(uCordinate);
			UIBoardSpot result = new UIBoardSpot();
			char line   = (char)searchResult.get(UiUserSpotKey.LINE).intValue();
			int  column = searchResult.get(UiUserSpotKey.COLUMN);
			result.set(line, column);
			return result;
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
			return line + String.valueOf(column);
		}

		public static UIBoardSpot parseString(String inputStrSpot) {
			/* エラーチェック */
			if(inputStrSpot == null){
				return null;
			}
			if(inputStrSpot.length() != 2 && inputStrSpot.length() != 3){
				return null;
			}
			UIBoardSpot result = new UIBoardSpot();
			char line   = inputStrSpot.charAt(0);
			int  column = Integer.valueOf(inputStrSpot.substring(1));
			result.set(line, column);
			if(result.isValid){
				return result;
			}else{
				return null;
			}
		}

		static class UiUserSpotConvMap{
			UserCordinate mUserCordinate;
			int mUICordinateLine;
			int mUICordinateClumn;
			public UiUserSpotConvMap(UserCordinate userCordinate, int uiCordinateLine, int uiCordinateClumn) {
				this.mUserCordinate = userCordinate;
				this.mUICordinateLine = uiCordinateLine;
				this.mUICordinateClumn = uiCordinateClumn;
			}
		}

		static class UiUserSpotConvTable {
			Set<UiUserSpotConvMap> mTable;

			public UiUserSpotConvTable() {
				this.mTable = new HashSet();
				initTable();
			}

			public enum UiUserSpotKey {
				LINE, COLUMN
			}

			public void initTable(){
				mTable.clear();

				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 6), 'A',   10));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 5), 'B',   9));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 6), 'B',   11));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 4), 'C',   8));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 5), 'C',   10));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 6), 'C',   12));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(9, 0), 'D',   1));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(8, 1), 'D',   3));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(7, 2), 'D',   5));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 4), 'D',   7));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 4), 'D',   9));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 5), 'D',   11));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 6), 'D',   13));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 7), 'D',   15));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 8), 'D',   17));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 9), 'D',   19));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(8, 0), 'E',   2));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(7, 1), 'E',   4));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 2), 'E',   6));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 3), 'E',   8));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 4), 'E',   10));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 5), 'E',   12));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 6), 'E',   14));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 7), 'E',   16));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 8), 'E',   18));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(7, 0), 'F',   3));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 1), 'F',   5));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 2), 'F',   7));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 3), 'F',   9));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 4), 'F',   11));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 5), 'F',   13));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 6), 'F',   15));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 7), 'F',   17));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(6, 0), 'G',   4));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 1), 'G',   6));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 2), 'G',   8));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 3), 'G',   10));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 4), 'G',   12));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 5), 'G',   14));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 6), 'G',   16));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(5, 0), 'H',   5));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 1), 'H',   7));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 2), 'H',   9));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 3), 'H',   11));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 4), 'H',   13));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 5), 'H',   15));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(4, 0), 'I',   6));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 1), 'I',   8));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 2), 'I',   10));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 3), 'I',   12));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 4), 'I',   14));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(3, 0), 'J',   7));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 1), 'J',   9));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 2), 'J',   11));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 3), 'J',   13));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(2, 0), 'K',   8));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 1), 'K',  10));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 2), 'K',  12));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(1, 0), 'L',  9));
				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 1), 'L', 11));

				mTable.add(new UiUserSpotConvMap(new UserCordinate(0, 0), 'M', 10));
			}

			UserCordinate getUserCordinateFromUiCordinate(int line, int column){
				Optional<UiUserSpotConvMap> searchResult = mTable.stream().filter(map -> map.mUICordinateLine == line && map.mUICordinateClumn == column).findFirst();
				return searchResult.get().mUserCordinate.clone();
			}

			Map<UiUserSpotKey, Integer> getUiCordinateFromUserCordinate(UserCordinate uCordinate){
				Optional<UiUserSpotConvMap> searchResult = mTable.stream().filter(map -> map.mUserCordinate.equals(uCordinate)).findFirst();
				Map<UiUserSpotKey, Integer> result = new HashMap<>();
				result.put(UiUserSpotKey.LINE,   searchResult.get().mUICordinateLine );
				result.put(UiUserSpotKey.COLUMN, searchResult.get().mUICordinateClumn);
				return result;
			}
		}
	}
}

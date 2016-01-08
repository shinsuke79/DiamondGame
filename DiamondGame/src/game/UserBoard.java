package game;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import common.Direction;
import common.TeamColor;
import game.Board.Cordinate;
import game.Board.Spot;

public class UserBoard {
	private Board          cloneBoard;
	private UserSpot[][]   boards;
	private TeamColor      mainTeam;
	private EnumMap<TeamColor, Set<UserPiece>> pieces;

	UserBoard(TeamColor mainTeam, Board board) {
		// ShallowCopy(DeepCopyはあえてしない)
		this.cloneBoard   = (Board)board.clone();
		changeMainTeam(mainTeam);
	}

	/* -------- Userに公開するAPI --------  */
	public void changeMainTeam(TeamColor team){
		this.mainTeam     = team;
		this.boards       = new UserSpot[10][10];
		for(TeamColor tc: TeamColor.values()){
			pieces.put(tc, new HashSet<>());
		}

		/* 各チームのベース地点を0,0として、UserSpotにSpotを登録してく
		   SpotにPieceが配置されていれば、UserSpotに変換してUserSpotに登録する */

		// チームごとのルートを取得
		Spot rootSpot = cloneBoard.getSpotFromCordinate(mainTeam.getRootCordinate());

		// ルートから、すべてのSpotの右前、左上と展開していく
		exactSpot(team, rootSpot, new UserCordinate(0, 0));
	}

	/* -------- Userには公開しないAPI --------  */
	private void exactSpot(TeamColor team, Spot spot, UserCordinate uCordinate){
		// 引数に該当するUserSpotをnewする
		UserSpot targetSpot = boards[uCordinate.x][uCordinate.y] = new UserSpot(spot, uCordinate);

		// SpotにPieceが存在すればUserSpotにUserPieceとして配置する
		if(spot.mPiece != null){
			UserPiece uPiece = new UserPiece(spot.mPiece);
			targetSpot.setPiece(uPiece);
			pieces.get(spot.mPiece.getmTeamColor()).add(uPiece);
		}

		// 右前側のSpot チーム視点の欲しい方角(右前)を基準方角し、次のSpotを取得する
		Direction convedDirectionRightF = Direction.RIGHT_FRONT.getRelativeDirection(team);
		Spot rightFront = cloneBoard.getSpotFromCordinate(spot.mCordinate.getMovedCordinate(1, convedDirectionRightF));
		if(rightFront != null){
			exactSpot(team, rightFront, uCordinate.getMovedCordinate(1, Direction.RIGHT_FRONT));
		}

		// 左前側のSpot チーム視点の欲しい方角(左前)を基準方角に変換し、次のSpotを取得する
		Direction convedDirectionLeftF = Direction.LEFT_FRONT. getRelativeDirection(team);
		Spot leftFront = cloneBoard.getSpotFromCordinate(spot.mCordinate.getMovedCordinate(1, convedDirectionLeftF));
		if(leftFront != null){
			exactSpot(team, leftFront, uCordinate.getMovedCordinate(1, Direction.LEFT_FRONT));
		}

	}

	public class UserPiece{
		private Piece basePiece;

		/* -------- Userに公開するAPI --------  */

		public TeamColor geTeamColor(){
			return basePiece.getmTeamColor();
		}

		/* -------- Userには公開しないAPI --------  */

		UserPiece(Piece basePiece) {
			this.basePiece = basePiece;
		}

		Piece getBasePiece(){
			return basePiece;
		}
	}

	public class UserSpot {
		private Spot baseSpot;
		private UserPiece piece;
		private UserCordinate cordinate;

		/* -------- Userに公開するAPI --------  */

		public TeamColor getTeamColor(){
			return baseSpot.mTeam;
		}

		public UserCordinate getCordinate(){
			return cordinate;
		}

		public UserPiece getPiece(){
			return piece;
		}

		/* -------- Userには公開しないAPI --------  */

		UserSpot(Spot baseSpot, UserCordinate cordinate) {
			this.baseSpot = baseSpot;
			this.cordinate = cordinate;
		}

		Cordinate getBaseCordinate(){
			return baseSpot.mCordinate;
		}

		void setPiece(UserPiece piece){
			this.piece = piece;
		}
	}

	public static class UserCordinate implements Cloneable{
		private int x;
		private int y;
		public UserCordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public int getX(){
			return x;
		}
		public int getY(){
			return y;
		}

		/**
		 * 指定された方角へdistance移動させた座標を返却する
		 * @param cordinate
		 * @param distance
		 * @return
		 */
		public UserCordinate getMovedCordinate(int distance, Direction direction) {
			assert distance == 2 || distance == 1;

			int dx=0, dy=0;

			switch(direction){
			case RIGHT_FRONT:
				dx = 0;
				dy = distance;
				break;
			case RIGHT:
				dx = -distance;
				dy = distance;
				break;
			case RIGHT_REAR:
				dx = -distance;
				dy = 0;
				break;
			case LEFT_REAR:
				dx = 0;
				dy = -distance;
				break;
			case LEFT:
				dx = distance;
				dy = -distance;
				break;
			case LEFT_FRONT:
				dx = distance;
				dy = 0;
				break;
			}

			return new UserCordinate(this.getX()+dx, this.getY()+dy);
		}
	}
}

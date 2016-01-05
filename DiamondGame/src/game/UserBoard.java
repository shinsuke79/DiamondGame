package game;

import java.util.HashSet;
import java.util.Set;

import common.TeamColor;
import game.Board.Spot;

public class UserBoard {
	private Board          cloneBoard;
	private UserSpot[][]   boards;
	private TeamColor      mainTeam;
	private Set<UserPiece> redPieces;
	private Set<UserPiece> yellowPieces;
	private Set<UserPiece> greenPieces;

	UserBoard(TeamColor mainTeam, Board board) {
		this.cloneBoard   = (Board)board.clone();
		changeMainTeam(mainTeam);
	}

	/* -------- Userに公開するAPI --------  */
	public void changeMainTeam(TeamColor team){
		this.mainTeam     = team;
		this.boards       = new UserSpot[10][10];
		this.redPieces    = new HashSet<>();
		this.yellowPieces = new HashSet<>();
		this.greenPieces  = new HashSet<>();
		// TODO
	}

	/* -------- Userには公開しないAPI --------  */

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

		Spot getBaseSpot(){
			return baseSpot;
		}

		void setPiece(UserPiece piece){
			this.piece = piece;
		}
	}

	public class UserCordinate{
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
	}
}

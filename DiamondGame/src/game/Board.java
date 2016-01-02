package game;

import common.TeamColor;

public class Board {
	/**
	 * x, y, z
	 */
	Spot[][][] mSpots;
	Piece[] mRedPieces;
	Piece[] mYellowPieces;
	Piece[] mGreenPieces;

	public Board() {
	}

	private void createBaseBoard() {
		/* Spot配列の初期化 */
		mSpots = new Spot[13][13][13];

		/* Piece配列の初期化 */
		mRedPieces = new Piece[10];
		for(int i=0;i<10;i++){ mRedPieces[i]=new Piece(i, TeamColor.RED); };
		mYellowPieces = new Piece[10];
		for(int i=0;i<10;i++){ mYellowPieces[i]=new Piece(i, TeamColor.YELLOW); };
		mGreenPieces = new Piece[10];
		for(int i=0;i<10;i++){ mGreenPieces[i]=new Piece(i, TeamColor.GREEN); };

		/* すべてのSpotを手入力していく */
		// x=0
		mSpots[0][9][9]=new Spot(mRedPieces[0]);
		// x=1
		mSpots[1][9][8]=new Spot(mRedPieces[1]); mSpots[1][8][9]=new Spot(mRedPieces[2]);
		// x=2
		mSpots[2][9][7]=new Spot(mRedPieces[3]); mSpots[2][8][8]=new Spot(mRedPieces[4]);
		mSpots[2][7][9]=new Spot(mRedPieces[5]);
		// x=3
		mSpots[3][12][3]=new Spot(TeamColor.YELLOW); mSpots[3][11][4]=new Spot(TeamColor.YELLOW);
		mSpots[3][10][5]=new Spot(TeamColor.YELLOW); mSpots[3][9][6]=new Spot(TeamColor.YELLOW, mRedPieces[6]);
		mSpots[3][8][7]=new Spot(mRedPieces[7]);  mSpots[3][7][8]=new Spot(mRedPieces[8]);
		mSpots[3][6][9]=new Spot(TeamColor.GREEN, mRedPieces[9]);  mSpots[3][5][10]=new Spot(TeamColor.GREEN);
		mSpots[3][4][11]=new Spot(TeamColor.GREEN); mSpots[3][3][12]=new Spot(TeamColor.GREEN);
		// x=4
		mSpots[4][11][3]=new Spot(TeamColor.YELLOW); mSpots[4][10][4]=new Spot(TeamColor.YELLOW);
		mSpots[4][9][5]=new Spot(TeamColor.YELLOW);  mSpots[4][8][6]=new Spot();
		mSpots[4][7][7]=new Spot(); mSpots[4][6][8]=new Spot();  mSpots[4][5][9]=new Spot(TeamColor.GREEN);
		mSpots[4][4][10]=new Spot(TeamColor.GREEN); mSpots[4][3][11]=new Spot(TeamColor.GREEN);
		// x=5
		mSpots[5][10][3]=new Spot(TeamColor.YELLOW);
		mSpots[5][9][4]=new Spot(TeamColor.YELLOW);  mSpots[5][8][5]=new Spot();
		mSpots[5][7][6]=new Spot(); mSpots[5][6][7]=new Spot();  mSpots[5][5][8]=new Spot();
		mSpots[5][4][9]=new Spot(TeamColor.GREEN); mSpots[5][3][10]=new Spot(TeamColor.GREEN);
		// x=6
		mSpots[6][9][3]=new Spot(TeamColor.YELLOW, mGreenPieces[9]);  mSpots[6][8][4]=new Spot();
		mSpots[6][7][5]=new Spot(); mSpots[6][6][6]=new Spot();  mSpots[6][5][7]=new Spot();
		mSpots[6][4][8]=new Spot(); mSpots[6][3][9]=new Spot(TeamColor.GREEN, mYellowPieces[9]);
		// x=7
		mSpots[7][9][2]=new Spot(mGreenPieces[5]);  mSpots[7][8][3]=new Spot(mGreenPieces[8]);
		mSpots[7][7][4]=new Spot(); mSpots[7][6][5]=new Spot();  mSpots[7][5][6]=new Spot();
		mSpots[7][4][7]=new Spot(); mSpots[7][3][8]=new Spot(mYellowPieces[8]);
		mSpots[7][2][9]=new Spot(mYellowPieces[5]);
		// x=8
		mSpots[8][9][1]=new Spot(mGreenPieces[2]);  mSpots[8][8][2]=new Spot(mGreenPieces[4]);
		mSpots[8][7][3]=new Spot(mGreenPieces[7]);  mSpots[8][6][4]=new Spot();
		mSpots[8][5][5]=new Spot();  mSpots[8][4][6]=new Spot(); mSpots[8][3][7]=new Spot(mYellowPieces[7]);
		mSpots[8][2][8]=new Spot(mYellowPieces[4]); mSpots[8][1][9]=new Spot(mYellowPieces[2]);
		// x=9
		mSpots[9][9][0]=new Spot(mGreenPieces[0]);  mSpots[9][8][1]=new Spot(mGreenPieces[1]);
		mSpots[9][7][2]=new Spot(mGreenPieces[3]);  mSpots[9][6][3]=new Spot(TeamColor.RED, mGreenPieces[6]);
		mSpots[9][5][4]=new Spot(TeamColor.RED);    mSpots[9][4][5]=new Spot(TeamColor.RED);
		mSpots[9][3][6]=new Spot(TeamColor.RED, mYellowPieces[6]); mSpots[9][2][7]=new Spot(mYellowPieces[3]);
		mSpots[9][1][8]=new Spot(mYellowPieces[1]); mSpots[9][0][9]=new Spot(mYellowPieces[0]);
		// x=10
		mSpots[10][5][3]=new Spot(TeamColor.RED);  mSpots[10][4][4]=new Spot(TeamColor.RED);
		mSpots[10][3][5]=new Spot(TeamColor.RED);
		// x=11
		mSpots[11][4][3]=new Spot(TeamColor.RED);  mSpots[11][3][4]=new Spot(TeamColor.RED);
		// x=12
		mSpots[12][3][3]=new Spot(TeamColor.RED);

		// Spotクラスに座標を登録する
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					Spot spot = mSpots[x][y][z];
					if(spot != null){
						spot.setCoordinate(x, y, z);
					}
				}
			}
		}

	}

	public void placePiece() {
		createBaseBoard();
	}

	public void movePiece(Move mMove) {
	}

	public int getTeamPoint(TeamColor teamColor) {
		return 0;
	}

	public boolean isFinishedTeam(TeamColor teamColor){
		return false;
	}

	public UserBoard getUserBoard(TeamColor myTeam) {
		return new UserBoard();
	}

	public boolean isMoveValid(Move move) {
		return true;
	}

	public class Spot{
		Piece     mPiece;
		TeamColor mTeam;
		int       mX, mY, mZ;
		public Spot() {
			mTeam  = null;
			mPiece = null;
		}
		public Spot(TeamColor team, Piece piece) {
			mTeam  = team;
			mPiece = piece;
		}
		public Spot(TeamColor team) {
			mTeam  = team;
			mPiece = null;
		}
		public Spot(Piece piece) {
			mTeam  = null;
			mPiece = piece;
		}
		public void setCoordinate(int x, int y, int z){
			mX = x;
			mY = y;
			mZ = z;
		}
	}

}

package game;

import common.TeamColor;

public class Board {

	public void placePiece() {
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

}

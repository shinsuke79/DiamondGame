package game;

import common.Direction;
import common.TeamColor;
import user.User;

public class Move {
	private User           mUser;
	private TeamColor      mTeam;

	/**
	 * どの駒を
	 */
	public  Piece          mPiece;

	/**
	 * どの方向に
	 */
	public  Direction      mDirection;

	/**
	 * 何マス進めるか
	 */
	public  int            mDistance;

	public User getmUser() {
		return mUser;
	}

	public TeamColor getmTeam() {
		return mTeam;
	}

	public Move(User user) {
		mUser = user;
		mTeam = user.getMyTeam();
	}

}

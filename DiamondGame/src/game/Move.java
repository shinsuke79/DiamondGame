package game;

import java.util.ArrayList;
import java.util.List;

import common.TeamColor;
import game.UserBoard.UserPiece;
import game.UserBoard.UserSpot;
import user.User;

public class Move {
	private User           mUser;
	private TeamColor      mTeam;

	/**
	 * どの駒を
	 */
	public  UserPiece mPiece;

	/**
	 * どう進めるか(配列)
	 */
	public List<UserSpot> mMoveSpots;

	public User getmUser() {
		return mUser;
	}

	public TeamColor getmTeam() {
		return mTeam;
	}

	public Move(User user) {
		mUser = user;
		mTeam = user.getMyTeam();
		mMoveSpots = new ArrayList<>();
	}

}

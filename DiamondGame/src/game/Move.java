package game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	public Move cloneMove(){
		Move clone = new Move(mUser);
		clone.mTeam      = this.mTeam;
		clone.mPiece     = this.mPiece;
		clone.mMoveSpots = new ArrayList<>(this.mMoveSpots);
		return clone;
	}

	@Override
	public String toString() {
		return "Move [mTeam=" + mTeam.getName()
			+ ", mPiece=" + mPiece != null ? mPiece.getNameStr():"null"
			+ ", mMoveSpots=" + mMoveSpots.stream().map((uSpot)->uSpot.getCordinate()).collect(Collectors.toList()) + "]";
	}
}

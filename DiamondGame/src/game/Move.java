package game;

import common.Direction;
import user.User;

public class Move {
	private User           mUser;
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

	public Move(User user) {
		mUser = user;
	}

}

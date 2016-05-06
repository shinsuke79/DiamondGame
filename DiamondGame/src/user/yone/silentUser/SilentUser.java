package user.yone.silentUser;

import common.DGLog;
import common.TeamColor;
import game.Move;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import game.UserBoard.UserPiece;
import user.User;
import user.UserInfo;

public class SilentUser extends User {

	private DGLog mLog;

	public SilentUser(UserInfo userInfo) {
		super(userInfo);

		// ログの有効化
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
	}

	@Override
	protected void think(UserBoard userBoard, Move moveResult) {
		say("...");

		UserCordinate cordinate = null;

		UserCordinate cordinate1 = new UserCordinate(0, 3);
		UserPiece piece1 = userBoard.getPieceFromUserCordinate(cordinate1);
		if(piece1 != null){
			say("... piece1");
			moveResult.mPiece = piece1;
			moveResult.mMoveSpots.add(userBoard.getCordinateFromUserCordinate(new UserCordinate(1, 3)));
			cordinate = cordinate1;
			say("... %s %s -> %s", moveResult.mPiece.getNameStr(), cordinate, moveResult.mMoveSpots.get(0));
			return;
		}

		UserCordinate cordinate2 = new UserCordinate(3, 0);
		UserPiece piece2 = userBoard.getPieceFromUserCordinate(cordinate2);
		if(piece2 != null){
			say("... piece2");
			moveResult.mPiece = piece2;
			moveResult.mMoveSpots.add(userBoard.getCordinateFromUserCordinate(new UserCordinate(3, 1)));
			cordinate = cordinate2;
			say("... %s %s -> %s", moveResult.mPiece.getNameStr(), cordinate, moveResult.mMoveSpots.get(0));
			return;
		}

	}

	@Override
	public void handShake(User handShakeUser, TeamColor teamColor) {
		say("...");
	}

	@Override
	public void notifyCancelled() {
		// NOP
	}

	public void say(String fmt, Object... args){
		mLog.info(getName()+"「"+fmt+"」", args);
	}

	public static class SilentUserInfo extends UserInfo {

		public SilentUserInfo(String name) {
			super(name);
		}

		@Override
		public Class<? extends User> getUserClass() {
			return SilentUser.class;
		}

		@Override
		public String getImageUrl() {
			return null;
		}

		@Override
		public String getDescription() {
			return "何もしないユーザー";
		}
	}

}

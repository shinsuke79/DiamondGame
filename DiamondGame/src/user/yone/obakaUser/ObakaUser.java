package user.yone.obakaUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import common.DGLog;
import common.TeamColor;
import game.Move;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import game.UserBoard.UserPiece;
import user.User;
import user.UserInfo;

/**
 * 現在選択できるPieceからランダムに駒を選択し、
 * 移動できるランダムな方向に移動させるおばかなUser<br>
 * でもテストの観点では非常に重要な役割を持ちます
 * @author 0000140105
 *
 */
public class ObakaUser extends User {

	private DGLog mLog;

	public ObakaUser(UserInfo userInfo) {
		super(userInfo);

		// ログの有効化
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
	}

	@Override
	protected void think(UserBoard userBoard, Move moveResult) {
		mLog.info("%s「俺の番だ」", getName());

		// 乱数生成器
		Random rnd = new Random();

		// 移動できるはずのPieceを取得
		List<UserPiece> movableUserPieces = new ArrayList<>(userBoard.getMovableUserPieces(getMyTeam()));
		mLog.info("%s「移動できる駒はこれか. %s」", getName(), movableUserPieces.stream()
													.map((pi)->pi.getNameStr()).collect(Collectors.toList()));

		// もう決めちゃう
		UserPiece userPiece = movableUserPieces.get(rnd.nextInt(movableUserPieces.size()));
		mLog.info("%s「これにする！ %s」", getName(), userPiece.getNameStr());

		// Moveに登録する
		moveResult.mPiece = userPiece;

		// 動かすと心に決めたUserPieceのUserSpotを取得する
		UserCordinate firstCordinate   = userBoard.getUserCordinateFromPiece(userPiece);
		UserCordinate currentCordinate = firstCordinate;
		boolean  isFirst     = true;
		int      count       = 0;

		// ぐるぐる回しながらcurrentSpotを更新していく
		while(true){
			if(count > 2){
				mLog.info("%s「やっぱもういいかな」", getName());
				break;
			}

			mLog.info("%s「%s は」", getName(), currentCordinate);

			// 取得できるSpot一覧を取得
			List<UserCordinate> movableCordinates = new ArrayList<>(userBoard.getMovableCordinates(currentCordinate, isFirst));
			mLog.info("%s「ここに移動できるのか %s」", getName(), movableCordinates);

			// エラーチェック
			if(isFirst && movableCordinates.size()==0){
				assert false;
			}

			// もう移動できないなら終了
			if(movableCordinates.size()==0){
				mLog.info("%s「ってどこにも行けないやん…」", getName());
				break;
			}

			// もう動かすマスを決めちゃう
			UserCordinate nextUserCordinate = movableCordinates.get(rnd.nextInt(movableCordinates.size()));
			mLog.info("%s「これにする！ %s」", getName(), nextUserCordinate);

			// 移動元に戻っちゃうようならその場で打ち切り
			if(firstCordinate.equals(nextUserCordinate) || moveResult.mMoveSpots.contains(nextUserCordinate)){
				mLog.info("%s「ここ移動したとこだわ…」", getName());
				break;
			}

			// 移動を登録
			moveResult.mMoveSpots.add(userBoard.getCordinateFromUserCordinate(nextUserCordinate));

			// 隣接するSpotへの移動なら即終了
			if(userBoard.isAdjacentCordinate(currentCordinate, nextUserCordinate)){
				break;
			}

			// nextをcurrentに設定
			currentCordinate = nextUserCordinate;

			// 初めてフラグを落とす
			isFirst = false;
			count++;

			mLog.info("%s「もう一回考えてみるか」", getName());
		}

		mLog.info("%s「こんなもんかな」", getName());
	}

	@Override
	public void handShake(User handShakeUser, TeamColor teamColor) {
		mLog.info("こんにちは、%s！(何も考えていない)", handShakeUser.getName());
	}

	@Override
	public void notifyCancelled() {
		// 何も蓄えていないので何もしない
	}

	public static class ObakaUserInfo extends UserInfo {

		public ObakaUserInfo(String name) {
			super(name);
		}

		@Override
		public Class<? extends User> getUserClass() {
			return ObakaUser.class;
		}

		@Override
		public String getImageUrl() {
			return null;
		}
	}

}

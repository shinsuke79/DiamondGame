package user.yone.obakaUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import common.DGLog;
import common.TeamColor;
import game.Move;
import game.UserBoard;
import game.UserBoard.UserPiece;
import game.UserBoard.UserSpot;
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
		mLog.info("おばか%s「俺の番だ」", getName());

		// 乱数生成器
		Random rnd = new Random();

		// 移動できるはずのPieceを取得
		List<UserPiece> movableUserPieces = new ArrayList<>(userBoard.getMovableUserPieces(getMyTeam()));
		mLog.info("おばか%s「移動できる駒はこれか. %s」", getName(), movableUserPieces.stream()
													.map((pi)->pi.getNameStr()).collect(Collectors.toList()));

		// もう決めちゃう
		UserPiece userPiece = movableUserPieces.get(rnd.nextInt(movableUserPieces.size()));
		mLog.info("おばか%s「これにする！ %s」", getName(), userPiece.getNameStr());

		// Moveに登録する
		moveResult.mPiece = userPiece;

		// 動かすと心に決めたUserPieceのUserSpotを取得する
		UserSpot firstSpot   = userBoard.getUserSpotFromPiece(userPiece);
		UserSpot currentSpot = firstSpot;
		boolean  isFirst     = true;
		int      count       = 0;

		// ぐるぐる回しながらcurrentSpotを更新していく
		while(true){
			if(count > 2){
				mLog.info("おばか%s「やっぱもういいかな」", getName());
				break;
			}

			mLog.info("おばか%s「%s は」", getName(), currentSpot);

			// 取得できるSpot一覧を取得
			List<UserSpot> movableSpots = new ArrayList<>(userBoard.getMovableSpots(currentSpot, isFirst));
			mLog.info("おばか%s「ここに移動できるのか %s」", getName(), movableSpots.stream().map((spot)->spot.getCordinate())
													.collect(Collectors.toList()));

			// エラーチェック
			if(isFirst && movableSpots.size()==0){
				assert false;
			}

			// もう移動できないなら終了
			if(movableSpots.size()==0){
				mLog.info("おばか%s「ってどこにも行けないやん…」", getName());
				break;
			}

			// もう動かすマスを決めちゃう
			UserSpot nextUserSpot = movableSpots.get(rnd.nextInt(movableSpots.size()));
			mLog.info("おばか%s「これにする！ %s」", getName(), nextUserSpot);

			// 移動元に戻っちゃうようならその場で打ち切り
			if(firstSpot==nextUserSpot || moveResult.mMoveSpots.contains(nextUserSpot)){
				mLog.info("おばか%s「ここ移動したとこだわ…」", getName());
				break;
			}

			// 移動を登録
			moveResult.mMoveSpots.add(nextUserSpot);

			// 隣接するSpotへの移動なら即終了
			if(userBoard.isAdjacentSpot(currentSpot, nextUserSpot)){
				break;
			}

			// nextをcurrentに設定
			currentSpot = nextUserSpot;

			// 初めてフラグを落とす
			isFirst = false;
			count++;

			mLog.info("おばか%s「もう一回考えてみるか」", getName());
		}

		mLog.info("おばか%s「こんなもんかな」", getName());
	}

	@Override
	public void handShake(User handShakeUser, TeamColor teamColor) {
		mLog.info("こんにちは、%s！(何も考えていない)", handShakeUser.getName());
	}

	@Override
	public void notifyCancelled() {
		// 何も蓄えていないので何もしない
	}

}

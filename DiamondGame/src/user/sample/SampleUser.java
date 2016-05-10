package user.sample;

import java.util.Random;
import java.util.Set;

import common.TeamColor;
import game.Board.Cordinate;
import game.Move;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import game.UserBoard.UserPiece;
import user.User;
import user.UserInfo;

public class SampleUser extends User {

	public SampleUser(UserInfo userInfo) {
		super(userInfo);
	}

	@Override
	protected void think(UserBoard userBoard, Move moveResult) {
		// 移動先がある駒一覧を取得
		UserPiece[] pieces = (UserPiece[])userBoard.getMovableUserPieces(getMyTeam()).toArray();
		// 移動する駒をランダムに決定
		UserPiece targetPiece = pieces[new Random().nextInt(pieces.length)];
		moveResult.mPiece = targetPiece;

		// 移動する駒が移動できるマス一覧を取得
		UserCordinate pieceCordinate = userBoard.getUserCordinateFromPiece(targetPiece);
		Set<UserCordinate> movableCordinates = userBoard.getMovableCordinates(pieceCordinate);

		// 評価値が最大になる移動先を選択
		Cordinate targetCordinate = movableCordinates.stream().map((UserCordinate u)->{
			// それぞれの移動先でMoveを作成
			Move move = moveResult.cloneMove();
			move.mMoveSpots.add(userBoard.getCordinateFromUserCordinate(pieceCordinate));
			return move;
		})
		.max((Move move1, Move move2)->{
			// それぞれのMoveをUserBoardに適用して評価値を比較
			int point1=0, point2=0;
			userBoard.init();
			userBoard.move(move1);
			point1 = userBoard.getPoint(getMyTeam());
			userBoard.init();
			userBoard.move(move2);
			point2 = userBoard.getPoint(getMyTeam());
			return point1 - point2;
		})
		// 最大となったMoveのCordinateを取得
		.get().mMoveSpots.get(0);
		moveResult.mMoveSpots.add(targetCordinate);
	}

	@Override
	public void handShake(User handShakeUser, TeamColor teamColor) {
		// 相手の情報が渡されます
		// NOP
	}

	@Override
	public void notifyCancelled() {
		// 制限時間切れになった時にコールされる関数です
		// NOP
	}

}

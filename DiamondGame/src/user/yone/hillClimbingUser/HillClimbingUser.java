package user.yone.hillClimbingUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import common.DGLog;
import common.TeamColor;
import game.Board.Cordinate;
import game.Move;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import game.UserBoard.UserPiece;
import user.User;
import user.UserInfo;

/**
 * 山登り法(hill climbing)を使用したユーザー<br>
 * 現状取りうる手の中で、一番UserCordinateのポイント(評価関数)が<br>
 * 高かった手を選択する
 * @author 0000140105
 *
 */
public class HillClimbingUser extends User {

	private DGLog mLog;

	public HillClimbingUser(UserInfo userInfo) {
		super(userInfo);

		// ログの有効化
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
	}

	@Override
	protected void think(UserBoard userBoard, Move moveResult) {
		say("山登り開始");

		// Nodeのリスト作成
		List<Node> nodeList = new ArrayList<>();

		// 移動できる駒を取得
		Set<UserPiece> movableUserPieces = userBoard.getMovableUserPieces(getMyTeam());
		say("探索するPieceは%dつ[%s]",movableUserPieces.size(), movableUserPieces.stream()
											.map((pi)->pi.getNameStr()).collect(Collectors.toList()));

		// 移動できる駒が移動できるCordinatesを探し、そこからMoveを洗い出す
		for(UserPiece uPiece : movableUserPieces){
			say("移動元の駒が%sの場合の探索",uPiece.getNameStr());
			// 移動元PieceのSpot取得
			UserCordinate userCordinate = userBoard.getUserCordinateFromPiece(uPiece);
			assert userCordinate != null;
			// 移動先候補を取得
			Set<UserCordinate> movableCordinates = userBoard.getMovableCordinates(userCordinate);
			say("移動先の候補は%dつ。", movableCordinates.size());
			for(UserCordinate nextCordinate : movableCordinates){
				say("移動先の%sから作られるMoveを洗い出す", nextCordinate);
				// 移動先候補から作成されうるMove一覧を取得
				List<Move> moveList =  createMoves(userBoard, uPiece, userCordinate, nextCordinate);
				say("移動の%s->%sから作られたMoveは%dつ。", userCordinate, nextCordinate, moveList.size());
				// 各MoveをNodeにしてNodeListに追加(その時hnも更新)
				for(Move move : moveList){
					Node node = new Node();
					userBoard.init();
					userBoard.move(move);
					node.hn = userBoard.getPoint(getMyTeam());
					userBoard.init();
					node.moveList.add(move);
					nodeList.add(node);
					say("%sのPointは%d", move, node.hn);
				}
			}
		}
		say("探索完了. nodeListのサイズ:%d ", nodeList.size());

		// nodeListをソート(hnが大きい順)
		nodeList.sort((n1, n2)-> (n1.hn-n2.hn)*-1 );

		int maxSize = nodeList.get(0).hn;
		List<Node> maxNodes = nodeList.stream().filter((n)->n.hn == maxSize).collect(Collectors.toList());
		Collections.shuffle(maxNodes);

		say("頂上に到着!! h(n):%d List:%s", maxNodes.get(0).hn, maxNodes.get(0).moveList);
		Move result = maxNodes.get(0).moveList.get(0);
		moveResult.mPiece = result.mPiece;
		for(Cordinate c : result.mMoveSpots){
			moveResult.mMoveSpots.add(c);
		}

	}

	/**
	 * 指定されたSpotからSpotへ移動した場合に考えられるMove一覧を返却します
	 * @param userBoard
	 * @param uPiece
	 * @param from
	 * @param to
	 * @return
	 */
	private List<Move> createMoves(UserBoard userBoard, UserPiece uPiece, UserCordinate from, UserCordinate to) {
		List<Move> result = new ArrayList<>();
		Set<UserCordinate> footPrints = new HashSet<>();

		// 一つ目を作成&登録
		Move move = new Move(this);
		move.mPiece = uPiece;
		move.mMoveSpots.add(userBoard.getCordinateFromUserCordinate(to));
		result.add(move);
		footPrints.add(from);
		footPrints.add(to);

		// 1マス移動ならこれでおしまい
		if(userBoard.isAdjacentCordinate(from, to)){
			return result;
		}

		// 2マス移動ならここから再帰的にResultに登録していく
		exact(userBoard, footPrints, result, move, to);

		return result;
	}

	private void exact(UserBoard userBoard, Set<UserCordinate> footPrints, List<Move> result, Move baseMove, UserCordinate cordinate){
		// 移動先一覧を取得
		Set<UserCordinate> movableCordinates = userBoard.getMovableCordinates(cordinate, false);

		// 移動先が見つからないなら終了
		if(movableCordinates.size() == 0){
			return;
		}

		// 移動先があればResultに追加しつつそれぞれを再帰的に展開
		for(UserCordinate nextCordinate : movableCordinates){
			// すでに足あとが付いているSpotならスキップ
			if(footPrints.contains(nextCordinate)){
				continue;
			}
			Move move = baseMove.cloneMove();
			move.mMoveSpots.add(userBoard.getCordinateFromUserCordinate(nextCordinate));
			result.add(move);
			footPrints.add(nextCordinate);
			exact(userBoard, footPrints, result, move, nextCordinate);
		}
	}

	public void say(String fmt, Object... args){
		mLog.info(getName()+"「"+fmt+"」", args);
	}

	@Override
	public void handShake(User handShakeUser, TeamColor teamColor) {
		// NOP
	}

	@Override
	public void notifyCancelled() {
		// NOP
	}

	/**
	 * 山登り方でのノード. 同じ状態で別ノードがあっても問題ないのでequalsとかは実装しない
	 * @author 0000140105
	 *
	 */
	class Node {
		List<Move> moveList;
		int hn;
		Node parent;

		public Node() {
			this.moveList = new ArrayList<>();
			this.hn       = 0;
			this.parent   = null;
		}

		public Node cloneNode(){
			Node clone = new Node();
			clone.hn = 0;
			clone.moveList = new ArrayList<>();
			for(Move move : this.moveList){
				clone.moveList.add(move.cloneMove());
			}
			clone.parent = this;
			return clone;
		}
	}

	public static class HillClimbingUserInfo extends UserInfo {

		public HillClimbingUserInfo(String name) {
			super(name);
		}

		@Override
		public Class<? extends User> getUserClass() {
			return HillClimbingUser.class;
		}

		@Override
		public String getImageUrl() {
			return null;
		}

		@Override
		public String getDescription() {
			return "山登り法を使用したユーザー.";
		}
	}

}

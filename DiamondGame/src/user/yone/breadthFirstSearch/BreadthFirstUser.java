package user.yone.breadthFirstSearch;

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

public class BreadthFirstUser extends User {

	private DGLog mLog;
	private final int EXACT_LIMIT = 3;

	public BreadthFirstUser(UserInfo userInfo) {
		super(userInfo);

		// ログの有効化
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
	}

	@Override
	protected void think(UserBoard userBoard, Move moveResult) {
		// Open/Closeリストの生成
		List<Node> openList  = new ArrayList<>();
		List<Node> closeList = new ArrayList<>();

		// 開始ノードの作成
		openList.add(createFirstNode(userBoard));

		// 探索の開始
		Node currentNode = null;
		while(true){
			// OpenListが空なら終了
			if(openList.isEmpty()){
				break;
			}

			// 先頭のノード取り出し
			currentNode = openList.remove(0);

			// ノードがゴールなら終了
			if(currentNode.hn == 1000){
				closeList.add(currentNode);
				break;
			}
			// ノードが展開限界ならCloseリストに入れて次のNode
			else if(currentNode.exactNum == EXACT_LIMIT ){
				closeList.add(currentNode);
				continue;
			}
			// その他のノードならノードを展開
			else{
				closeList.add(currentNode);
				List<Node> children = exactNode(userBoard, currentNode);
				for(Node child : children){
					openList.add(child);
				}
				continue;
			}
		}

		// CloseListをhn最大順にソート
		closeList.sort((n1, n2)-> (n1.hn - n2.hn)*-1 );

		// hnが最大のもののみ抽出
		int maxSize = closeList.get(0).hn;
		List<Node> maxNodes = closeList.stream().filter((n)->n.hn == maxSize).collect(Collectors.toList());
		Collections.shuffle(maxNodes);

		// 決めたNodeを辿って今回の一手を決める
		currentNode    = maxNodes.get(0);
		Node firstNode = null;
		while(true){
			if(currentNode.parent != null && currentNode.parent.parent == null){
				firstNode = currentNode;
				break;
			}
		}

		// resultMoveに代入
		Move result = firstNode.moveList.get(0);
		moveResult.mPiece = result.mPiece;
		for(Cordinate c : result.mMoveSpots){
			moveResult.mMoveSpots.add(c);
		}
	}

	/**
	 * 指定されたNodeから展開できるNodeをすべて作成する
	 * @param currentNode
	 * @return
	 */
	private List<Node> exactNode(UserBoard userBoard, Node currentNode) {
		List<Node> nodeList = new ArrayList<>();

		// currentNodeの値を使用して、UserBoardの状態を更新
		userBoard.init();
		for(Move move : currentNode.moveList){
			userBoard.move(move);
		}

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
					Node node = currentNode.cloneNode();
					node.moveList.add(move);
					userBoard.init();
					for(Move m : node.moveList){
						userBoard.move(move);
					}
					node.hn = userBoard.getPoint(getMyTeam());
					node.exactNum++;
					userBoard.init();
					nodeList.add(node);
					say("%sのPointは%d", move, node.hn);
				}
			}
		}
		say("探索完了. nodeListのサイズ:%d ", nodeList.size());
		return nodeList;
	}

	private Node createFirstNode(UserBoard userBoard) {
		Node ret = new Node();
		userBoard.init();
		ret.hn = userBoard.getPoint(getMyTeam());
		userBoard.init();
		return ret;
	}

	@Override
	public void handShake(User handShakeUser, TeamColor teamColor) {
		// NOP
	}

	@Override
	public void notifyCancelled() {
		// NOP
	}

	public void say(String fmt, Object... args){
		mLog.info("優柔不断"+getName()+"「"+fmt+"」", args);
	}

	/**
	 * 指定されたSpotからSpotへ移動した場合に考えうるMove一覧を返却します
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
		exactMove(userBoard, footPrints, result, move, to);

		return result;
	}

	private void exactMove(UserBoard userBoard, Set<UserCordinate> footPrints, List<Move> result, Move baseMove, UserCordinate cordinate){
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
			exactMove(userBoard, footPrints, result, move, nextCordinate);
		}
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
		int exactNum;

		public Node() {
			this.moveList = new ArrayList<>();
			this.hn       = 0;
			this.parent   = null;
			this.exactNum = 0;
		}

		public Node cloneNode(){
			Node clone = new Node();
			clone.hn = 0;
			clone.moveList = new ArrayList<>();
			for(Move move : this.moveList){
				clone.moveList.add(move.cloneMove());
			}
			clone.parent   = this;
			clone.exactNum = this.exactNum;
			return clone;
		}
	}
}

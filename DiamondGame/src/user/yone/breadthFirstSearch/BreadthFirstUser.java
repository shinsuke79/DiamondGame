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

/**
 * 幅優先探索でNターン先までのユーザーが選択できる駒移動 全パターンを予測し、<br>
 * その中の最適解を選択する慎重なユーザーです。<br>
 * Nターンは定数EXACT_LIMITを変更することで調整できます
 * @author yone
 *
 */
public class BreadthFirstUser extends User {

	private DGLog mLog;
	private final int EXACT_LIMIT = 2;

	public BreadthFirstUser(UserInfo userInfo) {
		super(userInfo);

		// ログの有効化
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
	}

	@Override
	protected void think(UserBoard userBoard, Move moveResult) {
		say("慎重に探索開始");
		StopWatch watch = new StopWatch();
		watch.start();

		// Open/Closeリストの生成
		List<Node> openList  = new ArrayList<>();
		List<Node> closeList = new ArrayList<>();

		// 開始ノードの作成
		openList.add(createFirstNode(userBoard));

		// 探索の開始
		Node currentNode = null;
		while(true){
			tweet("--------------------------------------------------------------------");
			tweet("ループ先頭。");
			tweet("OPEN数:%d", openList.size());
			tweet("CLOSE数:%d", closeList.size());

			// OpenListが空なら終了
			if(openList.isEmpty()){
				say("もう探索するところはないな");
				break;
			}

			// 先頭のノード取り出し
			currentNode = openList.remove(0);
			tweet("現在のノード:%s", currentNode);

			// ノードがゴールなら終了
			if(currentNode.hn == 1000){
				closeList.add(currentNode);
				tweet("ゴールに到達。終了");
				break;
			}
			// ノードが展開限界ならCloseリストに入れて次のNode
			else if(currentNode.exactNum == EXACT_LIMIT ){
				closeList.add(currentNode);
				tweet("もう限界");
				continue;
			}
			// その他のノードならノードを展開
			else{
				closeList.add(currentNode);
				List<Node> children = exactNode(userBoard, currentNode);
				tweet("ノードを展開。展開したNode数:%d", children.size());
				for(Node child : children){
					openList.add(child);
				}
				continue;
			}
		}

		// CloseListをhn最大順にソート
		closeList.sort((n1, n2)-> (n1.hn - n2.hn)*-1 );

		// exactNumが0のものは消去する
		closeList = closeList.stream().filter((n)->n.exactNum != 0).collect(Collectors.toList());

		// exactNumが1かつhn=1000があればそれを選択する
		List<Node> lastHands = closeList.stream().filter((n)->n.exactNum == 1 && n.hn == 1000).collect(Collectors.toList());
		if(lastHands.isEmpty()){
			// hnが最大のもののみ抽出
			int maxSize = closeList.get(0).hn;
			lastHands = closeList.stream().filter((n)->n.hn == maxSize).collect(Collectors.toList());
			Collections.shuffle(lastHands);
		}
		say("%dパターン考えましたが, %dターンでの最大点は%dみたいです", closeList.size(), EXACT_LIMIT, lastHands.get(0).hn);

		// 決めたNodeを辿って今回の一手を決める
		currentNode    = lastHands.get(0);
		say("%sを辿って今回の一手を決めます", currentNode);
		Node firstNode = null;
		while(true){
			if(currentNode.parent != null && currentNode.parent.exactNum == 0){
				firstNode = currentNode;
				break;
			}
			currentNode = currentNode.parent;
		}
		say("今回のNode:%s", firstNode);

		// resultMoveに代入
		Move result = firstNode.moveList.get(0);
		moveResult.mPiece = result.mPiece;
		for(Cordinate c : result.mMoveSpots){
			moveResult.mMoveSpots.add(c);
		}

		watch.stop();
		StopWatch.addTime(watch.getMilliSec());
		say("やっと終わった... %dMsec %dSec", watch.getMilliSec(), watch.getSec());
		StopWatch.printStatus();
	}

	/**
	 * 指定されたNodeから展開できるNodeをすべて作成する
	 * @param currentNode
	 * @return
	 */
	private List<Node> exactNode(UserBoard userBoard, Node currentNode) {
		List<Node> nodeList = new ArrayList<>();

		tweet("+++++");
		tweet("%sのノードから作られるノードを洗い出します(hn:%d exNum:%d)", currentNode, currentNode.hn, currentNode.exactNum);
		tweet("%sの親:%s", currentNode, currentNode.parent);
		for(int i=0; i<currentNode.moveList.size(); i++){
			tweet("move1: %s -> %s", currentNode.moveList.get(i).mPiece, currentNode.moveList.get(i).mMoveSpots);
		}

		// currentNodeの値を使用して、UserBoardの状態を更新
		updateUserBoard(userBoard, currentNode);

		// 移動できる駒を取得
		Set<UserPiece> movableUserPieces = userBoard.getMovableUserPieces(getMyTeam());
		tweet("探索するPieceは%dつ[%s]",movableUserPieces.size(), movableUserPieces.stream()
											.map((pi)->pi.getNameStr()).collect(Collectors.toList()));

		// 移動できる駒が移動できるCordinatesを探し、そこからMoveを洗い出す
		for(UserPiece uPiece : movableUserPieces){
			// 移動元PieceのSpot取得
			updateUserBoard(userBoard, currentNode);
			UserCordinate userCordinate = userBoard.getUserCordinateFromPiece(uPiece);
			assert userCordinate != null;
			// 移動先候補を取得
			Set<UserCordinate> movableCordinates = userBoard.getMovableCordinates(userCordinate);
			for(UserCordinate nextCordinate : movableCordinates){
				// 移動先候補から作成されうるMove一覧を取得
				List<Move> moveList =  createMoves(userBoard, uPiece, userCordinate, nextCordinate);
				tweet("移動の%s->%sから作られたMoveは%dつ。", userCordinate, nextCordinate, moveList.size());
				// 各MoveをNodeにしてNodeListに追加(その時hnも更新)
				for(Move move : moveList){
					Node node = currentNode.cloneNode();
					node.moveList.add(move);
					updateUserBoard(userBoard, node);
					node.hn = userBoard.getPoint(getMyTeam());
					node.exactNum++;
					nodeList.add(node);
				}
			}
		}
		tweet("探索完了. nodeListのサイズ:%d ", nodeList.size());
		return nodeList;
	}

	private Node createFirstNode(UserBoard userBoard) {
		Node ret = new Node();
		userBoard.init();
		ret.hn = userBoard.getPoint(getMyTeam());
		say("最初のNode作成: %s", ret);
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
		mLog.info(getName()+"「"+fmt+"」", args);
	}

	public void tweet(String fmt, Object... args){
		mLog.fine(getName()+"「"+fmt+"」", args);
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

	private void updateUserBoard(UserBoard userBoard, Node node){
		userBoard.init();
		for(Move m : node.moveList){
			userBoard.move(m);
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

		public String toString(){
			return String.format("%s(h:%d e:%d)", moveList, hn, exactNum);
		}
	}

	static class StopWatch {
		static List<Long> timeList = new ArrayList<>();

		private long start;
		private long end;
		public StopWatch() {
		}
		public void start(){
			start = System.nanoTime();
		}
		public void stop(){
			end   = System.nanoTime();
		}
		public long getMilliSec(){
			return (end - start)/(1000*1000);
		}
		public long getSec(){
			return (end - start)/(1000*1000*1000);
		}
		public static void addTime(long time){
			timeList.add(time);
		}
		public static void printStatus(){
			/*
			System.out.printf("Time Size:%d \n", timeList.size());
			System.out.printf("Max Time:%dMsec \n", timeList.stream().mapToLong((t)->t).max().getAsLong());
			System.out.printf("Min Time:%dMsec \n", timeList.stream().mapToLong((t)->t).min().getAsLong());
			System.out.printf("Average :%dMsec \n", (long)timeList.stream().mapToDouble((t)->t).average().getAsDouble());
			*/
		}
	}

	public static class BreadthFirstUserInfo extends UserInfo {

		public BreadthFirstUserInfo(String name) {
			super(name);
		}

		@Override
		public Class<? extends User> getUserClass() {
			return BreadthFirstUser.class;
		}

		@Override
		public String getImageUrl() {
			return null;
		}
	}
}

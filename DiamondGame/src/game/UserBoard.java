package game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import common.DGLog;
import common.Direction;
import common.TeamColor;
import game.Board.Cordinate;
import game.Board.Spot;
import user.User;

/**
 * ダイアモンドゲームの盤を真上から見た(どのユーザーからの視点でもない){@link Board}に対し、
 * あるユーザーの視点で見た盤(さらには頭の中で駒配置を変更した盤)を表現したクラス。<br>
 * {@link User}が自分の一手を考える際、真上から見た{@link Board}では自分の手元に自陣があるわけではないこと、
 * x,y,zの3軸でマス目の座標を考えなければならないことなどから、AIを作成するのが難しい。<br>
 * {@link UserBoard}は以下の機能を提供する.<br>
 * <ul>
 * <li>ユーザー視点の盤(自分の陣地が手元=座標0, 0に来る)</li>
 * <li>自由に駒配置を変更できる({@link Board}はGameMasterしか変更できない)</li>
 * <li>駒配置を自由に変更した状態で他チームの視点に変更できる(自分の評価関数を他チームにも適用できる)</li>
 * <li>自由に変更した駒配置を元に戻すことができる</li>
 * </ul>
 * @author 0000140105
 *
 */
public class UserBoard {
	/* 定数定義 */
	private static final int USER_SPOT_NUM = 10;

	/* 現在のUserSpotの状態が反映されたBoard */
	private Board          mCurrentCloneBoard;
	private TeamColor      mCurrentTeam;

	/* UserSpot自身の状態 */
	private UserSpot[][]                       mCurrentBoard;
	private EnumMap<TeamColor, Set<UserPiece>> mPieceTable;
	private Map<Cordinate, UserSpot>           mUserSpotTable;

	/* 生成初期の状態が反映されたBoard */
	private Board          mInitCloneBoard;
	private TeamColor      mInitTeam;

	/* デバッグ用 */
	DGLog   mLog;

	UserBoard(TeamColor mainTeam, Board board) {
		// ログシステムの生成
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());

		// Initで使用するBoardの初期化
		this.mInitCloneBoard = board.cloneBoard();
		this.mInitTeam       = mainTeam;

		// テーブル関係の初期化
		this.mCurrentBoard   = new UserSpot[USER_SPOT_NUM][USER_SPOT_NUM];
		this.mPieceTable     = new EnumMap<>(TeamColor.class);
		for(TeamColor tc: TeamColor.values()){
			mPieceTable.put(tc, new HashSet<>());
		}
		this.mUserSpotTable  = new HashMap<>();

		// CurrentBoardとTeamの設定
		this.mCurrentCloneBoard = this.mInitCloneBoard.cloneBoard();
		this.mCurrentTeam       = this.mInitTeam;

		// CloneBoardの情報を使用してBoardを生成する
		this.clearBoards();
		this.clearSpotTablePiece();
		this.syncBoards();
	}

	/* -------- Userに公開するAPI --------  */

	/**
	 * UserPiece(駒)をThinkで渡された直後の状態に戻します.<br>
	 * 視点も最初のチームに戻ります
	 */
	public void init(){
		mLog.fine("init Start");
		this.mCurrentCloneBoard = this.mInitCloneBoard.cloneBoard();
		this.mCurrentTeam       = this.mInitTeam;

		this.clearBoards();
		this.clearSpotTablePiece();
		this.syncBoards();

		mLog.fine("init End");
	}

	/**
	 * 現在のUserPiece(駒)配置を保ったまま、指定されたチームの視点に変更します
	 * @param team
	 */
	public void changeMainTeam(TeamColor team){
		mLog.fine("changeMainTeam(%s) start", team);

		this.mCurrentTeam = team;

		this.clearBoards();
		this.clearSpotTablePiece();
		this.syncBoards();

		mLog.fine("changeMainTeam(%s) end", team);
	}

	/**
	 * {@link Move}を使用してUserBoardの状態を変更します
	 * @param move
	 */
	public boolean move(Move move){
		mLog.fine("move Start");
		// 移動できるかチェック
		if(!mCurrentCloneBoard.isMoveValid(move, true)){
			mLog.info("move End ERROR");
			return false;
		}
		// 移動させる
		mCurrentCloneBoard.move(move, true);

		this.clearBoards();
		this.clearSpotTablePiece();
		this.syncBoards();

		mLog.fine("move End");
		return true;
	}

	/**
	 * [コンソール出力用] 指定された座標のUserSpot(マス目)を一文字の文字列に変換します<br>
	 * spot to console string
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private String s2cs(int x, int y){
		UserSpot spot = mCurrentBoard[x][y];
		if(spot == null){
			return "  ";
		}else if(spot.piece != null){
			switch(spot.piece.geTeamColor()){
			case GREEN:  return "緑";
			case RED:    return "赤";
			case YELLOW: return "黄";
			}
		}else{
			return "○";
		}
		return "  ";
	}

	/**
	 * 指定されたUserPiece(駒)が配置されているUserCordinate(座標)を返却します
	 * @param uPiece 検索に使用する駒
	 * @return uPieceが配置されているUserCordinate(座標)
	 */
	public UserCordinate getUserCordinateFromPiece(UserPiece uPiece) {
		UserSpot userSpot = getUserSpotFromPiece(uPiece);
		if(userSpot != null){
			return userSpot.cordinate;
		}else{
			return null;
		}
	}

	/**
	 * 指定されたUserPiece(駒)が配置されているUserSpot(マス目)を返却します
	 * @param uPiece 検索に使用する駒
	 * @return uPieceが配置されているUserSpot(マス目)
	 */
	UserSpot getUserSpotFromPiece(UserPiece uPiece){
		for(int x=0; x<USER_SPOT_NUM; x++){
			for(int y=0; y<USER_SPOT_NUM; y++){
				if(mCurrentBoard[x][y]!=null && mCurrentBoard[x][y].piece == uPiece){
					return mCurrentBoard[x][y];
				}
			}
		}
		return null;
	}

	/**
	 * 指定されたUserCordinate(マス目の座標)に該当するUserSpot(マス目)を返却します
	 * @param userCordinate 検索に使用するマス目の座標
	 * @return 指定された座標のUserSpot(マス目)
	 */
	UserSpot getUserSpotFromCordinate(UserCordinate userCordinate) {
		return mCurrentBoard[userCordinate.x][userCordinate.y];
	}

	/**
	 * [内部処理用関数] 指定されたbaseSpotに該当するUserSpot(マス目)を返却します
	 * @param baseSpot
	 * @return
	 */
	private UserSpot getUserSpotFromBaseSpot(Spot baseSpot){
		for(int x=0; x<USER_SPOT_NUM; x++){
			for(int y=0; y<USER_SPOT_NUM; y++){
				if(mCurrentBoard[x][y]!=null && mCurrentBoard[x][y].baseSpot.mCordinate.equals(baseSpot.mCordinate)){
					return mCurrentBoard[x][y];
				}
			}
		}
		return null;
	}

	/**
	 * 指定されたUserSpot(マス目)の周囲8方向のUserSpot(マス目)を返却します.<br>
	 * 隣接したマス目と、1つ離れたマス目を選択できます
	 * @param spot 周囲のマス目を取得したいマス目
	 * @param distance 隣接したマス目を取得したい場合は1, 1つ離れたマス目を取得したい場合は2
	 * @return 指定されたマス目の周囲8方向({@link Direction})のマス目.端の方向はnullを返却する
	 */
	private EnumMap<Direction, UserSpot> getAroundUserSpots(UserSpot spot, int distance){
		EnumMap<Direction, UserSpot> ret = new EnumMap<>(Direction.class);

		// cloneBoardにbaseSpotを指定して周囲8方向のSpotを取得
		EnumMap<Direction,Spot> aroundSpot = mCurrentCloneBoard.getAroundSpot(spot.baseSpot, distance);
		// 各方向を操作
		for(Direction d : Direction.values()){
			Spot currentSpot = aroundSpot.get(d);
			// Spotが存在しなければスキップ
			if(currentSpot == null){
				continue;
			}
			// 方角をUserSpot用に変換
			Direction userDirection = d.getRelativeDirection(mCurrentTeam);
			// currentSpotに該当するSpotを検索
			UserSpot userSpot = getUserSpotFromBaseSpot(currentSpot);
			// これも見つからない可能性があるのでなければスキップ
			if(userSpot == null){
				continue;
			}
			// Userの方角とUserSpotで登録
			ret.put(userDirection, userSpot);
		}
		return ret;
	}

	/**
	 * 指定されたUserSpot(マス目)が移動できるUserSpot(マス目)一覧を返却します
	 * @param spot チーム問わず、移動可能なマス目を知りたいマス目
	 * @return 移動可能なマス目の一覧.どこにも移動できなければ要素0の集合が返却される
	 */
	Set<UserSpot> getMovableSpots(UserSpot spot){
		return getMovableSpots(spot, true);
	}

	/**
	 * 指定されたUserSpot(マス目)が移動できるUserSpot(マス目)一覧を返却します
	 * @param spot チーム問わず、移動可能なマス目を知りたいマス目
	 * @param isFirst spotがすでに一回移動した場合のケースならfalseを設定(移動範囲1が除外される)
	 * @return 移動可能なマス目の一覧.どこにも移動できなければ要素0の集合が返却される
	 */
	Set<UserSpot> getMovableSpots(UserSpot spot, boolean isFirst){
		Set<UserSpot> result = new HashSet<>();
		// 最初は移動範囲１も含める
		if(isFirst){
			getAroundUserSpots(spot, 1).values().stream()
			.filter((us)->isAvailableMove(spot, us))
			.forEach((us)->result.add(us));
		}
		// 移動範囲２はいつでも取得できる
		getAroundUserSpots(spot, 2).values().stream()
							.filter((us)->isAvailableMove(spot, us))
							.forEach((us)->result.add(us));
		return result;
	}

	/**
	 * 指定されたUserCordinate(座標)が移動できるUserCordinate(座標)一覧を返却します
	 * @param uCordinate チーム問わず、移動可能な座標を知りたい座標
	 * @return 移動可能な座標の一覧.どこにも移動できなければ要素0の集合が返却される
	 */
	public Set<UserCordinate> getMovableCordinates(UserCordinate uCordinate) {
		return getMovableCordinates(uCordinate, true);
	}

	/**
	 * 指定されたUserCordinate(座標)が移動できるUserCordinate(座標)一覧を返却します
	 * @param uCordinate チーム問わず、移動可能な座標を知りたい座標
	 * @param isFirst spotがすでに一回移動した場合のケースならfalseを設定(移動範囲1が除外される)
	 * @return 移動可能な座標の一覧.どこにも移動できなければ要素0の集合が返却される
	 */
	public Set<UserCordinate> getMovableCordinates(UserCordinate uCordinate, boolean isFirst) {
		UserSpot userSpot = getUserSpotFromCordinate(uCordinate);
		Set<UserSpot> movableSpots = getMovableSpots(userSpot, isFirst);
		return movableSpots.stream().map((uSpot)->uSpot.cordinate).collect(Collectors.toSet());
	}

	/**
	 * 指定されたチームの移動可能なPiece一覧を返却する
	 * @param teamColor
	 * @return
	 */
	public Set<UserPiece> getMovableUserPieces(TeamColor teamColor){
		Set<UserPiece> pieces = getPiecesFromTeam(teamColor);
		Set<UserPiece> result = new HashSet<>();
		for(UserPiece up : pieces){
			UserSpot userSpot = getUserSpotFromPiece(up);
			if(userSpot == null){
				continue;
			}
			if(getMovableSpots(userSpot).size() > 0){
				result.add(up);
			}
		}
		return result;
	}

	/**
	 * 指定されたチームのUserPiece(駒)をすべて返却します
	 * @param team 返却したいチーム
	 * @return 指定されたチームのUserPiece(駒)すべて({@link Set}型)
	 */
	public Set<UserPiece> getPiecesFromTeam(TeamColor team){
		return new HashSet<>(mPieceTable.get(team));
	}

	/**
	 * 指定されたUserCordinate(マス目の座標)に配置されているUserPiece(駒)を返却します
	 * @param uCordinate 駒を取得する座標
	 * @return 指定された座標の駒. 駒やマス目が存在しなければnullを返却
	 */
	public UserPiece getPieceFromUserCordinate(UserCordinate uCordinate) {
		UserSpot userSpot = getUserSpotFromCordinate(uCordinate);
		if(userSpot == null){
			return null;
		}
		return userSpot.piece;
	}

	/**
	 * 指定されたUserSpot(マス目)が指定されたマス目に移動可能か返却します
	 * @param currentSpot 移動元のマス目. 実際に移動元のUserSpotに駒が配置されている必要はない
	 * @param nextSpot 移動先のマス目. 実際に配置されている駒も考慮して移動可能かチェックされる
	 * @return 移動可能ならtrue, 不可能ならfalse
	 */
	public boolean isAvailableMove(UserCordinate currentCordinate, UserCordinate nextCordinate){
		// nullは許容する
		if(currentCordinate == null || nextCordinate == null){
			return false;
		}

		UserSpot fromUserSpot = getUserSpotFromCordinate(currentCordinate);
		UserSpot nextUserSpot = getUserSpotFromCordinate(nextCordinate);

		// UserSpotにSpotが登録されていないことは保証しない
		assert fromUserSpot.baseSpot != null && nextUserSpot.baseSpot != null;

		// Board的に移動できるか確認する
		boolean result = mCurrentCloneBoard.isAvailableMove(fromUserSpot.baseSpot, nextUserSpot.baseSpot);

		return result;
	}

	/**
	 * 指定されたUserSpot(マス目)が指定されたマス目に移動可能か返却します
	 * @param currentSpot 移動元のマス目. 実際に移動元のUserSpotに駒が配置されている必要はない
	 * @param nextSpot 移動先のマス目. 実際に配置されている駒も考慮して移動可能かチェックされる
	 * @return 移動可能ならtrue, 不可能ならfalse
	 */
	boolean isAvailableMove(UserSpot currentSpot, UserSpot nextSpot){
		// nullは許容する
		if(currentSpot == null || nextSpot == null){
			return false;
		}

		// UserSpotにSpotが登録されていないことは保証しない
		assert currentSpot.baseSpot != null && nextSpot.baseSpot != null;

		// Board的に移動できるか確認する
		boolean result = mCurrentCloneBoard.isAvailableMove(currentSpot.baseSpot, nextSpot.baseSpot);

		return result;
	}

	/**
	 * uSpot1とuSpot2が隣接していればTrueを返します
	 * @param uSpot1 UserBoardに所属するSpot
	 * @param uSpot2 UserBoardに所属するSpot
	 * @return uSpot1とuSpot2が隣接していればTrue
	 * uSpot1とuSpot2は必ず同じUserBoardに所属しているUserSpotを指定すること
	 */
	boolean isAdjacentSpot(UserSpot uSpot1, UserSpot uSpot2){
		for(Entry<Direction, UserSpot> entry : getAroundUserSpots(uSpot1, 1).entrySet()){
			if(entry.getValue() != null && entry.getValue() == uSpot2){
				return true;
			}
		}
		return false;
	}

	/**
	 * uCordinate1とuCordinate2が隣接していればTrueを返します
	 * @param uCordinate1 UserBoardに所属する座標
	 * @param uCordinate2 UserBoardに所属する座標
	 * @return uCordinate1とuCordinate2が隣接していればTrue
	 */
	public boolean isAdjacentCordinate(UserCordinate uCordinate1, UserCordinate uCordinate2) {
		UserSpot userSpot1 = getUserSpotFromCordinate(uCordinate1);
		UserSpot userSpot2 = getUserSpotFromCordinate(uCordinate2);
		return isAdjacentSpot(userSpot1, userSpot2);
	}

	/**
	 * UserCordinateの座標をCordinateの座標に変換します
	 * @param uCordinate
	 * @return
	 */
	public Cordinate getCordinateFromUserCordinate(UserCordinate uCordinate) {
		UserSpot userSpot = getUserSpotFromCordinate(uCordinate);
		return userSpot.getBaseCordinate();
	}

	/**
	 * 現在のUserBoardの状態を保存します
	 * @return 保存したUserBoardの状態
	 */
	public UserBoardImage getUserBoardImage(){
		return new UserBoardImage(mCurrentCloneBoard);
	}

	/**
	 * UserBoardImageを使用してUserBoardの状態を以前の状態に復帰させます
	 * @param image
	 */
	public void applyUserBoardImage(UserBoardImage image){
		this.mCurrentCloneBoard = image.getmCloneBoard().cloneBoard();

		this.clearBoards();
		this.clearSpotTablePiece();
		this.syncBoards();
	}

	/**
	 * 指定されたチームのPointを返却します
	 * @param teamColor
	 * @return
	 */
	public int getPoint(TeamColor teamColor) {
		return mCurrentCloneBoard.getPoint(teamColor);
	}

	/* -------- Userには公開しないAPI --------  */
	/**
	 * [内部処理用関数] cloneBoardを使用してUserBoardを構築します
	 * @param team
	 * @param spot
	 * @param uCordinate
	 */
	private void exactSpot(TeamColor team, Spot spot, UserCordinate uCordinate){
		mLog.fine("exactSpot start team:%s spot:%s cordinate:%s", team, spot, uCordinate);

		// すでに展開済みのSpotなら何もしない
		if(mCurrentBoard[uCordinate.x][uCordinate.y] != null){
			return;
		}

		// 引数に該当するUserSpotをTableから検索する. なければnewして登録
		UserSpot targetSpot = null;
		targetSpot = mUserSpotTable.get(spot.mCordinate);
		if(targetSpot == null){
			targetSpot = new UserSpot(spot, uCordinate);
			mUserSpotTable.put(spot.mCordinate, targetSpot);
			mLog.fine("exactSpot create userSpot:%s ", targetSpot);
		}

		// mCurrentBoardに登録する
		mCurrentBoard[uCordinate.x][uCordinate.y] = targetSpot;
		mLog.fine("exactSpot regist userSpot:%s ", targetSpot);

		// SpotにPieceが存在すればUserSpotにUserPieceとして配置する
		if(spot.mPiece != null){
			// UserPieceが生成済みか確認。なければnewする
			UserPiece targetPiece = null;
			for(UserPiece up : mPieceTable.get(spot.mPiece.getmTeamColor())){
				if(up.basePiece == spot.mPiece){
					targetPiece = up;
					break;
				}
			}
			if(targetPiece == null){
				targetPiece = new UserPiece(spot.mPiece);
				mPieceTable.get(spot.mPiece.getmTeamColor()).add(targetPiece);
				mLog.fine("exactSpot create userPiece:%s ", targetPiece);
			}
			targetSpot.setPiece(targetPiece);
			mLog.fine("put userPiece :%s -> Spot:%s ", targetPiece, targetSpot);
		}

		// 右前側のSpot チーム視点の欲しい方角(右前)を基準方角し、次のSpotを取得する
		Direction convedDirectionRightF = Direction.RIGHT_FRONT.getRelativeDirection(team);
		Spot rightFront = mCurrentCloneBoard.getSpotFromCordinate(spot.mCordinate.getMovedCordinate(1, convedDirectionRightF));
		mLog.fine("exact rightFront(%s) -> Spot:%s ", convedDirectionRightF, rightFront);
		if(rightFront != null){
			exactSpot(team, rightFront, uCordinate.getMovedCordinate(1, Direction.RIGHT_FRONT));
		}

		// 左前側のSpot チーム視点の欲しい方角(左前)を基準方角に変換し、次のSpotを取得する
		Direction convedDirectionLeftF = Direction.LEFT_FRONT. getRelativeDirection(team);
		Spot leftFront = mCurrentCloneBoard.getSpotFromCordinate(spot.mCordinate.getMovedCordinate(1, convedDirectionLeftF));
		mLog.fine("exact leftFront(%s) -> Spot:%s ", convedDirectionLeftF, leftFront);
		if(leftFront != null){
			exactSpot(team, leftFront, uCordinate.getMovedCordinate(1, Direction.LEFT_FRONT));
		}

	}

	/**
	 * {@link UserBoard#mCurrentBoard}の初期化
	 */
	private void clearBoards(){
		assert mCurrentBoard != null;
		for(int x = 0; x < USER_SPOT_NUM; x++){
			for(int y = 0; y < USER_SPOT_NUM; y++){
				mCurrentBoard[x][y] = null;
			}
		}
		return;
	}

	/**
	 * {@link UserBoard#mCurrentCloneBoard}の状態を{@link UserBoard#mCurrentBoard}に反映させる
	 */
	private void syncBoards() {
		/* 各チームのベース地点を0,0として、UserSpotにSpotを登録してく
		   SpotにPieceが配置されていれば、UserSpotに変換してUserSpotに登録する */

		// チームごとのルートとなるSpotを取得
		Spot rootSpot = mCurrentCloneBoard.getSpotFromCordinate(mCurrentTeam.getRootCordinate());

		// ルートから、すべてのSpotの右前、左上と展開していく
		exactSpot(mCurrentTeam, rootSpot, new UserCordinate(0, 0));

		// CurrentCloneBoardのSpotとCurrentBoardのBaseBoardの参照を同期する
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					Spot spot = mCurrentCloneBoard.mSpots[x][y][z];
					if(spot != null){
						UserSpot userSpot = mUserSpotTable.get(spot.mCordinate);
						if(userSpot != null){
							userSpot.baseSpot = spot;
						}
					}
				}
			}
		}
	}

	/**
	 * {@link UserBoard#mUserSpotTable}に登録されているUserPieceを一旦登録解除します
	 */
	private void clearSpotTablePiece(){
		for(UserSpot us : mUserSpotTable.values()){
			us.piece = null;
		}
	}

	/**
	 * [コンソール出力用] 現在のUserPiece(駒)の状態をコンソールに出力します
	 */
	public void logConsoleUserBoardImage() {
		for(String s: logGetBoadString()){
			mLog.info(s);
		}
	}

	/**
	 * [コンソール出力用] 現在のUserPiece(駒)の状態をコンソール用に生成します
	 * @return
	 */
	public List<String> logGetBoadString(){
		List<String> result = new ArrayList<>();
		result.add(String.format("  ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲"));
		result.add(String.format("A                   %s                  ", s2cs(6,6)));
		result.add(String.format("B                 %s  %s                ", s2cs(6,5), s2cs(5,6)));
		result.add(String.format("C               %s  %s  %s              ", s2cs(6,4), s2cs(5,5), s2cs(4,6)));
		result.add(String.format("D %s  %s  %s  %s  %s  %s  %s  %s  %s  %s", s2cs(9,0), s2cs(8,1), s2cs(7,2), s2cs(6,3), s2cs(5,4), s2cs(4,5), s2cs(3,6), s2cs(2,7), s2cs(1,8), s2cs(0,9)));
		result.add(String.format("E   %s  %s  %s  %s  %s  %s  %s  %s  %s  ", s2cs(8,0), s2cs(7,1), s2cs(6,2), s2cs(5,3), s2cs(4,4), s2cs(3,5), s2cs(2,6), s2cs(1,7), s2cs(0,8)));
		result.add(String.format("F     %s  %s  %s  %s  %s  %s  %s  %s    ", s2cs(7,0), s2cs(6,1), s2cs(5,2), s2cs(4,3), s2cs(3,4), s2cs(2,5), s2cs(1,6), s2cs(0,7)));
		result.add(String.format("G       %s  %s  %s  %s  %s  %s  %s      ", s2cs(6,0), s2cs(5,1), s2cs(4,2), s2cs(3,3), s2cs(2,4), s2cs(1,5), s2cs(0,6)));
		result.add(String.format("H         %s  %s  %s  %s  %s  %s        ", s2cs(5,0), s2cs(4,1), s2cs(3,2), s2cs(2,3), s2cs(1,4), s2cs(0,5)));
		result.add(String.format("I           %s  %s  %s  %s  %s          ", s2cs(4,0), s2cs(3,1), s2cs(2,2), s2cs(1,3), s2cs(0,4)));
		result.add(String.format("J             %s  %s  %s  %s            ", s2cs(3,0), s2cs(2,1), s2cs(1,2), s2cs(0,3)));
		result.add(String.format("K               %s  %s  %s              ", s2cs(2,0), s2cs(1,1), s2cs(0,2)));
		result.add(String.format("L                 %s  %s                ", s2cs(1,0), s2cs(0,1)));
		result.add(String.format("M                   %s                  ", s2cs(0,0)));
		result.add(String.format("  ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲"));
		return result;
	}

	/**
	 * UserBoardに配置される駒を表したクラス
	 * @author 0000140105
	 *
	 */
	public class UserPiece{
		private Piece basePiece;

		/* -------- Userに公開するAPI --------  */

		/**
		 * 駒が誰のチームのものか返却する
		 * @return 駒が所属するチーム
		 */
		public TeamColor geTeamColor(){
			return basePiece.getmTeamColor();
		}

		/* -------- Userには公開しないAPI --------  */

		UserPiece(Piece basePiece) {
			this.basePiece = basePiece;
		}

		Piece getBasePiece(){
			return basePiece;
		}

		@Override
		public String toString() {
			return basePiece.toString();
		}

		public String getNameStr() {
			return basePiece.getNameStr();
		}
	}

	/**
	 * UserBoardのマス目を表すクラス<br>
	 * マス目には(x,y)の斜方座標({@link UserCordinate})が割り当てられている<br>
	 * マス目に駒がある場合、UserPieceを保持する
	 * @author 0000140105
	 *
	 */
	class UserSpot {
		private Spot baseSpot;
		private UserPiece piece;
		private UserCordinate cordinate;

		/* -------- Userに公開するAPI --------  */

		/**
		 * このマス目をゴールとするチームを返却します
		 * @return このマス目をゴールとするチーム、なければnullを返却
		 */
		public TeamColor getTeamColor(){
			return baseSpot.mTeam;
		}

		/**
		 * このマスの座標を返却します
		 * @return このマスの座標
		 */
		public UserCordinate getCordinate(){
			return cordinate;
		}

		/**
		 * このマスに配置されている駒を返却します
		 * @return このマスに配置されている駒. なければnullを返却
		 */
		public UserPiece getPiece(){
			return piece;
		}

		/* -------- Userには公開しないAPI --------  */

		UserSpot(Spot baseSpot, UserCordinate cordinate) {
			this.baseSpot = baseSpot;
			this.cordinate = cordinate;
		}

		Cordinate getBaseCordinate(){
			return baseSpot.mCordinate;
		}

		void setPiece(UserPiece piece){
			this.piece = piece;
		}

		@Override
		public String toString() {
			return "UserSpot [piece=" + piece +
					", cordinate=" + (cordinate!=null ? cordinate.getNameStr() : "null") + "]";
		}

	}

	/**
	 * UserBoard上の座標を表すクラス. ユーザーの手元が(0,0)になるようになっている
	 * @author 0000140105
	 *
	 */
	public static class UserCordinate implements Cloneable{
		private int x;
		private int y;
		static DGLog Log = new DGLog(Direction.class.getSimpleName());
		public UserCordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * この座標のX座標を返却する
		 * @return
		 */
		public int getX(){
			return x;
		}

		/**
		 * この座標のY座標を返却する
		 * @return
		 */
		public int getY(){
			return y;
		}

		/**
		 * この座標を指定された方角へdistance分移動させた座標を返却する
		 * @param cordinate
		 * @param distance
		 * @return
		 */
		public UserCordinate getMovedCordinate(int distance, Direction direction) {
			Log.fine("getMovedUserCordinate this:%s direction:%s distance:%d", this, direction, distance);
			assert distance == 2 || distance == 1;

			int dx=0, dy=0;

			switch(direction){
			case RIGHT_FRONT:
				dx = 0;
				dy = distance;
				break;
			case RIGHT:
				dx = -distance;
				dy = distance;
				break;
			case RIGHT_REAR:
				dx = -distance;
				dy = 0;
				break;
			case LEFT_REAR:
				dx = 0;
				dy = -distance;
				break;
			case LEFT:
				dx = distance;
				dy = -distance;
				break;
			case LEFT_FRONT:
				dx = distance;
				dy = 0;
				break;
			}
			UserCordinate result = new UserCordinate(this.x+dx, this.y+dy);
			Log.fine("getMovedUserCordinate result:%s ", result);
			return result;
		}
		@Override
		public String toString() {
			return "UserCordinate [x=" + x + ", y=" + y + "]";
		}

		public String getNameStr(){
			return "[" + x + ", " + y + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UserCordinate other = (UserCordinate) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		@Override
		public UserCordinate clone(){
			try {
				return (UserCordinate)super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class UserBoardImage {
		Board mCloneBoard;
		public UserBoardImage(Board board){
			mCloneBoard = board.cloneBoard();
		}
		Board getmCloneBoard() {
			return mCloneBoard;
		}
	}
}

package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import common.DGLog;
import common.Direction;
import common.TeamColor;

public class Board implements Cloneable {
	/**
	 * x, y, z
	 */
	Spot[][][] mSpots;
	Piece[] mRedPieces;
	Piece[] mYellowPieces;
	Piece[] mGreenPieces;
	DGLog   mLog;
	PointCalculator mPointCalculator;

	public Board() {
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.fine("Create " + getClass().getSimpleName());
	}

	/**
	 * BoardインスタンスをCloneします。
	 * ShallowコピーなのでPieceやSpotの中の参照はそのままです
	 */
	protected Board clone(){
		try {
			return (Board)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Board cloneBoard() {
		// Boardの生成
		Board ret = new Board();

		/* Spot配列の初期化 */
		ret.mSpots = new Spot[13][13][13];

		// Pieceのコピー(参照のみ)
		ret.mRedPieces = new Piece[10];
		for(int i=0;i<10;i++){ ret.mRedPieces[i]    = mRedPieces[i]; };
		ret.mYellowPieces = new Piece[10];
		for(int i=0;i<10;i++){ ret.mYellowPieces[i] = mYellowPieces[i]; };
		ret.mGreenPieces = new Piece[10];
		for(int i=0;i<10;i++){ ret.mGreenPieces[i]  = mGreenPieces[i]; };

		// Spotsのコピー(状態はコピーするがSpotはnewする)
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					Spot spot = mSpots[x][y][z];
					if(spot != null){
						ret.mSpots[x][y][z] = new Spot(spot.mTeam, spot.mPiece);
						ret.mSpots[x][y][z].setCoordinate(x, y, z);
					}
				}
			}
		}

		ret.mPointCalculator = this.mPointCalculator;

		return ret;
	}

	/**
	 * 自身の持つマス(Spot)、駒(Piece)を新たに配置します
	 */
	private void createBaseBoard() {
		mLog.config("createBaseBoard start");

		/* Spot配列の初期化 */
		mSpots = new Spot[13][13][13];

		/* Calculatorの生成 */
		mPointCalculator = new PointCalculator();

		/* Piece配列の初期化 */
		mRedPieces = new Piece[10];
		for(int i=0;i<10;i++){ mRedPieces[i]=new Piece(i, TeamColor.RED); };
		mYellowPieces = new Piece[10];
		for(int i=0;i<10;i++){ mYellowPieces[i]=new Piece(i, TeamColor.YELLOW); };
		mGreenPieces = new Piece[10];
		for(int i=0;i<10;i++){ mGreenPieces[i]=new Piece(i, TeamColor.GREEN); };

		/* すべてのSpotを手入力していく */
		// x=0
		mSpots[0][9][9]=new Spot(mRedPieces[0]);
		// x=1
		mSpots[1][9][8]=new Spot(mRedPieces[1]); mSpots[1][8][9]=new Spot(mRedPieces[2]);
		// x=2
		mSpots[2][9][7]=new Spot(mRedPieces[3]); mSpots[2][8][8]=new Spot(mRedPieces[4]);
		mSpots[2][7][9]=new Spot(mRedPieces[5]);
		// x=3
		mSpots[3][12][3]=new Spot(TeamColor.YELLOW); mSpots[3][11][4]=new Spot(TeamColor.YELLOW);
		mSpots[3][10][5]=new Spot(TeamColor.YELLOW); mSpots[3][9][6]=new Spot(TeamColor.YELLOW, mRedPieces[6]);
		mSpots[3][8][7]=new Spot(mRedPieces[7]);  mSpots[3][7][8]=new Spot(mRedPieces[8]);
		mSpots[3][6][9]=new Spot(TeamColor.GREEN, mRedPieces[9]);  mSpots[3][5][10]=new Spot(TeamColor.GREEN);
		mSpots[3][4][11]=new Spot(TeamColor.GREEN); mSpots[3][3][12]=new Spot(TeamColor.GREEN);
		// x=4
		mSpots[4][11][3]=new Spot(TeamColor.YELLOW); mSpots[4][10][4]=new Spot(TeamColor.YELLOW);
		mSpots[4][9][5]=new Spot(TeamColor.YELLOW);  mSpots[4][8][6]=new Spot();
		mSpots[4][7][7]=new Spot(); mSpots[4][6][8]=new Spot();  mSpots[4][5][9]=new Spot(TeamColor.GREEN);
		mSpots[4][4][10]=new Spot(TeamColor.GREEN); mSpots[4][3][11]=new Spot(TeamColor.GREEN);
		// x=5
		mSpots[5][10][3]=new Spot(TeamColor.YELLOW);
		mSpots[5][9][4]=new Spot(TeamColor.YELLOW);  mSpots[5][8][5]=new Spot();
		mSpots[5][7][6]=new Spot(); mSpots[5][6][7]=new Spot();  mSpots[5][5][8]=new Spot();
		mSpots[5][4][9]=new Spot(TeamColor.GREEN); mSpots[5][3][10]=new Spot(TeamColor.GREEN);
		// x=6
		mSpots[6][9][3]=new Spot(TeamColor.YELLOW, mGreenPieces[9]);  mSpots[6][8][4]=new Spot();
		mSpots[6][7][5]=new Spot(); mSpots[6][6][6]=new Spot();  mSpots[6][5][7]=new Spot();
		mSpots[6][4][8]=new Spot(); mSpots[6][3][9]=new Spot(TeamColor.GREEN, mYellowPieces[9]);
		// x=7
		mSpots[7][9][2]=new Spot(mGreenPieces[5]);  mSpots[7][8][3]=new Spot(mGreenPieces[8]);
		mSpots[7][7][4]=new Spot(); mSpots[7][6][5]=new Spot();  mSpots[7][5][6]=new Spot();
		mSpots[7][4][7]=new Spot(); mSpots[7][3][8]=new Spot(mYellowPieces[8]);
		mSpots[7][2][9]=new Spot(mYellowPieces[5]);
		// x=8
		mSpots[8][9][1]=new Spot(mGreenPieces[2]);  mSpots[8][8][2]=new Spot(mGreenPieces[4]);
		mSpots[8][7][3]=new Spot(mGreenPieces[7]);  mSpots[8][6][4]=new Spot();
		mSpots[8][5][5]=new Spot();  mSpots[8][4][6]=new Spot(); mSpots[8][3][7]=new Spot(mYellowPieces[7]);
		mSpots[8][2][8]=new Spot(mYellowPieces[4]); mSpots[8][1][9]=new Spot(mYellowPieces[2]);
		// x=9
		mSpots[9][9][0]=new Spot(mGreenPieces[0]);  mSpots[9][8][1]=new Spot(mGreenPieces[1]);
		mSpots[9][7][2]=new Spot(mGreenPieces[3]);  mSpots[9][6][3]=new Spot(TeamColor.RED, mGreenPieces[6]);
		mSpots[9][5][4]=new Spot(TeamColor.RED);    mSpots[9][4][5]=new Spot(TeamColor.RED);
		mSpots[9][3][6]=new Spot(TeamColor.RED, mYellowPieces[6]); mSpots[9][2][7]=new Spot(mYellowPieces[3]);
		mSpots[9][1][8]=new Spot(mYellowPieces[1]); mSpots[9][0][9]=new Spot(mYellowPieces[0]);
		// x=10
		mSpots[10][5][3]=new Spot(TeamColor.RED);  mSpots[10][4][4]=new Spot(TeamColor.RED);
		mSpots[10][3][5]=new Spot(TeamColor.RED);
		// x=11
		mSpots[11][4][3]=new Spot(TeamColor.RED);  mSpots[11][3][4]=new Spot(TeamColor.RED);
		// x=12
		mSpots[12][3][3]=new Spot(TeamColor.RED);

		// Spotクラスに座標を登録する
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					Spot spot = mSpots[x][y][z];
					if(spot != null){
						spot.setCoordinate(x, y, z);
					}
				}
			}
		}

		/* PointCalculatorに情報を登録 */
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					Spot spot = mSpots[x][y][z];
					if(spot != null){
						// ゴールのチームならその座標を登録
						if(spot.mTeam != null){
							mPointCalculator.addGoalSpot(spot.mTeam, spot.mCordinate);
						}
						// 駒の初期値であればその位置の座標を登録
						if(spot.mPiece != null){
							mPointCalculator.addPieceInitCordinate(spot.mPiece, spot.mCordinate);
						}
					}
				}
			}
		}

		mLog.config("createBaseBoard end");
		logConsoleBoardImage();
		logTeamPoints();
	}

	public void logConsoleBoardImage() {
		for(String s: getBoadString()){
			mLog.info(s);
		}
	}

	public void logTeamPoints(){
		Map<TeamColor, Integer> points = new HashMap<>();
		for(TeamColor teamColor : TeamColor.values()){
			points.put(teamColor, mPointCalculator.calcTeamPoint(teamColor, this));
		}
		mLog.info("Point: 赤:%d  黄:%d  緑:%d", points.get(TeamColor.RED),
												points.get(TeamColor.YELLOW),
												points.get(TeamColor.GREEN));
	}

	/**
	 * 指定されたチームのゴールに到達している駒の数を返す
	 * @param teamColor
	 * @return
	 */
	public int getGoalPieceCount(TeamColor teamColor) {
		// 指定されたSpotに自分のPieceがどれだけ乗っているかカウントする
		Set<Spot> teamSpots = getTeamGoalSpot(teamColor);
		long point = teamSpots.stream()
		.filter((s)->(s.mPiece != null) && (s.mPiece.getmTeamColor() == teamColor) )
		.count();
		mLog.fine("getTeamPoint %s -> %d", teamColor.getName(), point);
		return (int)point;
	}

	/**
	 * 指定されたチームのポイントを返します
	 * @param teamColor
	 * @return
	 */
	public int getPoint(TeamColor teamColor) {
		return mPointCalculator.calcTeamPoint(teamColor, this);
	}

	/**
	 * 自身の持つマス(Spot)、駒(Piece)を新たに配置します
	 */
	public void placePiece() {
		mLog.fine("placePiece start");
		createBaseBoard();
		mLog.fine("placePiece end");
	}

	public void move(Move move) {
		move(move, false);
	}

	public void move(Move move, boolean isSilent){
		if(!isSilent){
			mLog.info("move start %s", move);
		}

		// 移動が妥当であることの確認
		if(!isMoveValid(move, isSilent)){
			// ありえない
			assert false;
			return;
		}

		// 座標を更新する
		Piece piece = move.mPiece.getBasePiece();
		List<Spot> spots = move.mMoveSpots.stream().map((m)->getSpotFromCordinate(m.getBaseCordinate())).collect(Collectors.toList());
		Spot currentSpot = getSpotFromPiece(piece);
		Spot nextSpot    = spots.get(0);
		while(!spots.isEmpty()){
			mLog.fine("moving... cur:%s next:%s", currentSpot, nextSpot);
			movePiece(currentSpot, nextSpot);
			spots.remove(0);
			currentSpot = nextSpot;
			nextSpot    = !spots.isEmpty()? spots.get(0) : null;
		}

		if(!isSilent){
			mLog.info("move end %s", move);
			logConsoleBoardImage();
			logTeamPoints();
		}
	}

	/**
	 * 駒を動かす
	 * @param currentSpot
	 * @param nextSpot
	 */
	private void movePiece(Spot currentSpot, Spot nextSpot) {
		assert currentSpot.mPiece != null;
		assert nextSpot.mPiece    == null;
		Piece piece = currentSpot.mPiece;
		currentSpot.mPiece = null;
		nextSpot.mPiece    = piece;
		mLog.fine("movePiece %s -> %s", currentSpot, nextSpot);
	}

	/**
	 * 指定されたチームが既にゴール済みならtrueを返す
	 * @param teamColor
	 * @return
	 */
	public boolean isFinishedTeam(TeamColor teamColor){
		boolean result = getGoalPieceCount(teamColor) == 10;
		mLog.fine("isFinishedTeam? %s -> %b", teamColor.getName(), result);
		return result;
	}

	/**
	 * 指定されたチームの視点のUserBoardを提供する
	 * @param myTeam
	 * @return
	 */
	public UserBoard getUserBoard(TeamColor myTeam) {
		mLog.fine("getUserBoard called %s", myTeam);
		return new UserBoard(myTeam, this);
	}

	/**
	 * 指定されたMoveが移動可能であればtrueを返す
	 * @param move
	 * @return
	 */
	public boolean isMoveValid(Move move, boolean isSilent) {
		mLog.fine("isMoveValid start %s", move);

		// エラーチェック
		assert (move.getmUser() != null) && (move.getmTeam() != null);

		// エラーチェック
		if(move.mPiece == null || move.mMoveSpots == null || move.mMoveSpots.size() == 0){
			return false;
		}

		// User型のPiece/Spotを共通型へ変換
		Piece piece = move.mPiece.getBasePiece();
		List<Spot> spots = move.mMoveSpots.stream().map((m)->getSpotFromCordinate(m.getBaseCordinate())).collect(Collectors.toList());

		// 動かす駒がTeamColorに一致していることの確認
		if(piece.getmTeamColor() != move.getmTeam()){
			mLog.warning("isMoveValid error(teamColor) team:%s but piece:%s", move.getmTeam(), piece.getmTeamColor());
			return false;
		}

		// すべてのSpotに移動可能か確認する
		Spot currentSpot = getSpotFromPiece(piece);
		Spot nextSpot    = spots.get(0);
		boolean isFirst  = true;
		while(!spots.isEmpty()){
			boolean isAvailableMove = isAvailableMove(currentSpot, nextSpot);
			boolean isAdjacent      = isAdjacentSpot(currentSpot, nextSpot);
			if(isFirst){
				// 移動距離に関わらず、移動できるならOK
				if(isAvailableMove){
					if(!isSilent){
						mLog.info("isMoveValid(first) OK current:%s -> next:%s ", currentSpot, nextSpot);
					}
					// SpotsからDelete
					spots.remove(0);
					// 距離1にも関わらず、更に移動しようとした場合は失敗とする
					if(isAdjacentSpot(currentSpot, nextSpot) && !spots.isEmpty()){
						mLog.severe("isMoveValid(first) Failed But you attempted to continue", currentSpot, nextSpot);
						return false;
					}
					// 次の移動へ
					currentSpot = nextSpot;
					nextSpot    = !spots.isEmpty()? spots.get(0) : null;
					isFirst     = false;
					continue;
				}else{
					// 移動できないSpotだったら普通に失敗
					mLog.severe("isMoveValid(first) Failed current:%s -> next:%s ", currentSpot, nextSpot);
					return false;
				}
			}else{
				// 2回目以降の移動は距離2、かつ移動できなければならない
				if(isAvailableMove && !isAdjacent){
					if(!isSilent){
						mLog.info("isMoveValid OK current:%s -> next:%s ", currentSpot, nextSpot);
					}
					// SpotsからDelete
					spots.remove(0);

					// 次の移動へ
					currentSpot = nextSpot;
					nextSpot    = !spots.isEmpty()? spots.get(0) : null;
					isFirst     = false;
					continue;
				}else{
					// 移動できないSpot、もしくは隣接した移動なら失敗
					mLog.severe("isMoveValid Failed current:%s -> next:%s", currentSpot, nextSpot);
					return false;
				}
			}
		}

		// 移動可能
		mLog.fine("isMoveValid end(valid)");
		return true;
	}

	/**
	 * 指定されたMoveが移動可能であればtrueを返す
	 * @param move
	 * @return
	 */
	public boolean isMoveValid(Move move) {
		return isMoveValid(move, false);
	}

	/**
	 * 指定されたスポットから指定されたスポットまでPieceを移動可能か調べる
	 * @param currentSpot
	 * @param nextSpot
	 * @return
	 */
	boolean isAvailableMove(Spot currentSpot, Spot nextSpot) {
		mLog.fine("isAvailableMove start current:%s -> next:%s", currentSpot, nextSpot);

		// どちらかのSpotが存在しなければfalse
		if(currentSpot == null || nextSpot == null){
			mLog.warning("isMoveValid error(cant move) current:%s -> next:%s", currentSpot, nextSpot);
			return false;
		}

		// currentSpotの移動先候補にnextSpotが存在するか確認
		boolean   isContainAroundSpots = false;
		int       distance = -1;
		Direction direction = null;
		for(int i = 1; i < 3; i++){
			// 移動先候補を取得
			EnumMap<Direction,Spot> aroundSpots = getAroundSpot(currentSpot, i);
			// その中に移動先があれば取得
			for(Direction d : Direction.values()){
				if(aroundSpots.get(d) == nextSpot){
					isContainAroundSpots = true;
					distance             = i;
					direction            = d;
					break;
				}
			}
		}

		// 移動先が存在しなければfalse
		if(!isContainAroundSpots){
			mLog.warning("isAvailableMove error(not found) ");
			return false;
		}
		// 距離が1なら移動先にPieceが存在しなければ移動可能
		else if(distance == 1){
			boolean result = nextSpot.mPiece == null;
			mLog.fine("isAvailableMove distance=1 result:%b ", result);
			return result;
		}
		// 距離が2なら同方角の目の前にPieceが存在し、nextSpotにPieceが存在しなければ移動可能
		else if(distance == 2){
			Spot jumpSpot = getNextSpotUsingDirection(currentSpot, direction, 1);
			assert jumpSpot != null;
			boolean result = jumpSpot.mPiece != null && nextSpot.mPiece == null;
			mLog.fine("isAvailableMove distance=2 result:%b ", result);
			return result;
		}
		// それ以外はありえないはず
		else{
			assert false;
			return false;
		}
	}

	/**
	 * 指定されたSpotの周囲のSpotを返す。<br>
	 * 隣接したSpotと一つ空けたSpotの2パターンが指定できる
	 * @param spot
	 * @param distance
	 * @return
	 */
	EnumMap<Direction, Spot> getAroundSpot(Spot spot, int distance) {
		mLog.fine("getAroundSpot start spot:%s distance:%d ", spot, distance);
		assert spot != null;
		assert distance == 1 || distance == 2;

		EnumMap<Direction, Spot> result = new EnumMap<>(Direction.class);
		for(Direction d: Direction.values()){
			result.put(d, getNextSpotUsingDirection(spot, d, distance));
		}

		for(Entry<Direction, Spot> e: result.entrySet()){
			mLog.fine("getAroundSpot %s:\t%s ", e.getKey(), e.getValue());
		}
		return result;
	}

	/**
	 * Spot1とSpot2が隣接していればTrueを返します
	 * @param spot1 Boardに所属するSpot
	 * @param spot2 Boardに所属するSpot
	 * @return Spot1とSpot2が隣接していればTrue
	 * spot1とspot2は必ず同じBoardに所属しているSpotを指定すること
	 */
	boolean isAdjacentSpot(Spot spot1, Spot spot2){
		for(Entry<Direction, Spot> entry : getAroundSpot(spot1, 1).entrySet()){
			if(entry.getValue() != null && entry.getValue() == spot2){
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定されたTeamの陣地であるSpot郡を返却します
	 * @param team
	 * @return
	 */
	private Set<Spot> getTeamGoalSpot(TeamColor team){
		mLog.fine("getTeamGoalSpot start team:%s ", team);
		HashSet<Spot> result = new HashSet<>();
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					if(mSpots[x][y][z] != null && mSpots[x][y][z].mTeam == team){
						result.add(mSpots[x][y][z]);
					}
				}
			}
		}
		for(Spot s: result){
			mLog.fine("getTeamGoalSpot %s ", s);
		}
		return result;
	}

	/**
	 * 指定されたSpotに隣接する指定方角のSpotを返却する。<br>
	 * 隣接したSpotと一つ空けたSpotの2パターンが指定できる
	 * @param spot
	 * @param direction
	 * @param distance
	 * @return
	 */
	private Spot getNextSpotUsingDirection(Spot spot, Direction direction, int distance){
		mLog.fine("getNextSpotUsingDirection start spot:%s direction:%s distance:%d", spot, direction, distance);

		// Spotが存在しなければnullを返す
		if(spot == null){
			mLog.fine("getNextSpotUsingDirection spot is null");
			return null;
		}

		// 指定された方向の座標を取得する
		Cordinate newCordinate = spot.mCordinate.getMovedCordinate(distance, direction);
		if(newCordinate == null || !checkCordinate(newCordinate)){
			mLog.fine("getNextSpotUsingDirection cordinate is over");
			return null;
		}

		// 新たな座標をSpotに変換してReturn
		Spot result = getSpotFromCordinate(newCordinate);
		mLog.fine("getNextSpotUsingDirection result :%s", result);
		return result;
	}

	/**
	 * Cordinateが存在するものであればtrueを返却する
	 * @param cordinate
	 * @return
	 */
	private boolean checkCordinate(Cordinate cordinate){
		IntPredicate checker = (c)->(0<=c && c<=12);
		boolean result = checker.test(cordinate.x)
				&& checker.test(cordinate.y)
				&& checker.test(cordinate.z);
		mLog.fine("checkCordinate cordinate:%s result:%b", cordinate, result);
		return result;
	}

	/**
	 * 座標から特定されたSpotを返却する
	 * @param cordinate(範囲外を許容)
	 * @return 範囲外の場合nullを返す
	 */
	Spot getSpotFromCordinate(Cordinate cordinate) {
		if(checkCordinate(cordinate)){
			return mSpots[cordinate.x][cordinate.y][cordinate.z];
		}else{
			return null;
		}
	}

	/**
	 * 指定されたPieceが存在するSpotを返却する<br>
	 * @deprecated PieceからSpotを取得するのには無駄に計算量を必要とします
	 * @param piece
	 * @return
	 */
	Spot getSpotFromPiece(Piece piece) {
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					if(mSpots[x][y][z] != null && mSpots[x][y][z].mPiece == piece){
						return mSpots[x][y][z];
					}
				}
			}
		}
		return null;
	}

	/**
	 * 指定されたTeamのPiece一覧を返却します
	 * @param color
	 * @return
	 */
	Set<Piece> getPiecesFromTeam(TeamColor color){
		switch(color){
		case GREEN:  return new HashSet<>(Arrays.asList(mGreenPieces));
		case RED:    return new HashSet<>(Arrays.asList(mRedPieces));
		case YELLOW: return new HashSet<>(Arrays.asList(mYellowPieces));
		}
		return null;
	}

	/**
	 * [コンソール出力用] 指定されたBoardの状況をコンソール用に生成します
	 * String.format使えばよかった…
	 * @return
	 */
	public List<String> getBoadString(){
		List<String> result = new ArrayList<>();
		result.add(" ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑭⑮⑯⑰⑱⑲");
		result.add("A                    "+s2cs(12,3,3)+"                  ");
		result.add("B                  "+s2cs(11,4,3)+"  "+s2cs(11,3,4)+"                ");
		result.add("C                "+s2cs(10,5,3)+"  "+s2cs(10,4,4)+"  "+s2cs(10,3,5)+"              ");
		result.add("D  "+s2cs(9,9,0)+"  "+s2cs(9,8,1)+"  "+s2cs(9,7,2)+"  "+s2cs(9,6,3)+"  "+
				s2cs(9,5,4)+"  "+s2cs(9,4,5)+"  "+s2cs(9,3,6)+"  "+s2cs(9,2,7)+"  "+
				s2cs(9,1,8)+"  "+s2cs(9,0,9)+"");
		result.add("E    "+s2cs(8,9,1)+"  "+s2cs(8,8,2)+"  "+s2cs(8,7,3)+"  "+s2cs(8,6,4)+"  "+s2cs(8,5,5)+"  "+
				s2cs(8,4,6)+"  "+s2cs(8,3,7)+"  "+s2cs(8,2,8)+"  "+s2cs(8,1,9)+"  ");
		result.add("F      "+s2cs(7,9,2)+"  "+s2cs(7,8,3)+"  "+s2cs(7,7,4)+"  "+s2cs(7,6,5)+"  "+s2cs(7,5,6)+"  "+
				s2cs(7,4,7)+"  "+s2cs(7,3,8)+"  "+s2cs(7,2,9)+"    ");
		result.add("G        "+s2cs(6,9,3)+"  "+s2cs(6,8,4)+"  "+s2cs(6,7,5)+"  "+s2cs(6,6,6)+"  "+s2cs(6,5,7)+"  "+
				s2cs(6,4,8)+"  "+s2cs(6,3,9)+"      ");
		result.add("H      "+s2cs(5,10,3)+"  "+s2cs(5,9,4)+"  "+s2cs(5,8,5)+"  "+s2cs(5,7,6)+"  "+s2cs(5,6,7)+"  "+
				s2cs(5,5,8)+"  "+s2cs(5,4,9)+"  "+s2cs(5,3,10)+"    ");
		result.add("I    "+s2cs(4,11,3)+"  "+s2cs(4,10,4)+"  "+s2cs(4,9,5)+"  "+s2cs(4,8,6)+"  "+s2cs(4,7,7)+"  "+
				s2cs(4,6,8)+"  "+s2cs(4,5,9)+"  "+s2cs(4,4,10)+"  "+s2cs(4,3,11)+"  ");
		result.add("J  "+s2cs(3,12,3)+"  "+s2cs(3,11,4)+"  "+s2cs(3,10,5)+"  "+s2cs(3,9,6)+"  "+s2cs(3,8,7)+"  "+
				s2cs(3,7,8)+"  "+s2cs(3,6,9)+"  "+s2cs(3,5,10)+"  "+s2cs(3,4,11)+"  "+s2cs(3,3,12)+"");
		result.add("K                "+s2cs(2,9,7)+"  "+s2cs(2,8,8)+"  "+s2cs(2,7,9)+"              ");
		result.add("L                  "+s2cs(1,9,8)+"  "+s2cs(1,8,9)+"                ");
		result.add("M                    "+s2cs(0,9,9)+"                  ");
		result.add(" ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑭⑮⑯⑰⑱⑲");
		return result;
	}
	/**
	 * [コンソール出力用] 指定された座標のSpotを一文字の文字列に変換します spot to console string
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private String s2cs(int x, int y, int z){
		Spot spot = getSpotFromCordinate(new Cordinate(x, y, z));
		if(spot.mPiece != null){
			switch(spot.mPiece.getmTeamColor()){
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
	 * Board内のマスを表すクラス<br>
	 * このクラスはUserにも見えるため、setterを設けないか、
	 * もしくは無印かprivateで宣言すること
	 * @author 0000140105
	 *
	 */
	public class Spot{
		Piece     mPiece;
		TeamColor mTeam;
		Cordinate mCordinate;
		public Spot() {
			mTeam  = null;
			mPiece = null;
		}
		public Spot(TeamColor team, Piece piece) {
			mTeam  = team;
			mPiece = piece;
		}
		public Spot(TeamColor team) {
			mTeam  = team;
			mPiece = null;
		}
		public Spot(Piece piece) {
			mTeam  = null;
			mPiece = piece;
		}
		void setCoordinate(int x, int y, int z){
			mCordinate = new Cordinate(x, y, z);
		}
		private Board getOuterType() {
			return Board.this;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((mCordinate == null) ? 0 : mCordinate.hashCode());
			result = prime * result + ((mPiece == null) ? 0 : mPiece.hashCode());
			result = prime * result + ((mTeam == null) ? 0 : mTeam.hashCode());
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
			Spot other = (Spot) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (mCordinate == null) {
				if (other.mCordinate != null)
					return false;
			} else if (!mCordinate.equals(other.mCordinate))
				return false;
			if (mPiece == null) {
				if (other.mPiece != null)
					return false;
			} else if (!mPiece.equals(other.mPiece))
				return false;
			if (mTeam != other.mTeam)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "Spot [Piece=" + (mPiece!=null ? mPiece.getNameStr() : "null") +
					", GoalTeam=" + (mTeam != null ? mTeam.getSimpleName() : "null") +
					", Cordinate=" + mCordinate.getNameStr() + "]";
		}
	}

	/**
	 * 座標を表すクラス<br>
	 * このクラスはUserにも見えるため、setterを設けないか、
	 * もしくは無印かprivateで宣言すること
	 * @author 0000140105
	 *
	 */
	public static class Cordinate implements Cloneable {
		public int x, y, z;
		static DGLog Log = new DGLog(Direction.class.getSimpleName());
		public Cordinate(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public Cordinate clone() {
			try {
				return (Cordinate)super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}

		public void setCordinate(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public void setCordinate(Cordinate cordinate) {
			setCordinate(cordinate.x, cordinate.y, cordinate.z);
		}

		/**
		 * 指定された方角へdistance移動させた座標を返却する
		 * @param cordinate
		 * @param distance
		 * @return
		 */
		public Cordinate getMovedCordinate(int distance, Direction direction) {
			Log.fine("getMovedCordinate this:%s direction:%s distance:%d", this, direction, distance);
			assert distance == 2 || distance == 1;
			Cordinate result = this.clone();

			int dx=0, dy=0, dz=0;

			switch(direction){
			case RIGHT_FRONT:
				dx = distance;
				dy = -distance;
				dz = 0;
				break;
			case RIGHT:
				dx = 0;
				dy = -distance;
				dz = distance;
				break;
			case RIGHT_REAR:
				dx = -distance;
				dy = 0;
				dz = distance;
				break;
			case LEFT_REAR:
				dx = -distance;
				dy = distance;
				dz = 0;
				break;
			case LEFT:
				dx = 0;
				dy = distance;
				dz = -distance;
				break;
			case LEFT_FRONT:
				dx = distance;
				dy = 0;
				dz = -distance;
				break;
			}

			result.x += dx;
			result.y += dy;
			result.z += dz;

			Log.fine("getMovedCordinate result:%s ", result);
			return result;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
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
			Cordinate other = (Cordinate) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Cordinate [x=" + x + ", y=" + y + ", z=" + z + "]";
		}

		public String getNameStr(){
			return "[" + x + ", " + y + ", " + z + "]";
		}
	}

}

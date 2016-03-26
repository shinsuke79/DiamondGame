package game;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.DGLog;
import common.TeamColor;
import game.Board.Cordinate;
import game.Board.Spot;

public class PointCalculator {
	public final static int MAX_POINT = 100;

	Map<Piece, Cordinate>              mPieceInitCordinateTable;
	EnumMap<TeamColor, Set<Cordinate>> mGoalCordinateTable;
	DGLog mLog;


	public PointCalculator() {
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());

		mPieceInitCordinateTable = new HashMap<>();
		mGoalCordinateTable      = new EnumMap<>(TeamColor.class);
		for(TeamColor tc : TeamColor.values()){
			mGoalCordinateTable.put(tc, new HashSet<>());
		}
	}

	/**
	 * 指定されたBoardにおいて指定されたTeamのポイントを計算します. <br>
	 * ポイントは初期位置が0ポイント、すべての駒がゴールに辿り着いた場合、1000ポイントとなります
	 * @param teamColor 計算したいチーム
	 * @param board 計算に使用するBoard
	 * @return そのチームのポイント(最大1000)
	 */
	public int calcTeamPoint(TeamColor teamColor, Board board){
		mLog.fine("calcTeamPoint(%s) start", teamColor.getSimpleName());

		// 対象チームのGoalとなる座標一覧を取得
		Set<Cordinate> goalCordinates = getGoalSpots(teamColor);

		// ポイントとなる情報郡一覧を生成
		Map<Cordinate, PointInfo> points = new HashMap<>();
		for(Cordinate goalCordinate : goalCordinates){
			points.put(goalCordinate, new PointInfo());
		}

		// #25 パフォーマンス改善
		List<Cordinate> entryKeyList = new ArrayList<>(points.keySet());

		// GoalCordinateを深い順にソートする(深いと大きな値になる=浅い順に-1をかける)
		entryKeyList.sort((c1, c2)->compareCordinate(teamColor, c1, c2)*-1);

		/* 駒を持っていないGoalCordinateはルールにしたがってターゲットとなるPieceを決める */

		// ゴールしていないPiece一覧を取得
		List<Piece> notGoalPieces = new ArrayList<>();
		for(Piece piece : board.getPiecesFromTeam(teamColor)){
			Spot spot = board.getSpotFromPiece(piece);
			assert spot != null;
			if(spot.mTeam != piece.getmTeamColor()){
				notGoalPieces.add(piece);
			}
		}

		// ゴールしていないPieceを初期位置から離れている順にソート
		notGoalPieces.sort((p1, p2)->comparePiece(board, p1, p2)*-1);

		// ポイントの計算
		int notGoalPieceCounter = 0;
		int sum                 = 0;
		for(Cordinate cordinate : entryKeyList){
			/* ゴール済みではないSpotに計算対象のPieceを結びつける */
			Spot spot = board.getSpotFromCordinate(cordinate);
			// ゴールしていればMAX_POINTを付ける
			if(spot.mPiece != null && spot.mTeam == spot.mPiece.getmTeamColor()){
				points.get(cordinate).mPoint = MAX_POINT;
			}
			// ゴールしていなければpieceを登録
			else{
				points.get(cordinate).mTargetPiece = notGoalPieces.get(notGoalPieceCounter);
				notGoalPieceCounter++;
			}

			/* 得点を計算 */
			PointInfo pointInfo = points.get(cordinate);
			if(pointInfo.mPoint == MAX_POINT){
				// すでにMAX_POINTがMAXなら計算不要
			}else{
				// ゴールの座標
				Cordinate goalCordinate    = cordinate;
				// 現在の座標
				Cordinate currentCordinate = board.getSpotFromPiece(pointInfo.mTargetPiece).mCordinate;
				// 初期位置の座標
				Cordinate initCordinate    = getPieceInitCordinate(pointInfo.mTargetPiece);

				// 初期位置～ゴールの距離
				int init2goal    = calcDistance(initCordinate, goalCordinate);
				// 現在位置～ゴールの距離
				int current2goal = calcDistance(currentCordinate, goalCordinate);

				// ポイントの計算(ゴールまで残り10%なら90点を返す)
				pointInfo.mPoint = (int)(100 - (((double)current2goal / init2goal)*100));
			}

			/* 得点合計を計算 */
			sum += pointInfo.mPoint;
		}

		mLog.fine("calcTeamPoint(%s) end -> %d", teamColor.getSimpleName(), sum);
		return sum;
	}

	/**
	 *
	 * @param p1
	 * @param p2
	 * @return p1のほうが初期位置から離れているなら正の値
	 */
	private int comparePiece(Board board, Piece p1, Piece p2) {
		int p1Distance = 0;
		int p2Distance = 1;

		Cordinate p1InitCordinate = mPieceInitCordinateTable.get(p1);
		Cordinate p2InitCordinate = mPieceInitCordinateTable.get(p2);

		@SuppressWarnings("deprecation")
		Cordinate p1CurrentCordinate = board.getSpotFromPiece(p1).mCordinate;
		@SuppressWarnings("deprecation")
		Cordinate p2CurrentCordinate = board.getSpotFromPiece(p2).mCordinate;

		p1Distance = calcDistance(p1InitCordinate, p1CurrentCordinate);
		p2Distance = calcDistance(p2InitCordinate, p2CurrentCordinate);

		return p1Distance - p2Distance;
	}

	/**
	 * Cordinateの距離計算
	 * @param c1
	 * @param c2
	 * @return
	 */
	private int calcDistance(Cordinate c1, Cordinate c2) {
		/* #25 パフォーマンス改善
		return (int)sqrt( pow(c1.x-c2.x, 2) + pow(c1.y-c2.y, 2) + pow(c1.z-c2.z, 2) );
		 */
		return (int)sqrt( (c1.x-c2.x)*(c1.x-c2.x) + (c1.y-c2.y)*(c1.y-c2.y) + (c1.z-c2.z)*(c1.z-c2.z) );
	}

	/**
	 *
	 * @param teamColor
	 * @param c1
	 * @param c2
	 * @return c1のほうが深い場合は正の値、c2のほうが深い場合は負の値、同じなら0
	 */
	private int compareCordinate(TeamColor teamColor, Cordinate c1, Cordinate c2) {
		int c1Deep = 0;
		int c2Deep = 0;

		switch(teamColor){
		case RED:
			c1Deep = c1.x*100 + c1.y;
			c2Deep = c2.x*100 + c2.y;
			break;
		case GREEN:
			c1Deep = c1.z*100 + c1.x;
			c2Deep = c2.z*100 + c2.x;
			break;
		case YELLOW:
			c1Deep = c1.y*100 + c1.z;
			c2Deep = c2.y*100 + c2.z;
			break;
		}

		// Deepは深いほうが大きな値
		return c1Deep - c2Deep;
	}

	/**
	 * 指定されたTeamのゴールとなる座標を登録します
	 * @param mTeam
	 * @param mCordinate
	 */
	public void addGoalSpot(TeamColor team, Cordinate cordinate) {
		mGoalCordinateTable.get(team).add(cordinate);
	}

	/**
	 * 指定されたTeamのゴールとなる座標一覧を返却します
	 * @param team
	 * @return
	 */
	public Set<Cordinate> getGoalSpots(TeamColor team){
		return new HashSet<>(mGoalCordinateTable.get(team));
	}

	/**
	 * 指定された駒の初期位置を登録します
	 * @param mPiece
	 * @param mCordinate
	 */
	public void addPieceInitCordinate(Piece piece, Cordinate cordinate) {
		mPieceInitCordinateTable.put(piece, cordinate);
	}

	/**
	 * 指定されたPieceの初期座標を返却します
	 * @param piece
	 * @return
	 */
	public Cordinate getPieceInitCordinate(Piece piece){
		return mPieceInitCordinateTable.get(piece);
	}

	class PointInfo{
		int   mPoint;
		Piece mTargetPiece;
		@Override
		public String toString() {
			return "PointInfo [mPoint=" + mPoint + ", mTargetPiece=" + mTargetPiece.getNameStr() + "]";
		}
	}
}

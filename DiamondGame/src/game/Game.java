package game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import common.DGLog;
import common.TeamColor;
import game.Board.Cordinate;
import user.User;
import user.UserInfo;
import user.humanUser.HumanUser;
import view.Event.ChangeTernEvent;
import view.Event.GameFinishedEvent;
import view.Event.GameStartedEvent;
import view.Event.PieceMovedEvent;
import view.Event.PointChangedEvent;
import view.Event.TeamReachedGoalEvent;
import view.UserInterface;

/**
 * ゲームの場を表現するクラス
 * @author yone
 *
 */
public class Game {
	UserInterface mUI;
	Map<TeamColor, User> mUsers;
	GameConfig mRule;
	GameMaster mGameMaster;
	Board      mBoard;
	TeamColor  mCurrentTeam;
	TeamColor  mFirstTeam;
	List<TeamColor> mGoalTeams;
	int        mTernNo;
	DGLog   mLog;
	boolean mIsFinished;

	public Game(GameConfig gameConfig) {
		assert gameConfig != null;
		mRule = gameConfig;

		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());

		mIsFinished = false;
	}

	/**
	 * ゲームに参加するユーザークラスを登録します
	 * 引数に指定するusersには３チームすべてのUserが登録されていなければいけません
	 * @param users
	 */
	public void assignUsers(Map<TeamColor, User> users) {
		assert users != null;
		assert users.size() == 3;
		mLog.info("assignUsers %s", users);
		mUsers = users;
	}

	/**
	 * ゲームを進行するゲームマスターを登録します
	 * @param gameMaster
	 */
	public void assignGameMaster(GameMaster gameMaster) {
		mLog.info("assignGameMaster %h", gameMaster);
		mGameMaster = gameMaster;
		gameMaster.setGame(this);
	}

	/**
	 * ゲームにUserInterfaceを実装したクラスを登録します
	 * @param ui
	 */
	public void setUserInterface(UserInterface ui) {
		assert ui != null;
		mLog.info("setUserInterface %s", ui.getClass().getName());
		mUI = ui;
	}

	/**
	 * ゲームを開始するための準備を行います
	 */
	public void prepareGame() {
		mLog.info("prepareGame start");
		mGoalTeams = new ArrayList<>();

		/* Game層で行なうべき処理 */
		editUser();

		/* あとはゲームマスターが準備する */
		mGameMaster.prepareGame();

		mLog.info("prepareGame end");
	}

	/**
	 * ユーザーに関わる情報を調節します
	 */
	private void editUser() {
		// HumanUserに限り、UIクラスを設定する
		mUsers.values().stream().filter((u)-> u instanceof HumanUser).forEach((u)->{
			mLog.info("set UI to User#%h ", u);
			((HumanUser)u).setUserInterface(mUI);
		});;
	}

	/* ボードを作成する */
	public void createBoard(){
		mLog.info("createBoard start");
		mBoard = new Board();
		mLog.info("createBoard end");
	}

	/* ゲームをする雰囲気になる */
	public void startGame() {
		mLog.info("startGame start");
		mIsFinished = false;
		mGameMaster.startGame();

		// イベントの通知
		EnumMap<TeamColor, UserInfo> users = new EnumMap<>(TeamColor.class);
		for(Entry<TeamColor, User> entry : mUsers.entrySet()){
			users.put(entry.getKey(), entry.getValue().getUserInfo());
		}
		mUI.notifyEvent(new GameStartedEvent(this, users, mFirstTeam));

		mLog.info("startGame end");
	}

	/* ゲームを終了する雰囲気になる */
	public void finishGame(){
		mLog.info("finishGame start");

		// 内部状態の変更
		mIsFinished = true;

		// イベントの送信
		ArrayList<TeamColor> goalTeams = new ArrayList<>(mGoalTeams);
		EnumMap<TeamColor, Integer> teamPoints = new EnumMap<>(TeamColor.class);
		for(TeamColor tc : TeamColor.values()){
			teamPoints.put(tc, getTeamPoint(tc));
		}
		mUI.notifyEvent(new GameFinishedEvent(this, goalTeams, teamPoints));

		mLog.info("finishGame end");
	}

	public void pieceMoved(Move move){
		Cordinate cordinate = mBoard.getSpotFromPiece(move.mPiece.getBasePiece()).mCordinate;
		mUI.notifyEvent(new PieceMovedEvent(this, move.getmTeam(), move, cordinate));
	}

	/**
	 * 最初と現在のチームを設定しておく
	 * @param firstTeam
	 */
	public void setFirstTeam(TeamColor firstTeam) {
		mLog.info("setFirstTeam firstTeam:%s", firstTeam);
		mCurrentTeam = mFirstTeam = firstTeam;
	}

	public UserInterface getUI() {
		mLog.fine("getUI ui:%s", mUI.getClass().getName());
		return mUI;
	}

	public Map<TeamColor, User> getUsers() {
		mLog.fine("getUsers %s", mUsers);
		return mUsers;
	}

	public GameConfig getRule() {
		return mRule;
	}

	public GameMaster getGameMaster() {
		return mGameMaster;
	}

	public Board getBoard() {
		return mBoard;
	}

	public User getCurrentUser() {
		User user = mUsers.get(mCurrentTeam);
		mLog.fine("getCurrentUser %s", user);
		return user;
	}

	public UserBoard getUserBoard(User user) {
		return mBoard.getUserBoard(user.getMyTeam());
	}

	/**
	 * 現在のチームの次のチームを返却します
	 * すでにゴール済みのチームは含まれません
	 * @return
	 */
	public TeamColor getNextTeam() {
		TeamColor nextTeam = mCurrentTeam;
		while(true){
			nextTeam = nextTeam.getNext();
			if(!mGoalTeams.contains(nextTeam)){
				break;
			}
		}
		mLog.info("getNextTeam %s", nextTeam);
		return nextTeam;
	}

	/**
	 * 次のチームを設定します
	 * @param nextTeam
	 */
	public void setNextTeam(TeamColor nextTeam) {
		mLog.info("setNextTeam %s", nextTeam);
		mCurrentTeam = nextTeam;

		// イベントの送信
		mUI.notifyEvent(new ChangeTernEvent(this, mCurrentTeam, mUsers.get(mCurrentTeam).getUserInfo()));
	}

	/**
	 * ターン数を返却します
	 * @return
	 */
	public int getmTernNo() {
		return mTernNo;
	}

	/**
	 * ターン数を設定します
	 * @param mTernNo
	 */
	public void setmTernNo(int mTernNo) {
		mLog.info("setmTernNo %d", mTernNo);
		this.mTernNo = mTernNo;
	}

	public List<TeamColor> getGoalTeams() {
		mLog.fine("getGoalTeams %s", mGoalTeams);
		return mGoalTeams;
	}

	public void addGoalTeam(TeamColor teamColor){
		mLog.fine("addGoalTeam %s", teamColor);
		assert !mGoalTeams.contains(teamColor);
		mGoalTeams.add(teamColor);

		// イベントの送信
		ArrayList<TeamColor> goalTeams = new ArrayList<>(mGoalTeams);
		EnumMap<TeamColor, Integer> teamPoints = new EnumMap<>(TeamColor.class);
		for(TeamColor tc : TeamColor.values()){
			teamPoints.put(tc, getTeamPoint(tc));
		}
		mUI.notifyEvent(new TeamReachedGoalEvent(this, teamPoints, mUsers.get(teamColor).getUserInfo(), teamColor, goalTeams));
	}

	public int getTeamPoint(TeamColor teamColor){
		int teamPoint = mBoard.getGoalPieceCount(teamColor);
		mLog.fine("getTeamPoint %s -> %d", teamColor, teamPoint);
		return teamPoint;
	}

	public boolean isFinished() {
		return mIsFinished;
	}

	public void updatedPoints() {
		EnumMap<TeamColor, Integer> teamPoints = new EnumMap<>(TeamColor.class);
		for(TeamColor tc : TeamColor.values()){
			teamPoints.put(tc, getTeamPoint(tc));
		}
		mUI.notifyEvent(new PointChangedEvent(this, teamPoints));
	}
}

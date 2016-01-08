package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.DGLog;
import common.TeamColor;
import user.User;
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

	public Game(GameConfig gameConfig) {
		assert gameConfig != null;
		mRule = gameConfig;

		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
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
		mLog.info("assignGameMaster %s", ui.getClass().getName());
		mUI = ui;
	}

	/**
	 * ゲームを開始するための準備を行います
	 */
	public void prepareGame() {
		mLog.info("prepareGame start");
		mGoalTeams = new ArrayList<>();

		/* あとはゲームマスターが準備する */
		mGameMaster.prepareGame();

		mLog.info("prepareGame end");
	}

	/* ボードを作成する */
	public void createBoard(){
		mLog.info("createBoard start");
		mBoard = new Board();
		mLog.info("createBoard end");
	}

	/* ゲームをする気持ちになる */
	public void startGame() {
		mLog.info("startGame start");
		mGameMaster.startGame();
		mLog.info("startGame end");
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
		TeamColor nextTeam = null;
		while(true){
			nextTeam = mCurrentTeam.getNext();
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
		mLog.info("getGoalTeams %s", mGoalTeams);
		return mGoalTeams;
	}

	public int getTeamPoint(TeamColor teamColor){
		int teamPoint = mBoard.getTeamPoint(teamColor);
		mLog.fine("getTeamPoint %s -> %d", teamColor, teamPoint);
		return teamPoint;
	}
}

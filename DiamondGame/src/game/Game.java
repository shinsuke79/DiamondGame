package game;

import java.util.List;
import java.util.Map;

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
	List<TeamColor> goalTeams;
	int        mTernNo;

	public Game(GameConfig gameConfig) {
		assert gameConfig != null;
		mRule = gameConfig;
	}

	/**
	 * ゲームに参加するユーザークラスを登録します
	 * 引数に指定するusersには３チームすべてのUserが登録されていなければいけません
	 * @param users
	 */
	public void assignUsers(Map<TeamColor, User> users) {
		assert users != null;
		assert users.size() == 3;
		mUsers = users;
	}

	/**
	 * ゲームを進行するゲームマスターを登録します
	 * @param gameMaster
	 */
	public void assignGameMaster(GameMaster gameMaster) {
		mGameMaster = gameMaster;
		gameMaster.setGame(this);
	}

	/**
	 * ゲームにUserInterfaceを実装したクラスを登録します
	 * @param ui
	 */
	public void setUserInterface(UserInterface ui) {
		assert ui != null;
		mUI = ui;
	}

	/**
	 * ゲームを開始するための準備を行います
	 */
	public void prepareGame() {
		/* ゲームマスターが準備する */
		mGameMaster.prepareGame();
	}

	/* ボードを作成する */
	public void createBoard(){
		mBoard = new Board();
	}

	/* ゲームをする気持ちになる */
	public void startGame() {
		mGameMaster.startGame();
	}

	/**
	 * 最初と現在のチームを設定しておく
	 * @param firstTeam
	 */
	public void setFirstTeam(TeamColor firstTeam) {
		mCurrentTeam = mFirstTeam = firstTeam;
	}

	public UserInterface getUI() {
		return mUI;
	}

	public Map<TeamColor, User> getUsers() {
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
		return mUsers.get(mCurrentTeam);
	}

	public UserBoard getUserBoard() {
		return new UserBoard();
	}

	public TeamColor getNextTeam() {
		// TODO 終わっているチームを除外する
		return mCurrentTeam.getNext();
	}

	public void setNextTeam(TeamColor nextTeam) {
		mCurrentTeam = nextTeam;
	}

	public int getmTernNo() {
		return mTernNo;
	}

	public void setmTernNo(int mTernNo) {
		this.mTernNo = mTernNo;
	}
}

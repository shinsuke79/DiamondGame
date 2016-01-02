package view.consoleUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import common.DGConfig;
import common.DGLog;
import common.TeamColor;
import game.Game;
import game.GameConfig;
import game.GameManager;
import game.GameMaster;
import user.User;
import user.UserInfo;
import user.UserManager;
import view.UserInterface;

public class ConsoleUserInterface implements UserInterface, Runnable {
	private GameManager mGameMgr;
	private UserManager mUserMgr;
	private State       mState;
	private DGLog       mLog;
	private GameConfig  mGameConfig;
	private Game mGame;

	public ConsoleUserInterface(GameManager gameMgr, UserManager userMgr) {
		assert (gameMgr != null) && (userMgr != null);

		mGameMgr = gameMgr;
		mUserMgr = userMgr;
		mState   = State.INIT;
		mLog     = new DGLog(this.getClass().getSimpleName());
		mGameConfig = null;
		mGame       = null;
	}

	@Override
	public void start() {
		new Thread(this).start();
	}

	private void stateControl() {
		mLog.fine("stateControl state:%s ", mState.name());
		switch(mState){
		case INIT:
			procInInit();
			break;
		case CONFIG:
			procInConfig();
			break;
		case CREATE_GAME:
			procInCreateGame();
			break;
		case WAIT_START:
			procInWaitStart();
			break;
		}
	}

	private void procInWaitStart() {
		assert (mGameMgr != null) && (mUserMgr != null);
		assert (mGameConfig != null) && (mGame != null);
		System.out.println("1:ゲーム開始");
		int start = -1;
		while(start != 1){
			start = getUserInputAsInt();
			if(start != 1){
				System.out.println("入力に誤りがあります");
			}
		}

		mLog.info("*** start game! ***");
		mState = State.RUNNING;

		/* ゲーム開始(これ以降UIはイベントドリブンで表示を更新していく) */
		mGame.startGame();
	}

	/**
	 * 作成したゲーム設定を元にゲームを開始する
	 */
	private void procInCreateGame() {
		assert (mGameMgr != null) && (mUserMgr != null) && (mGameConfig != null);

		mLog.fine("procInCreateGame");

		mGame = mGameMgr.createGame(mGameConfig);

		/* ユーザーの作成 */
		Map<TeamColor, User> createdUsers = new HashMap<>();
		for(Entry<TeamColor, UserInfo> entry: mGameConfig.users.entrySet()){
			User createdUser = mUserMgr.createUser(entry.getValue());
			assert createdUser != null;
			createdUsers.put(entry.getKey(), createdUser);
		}

		/* ゲームマスターの生成 */
		GameMaster gameMaster = new GameMaster();
		mGame.assignGameMaster(gameMaster);

		/* ゲームオブジェクトに対して情報を設定していく */
		mGame.assignUsers(createdUsers);
		mGame.setUserInterface(this);

		/* ゲームオブジェクトにゲーム開始準備要請 */
		mGame.prepareGame();

		mState = State.WAIT_START;
	}

	/**
	 * ゲームを開始、もしくは過去のゲームやユーザーの情報を表示
	 */
	private void procInInit() {
		System.out.println("1:ゲーム開始");
		int input = getUserInputAsInt();
		mLog.fine("procInInit input:%d", input);
		if(input == -1){
			System.err.println("入力に誤りがあります");
			return;
		}
		switch(input){
		case 1:
			System.out.println("ゲーム開始");
			mState = State.CONFIG;
			break;
		default:
			System.err.println("入力に誤りがあります");
			return;
		}

	}

	/**
	 * ゲームを開始するために必要な情報を入力してもらう
	 */
	private void procInConfig() {
		GameConfig config = new GameConfig();

		// 使用するユーザー
		Map<TeamColor,UserInfo> selectedUsers = new HashMap<>();
		List<UserInfo> allUsers       = mUserMgr.getAllUsers();
		assert allUsers != null;
		for(TeamColor color: TeamColor.values()){
			System.out.printf("%sのユーザーを選択してください\n", color.getName());
			UserInfo selectedUser = selectUser(allUsers);
			if(selectedUser != null){
				selectedUsers.put(color, selectedUser);
			}else{
				System.err.println("入力に誤りがあります。");
				continue;
			}
		}
		config.users = selectedUsers;

		// 制限時間
		int timeLimit = -1;
		while(timeLimit < DGConfig.LowestTimeLimitSec){
			System.out.printf("１ターンあたりの制限時間を秒単位で入力してください(最低%d秒)\n", DGConfig.LowestTimeLimitSec);
			timeLimit = getUserInputAsInt();
			mLog.config("user selected timelimit:%d", timeLimit);
			if(timeLimit < DGConfig.LowestTimeLimitSec)
				System.err.println("入力に誤りがあります");
		}
		config.timeLimitSec = timeLimit;

		// 記録として残すかどうか
		int recordResult = -1;
		while(!(recordResult == 1 || recordResult == 2)){
			System.out.printf("1:このゲーム結果を記録に残す 2:このゲーム結果を記録に残さない\n");
			recordResult = getUserInputAsInt();
			mLog.config("user selected recordResult:%d", recordResult);
			if(!(recordResult == 1 || recordResult == 2))
				System.err.println("入力に誤りがあります");
		}
		config.recordResult = recordResult==1? true : false;

		// GameConfigを使用してGameを作成する
		mLog.info("config finish %s", config.toString());
		mGameConfig = config;

		mState = State.CREATE_GAME;
	}

	private UserInfo selectUser(List<UserInfo> allUsers) {
		UserInfo user = null;
		for(int i=0; i<allUsers.size(); i++){
			System.out.printf("%d:%s",i,allUsers.get(i).getName());
			System.out.print("\t");
		}
		System.out.println();
		int input = getUserInputAsInt();
		mLog.fine("selected input:%d", input);
		try {
			if(input == -1)   throw new RuntimeException();
			user           = allUsers.get(input);
			if(user  == null) throw new RuntimeException();
		} catch (Exception e) {
			System.err.println("入力に誤りがあります");
			return null;
		}
		mLog.config("selected user:%s", user.getName());
		return user;
	}

	private String getUserInput() {
		try {
			System.out.print(">");
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private int getUserInputAsInt() {
		try {
			return Integer.parseInt(getUserInput());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void run() {
		mLog.info("start ConsoleUI ");
		try {
			while(!mState.equals(State.TERMINATE)){
				stateControl();
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
				mState = State.TERMINATE;
		}
	}
}

// 　①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑭⑮⑯⑰⑱
//A　　　　　　　　　　○　　　　　　　　　
//B　　　　　　　　　○　○　　　　　　　　
//C　　　　　　　　○　○　○　　　　　　　
//D　緑　緑　緑　緑　○　○　黃　黃　黃　黃
//E　　緑　緑　緑　○　○　○　黃　黃　黃　
//F　　　緑　緑　○　○　○　○　黃　黃　　
//G　　　　緑　○　○　○　○　○　黃　　　
//H　　　○　○　○　○　○　○　○　○　　
//I　　○　○　○　○　○　○　○　○　○　
//J　○　○　○　赤　赤　赤　赤　○　○　○
//K　　　　　　　　赤　赤　赤　　　　　　　
//L　　　　　　　　　赤　赤　　　　　　　　
//M　　　　　　　　　　赤　　　　　　　　　

enum State {
	INIT,
	CONFIG,
	CREATE_GAME,
	WAIT_START,
	RUNNING,
	TERMINATE
}
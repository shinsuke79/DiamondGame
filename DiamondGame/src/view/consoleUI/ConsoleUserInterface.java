package view.consoleUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import common.DGConfig;
import common.DGLog;
import common.TeamColor;
import game.Game;
import game.GameConfig;
import game.GameManager;
import game.GameMaster;
import game.UserBoard;
import game.UserBoard.UserCordinate;
import user.User;
import user.UserInfo;
import user.UserManager;
import user.humanUser.HumanUser;
import view.Event;
import view.Event.GameFinishedEvent;
import view.UserInterface;

public class ConsoleUserInterface implements UserInterface, Runnable {
	private GameManager mGameMgr;
	private UserManager mUserMgr;
	private State       mState;
	private DGLog       mLog;
	private GameConfig  mGameConfig;
	private Game mGame;
	private Queue<Event> mEvents;

	public ConsoleUserInterface() {
		mState   = State.INIT;
		mLog     = new DGLog(this.getClass().getSimpleName());
		mGameConfig = null;
		mGame       = null;
		mEvents = new LinkedList<>();
	}

	@Override
	public void setManager(GameManager gameMgr, UserManager userMgr) {
		assert (gameMgr != null) && (userMgr != null);
		mGameMgr = gameMgr;
		mUserMgr = userMgr;
	}

	@Override
	public void start() {
		new Thread(this).start();
	}

	private void stateControl() {
		// mLog.fine("stateControl state:%s ", mState.name());
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
		case RUNNING:
			procInRunning();
			break;
		}
	}

	private void procInRunning() {
		Event event = getEvent();
		if(event == null){
			return;
		}

		switch(event.getEventId()){
		case GAME_FINISHED:
			assert event instanceof GameFinishedEvent;
			receiveGameFinishedEvent((GameFinishedEvent)event);
			break;
		}
	}

	private void receiveGameFinishedEvent(GameFinishedEvent event){
		List<TeamColor> goalTeams = event.getGoalTeams();
		StringBuilder result = new StringBuilder("");
		if(!goalTeams.isEmpty()){
			for(int i=0; i<goalTeams.size(); i++){
				result.append(String.format("%d位:%s ", i+1, goalTeams.get(i)));
			}
		}else{
			result.append("誰もゴール出来ませんでした");
		}
		System.out.println("ゲーム終了! 結果=>" + result);
		mState = State.TERMINATE;
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
			// SmoothDebugOnConsoleがTrueの場合、ゲーム設定を省略する
			if(!DGConfig.SmoothDebugOnConsole){
				mState = State.CONFIG;
			}else{
				constructConfig();
				mState = State.CREATE_GAME;
			}
			break;
		default:
			System.err.println("入力に誤りがあります");
			return;
		}

	}

	/**
	 * デバッグ用：ユーザー入力をせずにConfigを構築します
	 */
	private void constructConfig() {
		GameConfig config = new GameConfig();
		config.recordResult = false;
		config.timeLimitSec = 10000;
		config.users = new HashMap<TeamColor, UserInfo>();
		config.users.put(TeamColor.RED, mUserMgr.getAllUsers().get(0));
		config.users.put(TeamColor.GREEN, mUserMgr.getAllUsers().get(1));
		config.users.put(TeamColor.YELLOW, mUserMgr.getAllUsers().get(2));
		mLog.info("config finish %s", config.toString());
		mGameConfig = config;
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

	@Override
	public UIHand askHand(HumanUser humanUser, UserBoard userBoard) {
		// ヘッダー表示
		System.out.println();
		System.out.println("↓=====================================================================================↓");
		System.out.println("★★" + humanUser.getMyTeam().getName()+"のターン★★");

		// コンソールに現在の状況を表示
		userBoard.logConsoleUserBoardImage();

		// 移動元を選択してもらう
		System.out.println("移動元のマスを選択してください");

		// 移動できる座標の候補一覧を取得
		List<UserCordinate> fromUserCordinates =  userBoard.getPiecesFromTeam(humanUser.getMyTeam()).stream()
										.map((up)->userBoard.getUserCordinateFromPiece(up))
										.filter((uCordinate) -> userBoard.getMovableCordinates(uCordinate).size() > 0)
										.collect(Collectors.toList());
		List<UIBoardSpot> fromUISpots = fromUserCordinates.stream()
										.map((uCordinate)->UIBoardSpot.parseUserCordinate(uCordinate))
										.collect(Collectors.toList());

		UIBoardSpot fromUiSpot = null;
		while(fromUiSpot == null){
			// 候補を表示
			System.out.print("移動元候補：");
			fromUISpots.forEach((us)->System.out.print(us.getValueStr()+","));
			System.out.println();

			// ユーザ入力
			UIBoardSpot inputUiSpot = selectUISpot();

			// fromUISpotsに含まれているか確認
			if(fromUISpots.contains(inputUiSpot)){
				fromUiSpot = inputUiSpot;
			}
			// ダメならやり直し
			else{
				System.out.println("入力に誤りがあります");
			}
		}

		// 選択したSpotからPieecを移動させる軌跡を構築する
		System.out.println("駒を移動させる軌跡を選択していきます");
		List<UIBoardSpot> to = new ArrayList<>();
		UIBoardSpot currentToSpot = fromUiSpot;
		while(true){
			// 最初に選んだSpotを表示
			System.out.printf("移動元: %s \n", fromUiSpot.getValueStr());

			// 現在のSpotを表示
			System.out.printf("現在: %s \n", currentToSpot.getValueStr());

			// 現在のSpotから移動可能なSpot(全方位1と2)を取得する
			UserCordinate currentToUserCordinate = currentToSpot.toUserCordinate();
			Set<UserCordinate> toMovableCordinate = userBoard.getMovableCordinates(currentToUserCordinate);
			List<UIBoardSpot> toMovableUISpots = toMovableCordinate.stream()
											.map((uCordinate)->UIBoardSpot.parseUserCordinate(uCordinate))
											.collect(Collectors.toList());

			// 表示
			System.out.print("移動先候補：");
			toMovableUISpots.forEach((us)->System.out.print(us.getValueStr()+","));
			System.out.println();

			// ユーザー入力
			UIBoardSpot inputUiSpot = selectUISpot();
			mLog.fine("User Sellect UiSpot:%s", inputUiSpot.getValueStr());

			// 候補に含まれているか確認
			if(!toMovableUISpots.contains(inputUiSpot)){
				mLog.fine("uiSpot is not included in toMovableUISpots");
				System.out.println("入力に誤りがあります");
				continue;
			}

			// Listに追加
			to.add(inputUiSpot);

			// currentを今選択したSpotに変更
			currentToSpot = inputUiSpot;

			// 続けますか？(yse/no/reset)
			System.out.println("Pieceの移動を継続しますか？(yes:続ける no:終わり reset:最初から)");
			String userChoice = null;
			while(true){
				userChoice = getUserInput();
				if(userChoice == null){
					System.out.println("入力に誤りがあります");
					continue;
				}
				if(!Arrays.asList("yes","no","reset").contains(userChoice)){
					System.out.println("入力に誤りがあります");
					continue;
				}
				break;
			}

			// yesなら続ける、resetならListとcurrentをリセットして続ける、noなら終了
			if(userChoice.equals("yes")){
				continue;
			}else if(userChoice.equals("reset")){
				currentToSpot = fromUiSpot;
				to.clear();
				continue;
			}else{
				break;
			}
		}

		// 返却する情報を生成
		UIHand result = new UIHand();
		result.from = fromUiSpot;
		result.to   = to;

		System.out.println();
		System.out.println("↑=====================================================================================↑");

		return result;
	}

	private UIBoardSpot selectUISpot() {
		String inputStrSpot;
		UIBoardSpot inputUiSpot;

		do{
			// 初期化
			inputStrSpot = null;
			inputUiSpot  = null;

			// 文字列を取得
			inputStrSpot = getUserInput();
			if(inputStrSpot == null){
				System.out.println("入力に誤りがあります");
				continue;
			}

			// UIBoardSpotに変換できるかチャレンジ
			inputUiSpot = UIBoardSpot.parseString(inputStrSpot);
			if(inputUiSpot == null){
				System.out.println("入力に誤りがあります");
				continue;
			}

		}while(inputStrSpot==null || inputUiSpot==null);

		// 変換できたらリターン
		return inputUiSpot;
	}

	@Override
	public synchronized void notifyEvent(Event event) {
		mEvents.add(event);
	}

	private synchronized Event getEvent(){
		return mEvents.poll();
	}
}

// 　①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲
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
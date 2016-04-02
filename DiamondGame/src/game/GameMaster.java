package game;

import java.util.Map.Entry;

import common.DGLog;
import common.TeamColor;
import user.User;
import user.User.UserMessage;

public class GameMaster implements Runnable {
	private final static int WAIT_MSEC = 1;

	Game mGame;
	Thread mThread;
	DGLog  mLog;
	State  mState;
	Move   mCurrentMove;
	private Move mMove;
	private int  mWaitCount;
	private boolean mIsReceiveFinished;

	public GameMaster() {
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
		mGame = null;
		mThread = null;
		mState = State.INIT;
		mMove   = null;
		mWaitCount = 0;
	}

	@Override
	public void run() {
		mLog.info("start GameMasterThread ");
		try {
			while(!mState.equals(State.TERMINATE)){
				stateControl();
				Thread.sleep(WAIT_MSEC);
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
				mState = State.TERMINATE;
		}
	}

	private void stateControl() {
		// mLog.fine("stateControl state:%s", mState.name());
		switch(mState){
		case CALL_USER:
			procInCallUser();
			break;
		case WAIT_CALLBACK:
			procInWaitCallback();
			break;
		case MOVE:
			procInMove();
			break;
		default:
			break;
		}
	}

	private void procInCallUser() {
		mLog.fine("start procInCallUser ");
		// この関数が呼ばれた時点で次のゲームは終了していないことが確定している

		// 現在のユーザーを取得する
		User user = mGame.getCurrentUser();
		mLog.info("Tern:%d Team:%s User:%s ", mGame.getmTernNo(), user.getMyTeam().getName(), user.getName());

		// 現在のユーザーをその気にさせる
		user.startThinking();

		// 現在のユーザーに出番を通知する
		mMove      = new Move(user);
		mWaitCount = 0;
		user.say(UserMessage.YOUR_TERN, this, mGame.getUserBoard(user), mMove);

		// ユーザーからのコールバック待ちに遷移する
		mState = State.WAIT_CALLBACK;
	}

	private void procInWaitCallback() {
		// mLog.fine("start procInWaitCallback ");
		// Userからのコールバックを待つ
		boolean isMoveReady = false;
		boolean isCancelled = false;
		if(isCallbackReveived()){
			isMoveReady = true;
		}else if(mWaitCount > ((mGame.mRule.timeLimitSec*1000)/WAIT_MSEC)){
			isMoveReady = true;
			isCancelled = true;
		}else{
			isMoveReady = false;
			mWaitCount++;
		}

		// 1秒ごと出力
		if((mWaitCount%(1000/WAIT_MSEC))==0){
			mLog.fine("MoveReady:%b Cancelled:%b(waitCount:%d/%d) ", isMoveReady, isCancelled,
					mWaitCount, ((mGame.mRule.timeLimitSec*1000)/WAIT_MSEC));
		}

		// Userからのコールバックが返ってくるか、タイムアウトしたら次へ進む
		if(isMoveReady){
			mState = State.MOVE;
			mWaitCount = 0;

			// ユーザーを止める
			User user = mGame.getCurrentUser();
			user.stopThinking();

			// 強制的に止められた場合、ユーザーに時間切れを通知する
			if(isCancelled){
				user.notifyCancelled();
			}
		}
	}

	private synchronized boolean isCallbackReveived() {
		return mIsReceiveFinished;
	}

	private void procInMove() {
		mLog.fine("start procInMove ");

		// Moveが妥当なものか確認
		if(!checkMove(mMove)){
			// TODO 正しくない場合は適当な駒を１つ動かすように設定
			// assert false;
		}

		// ボードの駒を動かす
		Board board = mGame.getBoard();
		board.move(mMove);

		// ボードの状態をゲームに反映させる
		updateStatus();

		// ゲームが継続可能か判断する
		if(judgeContinueGame()){
			// 継続可能なら次のユーザーを設定
			synchronized (this) {
				TeamColor nextTeam = mGame.getNextTeam();
				mGame.setNextTeam(nextTeam);
				mGame.setmTernNo(mGame.getmTernNo()+1);
				mLog.fine("Continue game. nextTeam:%s ", nextTeam.getName());
				mWaitCount = 0;
				mMove     = null;
				mIsReceiveFinished = false;
				mState = State.CALL_USER;
			}
		}else{
			// ゲーム終了をUIに通知
			mLog.info("Finish game! ");
			mState = State.TERMINATE;
		}
	}

	/**
	 * ゲームが継続可能かチェック
	 * @return ゲームが継続可能ならtrueを返す
	 */
	private boolean judgeContinueGame() {
		return mGame.getGoalTeams().size() < 2;
	}

	private void updateStatus() {
		// ゴールしたチームの更新
		updateGoalTeams();
	}

	private void updateGoalTeams() {
		for(TeamColor tc : TeamColor.values()){
			// ゴール済みであるにも関わらず、ゴールチーム一覧に含まれていない場合
			if(mGame.getBoard().isFinishedTeam(tc) && !mGame.getGoalTeams().contains(tc)){
				mGame.getGoalTeams().add(tc);
			}
		}
		mLog.info("updateGoalTeams %s", mGame.getGoalTeams());
	}

	/**
	 * 指定されたMoveが正しいか確認します
	 * @param move
	 * @return
	 */
	private boolean checkMove(Move move) {
		return mGame.getBoard().isMoveValid(move);
	}

	public void setGame(Game game) {
		assert game != null;
		mGame = game;
	}

	public void prepareGame() {
		/* 初期化 */
		mGame.setmTernNo(0);

		/* ボードを作る */
		mGame.createBoard();
		Board board = mGame.getBoard();

		/* 駒の配置 */
		board.placePiece();

		/* 席に着席させる */
		for(Entry<TeamColor, User> entry: mGame.getUsers().entrySet()){
			entry.getValue().sitDown(entry.getKey());
		}

		/* 自己紹介 */
		for(Entry<TeamColor, User> entry: mGame.getUsers().entrySet()){
			entry.getValue().greetGameMaster(this);
		}

		/* ルールを説明する */
		for(Entry<TeamColor, User> entry: mGame.getUsers().entrySet()){
			entry.getValue().explainRules(mGame.getRule());
		}

		/* お互いに握手させる */
		for(Entry<TeamColor, User> entry1: mGame.getUsers().entrySet()){
			User targetUser = entry1.getValue();
			for(Entry<TeamColor, User> entry2: mGame.getUsers().entrySet()){
				User handShakeUser = entry2.getValue();
				if(targetUser != handShakeUser){
					targetUser.handShake(handShakeUser, entry2.getKey());
				}
			}
		}

		/* 順序の決定 */
		TeamColor firstTeam = TeamColor.values()[(int)(Math.random()*TeamColor.values().length)];
		synchronized (this) {
			mGame.setFirstTeam(firstTeam);
		}
		mGame.getUsers().get(firstTeam).tellOrder(0);
		mGame.getUsers().get(firstTeam.getNext()).tellOrder(1);
		mGame.getUsers().get(firstTeam.getNext().getNext()).tellOrder(2);
	}

	public void startGame(){
		mState = State.CALL_USER;
		mWaitCount = 0;
		mMove     = null;
		mIsReceiveFinished = false;
		mThread = new Thread(this);
		mThread.start();
	}

	public enum GameMasterMessage{
		FINISHED
	}

	public final synchronized void say(GameMasterMessage message, User caller, Object... objects) {
		mLog.fine("say message:%s calller:%h", message.name(), caller);
		if(message.equals(GameMasterMessage.FINISHED)){
			if(caller == mGame.getCurrentUser() && mState.equals(State.WAIT_CALLBACK)){
				mIsReceiveFinished = true;
			}
		}
	}
}

enum State {
	INIT,
	CALL_USER,
	WAIT_CALLBACK,
	MOVE,
	TERMINATE;
}


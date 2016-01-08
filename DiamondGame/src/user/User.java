package user;

import common.DGLog;
import common.TeamColor;
import game.GameConfig;
import game.GameMaster;
import game.GameMaster.GameMasterMessage;
import game.Move;
import game.UserBoard;

public abstract class User implements Runnable {
	private final static int WAIT_MSEC = 1;

	// 子に見せる変数
	protected TeamColor  mMyTeam;
	protected GameConfig mRule;
	protected int        mMyOrder;

	// 子にも見せない変数
	private   Thread     mThread;
	private   DGLog      mLog;
	private   State      mState;
	private   GameMaster mGameMaster;
	private   UserInfo   mUserInfo;

	// YOUR_TERN用変数
	private   boolean    mIsReceiveYourTern;
	private   UserBoard  mUserBoard;
	private   Move       mMove;

	public User(UserInfo userInfo) {
		mGameMaster = null;
		mThread = null;
		mState  = State.INIT;
		mLog = new DGLog(getClass().getSimpleName() + "#" + Integer.toHexString(this.hashCode()));
		mLog.info("Create " + getClass().getSimpleName());
		mUserInfo = userInfo;
	}

	@Override
	public final void run() {
		mLog.fine("start UserThread ");
		try {
			while(!getState().equals(State.TERMINATE)){
				stateControl();
				if(!getState().equals(State.TERMINATE)){
					Thread.sleep(WAIT_MSEC);
				}
			}
		} catch (InterruptedException | ThreadDeath e) {
				mState = State.TERMINATE;
		}
	}

	private final void stateControl() {
		// mLog.fine("stateControl state:%s", mState.name());
		switch(getState()){
		case WAIT_TERN:
			procInWaitMyTern();
			break;
		case THINKING:
			procInThinking();
			break;
		default:
			break;
		}
	}

	private final void procInThinking() {
		mLog.info("call Think");
		think(mUserBoard, mMove);
		mLog.info("Think Finished.");
		setState(State.TERMINATE);
		mGameMaster.say(GameMasterMessage.FINISHED, this);
		mUserBoard = null;
		mMove      = null;
	}

	protected abstract void think(UserBoard userBoard, Move moveResult);

	private final void procInWaitMyTern() {
		if(mIsReceiveYourTern){
			mLog.fine("Notified MyTern");
			setState(State.THINKING);
		}
	}

	public final void sitDown(TeamColor teamColor) {
		assert teamColor != null;
		if(mMyTeam == null){
			mMyTeam = teamColor;
		}
	}

	public final void explainRules(GameConfig rule) {
		assert rule != null;
		if(mRule == null){
			mRule = rule;
		}
	}

	public abstract void handShake(User handShakeUser, TeamColor teamColor);

	public final void tellOrder(int order) {
		mMyOrder = order;
	}

	public final void say(UserMessage message, GameMaster gameMaster, Object... objects){
		mLog.fine("say message:%s gameMaster:%h", message.name(), gameMaster);
		assert gameMaster == mGameMaster;
		if(message.equals(UserMessage.YOUR_TERN)){
			mIsReceiveYourTern = true;
			assert objects[0] instanceof UserBoard;
			assert objects[1] instanceof Move;
			mUserBoard = (UserBoard)objects[0];
			mMove      = (Move)objects[1];
		}
	}

	public synchronized final void startThinking() {
		mLog.fine("startThinking");
		mIsReceiveYourTern = false;
		mUserBoard         = null;
		mMove              = null;
		setState(State.WAIT_TERN);
		mThread = new Thread(this);
		mThread.start();
	}

	public final synchronized void stopThinking() {
		assert mThread != null;
		if(mThread.isAlive() && !mState.equals(State.TERMINATE)){
			setState(State.TERMINATE);
			mLog.warning("stopThinking");
			mThread.interrupt();
			mThread.stop(); // AI作成者にフラグをチェックしてもらうわけにはいかないのでこいつで止める
		}
	}

	public synchronized final State getState() {
		return mState;
	}

	public synchronized final void setState(State state) {
		this.mState = state;
	}

	public enum UserMessage {
		YOUR_TERN
	}

	public abstract void notifyCancelled();

	public final void greetGameMaster(GameMaster gameMaster) {
		assert gameMaster != null;
		if(mGameMaster == null){
			mGameMaster = gameMaster;
		}
	}

	@Override
	public String toString() {
		return "User [mMyTeam=" + mMyTeam + ", mMyOrder=" + mMyOrder + ", mUserInfo=" + mUserInfo + "]";
	}

	public String getName(){
		return mUserInfo.getName();
	}

	public TeamColor getMyTeam() {
		return mMyTeam;
	}

	public GameConfig getRule() {
		return mRule;
	}

	public int getMyOrder() {
		return mMyOrder;
	}


}

enum State {
	INIT,
	WAIT_TERN,
	THINKING,
	TERMINATE;
}

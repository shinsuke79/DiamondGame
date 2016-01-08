package main;

import common.DGLog;
import game.GameManager;
import user.UserManager;
import view.UserInterface;
import view.consoleUI.ConsoleUserInterface;

public class Main {

	private DGLog mLog;

	public static void main(String[] args) {
		new Main().start();
	}

	public Main() {
		// ログの取得
		mLog = new DGLog(Main.class.getSimpleName());
	}

	private void start() {
		mLog.info("*** Start DiamondGame!! since 2016-1-2 ***");

		/* Managerの生成 */
		UserManager userMgr = new UserManager();
		GameManager gameMgr = new GameManager();

		/* UIの生成 */
		UserInterface ui    = new ConsoleUserInterface();
		ui.setManager(gameMgr, userMgr);

		/* ゲームデータのロード */
		userMgr.load();
		gameMgr.load();


		/* ゲーム開始(これ以降の操作はUIからとなる) */
		ui.start();
	}
}

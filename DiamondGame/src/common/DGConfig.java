package common;

import java.util.logging.Level;

public class DGConfig {
	/**
	 * 本番はWARNING それ以外はALL
	 */
	public static Level LogLevel = Level.WARNING;

	/**
	 * １ターンあたりの制限時間
	 */
	public static int LowestTimeLimitSec = 1;

	/**
	 * コンソールでのデバッグのためにユーザー
	 * 入力を省略するか
	 */
	public static boolean SmoothDebugOnConsole = false;

	/**
	 * 画面サイズ(X)
	 */
	public static final int WindowSize_X = 1280;

	/**
	 * 画面サイズ(Y)
	 */
	public static final int WindowSize_Y = 720;
}

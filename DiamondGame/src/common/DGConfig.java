package common;

import java.util.logging.Level;

public class DGConfig {
	/**
	 * 本番はWARNING それ以外はALL
	 */
	public static Level LogLevel = Level.CONFIG;

	/**
	 * １ターンあたりの制限時間
	 */
	public static int LowestTimeLimitSec = 5;
}

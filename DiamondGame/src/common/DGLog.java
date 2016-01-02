package common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * DiamondGame内で使用するログです<br>
 * レベルが5段階あります。通常時はすべてのログレベルが出力され、<br>
 * 大会当日はINFO以上が出力されます.<br>
 * <b>サンプル</b><br>
 * DGLog log = new DGLog("MyLog");<br>
 * log.severe("Test(info) %d %b %#08x %h %s", 55, false, 255, "aaa", "hello");<br>
 * log.warning("Test(info) %d %b %#08x %h %s", 55, false, 255, "aaa", "hello");<br>
 * log.info("Test(info) %d %b %#08x %h %s", 55, false, 255, "aaa", "hello");<br>
 * log.config("Test(info) %d %b %#08x %h %s", 55, false, 255, "aaa", "hello");<br>
 * log.fine("Test(info) %d %b %#08x %h %s", 55, false, 255, "aaa", "hello");<br>
 * @author yone
 */
public class DGLog {
	private Logger mLogger;
	private String mLogName;

	public DGLog(String logName) {
		mLogName = logName;

		/*
 * マルチスレッドでうまく動かないから廃止
		mLogger  = Logger.getLogger(logName);
        //コンソールに出力するハンドラーを生成
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new DGLogFormatter());
        handler.setLevel(DGConfig.LogLevel);
        mLogger.setLevel(DGConfig.LogLevel);
        mLogger.addHandler(handler);
        mLogger.setUseParentHandlers(false);
*/

	}

	/**
	 * 書式はString.formatに準ずる(d o x c s b h)
	 * http://goo.gl/PCki3d
	 * @param format
	 * @param args
	 */
	public void severe(String format, Object... args) {
		log(Level.SEVERE, format, args);
	}

	public void warning(String format, Object... args) {
		log(Level.WARNING, format, args);
	}

	public void info(String format, Object... args) {
		log(Level.INFO, format, args);
	}

	public void config(String format, Object... args) {
		log(Level.CONFIG, format, args);
	}

	public void fine(String format, Object... args) {
		log(Level.FINE, format, args);
	}

	public void log(Level level, String format, Object... args){
		if(DGConfig.LogLevel.intValue() <= level.intValue()){
			String formatStr = String.format(format, args);
			Date dat = new Date();
			System.out.printf("%tF %tT:%tL\t%s\t%s\t%s\n", dat, dat, dat, mLogName, level.getName(), formatStr);
		}
	}
}

class DGLogFormatter extends Formatter {
    private static final String format = "%1$tF %1$tT:%1$tL\t%3$s\t%4$s\t%5$s%n";
    private final Date dat = new Date();

    public synchronized String format(LogRecord record) {
        dat.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
               source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(format,
                             dat,
                             source,
                             record.getLoggerName(),
                             record.getLevel().getName(),
                             message,
                             throwable);
    }
}

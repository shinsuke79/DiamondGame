package view.javaFxUI;

import java.net.URL;

public class FxResouceMgr {
	public static URL GetViewUrl(String fileName){
		return FxResouceMgr.class.getResource("view/"+fileName);
	}
	public static URL GetResUrl(String fileName){
		return FxResouceMgr.class.getResource("res/"+fileName);
	}
}

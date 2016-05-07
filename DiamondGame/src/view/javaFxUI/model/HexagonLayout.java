package view.javaFxUI.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Polygon;

public class HexagonLayout {
	/**
	 * 頂点の数(固定)
	 */
	public static final int APEX_NUM = 6;
	/**
	 * 頂点の仮想座標
	 */
	List<FxCord> apexCordinates = new ArrayList<>();
	/**
	 * 左上の仮想座標
	 */
	FxCord upperLeftCord;
	/**
	 * 右下の仮想座標
	 */
	FxCord lowerRightCord;
	/**
	 * 中心の仮想座標
	 */
	FxCord centerCord;
	/**
	 * X軸の仮想長さ
	 */
	FxDur xDuration;
	/**
	 * Y軸の仮想長さ
	 */
	FxDur yDuration;

	/**
	 * 	Rectangleと同様の指定方法で六角形情報を作成する
	 * @param upperLeft
	 * @param xDur
	 * @param yDur
	 * @return
	 */
	public HexagonLayout(CreateType createType, FxCord cord, FxDur xDur, FxDur yDur) {
		xDuration = xDur;
		yDuration = yDur;

		if(createType == CreateType.CENTER){
			centerCord = cord;
		}else if(createType == CreateType.LEFT_UPPER){
			centerCord = new FxCord(
					cord.getX() + xDur.getDuration()/2,
					cord.getY() + yDur.getDuration()/2
					);
		}

		PolygonCoefficient coefficient = PolygonCoefficient.getPolygonCoefficient(APEX_NUM);
		for(int i=0; i<coefficient.apexNum; i++){
			double xCoff = coefficient.x[i];
			double yCoff = coefficient.y[i];
			FxCord apexCord = new FxCord(
					centerCord.getX() + xCoff*xDur.getDuration()/2,
					centerCord.getY() + yCoff*yDur.getDuration()/2
					);
			apexCordinates.add(apexCord);
		}
	}

	/**
	 * 作成した六角形の{@link Polygon}を作成する<br>
	 * 作成されたPolygonに指定されている座標は仮想ではなくPixcelである点に注意
	 * @return
	 */
	public Polygon getPolygonAsPixcel(){
		Polygon ret = new Polygon();
		for(FxCord fxc : apexCordinates){
			ret.getPoints().add(fxc.getPixelX());
			ret.getPoints().add(fxc.getPixelY());
		}
		return ret;
	}

	public static enum CreateType{
		CENTER,
		LEFT_UPPER
	}
}
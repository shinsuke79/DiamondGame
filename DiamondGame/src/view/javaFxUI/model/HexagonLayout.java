package view.javaFxUI.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;

/**
 * 仮想画面に配置される六角形の座標情報群
 * @author yone
 *
 */
public class HexagonLayout {
	/**
	 * 頂点の数(固定)
	 */
	public static final int APEX_NUM = 6;
	/**
	 * 頂点の仮想座標
	 */
	List<FxCord> mApexCord = new ArrayList<>();
	/**
	 * 中心の仮想座標
	 */
	FxCord mCenterCord;
	/**
	 * X軸の仮想長さ
	 */
	FxDur mXDur;
	/**
	 * Y軸の仮想長さ
	 */
	FxDur mYDur;
	/**
	 * 保有しているPolygon
	 */
	Polygon mPolygon;

	/**
	 * 	Rectangleと同様の指定方法で六角形情報を作成する
	 * @param upperLeft
	 * @param xDur
	 * @param yDur
	 * @return
	 */
	public HexagonLayout(CreateType createType, FxCord cord, FxDur xDur, FxDur yDur) {
		// Durationはそのまま代入
		mXDur = xDur;
		mYDur = yDur;

		// CreateType毎に中心座標を求める
		if(createType == CreateType.CENTER){
			mCenterCord = cord;
		}else if(createType == CreateType.LEFT_UPPER){
			mCenterCord = new FxCord(
					cord.getX() + xDur.getValue()/2,
					cord.getY() + yDur.getValue()/2
					);
		}

		// 六角形の頂点の座標(係数)を使用して指定された大きさの六角形を作成する
		PolygonCoefficient coefficient = PolygonCoefficient.getPolygonCoefficient(APEX_NUM);
		for(int i=0; i<coefficient.apexNum; i++){
			double xCoff = coefficient.x[i];
			double yCoff = coefficient.y[i];
			FxCord apexCord = new FxCord(
					mCenterCord.getX() + xCoff*xDur.getValue()/2,
					mCenterCord.getY() + yCoff*yDur.getValue()/2
					);
			mApexCord.add(apexCord);
		}

		// メンバを使用してPolygonを生成
		mPolygon = createPolygonAsPixcel();
	}

	/**
	 * 作成した六角形の{@link Polygon}を作成する<br>
	 * 作成されたPolygonに指定されている座標は仮想ではなくPixcelである点に注意
	 * @return
	 */
	public Polygon createPolygonAsPixcel(){
		Polygon ret = new Polygon();
		for(FxCord fxc : mApexCord){
			ret.getPoints().add(fxc.getPixelX());
			ret.getPoints().add(fxc.getPixelY());
		}
		ret.getStyleClass().add("Polygon");
		ret.setOnMouseClicked((value)-> System.out.println("hexagonClicked"));
		return ret;
	}

	public List<Node> getNodes(){
		List<Node> ret = new ArrayList<>();
		ret.add(mPolygon);
		return ret;
	}

	public static enum CreateType{
		CENTER,
		LEFT_UPPER
	}
}
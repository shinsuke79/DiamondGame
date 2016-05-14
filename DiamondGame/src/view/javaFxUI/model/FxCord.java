package view.javaFxUI.model;

import javafx.scene.shape.Circle;

/**
 * 仮想上の座標
 * @author yone
 *
 */
public class FxCord extends FxObject {
	double x, y;
	public FxCord(double x, double y, Tag...tags) {
		super(tags);
		this.x = x;
		this.y = y;
	}
	public double getPixelX(){
		return x*1; // TODO Panelのサイズと仮想画面のサイズを変える場合は係数をかける必要あり
	}
	public double getPixelY(){
		return y*1; // TODO Panelのサイズと仮想画面のサイズを変える場合は係数をかける必要あり
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	@Override
	public String toString() {
		return "FxCord [x=" + x + ", y=" + y + "]";
	}
	public Circle createCircleShape(FxDur radius) {
		Circle ret = new Circle(getPixelX(), getPixelY(), radius.getPixcelDuration());
		return ret;
	}
}
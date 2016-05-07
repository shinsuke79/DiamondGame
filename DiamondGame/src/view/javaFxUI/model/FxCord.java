package view.javaFxUI.model;

/**
 * 仮想上の座標
 * @author yone
 *
 */
public class FxCord {
	double x, y;
	public FxCord(double x, double y) {
		super();
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
}
package view.javaFxUI.model;

/**
 * 仮想上の長さ
 * @author yone
 *
 */
public class FxDur extends FxObject {
	double d;
	public FxDur(double d, Tag...tags) {
		super(tags);
		this.d = d;
	}
	public double getValue() {
		return d;
	}
	public double getPixcelDuration() {
		// TODO Panelのサイズと仮想画面のサイズを変える場合は係数をかける必要あり
		// あとベクトルじゃないと係数が定まらない
		return d*1;
	}
	@Override
	public String toString() {
		return "FxDur [d=" + d + "]";
	}

}
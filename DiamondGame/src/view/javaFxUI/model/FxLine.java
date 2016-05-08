package view.javaFxUI.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.shape.Line;

public class FxLine extends FxObject {
	FxCord startCord;
	FxCord endCord;

	public FxLine(FxCord startCord, FxCord endCord, Tag...tags) {
		super(tags);
		assert startCord!=null && endCord != null;

		this.startCord = startCord;
		this.endCord = endCord;
	}

	/**
	 * 直線の長さを求める
	 * @return
	 */
	public FxDur getDuration(){
		double duration = Math.sqrt(
				(startCord.x-endCord.x)*(startCord.x-endCord.x) +
				(startCord.y-endCord.y)*(startCord.y-endCord.y));
		return new FxDur(duration);
	}

	/**
	 * 始点と終点を反対にした{@link FxLine}を返却します
	 * @return
	 */
	public FxLine getReverseLine(){
		return new FxLine(endCord, startCord);
	}

	/**
	 * Lineを指定された長さで分割し、その中の一点の座標を返却する
	 * @param divideNum
	 * @param index
	 * @return
	 */
	public FxCord getFxCordOnLine(int divideNum, int index){
		assert divideNum!=0;
		assert 0<=index && index<=divideNum;

		// ａｂベクトルを求め
		double xv = endCord.x - startCord.x;
		double yv = endCord.y - startCord.y;

		// ベクトルの大きさを求め
		double r = Math.sqrt(xv*xv + yv*yv);

		// 点ａからａｂベクトル方向、距離kの点を求める
		double x = startCord.x + xv/r*(getDuration().getValue()/divideNum)*index;
		double y = startCord.y + yv/r*(getDuration().getValue()/divideNum)*index;

		return new FxCord(x, y);
	}

	@Override
	public String toString() {
		return "FxLine [startCord=" + startCord + ", endCord=" + endCord + "]";
	}

	/**
	 * 指定された{@link FxLine}との交点を求める
	 * @param other
	 * @return
	 */
	public FxCord getIntersection(FxLine other){
		double x1 = this.startCord.x;
		double y1 = this.startCord.y;
		double x2 = this.endCord.x;
		double y2 = this.endCord.y;
		double x3 = other.startCord.x;
		double y3 = other.startCord.y;
		double x4 = other.endCord.x;
		double y4 = other.endCord.y;

		double a1 = (y2-y1)/(x2-x1);
		double a3 = (y4-y3)/(x4-x3);
		double x = (a1*x1-y1-a3*x3+y3)/(a1-a3);
		double y = (y2-y1)/(x2-x1)*(x-x1)+y1;

		return new FxCord(x, y);
	}

	public Line createLineShape(){
		Line line = new Line(startCord.x, startCord.y, endCord.x, endCord.y);
		return line;
	}

	/**
	 * 指定されたタグを指定数回転させた際のTagを返却する
	 * @param tag
	 * @param direction +1なら右、-1なら左
	 * @return
	 */
	public static Tag getTagUsingDirection(Tag tag, int direction){
		assert Arrays.asList(Tag.END_X, Tag.END_Y, Tag.END_Z, Tag.START_X, Tag.START_Y, Tag.START_Z).contains(tag);
		Map<Tag, Integer> tagMap = new HashMap<>();
		tagMap.put(Tag.START_X, 0);
		tagMap.put(Tag.END_Z, 1);
		tagMap.put(Tag.START_Y, 2);
		tagMap.put(Tag.END_X, 3);
		tagMap.put(Tag.START_Z, 4);
		tagMap.put(Tag.END_Y, 5);

		int currentTagValue = tagMap.get(tag);
		int resultTagValue = (currentTagValue+6+direction)%6;

		for(Entry<Tag, Integer> entry : tagMap.entrySet()){
			if(entry.getValue() == resultTagValue){
				return entry.getKey();
			}
		}

		// バグ
		assert false;
		return null;
	}
}

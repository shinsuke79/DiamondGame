package common;

import game.Board.Cordinate;

public enum Direction {
	RIGHT_FRONT(0), // ↗
	RIGHT(1),       // →
	RIGHT_REAR(2),  // ↘
	LEFT_REAR(3),   // ↙
	LEFT(4),        // ←
	LEFT_FRONT(5);  // ↖

	int mNum;
	private Direction(int num) {
		mNum = num;
	}

	/**
	 * 自身が基準方角だったと仮定して、指定された色視点での方角を返す
	 * 基準の方角は赤チームの視点としており、黄色は-1、緑は-2することで
	 * その色視点の方向に変換できる
	 * @param color
	 * @return
	 */
	public Direction getRelativeDirection(TeamColor color){
		int relativeNum = 0;
		switch(color){
		case RED:
			// 赤はそのまま
			relativeNum = mNum;
			break;
		case GREEN:
			// 緑は-2
			relativeNum = (6+mNum-2)%6;
			break;
		case YELLOW:
			// 黄は-1
			relativeNum = (6+mNum-1)%6;
			break;
		}
		// 新たなnumに該当するDirectionを返却
		return ConvDirectionFromNum(relativeNum);
	}

	/**
	 * numに該当するDirectionを返す
	 * @param num
	 * @return
	 */
	private static Direction ConvDirectionFromNum(int num){
		for(Direction d : Direction.values()){
			if(d.mNum == num){
				return d;
			}
		}
		return null;
	}

	/**
	 * 指定された方角へdistance移動させた座標を返却する
	 * @param cordinate
	 * @param distance
	 * @return
	 */
	public Cordinate getMovedCordinate(Cordinate cordinate, int distance) {
		assert distance == 2 || distance == 1;
		Cordinate result = cordinate.clone();

		int dx=0, dy=0, dz=0;

		switch(this){
		case RIGHT_FRONT:
			dx = distance;
			dy = -distance;
			dz = 0;
			break;
		case RIGHT:
			dx = 0;
			dy = -distance;
			dz = distance;
			break;
		case RIGHT_REAR:
			dx = -distance;
			dy = 0;
			dz = distance;
			break;
		case LEFT_REAR:
			dx = -distance;
			dy = distance;
			dz = 0;
			break;
		case LEFT:
			dx = 0;
			dy = distance;
			dz = -distance;
			break;
		case LEFT_FRONT:
			dx = distance;
			dy = 0;
			dz = -distance;
			break;
		}

		result.x += dx;
		result.y += dy;
		result.z += dz;

		return result;
	}
}

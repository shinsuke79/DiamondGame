package common;

public enum Direction {
	RIGHT_FRONT(0), // ↗
	RIGHT(1),       // →
	RIGHT_REAR(2),  // ↘
	LEFT_REAR(3),   // ↙
	LEFT(4),        // ←
	LEFT_FRONT(5);  // ↖

	static DGLog Log = new DGLog(Direction.class.getSimpleName());

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
			// 緑は+2
			relativeNum = (6+mNum+2)%6;
			break;
		case YELLOW:
			// 黄は+4
			relativeNum = (6+mNum+4)%6;
			break;
		}
		// 新たなnumに該当するDirectionを返却
		Direction result = ConvDirectionFromNum(relativeNum);
		Log.fine("getRelativeDirection this:%s color:%s -> %s ", this.name(), color.getName(), result.name());
		return result;
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
}

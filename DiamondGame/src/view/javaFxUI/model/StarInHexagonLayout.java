package view.javaFxUI.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import view.javaFxUI.model.FxObject.Tag;

public class StarInHexagonLayout extends HexagonLayout {
	private ArrayFxObjectList<FxLine> mOutLines;
	private ArrayFxObjectList<FxLine> mStarLines;
	private Map<XYZ, ArrayFxObjectList<FxLine>> mMatrix;
	private FxCord mCords[][][];

	public StarInHexagonLayout(CreateType createType, FxCord cord, FxDur xDur, FxDur yDur) {
		super(createType, cord, xDur, yDur);

		/* 外周Lineの確定 */
		mOutLines = new ArrayFxObjectList<>();
		mOutLines.add(new FxLine(mApexCord.get(3), mApexCord.get(2), Tag.END_X));
		mOutLines.add(new FxLine(mApexCord.get(2), mApexCord.get(1), Tag.START_Z));
		mOutLines.add(new FxLine(mApexCord.get(1), mApexCord.get(0), Tag.END_Y));
		mOutLines.add(new FxLine(mApexCord.get(0), mApexCord.get(5), Tag.START_X));
		mOutLines.add(new FxLine(mApexCord.get(5), mApexCord.get(4), Tag.END_Z));
		mOutLines.add(new FxLine(mApexCord.get(4), mApexCord.get(3), Tag.START_Y));

		/* 星形Lineの確定 */
		mStarLines = new ArrayFxObjectList<>();
		// X軸担当の星線
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.START_X).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.START_Y).getFxCordOnLine(2, 1),
				Tag.START_X, Tag.STAR_RIGHT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.START_X).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.START_Z).getFxCordOnLine(2, 1),
				Tag.START_X, Tag.STAR_LEFT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.END_X).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.END_Y).getFxCordOnLine(2, 1),
				Tag.END_X, Tag.STAR_RIGHT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.END_X).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.END_Z).getFxCordOnLine(2, 1),
				Tag.END_X, Tag.STAR_LEFT));
		// Y軸担当の星線
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.START_Y).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.START_Z).getFxCordOnLine(2, 1),
				Tag.START_Y, Tag.STAR_RIGHT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.START_Y).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.START_X).getFxCordOnLine(2, 1),
				Tag.START_Y, Tag.STAR_LEFT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.END_Y).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.END_Z).getFxCordOnLine(2, 1),
				Tag.END_Y, Tag.STAR_RIGHT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.END_Y).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.END_X).getFxCordOnLine(2, 1),
				Tag.END_Y, Tag.STAR_LEFT));
		// X軸担当の星線
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.START_Z).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.START_X).getFxCordOnLine(2, 1),
				Tag.START_Z, Tag.STAR_RIGHT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.START_Z).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.START_Y).getFxCordOnLine(2, 1),
				Tag.START_Z, Tag.STAR_LEFT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.END_Z).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.END_X).getFxCordOnLine(2, 1),
				Tag.END_Z, Tag.STAR_RIGHT));
		mStarLines.add(new FxLine(
				mOutLines.findByTags(Tag.END_Z).getFxCordOnLine(2, 1),
				mOutLines.findByTags(Tag.END_Y).getFxCordOnLine(2, 1),
				Tag.END_Z, Tag.STAR_LEFT));

		/* XYZそれぞれのMtrixを作成 */
		mMatrix = new HashMap<>();
		mMatrix.put(XYZ.X, createMatrixLines(Tag.START_X));
		mMatrix.put(XYZ.Y, createMatrixLines(Tag.START_Y));
		mMatrix.put(XYZ.Z, createMatrixLines(Tag.START_Z));

		/* 交点の座標を作成する */
		mCords = new FxCord[13][13][13];
		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					mCords[x][y][z] = getFxCord(x, y, z);
				}
			}
		}
	}

	private ArrayFxObjectList<FxLine> createMatrixLines(Tag startLineTag) {
		ArrayFxObjectList<FxLine> ret = new ArrayFxObjectList<>();

		Tag firstRightLineTag  = FxLine.getTagUsingDirection(startLineTag, 1);
		Tag firstLeftLineTag   = FxLine.getTagUsingDirection(startLineTag, -1);
		Tag secondRightLineTag = FxLine.getTagUsingDirection(startLineTag, 2);
		Tag secondLeftLineTag  = FxLine.getTagUsingDirection(startLineTag, -2);

		FxLine firstRightLine = mOutLines.findByTags(firstRightLineTag);
		FxLine firstLeftLine  = mOutLines.findByTags(firstLeftLineTag).getReverseLine();
		for(int i=0; i<=6; i++){
			FxLine line = new FxLine(
					firstLeftLine.getFxCordOnLine(6, i),
					firstRightLine.getFxCordOnLine(6, i)
					);
			ret.add(line);
		}

		FxLine secondRightLine = mOutLines.findByTags(secondRightLineTag);
		FxLine secondLeftLine  = mOutLines.findByTags(secondLeftLineTag).getReverseLine();
		for(int i=1; i<=6; i++){
			FxLine line = new FxLine(
					secondLeftLine.getFxCordOnLine(6, i),
					secondRightLine.getFxCordOnLine(6, i)
					);
			ret.add(line);
		}

		return ret;
	}

	@Override
	public List<Node> getNodes() {
		List<Node> nodes = super.getNodes();
		for(FxLine l : mStarLines){
			Line starLineShape = l.createLineShape();
			starLineShape.getStyleClass().add("StarLine");
			nodes.add(starLineShape);
		}
		for(Entry<XYZ, ArrayFxObjectList<FxLine>>  entry : mMatrix.entrySet()){
			for(FxLine line : entry.getValue()){
				Line lineShape = line.createLineShape();
				lineShape.getStyleClass().add(entry.getKey()+"MatrixLine");
				nodes.add(lineShape);
			}
		}

		for(int x=0; x<13; x++){
			for(int y=0; y<13; y++){
				for(int z=0; z<13; z++){
					FxCord fxCord = mCords[x][y][z];
					Text text = new Text(fxCord.x, fxCord.y, x+","+y+","+z);
					nodes.add(text);
				}
			}
		}
		return nodes;
	}

	public FxCord getFxCord(int x, int y, int z){
		assert 0 <= x && x <= 12;
		assert 0 <= y && y <= 12;
		assert 0 <= z && z <= 12;

		FxLine fxLineX = mMatrix.get(XYZ.X).get(x);
		FxLine fxLineY = mMatrix.get(XYZ.Y).get(y);
		FxLine fxLineZ = mMatrix.get(XYZ.Z).get(z);

		// XとYの交点から座標を求める
		FxCord ret = fxLineX.getIntersection(fxLineY);
		return ret;
	}

	public static enum XYZ {
		X,
		Y,
		Z
	}
}

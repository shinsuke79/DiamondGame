package view.javaFxUI.model;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.shape.Line;
import view.javaFxUI.model.FxObject.Tag;

public class StarInHexagonLayout extends HexagonLayout {
	private ArrayFxObjectList<FxLine> mOutLines;
	private ArrayFxObjectList<FxLine> mStarLines;
	private ArrayFxObjectList<FxLine> mXMatrix;

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

		mXMatrix = new ArrayFxObjectList<>();
		Tag startLineTag       = Tag.START_X;
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
			mXMatrix.add(line);
		}
	}

	@Override
	public List<Node> getNodes() {
		List<Node> nodes = super.getNodes();
		for(FxLine l : mStarLines){
			Line starLineShape = l.createLineShape();
			starLineShape.getStyleClass().add("StarLine");
			nodes.add(starLineShape);
		}
		for(FxLine l : mXMatrix){
			Line xMatrixLineShape = l.createLineShape();
			xMatrixLineShape.getStyleClass().add("XMatrixLine");
			nodes.add(xMatrixLineShape);
		}
		return nodes;
	}
}

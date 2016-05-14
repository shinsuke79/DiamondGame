package view.javaFxUI.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import common.TeamColor;
import game.Board;
import game.Board.Cordinate;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import view.javaFxUI.model.FxObject.Tag;

public class GameBoardLayout extends StarInHexagonLayout {
	protected Map<TeamColor, ArrayFxObjectList<FxLine>> mMatrix;
	protected List<FxLine> mSpotLines;
	protected List<Line> mSpotLineShape;
	protected Map<Cordinate, FxCord> mSpotCord;
	protected List<Circle> mSppotCircles;

	public GameBoardLayout(CreateType createType, FxCord cord, FxDur xDur, FxDur yDur) {
		super(createType, cord, xDur, yDur);

		mMatrix = new HashMap<>();

		// 親クラスが作ったメンバ変数に赤、緑、黄の概念を追加する
		addTeamColorConcept();

		// 星形内の線を作成する
		mSpotLines = new ArrayFxObjectList<>();
		mSpotLines.addAll(createSpotLines(Tag.START_RED));
		mSpotLines.addAll(createSpotLines(Tag.START_YELLOW));
		mSpotLines.addAll(createSpotLines(Tag.START_GREEN));
		mSpotLines.addAll(createSpotLines(Tag.END_RED));
		mSpotLines.addAll(createSpotLines(Tag.END_YELLOW));
		mSpotLines.addAll(createSpotLines(Tag.END_GREEN));

		mSpotLineShape = new ArrayList<>();
		for(FxLine line : mSpotLines){
			Line lineShape = line.createLineShape();
			lineShape.getStyleClass().add("SpotLine");
			mSpotLineShape.add(lineShape);
		}

		// マスを表現する座標を作成する
		mSpotCord = new HashMap<>();
		List<Cordinate> spotCords = Board.getSpotCordinates();
		for (Cordinate cordinate : spotCords) {
			mSpotCord.put(cordinate, getFxCord(cordinate));
		}

		mSppotCircles = new ArrayList<>();
		for(FxCord spotCord : mSpotCord.values()){
			Circle createCircleShape = spotCord.createCircleShape(new FxDur(5));
			createCircleShape.getStyleClass().add("SpotCircle");
			mSppotCircles.add(createCircleShape);
		}
	}

	@Override
	public List<Node> getNodes() {
		List<Node> nodes = super.getNodes();
		for (Node node : mSpotLineShape) {
			nodes.add(node);
		}
		for(Node node : mSppotCircles){
			nodes.add(node);
		}
		return nodes;
	}

	private FxCord getFxCord(Cordinate cord) {
		return super.getFxCord(cord.x, cord.y, cord.z);
	}

	private List<FxLine> createSpotLines(Tag outLineTag) {
		ArrayList<FxLine> ret = new ArrayList<>();

		FxLine lineRight = mStarLines.findByTags(outLineTag, Tag.STAR_RIGHT);
		FxLine lineLeft  = mStarLines.findByTags(outLineTag, Tag.STAR_LEFT);
		for(int i=0; i<=9; i++){
			FxLine line = new FxLine(
					lineRight.getFxCordOnLine(9, i),
					lineLeft.getFxCordOnLine(9, i)
					);
			ret.add(line);
		}

		return ret;
	}

	/**
	 * 親クラスが作ったX,Y,Zの要素に赤、緑、黄の概念を追加する
	 */
	private void addTeamColorConcept() {
		// 外周
		for (FxLine outLine : mOutLines) {
			for(Tag t : outLine.getTags()){
				Tag colorTag = getColorTag(t);
				if(colorTag != null){
					outLine.addTag(colorTag);
				}
			}
		}
		// 星形
		for(FxLine starLine : mStarLines){
			for(Tag t : starLine.getTags()){
				Tag colorTag = getColorTag(t);
				if(colorTag != null){
					starLine.addTag(colorTag);
				}
			}
		}
		// Matrix
		for(Entry<XYZ, ArrayFxObjectList<FxLine>> lines : super.mMatrix.entrySet()){
			TeamColor teamColor = getTeamColor(lines.getKey());
			mMatrix.put(teamColor, lines.getValue());
		}
	}

	private Tag getColorTag(Tag xyzTag){
		switch(xyzTag){
		case START_X: return Tag.START_RED;
		case START_Y: return Tag.START_YELLOW;
		case START_Z: return Tag.START_GREEN;
		case END_X:   return Tag.END_RED;
		case END_Y:   return Tag.END_YELLOW;
		case END_Z:   return Tag.END_GREEN;
		default:
		}
		return null;
	}

	private TeamColor getTeamColor(XYZ xyz){
		switch(xyz){
		case X: return TeamColor.RED;
		case Y: return TeamColor.YELLOW;
		case Z: return TeamColor.GREEN;
		}
		return null;
	}

	private XYZ getXYZ(TeamColor teamColor){
		switch(teamColor){
		case RED:    return XYZ.X;
		case YELLOW: return XYZ.Y;
		case GREEN:  return XYZ.Z;
		}
		return null;
	}

}

package view.javaFxUI.control;

import java.net.URL;
import java.util.ResourceBundle;

import game.GameConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import view.javaFxUI.ControlOption;
import view.javaFxUI.ControlOption.ValueId;
import view.javaFxUI.DGFxControl;
import view.javaFxUI.model.FxCord;
import view.javaFxUI.model.FxDur;
import view.javaFxUI.model.HexagonLayout;
import view.javaFxUI.model.HexagonLayout.CreateType;

public class GameControl implements Initializable, DGFxControl {
	GameConfig mGameConfig;

	@FXML Pane rootPane;

	@Override
	public void setOption(ControlOption option) {
		// GameConfigの保存
		GameConfig gameConfig = option.getValue(ValueId.GAME_CONFIG, GameConfig.class);
		assert gameConfig != null;
		this.mGameConfig = gameConfig;

		// ゲーム情報の構築
		// GUIの更新
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		HexagonLayout hexagonLayout = new HexagonLayout(
				CreateType.LEFT_UPPER,
				new FxCord(0, 20),
				new FxDur(680),
				new FxDur(680)
				);
		Polygon hexagon = hexagonLayout.getPolygonAsPixcel();
		hexagon.setFill(Color.TRANSPARENT);
		hexagon.setStroke(Color.DARKBLUE);
		rootPane.getChildren().add(hexagon);
	}

	@Override
	public void onShown() {
	}
}

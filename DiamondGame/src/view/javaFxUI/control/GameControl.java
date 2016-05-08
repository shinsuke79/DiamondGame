package view.javaFxUI.control;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import game.GameConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import view.javaFxUI.ControlOption;
import view.javaFxUI.ControlOption.ValueId;
import view.javaFxUI.DGFxControl;
import view.javaFxUI.model.FxCord;
import view.javaFxUI.model.FxDur;
import view.javaFxUI.model.HexagonLayout.CreateType;
import view.javaFxUI.model.StarInHexagonLayout;

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
		StarInHexagonLayout starLayout = new StarInHexagonLayout(
				CreateType.LEFT_UPPER,
				new FxCord(0, 20),
				new FxDur(680),
				new FxDur(680)
				);
		List<Node> nodes = starLayout.getNodes();
		for(Node n : nodes){
			rootPane.getChildren().add(n);
		}
	}

	@Override
	public void onShown() {
	}
}

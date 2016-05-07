package view.javaFxUI.control;

import java.net.URL;
import java.util.ResourceBundle;

import game.GameConfig;
import javafx.fxml.Initializable;
import view.javaFxUI.ControlOption;
import view.javaFxUI.ControlOption.ValueId;
import view.javaFxUI.DGFxControl;

public class GameControl implements Initializable, DGFxControl {
	GameConfig mGameConfig;

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
	}

	@Override
	public void onShown() {
	}
}

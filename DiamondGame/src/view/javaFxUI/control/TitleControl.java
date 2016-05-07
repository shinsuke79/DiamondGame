package view.javaFxUI.control;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.JavaFX;
import view.javaFxUI.ControlOption;
import view.javaFxUI.DGFxControl;
import view.javaFxUI.FxResouceMgr;

public class TitleControl implements Initializable, DGFxControl {
	@FXML Button startButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startButton.setOnAction((value)->{
			// ボタンを無効に
			startButton.setDisable(true);

			// 次の画面へ遷移
			FXMLLoader gameConfigLoader = new FXMLLoader(FxResouceMgr.GetViewUrl("GameConfig.fxml"));
			JavaFX instance = JavaFX.getInstance();
			instance.changeScene(gameConfigLoader, null);
		});
	}

	@Override
	public void setOption(ControlOption option) {
		// NOP
	}

	@Override
	public void onShown() {
		// NOP
	}

}

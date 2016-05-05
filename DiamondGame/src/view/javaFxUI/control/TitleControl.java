package view.javaFxUI.control;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.JavaFX;
import view.javaFxUI.DGFxControl;

public class TitleControl implements Initializable, DGFxControl {
	@FXML Button startButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startButton.setOnAction((value)->{
			// ボタンを無効に
			startButton.setDisable(true);

			// 次の画面へ遷移
			JavaFX instance = JavaFX.getInstance();
			// instance.changeScene(new FXMLLoader(), null);
		});
	}

	@Override
	public void setOption(Map<String, Object> option) {
	}

}

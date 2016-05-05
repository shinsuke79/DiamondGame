package main;

import java.io.IOException;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view.javaFxUI.DGFxControl;
import view.javaFxUI.FxResouceMgr;

public class JavaFX extends Application {
	Stage mStage;
	static JavaFX Instance;

	@Override
	public void start(Stage primaryStage) {
		Instance = this;
		mStage   = primaryStage;

		mStage.setTitle("DiamondGame for JavaFX");
		mStage.setResizable(false);

		FXMLLoader fxmlLoader = new FXMLLoader(FxResouceMgr.GetViewUrl("Title.fxml"));

		try {
			changeScene(fxmlLoader, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JavaFX getInstance(){
		assert Instance != null;
		return Instance;
	}

	public void changeScene(FXMLLoader loader, Map<String, Object> option) throws IOException{
		Pane pane = loader.load();
		Scene scene = new Scene(pane);

		Object controller = loader.getController();
		if(controller instanceof DGFxControl){
			((DGFxControl) controller).setOption(option);
		}

		mStage.setScene(scene);
		mStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

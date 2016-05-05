package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFX extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("DiamondGame for JavaFX");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

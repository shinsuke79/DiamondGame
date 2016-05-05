package view.javaFxUI.control;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import view.javaFxUI.DGFxControl;

public class TitleControl implements Initializable, DGFxControl {
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Initialize!");
	}

	@Override
	public void setOption(Map<String, Object> option) {
		System.out.println("set Option!");
	}

}

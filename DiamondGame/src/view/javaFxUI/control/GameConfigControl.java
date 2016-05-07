package view.javaFxUI.control;

import java.net.URL;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.ResourceBundle;

import common.TeamColor;
import game.GameConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import main.JavaFX;
import user.UserInfo;
import user.UserManager;
import view.javaFxUI.ControlOption;
import view.javaFxUI.DGFxControl;

public class GameConfigControl implements Initializable, DGFxControl {
	@FXML Button startButton;
	@FXML Button backButton;
	@FXML ListView<UserInfo> user1ListView;
	@FXML ListView<UserInfo> user2ListView;
	@FXML ListView<UserInfo> user3ListView;
	@FXML Slider timeLimitSlideBar;

	UserManager mUserMgr;

	public GameConfigControl() {
		mUserMgr = new UserManager();
		mUserMgr.load();
	}

	@Override
	public void setOption(ControlOption option) {
		// TODO 別の画面から戻ってくる場合はここでGameConfigの値をUIに反映させる
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/* ListViewにまとめて設定することが多いのでList化しておく */
		List<ListView<UserInfo>> list = Arrays.asList(user1ListView, user2ListView, user3ListView);

		/* ListViewの設定 */

		// 選択できるUserInfoを各UserInfoを登録していく
		for(UserInfo ui : mUserMgr.getAllUsers()){
			list.forEach((listView)->listView.getItems().add(ui));
		}
		// ListViewに表示する名前とToolTipを独自に設定する
		list.forEach((listView)->{
			// この書き方がお決まりらしい。ListViewは表示されている分だけしかインスタンスを作っていないらしい
			listView.setCellFactory(new Callback<ListView<UserInfo>, ListCell<UserInfo>>() {
				@Override
				public ListCell<UserInfo> call(ListView<UserInfo> param) {
					return new UserInfoCell();
				}
			});
		});

		/* Startボタンの設定 */

		// 初期値はDisableしておく
		startButton.setDisable(true);
		// 選択が変更された時にはボタンを有効化するか確認する
		list.forEach((listView)->{
			listView.getSelectionModel().selectedItemProperty().addListener((value, oldObj, newObj)->{
				startButton.setDisable(!checkUserInput());
			});
		});
		// ボタンが押されたら次の画面へ
		startButton.setOnAction((value)->{
			startButton.setDisable(true);
			if(checkUserInput()){
				// 次の画面へ
				ControlOption option = new ControlOption();
				option.setValue(ControlOption.ValueId.GAME_CONFIG, createGameConfig());
				JavaFX.getInstance().changeScene("Game.fxml", option);
			}else{
				// 選択に失敗した->再度ボタンが押せるか確認する
				startButton.setDisable(!checkUserInput());
			}
		});

		/* backボタンの設定 */
		// 元に戻る
		backButton.setOnAction((value)->{
			backButton.setDisable(true);
			JavaFX.getInstance().changeScene("Title.fxml", null);
		});
	}

	private GameConfig createGameConfig() {
		GameConfig ret = new GameConfig();
		ret.recordResult = false;
		ret.timeLimitSec = (int)timeLimitSlideBar.getValue();
		ret.users = new EnumMap<>(TeamColor.class);
		ret.users.put(TeamColor.RED, user1ListView.getSelectionModel().getSelectedItem());
		ret.users.put(TeamColor.GREEN, user2ListView.getSelectionModel().getSelectedItem());
		ret.users.put(TeamColor.YELLOW, user3ListView.getSelectionModel().getSelectedItem());
		return ret;
	}

	public boolean checkUserInput(){
		UserInfo user1 = user1ListView.getSelectionModel().getSelectedItem();
		UserInfo user2 = user2ListView.getSelectionModel().getSelectedItem();
		UserInfo user3 = user3ListView.getSelectionModel().getSelectedItem();
		return user1!=null && user2!=null && user3!=null;
	}

	public static class UserInfoCell extends ListCell<UserInfo> {

		@Override
		protected void updateItem(UserInfo item, boolean empty) {
			super.updateItem(item, empty);
			if(empty){
				setText(null);
				setGraphic(null);
			}else{
				setText(item.getName());
				setTooltip(new Tooltip(item.getDescription()));
			}
		}
	}

	@Override
	public void onShown() {
		// NOP
	}
}

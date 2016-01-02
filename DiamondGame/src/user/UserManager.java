package user;

import java.util.ArrayList;
import java.util.List;

import common.TeamColor;
import game.Move;
import game.UserBoard;

public class UserManager {
	ArrayList<UserInfo> mUsers;

	public UserManager(){
		mUsers = new ArrayList<>();
	}

	public void load() {
		mUsers = new ArrayList<>();
		mUsers.add(new UserInfo("Player1"));
		mUsers.add(new UserInfo("Player2"));
		mUsers.add(new UserInfo("Player3"));
	}

	public List<UserInfo> getAllUsers() {
		return mUsers;
	}

	public User createUser(UserInfo userInfo) {
		return new User(userInfo) {
			@Override
			public void notifyCancelled() {
			}
			@Override
			protected void think(UserBoard userBoard, Move moveResult) {
			}
			@Override
			public void handShake(User handShakeUser, TeamColor teamColor) {
			}
		};
	}

}

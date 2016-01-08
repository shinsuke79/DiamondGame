package user;

import java.util.ArrayList;
import java.util.List;

import common.DGLog;
import common.TeamColor;
import game.Move;
import game.UserBoard;

public class UserManager {
	ArrayList<UserInfo> mUsers;
	DGLog mLog;

	public UserManager(){
		mUsers = new ArrayList<>();
		mLog   = new DGLog("UserManager");
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
		// TODO
		return new User(userInfo) {
			@Override
			public void notifyCancelled() {
			}
			@Override
			protected void think(UserBoard userBoard, Move moveResult) {
				try {
					// int rondomTime = (int)(Math.random()*1000);
					int rondomTime = 300000;
					mLog.info("think wait %dMsec", rondomTime);
					Thread.sleep(rondomTime);
				} catch (InterruptedException e) {
				}
			}
			@Override
			public void handShake(User handShakeUser, TeamColor teamColor) {
			}
		};
	}

}

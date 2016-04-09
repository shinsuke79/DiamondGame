package user;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import common.DGLog;
import user.humanUser.HumanUser;
import user.yone.breadthFirstSearch.BreadthFirstUser;
import user.yone.breadthFirstSearch.BreadthFirstUserNext;
import user.yone.hillClimbingUser.HillClimbingUser;
import user.yone.obakaUser.ObakaUser;
import user.yone.silentUser.SilentUser;

public class UserManager {
	ArrayList<UserInfo> mUsers;
	DGLog mLog;

	public UserManager(){
		mUsers = new ArrayList<>();
		mLog   = new DGLog("UserManager");
	}

	public void load() {
		mUsers = new ArrayList<>();
		// 上位3つはDGConfig.javaのSmoothDebugOnConsoleをtrueにした場合に自動で選択されるUser
		mUsers.add(new HillClimbingUser.HillClimbingUserInfo("山登りCOM"));
		mUsers.add(new BreadthFirstUser.BreadthFirstUserInfo("優柔不断COM"));
		mUsers.add(new BreadthFirstUserNext.BreadthFirstNextUserInfo("よね"));
		// ここより下は自由に追加して良い
		mUsers.add(new SilentUser.SilentUserInfo("無口COM"));
		mUsers.add(new ObakaUser.ObakaUserInfo("おばかCOM"));
		mUsers.add(new HumanUser.HumanUserInfo("プレイヤー"));
	}

	public List<UserInfo> getAllUsers() {
		return mUsers;
	}

	public User createUser(UserInfo userInfo) {
		User ret = null;

		// コンストラクタクラス生成用
		Class<? extends User> userClass = userInfo.getUserClass();
		Class<?>[] types = { UserInfo.class };
		Constructor<? extends User> constructor;

		// インスタンス生成用
		Object[] args = { userInfo };

		// 実際の生成処理
		try {
			constructor = userClass.getConstructor(types);
			ret         = constructor.newInstance(args);
		} catch (SecurityException |
		         NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			assert false;
		}

		return ret;
	}
}

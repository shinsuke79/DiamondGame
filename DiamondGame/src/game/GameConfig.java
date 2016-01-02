package game;

import java.util.Map;

import common.TeamColor;
import user.UserInfo;

public class GameConfig {
	public Map<TeamColor, UserInfo> users;
	public int timeLimitSec;
	public boolean recordResult;
	@Override
	public String toString() {
		return "GameConfig [users=" + users + ", timeLimitSec=" + timeLimitSec + ", recordResult=" + recordResult + "]";
	}
}
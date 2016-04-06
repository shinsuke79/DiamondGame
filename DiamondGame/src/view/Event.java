package view;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import common.TeamColor;
import game.Board.Cordinate;
import game.Game;
import game.Move;
import user.UserInfo;

public class Event {
	private Game    game;
	private EventId eventId;

	public EventId getEventId() {
		return eventId;
	}

	public Game getGame() {
		return game;
	}

	public Event(EventId eventId, Game game) {
		super();
		this.eventId = eventId;
		this.game    = game;
	}

	public static enum EventId {
		GAME_STARTED,      // ゲームが開始された
		CHANGE_TERN,       // ターンが変更された
		PIECE_MOVED,       // ピースが変更された
		POINT_CHANGED,     // 得点が変化した
		TEAM_REACHED_GOAL, // チームがゴールに到達した
		GAME_FINISHED,     // ゲームが終了した
	}

	/**
	 * ゲームが開始された際に通知されるイベント
	 * @author 0000140105
	 *
	 */
	public static class GameStartedEvent extends Event {
		EnumMap<TeamColor, UserInfo> teams;
		TeamColor firstTeam;
		public GameStartedEvent(Game game, EnumMap<TeamColor, UserInfo> teams, TeamColor firstTeam) {
			super(EventId.GAME_STARTED, game);
			this.teams     = new EnumMap<>(teams);
			this.firstTeam = firstTeam;
		}
		public EnumMap<TeamColor, UserInfo> getTeams() {
			return new EnumMap<>(teams);
		}
		public TeamColor getFirstTeam() {
			return firstTeam;
		}
	}

	/**
	 * チームが変更された時に通知されるイベント
	 * @author 0000140105
	 *
	 */
	public static class ChangeTernEvent extends Event {
		TeamColor currentTeam;
		UserInfo  userInfo;
		public ChangeTernEvent(Game game, TeamColor currentTeam, UserInfo  userInfo) {
			super(EventId.CHANGE_TERN, game);
			this.currentTeam = currentTeam;
			this.userInfo    = userInfo;
		}
		public TeamColor getCurrentTeam() {
			return currentTeam;
		}
		public UserInfo getUserInfo() {
			return userInfo;
		}
	}

	/**
	 * 駒が移動した時に通知されるイベント
	 * @author 0000140105
	 *
	 */
	public static class PieceMovedEvent extends Event {
		TeamColor team;
		Move      move;
		Cordinate firstCordinate;
		public PieceMovedEvent(Game game, TeamColor team, Move move, Cordinate firstCordinate) {
			super(EventId.PIECE_MOVED, game);
			this.team           = team;
			this.move           = move;
			this.firstCordinate = firstCordinate;
		}
		public TeamColor getTeam() {
			return team;
		}
		public Move getMove() {
			return move;
		}
		public Cordinate getFirstCordinate() {
			return firstCordinate;
		}
	}

	/**
	 * ポイントが変化した際に通知されるイベント
	 * @author 0000140105
	 */
	public static class PointChangedEvent extends Event {
		EnumMap<TeamColor, Integer> teamPoints;
		public PointChangedEvent(Game game, EnumMap<TeamColor, Integer> teamPoints) {
			super(EventId.POINT_CHANGED, game);
			this.teamPoints = new EnumMap<>(teamPoints);
		}
		public EnumMap<TeamColor, Integer> getTeamPoints() {
			return new EnumMap<>(teamPoints);
		}
	}

	/**
	 * チームがゴールした時に通知されるイベント
	 * @author 0000140105
	 *
	 */
	public static class TeamReachedGoalEvent extends Event {
		EnumMap<TeamColor, Integer> teamPoints;
		UserInfo        goalUser;
		TeamColor       goalTeam;
		List<TeamColor> goalTeams;
		public TeamReachedGoalEvent(Game game, EnumMap<TeamColor, Integer> teamPoints,
				UserInfo goalUser, TeamColor goalTeam, List<TeamColor> goalTeams) {
			super(EventId.TEAM_REACHED_GOAL, game);
			this.teamPoints = new EnumMap<>(teamPoints);
			this.goalUser   = goalUser;
			this.goalTeam   = goalTeam;
			this.goalTeams  = new ArrayList<>(goalTeams);
		}
		public EnumMap<TeamColor, Integer> getTeamPoints() {
			return new EnumMap<>(teamPoints);
		}
		public UserInfo getGoalUser() {
			return goalUser;
		}
		public TeamColor getGoalTeam() {
			return goalTeam;
		}
		public List<TeamColor> getGoalTeams() {
			return new ArrayList<>(goalTeams);
		}
	}

	/**
	 * ゲームが終了した際に通知されるイベント
	 * @author 0000140105
	 *
	 */
	public static class GameFinishedEvent extends Event {
		List<TeamColor> goalTeams;
		EnumMap<TeamColor, Integer> teamPoints;
		public GameFinishedEvent(Game game, List<TeamColor> goalTeams, EnumMap<TeamColor, Integer> teamPoints) {
			super(EventId.GAME_FINISHED, game);
			assert goalTeams != null;
			this.goalTeams  = goalTeams;
			this.teamPoints = teamPoints;
		}
		public List<TeamColor> getGoalTeams() {
			return new ArrayList<>(goalTeams);
		}
		public EnumMap<TeamColor, Integer> getTeamPoints() {
			return new EnumMap<>(this.teamPoints);
		}
	}

}
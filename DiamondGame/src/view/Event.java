package view;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import common.TeamColor;

public class Event {
	private EventId eventId;

	public EventId getEventId() {
		return eventId;
	}

	public Event(EventId eventId) {
		super();
		this.eventId = eventId;
	}

	public static enum EventId {
		GAME_FINISHED, // ゲームが終了した
	}

	public static class GameFinishedEvent extends Event {
		List<TeamColor> goalTeams;
		EnumMap<TeamColor, Integer> teamPoints;
		public GameFinishedEvent(List<TeamColor> goalTeams, EnumMap<TeamColor, Integer> teamPoints) {
			super(EventId.GAME_FINISHED);
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
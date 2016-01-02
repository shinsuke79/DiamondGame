package game;

public class GameManager {

	public void load() {
	}

	public Game createGame(GameConfig gameConfig) {
		return new Game(gameConfig);
	}

}

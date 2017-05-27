package spaceio.game.data;

import spaceio.game.engine.GameEngine;

public class GameData {
    private static GameData instance;
    private GameEngine gameEngine;

    public static GameData getInstance() {
        if (GameData.instance == null) {
            GameData.instance = new GameData();
        }
        return GameData.instance;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
}

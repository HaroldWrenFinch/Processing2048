package com.haroldwren.machine.game2048.runner;

import org.encog.ml.MLRegression;

public class GameNoGUIRunner {
    private GameLogic gameLogic = new GameLogic(null);

    public GameNoGUIRunner() {
    }

    public void setup() {
        gameLogic.setup();
    }

    public void run(MLRegression network) {
        gameLogic.run(network);
    }

    public Boolean isGameOver() {
        return gameLogic.gameOver();
    }

    public Long getScore() {
        return gameLogic.score;
    }

    public Long getSzamlalo() {
        return gameLogic.szamlalo;
    }
}

package com.haroldwren.machine.game2048.runner;

import com.haroldwren.machine.game2048.movelogic.MoveLogicType;
import org.encog.ml.MLRegression;

public class GameNoGUIRunner {
    private GameLogic gameLogic = new GameLogic(null);

    public GameNoGUIRunner(double[] genes) {
        gameLogic.setGenes(genes);
    }

    public void setup(MoveLogicType moveLogicType) {
        gameLogic.setup(moveLogicType);
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
}

package com.haroldwren.machine.game2048.neural;

import com.haroldwren.machine.game2048.runner.GameNoGUIRunner;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.neural.neat.NEATNetwork;

import static java.lang.Math.pow;

public class Game2048NeuralScore implements CalculateScore {
    /**
     * The training set.
     */
    private final MLDataSet training;

    /**
     * Construct a training set score calculation.
     *
     * @param training
     *            The training data to use.
     */
    public Game2048NeuralScore(final MLDataSet training) {
        this.training = training;
    }

    /**
     * Calculate the score for the network.
     * @param method The network to calculate for.
     * @return The score.
     */
    public double calculateScore(final MLMethod method) {
        GameNoGUIRunner populationOptimizerRunner = new GameNoGUIRunner();
        populationOptimizerRunner.setup();

        Double avarageScore = 0.0;

        double run = 1;

        for(int i = 0; i<run; i++) {
            Long szamlalo = 0L;
            while(!populationOptimizerRunner.isGameOver() && szamlalo < 2000) {
                populationOptimizerRunner.run((NEATNetwork) method);
                szamlalo++;
            }
            Double calculation = populationOptimizerRunner.getScore().doubleValue()
                    - (populationOptimizerRunner.getRossz() * 1000) + (10 * (2000 - szamlalo));
            if(populationOptimizerRunner.getScore() > Game2048NeuralNetworkLogic.maxScore) {
                Game2048NeuralNetworkLogic.maxScore = populationOptimizerRunner.getScore();
                System.out.println("JÓ: " + Game2048NeuralNetworkLogic.maxScore);
            }
            avarageScore += calculation;
        }

        return pow(avarageScore/run, 1);
    }

    /**
     * A training set based score should always seek to lower the error,
     * as a result, this method always returns true.
     * @return Returns true.
     */
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        if(this.training instanceof BufferedMLDataSet) {
            return true;
        }
        return false;
    }

}



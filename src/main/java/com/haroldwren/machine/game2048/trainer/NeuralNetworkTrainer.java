package com.haroldwren.machine.game2048.trainer;

import com.haroldwren.machine.game2048.neural.Game2048NeuralNetworkLogic;

import java.io.IOException;

public class NeuralNetworkTrainer {
    public static void main(String[] args) throws IOException {
        Game2048NeuralNetworkLogic game2048NeuralNetworkLogic = new Game2048NeuralNetworkLogic();
        game2048NeuralNetworkLogic.init();
    }
}

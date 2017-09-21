package com.haroldwren.machine.game2048.trainer;

import com.haroldwren.machine.game2048.neural.Game2048NeuralNetworkLogic;

import java.io.IOException;


/**
 * 512: 300 lépés  - 4520 pont
 * 1024: 505 lépés - 9264  pont
 * 2048: 991 lépés - 20560 pont
 * vége: 1791 lépés - 34552 pont
 */
public class NeuralNetworkTrainer {
    public static void main(String[] args) throws IOException {
        Game2048NeuralNetworkLogic game2048NeuralNetworkLogic = new Game2048NeuralNetworkLogic();
        game2048NeuralNetworkLogic.init();
    }
}

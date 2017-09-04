package com.haroldwren.machine.game2048.movelogic;

import com.haroldwren.machine.game2048.runner.GameLogic;

import java.util.Random;

public class RandomMoveLogic implements MoveLogic {
    private Random rand = new Random();

    /**
     * Do random logic
     *
     */
    public void doRandomLogic(GameLogic gameLogic) throws InterruptedException {
        double check = rand.nextDouble();

        if(check < 0.25) {
            gameLogic.move(1, 0);
        } else if(check >= 0.25 && check < 0.50){
            gameLogic.move(-1, 0);
        } else if(check >= 0.50 && check < 0.75){
            gameLogic.move(0, 1);
        } else if(check >= 0.75){
            gameLogic.move(0, -1);
        }
    }

}

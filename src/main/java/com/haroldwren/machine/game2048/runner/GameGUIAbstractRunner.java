package com.haroldwren.machine.game2048.runner;

import processing.core.PApplet;

public abstract class GameGUIAbstractRunner extends PApplet {
    protected GameLogic gameLogic;

    @Override
    public void settings() {
        gameLogic = new GameLogic(this);
        gameLogic.settings();
    }

    @Override
    public abstract void setup();

    @Override
    public void draw() {
        gameLogic.run(null);
        //noLoop();
    }

    @Override
    public void keyPressed() {
        gameLogic.keyPressed();
    }

    public static void main(String[] args) {
        PApplet.main(GameGUIRunner.class, args);
    }
}


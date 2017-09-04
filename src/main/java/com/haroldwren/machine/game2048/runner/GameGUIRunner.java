package com.haroldwren.machine.game2048.runner;

import com.haroldwren.machine.game2048.movelogic.MoveLogicType;

public class GameGUIRunner extends GameGUIAbstractRunner {
    @Override
    public void setup() {
        gameLogic.setup(MoveLogicType.NEURAL);
    }
}

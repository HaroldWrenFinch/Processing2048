package com.haroldwren.machine.game2048.runner;

import com.haroldwren.machine.PAppletProxy;
import com.haroldwren.machine.game2048.elements.PlayerType;
import com.haroldwren.machine.game2048.movelogic.MoveLogic;
import com.haroldwren.machine.game2048.movelogic.MoveLogicType;
import com.haroldwren.machine.game2048.movelogic.NeuralMoveLogic;
import com.haroldwren.machine.game2048.movelogic.RandomMoveLogic;
import org.encog.ml.MLRegression;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class GameLogic {
    private final static PlayerType PLAYER = PlayerType.AI;
    private MoveLogic moveLogic;
    private PApplet pApplet;
    private Consumer<MLRegression> func;

    private Random rand = new Random();
    private static final int BOARD_SIZE = 4;
    private static final int PADDING = 20;
    private static final int BLOCK_SIZE = 100;
    private static final int TABLE_SIZE = PADDING*(BOARD_SIZE+1)+BLOCK_SIZE*BOARD_SIZE;
    private static final int ANIM_LENGTH = 10;

    public int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] boardCopy = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] prevBoard[] = new int[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE-1];

    public Long score = 0L;
    private boolean isGameOver = true;
    private int animStart;
    public Long szamlalo = 0L;
    private int rossz = 0;
    public int sic = 0;

    public GameLogic(PApplet pApplet) {
        this.pApplet = pApplet;
        if(pApplet!=null) {
        }
    }

    public void settings() {
        if(pApplet!=null) {
            pApplet.size(TABLE_SIZE, TABLE_SIZE);
        }
    }

    public void restart() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        boardCopy = new int[BOARD_SIZE][BOARD_SIZE];
        spawn();
        score = 0L;
        isGameOver = false;
        rossz = 0;
        szamlalo = 0L;
    }

    public void spawn() {
        List<Integer> xPositions = new ArrayList<>();
        List<Integer> yPositions = new ArrayList<>();
        for(int row = 0; row < board.length; row++) {
            for(int column = 0; column < board[row].length; column++) {
                if(board[row][column]==0) {
                    xPositions.add(column);
                    yPositions.add(row);
                }
            }
        }
        Integer random = rand.nextInt(xPositions.size());
        int yPos = yPositions.get(random);
        int xPos = xPositions.get(random);
        board[yPos][xPos] = rand.nextDouble() < 0.9 ? 2 : 4;
        prevBoard[yPos][xPos][0] = -1;
    }

    public void setup(MoveLogicType moveLogicType) {
        PAppletProxy pAppletProxy = PAppletProxy.getInstance(pApplet); // initialize the draw instance.
        pAppletProxy.setGivenHeight(TABLE_SIZE);
        pAppletProxy.setGivenWidth(TABLE_SIZE);
        if(pApplet!=null) {
            pApplet.frameRate(100);
            pApplet.textFont(pApplet.createFont("Courier", 20));
        }

        switch (moveLogicType) {
            case NEURAL:
                moveLogic = new NeuralMoveLogic();
                func = (network) -> ((NeuralMoveLogic) moveLogic).doNeuralLogic(this, network);
                break;
            case RANDOM:
                moveLogic = new RandomMoveLogic();
                func = (network) -> {
                    try {
                        ((RandomMoveLogic) moveLogic).doRandomLogic(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
                break;
        }
        restart();
    }

    public void run(MLRegression network) {
        PAppletProxy pAppletProxy = PAppletProxy.getInstance(null);
        float duration = 0;
        if(pApplet!=null) {
            pApplet.background(0);

            pApplet.background(255);
            pApplet.noStroke();
            pAppletProxy.coloredRectangle(0, 0, pApplet.width, pApplet.height, 0, pApplet.color(150));
            for (int row = 0; row < board.length; row++) {
                for (int column = 0; column < board[row].length; column++) {
                    float xPos = PADDING + (PADDING + BLOCK_SIZE) * column;
                    float yPos = PADDING + (PADDING + BLOCK_SIZE) * row;
                    pAppletProxy.coloredRectangle(xPos, yPos, BLOCK_SIZE, BLOCK_SIZE,
                            5, pApplet.color(200));
                }
            }
            duration = (pApplet.frameCount - animStart) * 1.0f / ANIM_LENGTH;
        }
        float textvoff = 40;
        for(int row = 0; row<board.length; row++) {
            for(int column = 0; column < board[row].length; column++) {
                float xTransition = PADDING+(PADDING+BLOCK_SIZE)*column;
                float yTransition = PADDING+(PADDING+BLOCK_SIZE)*row;
                float xPos = xTransition;
                float yPos = yTransition;
                int value = board[row][column];

                if (((pApplet!=null && pApplet.frameCount - animStart < ANIM_LENGTH) || pApplet==null) && prevBoard[row][column][0] > 0) {
                    int prevYPos = PADDING + (PADDING+BLOCK_SIZE) * prevBoard[row][column][1];
                    int prevXPos = PADDING + (PADDING+BLOCK_SIZE) * prevBoard[row][column][2];
                    xPos = (xPos - prevXPos) * duration + prevXPos;
                    yPos = (yPos - prevYPos) * duration + prevYPos;
                    if (prevBoard[row][column][0] > 1) {
                        value = prevBoard[row][column][0];
                        if(pApplet!=null) {
                            float power = pApplet.log(value)/pApplet.log(2);
                            pAppletProxy.coloredRectangle(xTransition, yTransition, BLOCK_SIZE, BLOCK_SIZE,
                                    5, pApplet.color(255-power*255/11, power*255/11, 0));
                            pAppletProxy.coloredText(""+prevBoard[row][column][0], xTransition, yTransition+textvoff,
                                    BLOCK_SIZE, BLOCK_SIZE, pApplet.color(0),
                                    20, pApplet.CENTER);
                        }
                    }
                }

                if ((pApplet!=null && pApplet.frameCount - animStart > ANIM_LENGTH) || prevBoard[row][column][0] >= 0) {
                    if (prevBoard[row][column][0] >= 2) {
                        if(pApplet!=null) {
                            float grow = pApplet.abs(0.5f - duration) * 2;
                            if(pApplet.frameCount - animStart > ANIM_LENGTH * 3) {
                                grow = 1;
                            } else {
                            }
                            pAppletProxy.coloredRectangle(xPos-2*grow, yPos-2*grow, BLOCK_SIZE+4*grow, BLOCK_SIZE+4*grow,
                                    5, pApplet.color(255,255,0,100));
                        }
                    } else if (prevBoard[row][column][0]==1) {
                        if(pApplet!=null) {
                            pAppletProxy.coloredRectangle(xPos-2, yPos-2, BLOCK_SIZE+4, BLOCK_SIZE+4,
                                    5, pApplet.color(255,100));
                        }
                    }
                    if(pApplet!=null) {
                        pApplet.fill(200);
                        if (value > 0) {
                            float power = pApplet.log(value)/pApplet.log(2);
                            pAppletProxy.coloredRectangle(xPos, yPos, BLOCK_SIZE, BLOCK_SIZE,
                                    5, pApplet.color(255-power*255/11, power*255/11, 0));
                            pAppletProxy.coloredText(""+value, xPos, yPos+textvoff,
                                    BLOCK_SIZE, BLOCK_SIZE, pApplet.color(0),
                                    20, pApplet.CENTER);
                        }
                    }
                }
            }
        }

        if(pApplet!=null) {
            pAppletProxy.coloredText("Score: " + score, 10, 5, 100, 50, pApplet.color(0), 10, pApplet.LEFT);
            if(isGameOver) {
                pAppletProxy.coloredRectangle(0, 0, pApplet.width, pApplet.height, 0, pApplet.color(255, 100));
                pAppletProxy.coloredText("Játék vége! Kattints újraindításhoz", 0, pApplet.height/2, pApplet.width, 50, pApplet.color(0),
                        30, pApplet.CENTER);
                if(pApplet.mousePressed) restart();
            }

        }

//        if(sic>=100) {
            if(PLAYER == PlayerType.AI) {
                func.accept(network);
            }
//            sic = 0;
//        }
//        sic++;
    }

    public int[][] go(int deltaY, int deltaX, boolean check) {
        int[][] boardCopy = new int[BOARD_SIZE][BOARD_SIZE];
        for(int row = 0; row < board.length; row++) {
            for(int column = 0; column < board[row].length; column++) {
                boardCopy[row][column] = board[row][column];
            }
        }
        prevBoard = new int[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE-1];
        boolean moved = false;
        if(deltaX != 0 || deltaY != 0) {
            int direction = deltaX != 0 ? deltaX : deltaY;
            for(int perpendicular = 0; perpendicular < board.length; perpendicular++) {
                int tangential = direction > 0 ? board.length - 2 : 1;
                int stop = direction > 0 ? -1 : board.length;
                for(; tangential != stop; tangential -= direction) {
                    int yPos = deltaX != 0 ? perpendicular : tangential;
                    int xPos = deltaX != 0 ? tangential : perpendicular;
                    int targetYPos = yPos;
                    int targetXPos = xPos;
                    if(boardCopy[yPos][xPos]==0) continue;
                    int i=(deltaX != 0 ? xPos : yPos)+direction;
                    int stopSign = direction > 0 ? board.length : -1;
                    for(; i != stopSign; i+=direction) {
                        int row = deltaX != 0 ? yPos : i;
                        int column = deltaX != 0 ? i : xPos;
                        if(boardCopy[row][column] != 0 && boardCopy[row][column] != board[yPos][xPos]) break;
                        if(deltaX != 0) {
                            targetXPos = i;
                        } else {
                            targetYPos = i;
                        }

                    }
                    // x and y are now the block position, tx ty are where the block is sliding into
                    if(( deltaX != 0 && targetXPos == xPos) || (deltaY != 0 && targetYPos == yPos)) continue;
                    else if(boardCopy[targetYPos][targetXPos] == board[yPos][xPos]) {
                        prevBoard[targetYPos][targetXPos][0] = boardCopy[targetYPos][targetXPos];
                        boardCopy[targetYPos][targetXPos] *= 2;
                        score += boardCopy[targetYPos][targetXPos];
                        moved = true;
                    } else if((deltaX != 0 && targetXPos != xPos) || (deltaY != 0 && targetYPos != yPos)) {
                        prevBoard[targetYPos][targetXPos][0] = 1;
                        boardCopy[targetYPos][targetXPos] = boardCopy[yPos][xPos];
                        moved = true;
                    }
                    if(moved) {
                        prevBoard[targetYPos][targetXPos][1] = yPos;
                        prevBoard[targetYPos][targetXPos][2] = xPos;
                        boardCopy[yPos][xPos] = 0;
                    }
                }
            }
        }
        if(moved) {
            if(check) {
                szamlalo++;
            }
        }
        if(!moved) {
            if(check) {
                rossz++;
            }
            return null;
        }
        if(pApplet!=null) {
            animStart = pApplet.frameCount;
        }
        return boardCopy;
    }

    public void keyPressed() {
        int deltaY = 0;
        int deltaX = 0;
        if(pApplet!=null) {
            deltaY = pApplet.keyCode==pApplet.UP ? -1 : (pApplet.keyCode==pApplet.DOWN ? 1 : 0);
            deltaX = pApplet.keyCode==pApplet.LEFT ? -1 : (pApplet.keyCode==pApplet.RIGHT ? 1 : 0);
        }
        move(deltaY, deltaX);
    }

    public boolean move(int deltaY, int deltaX) {
        boolean success = false;
        if(!isGameOver) {
            int[][] newBoard = go(deltaY, deltaX, true);
            if(newBoard != null) {
                board = newBoard;
                spawn();
                success = true;
//                System.out.println(szamlalo);
//                System.out.println(score.doubleValue()/szamlalo.doubleValue());
            }
        }
        if(gameOver()) {
            isGameOver = true;
        }
        return success;
    }

    public boolean gameOver() {
        int[] deltaXs = {1, -1, 0, 0};
        int[] deltaYs = {0, 0, 1, -1};
        int[][][] prevbak = prevBoard;
        boolean result = true;
        Long prevscore = score;
        for(int i = 0; i < BOARD_SIZE; i++) {
            if(go(deltaYs[i], deltaXs[i], false) != null) {
                result = false;
            }
        }
        prevBoard = prevbak;
        score = prevscore;
        if(rossz>0) {
            if(pApplet==null) {
                result = true;
            }
        }
        if(score<3200L) {
            score = 0L;
        }
        return result;
    }

}

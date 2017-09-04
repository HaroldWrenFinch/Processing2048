package com.haroldwren.machine.game2048.basic;

public class Runner {
//
//    @Override
//    public void draw() {
//        background(255);
//        noStroke();
//        coloredRectangle(0,0, width, height, 0, color(150));
//        for(int row = 0; row<board.length; row++) {
//            for(int column = 0; column < board[row].length; column++) {
//                float xPos = PADDING+(PADDING+BLOCK_SIZE)*column;
//                float yPos = PADDING+(PADDING+BLOCK_SIZE)*row;
//                coloredRectangle(xPos, yPos, BLOCK_SIZE, BLOCK_SIZE,
//                        5, color(200));
//            }
//        }
//        float gscore = 0, textvoff = 40;
//        for(int row = 0; row<board.length; row++) {
//            for(int column = 0; column < board[row].length; column++) {
//                float xTransition = PADDING+(PADDING+BLOCK_SIZE)*column;
//                float yTransition = PADDING+(PADDING+BLOCK_SIZE)*row;
//                float xPos = xTransition;
//                float yPos = yTransition;
//                int value = board[row][column];
//
//                float duration = (frameCount - animStart) * 1.0f / animLength;
//
//                if (frameCount - animStart < animLength && prevBoard[row][column][0] > 0) {
//                    int prevYPos = PADDING + (PADDING+BLOCK_SIZE) * prevBoard[row][column][1];
//                    int prevXPos = PADDING + (PADDING+BLOCK_SIZE) * prevBoard[row][column][2];
//                    xPos = (xPos - prevXPos) * duration + prevXPos;
//                    yPos = (yPos - prevYPos) * duration + prevYPos;
//                    if (prevBoard[row][column][0] > 1) {
//                        value = prevBoard[row][column][0];
//                        float power = log(value)/log(2);
//                        coloredRectangle(xTransition, yTransition, BLOCK_SIZE, BLOCK_SIZE,
//                                5, color(255-power*255/11, power*255/11, 0));
//                        coloredText(""+prevBoard[row][column][0], xTransition, yTransition+textvoff,
//                                BLOCK_SIZE, BLOCK_SIZE, color(0),
//                                20, CENTER);
//                    }
//                }
//
//                if (frameCount - animStart > animLength || prevBoard[row][column][0] >= 0) {
//                    if (prevBoard[row][column][0] >= 2) {
//                        float grow = abs(0.5f - duration) * 2;
//                        if(frameCount - animStart > animLength * 3) {
//                            grow = 1;
//                        }
//                        else {
//                            gscore = grow;
//                        }
//                        coloredRectangle(xPos-2*grow, yPos-2*grow, BLOCK_SIZE+4*grow, BLOCK_SIZE+4*grow,
//                                5, color(255,255,0,100));
//                    }
//                    else if (prevBoard[row][column][0]==1) {
//                        coloredRectangle(xPos-2, yPos-2, BLOCK_SIZE+4, BLOCK_SIZE+4,
//                                5, color(255,100));
//                    }
//                    fill(200);
//                    if (value > 0) {
//                        float power = log(value)/log(2);
//                        coloredRectangle(xPos, yPos, BLOCK_SIZE, BLOCK_SIZE,
//                                5, color(255-power*255/11, power*255/11, 0));
//
//                        coloredText(""+value, xPos, yPos+textvoff,
//                                BLOCK_SIZE, BLOCK_SIZE, color(0),
//                                20, CENTER);
//                    }
//                }
//            }
//        }
//        coloredText("Score: " + score, 10, 5, 100, 50, color(0), 10, LEFT);
//        if(isGameOver) {
//            coloredRectangle(0, 0, width, height, 0, color(255, 100));
//            coloredText("Játék vége! Kattints újraindításhoz", 0, height/2, width, 50, color(0),
//                    30, CENTER);
//            if(mousePressed) restart();
//        }
//    }
//
//    public int[][] go(int deltaY, int deltaX) {
//        int[][] boardCopy = new int[BOARD_SIZE][BOARD_SIZE];
//        for(int row = 0; row < board.length; row++) {
//            for(int column = 0; column < board[row].length; column++) {
//                boardCopy[row][column] = board[row][column];
//            }
//        }
//        prevBoard = new int[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE-1];
//        boolean moved = false;
//        if(deltaX != 0 || deltaY != 0) {
//            int direction = deltaX != 0 ? deltaX : deltaY;
//            for(int perpendicular = 0; perpendicular < board.length; perpendicular++) {
//                int tangential = direction > 0 ? board.length - 2 : 1;
//                int stop = direction > 0 ? -1 : board.length;
//                for(; tangential != stop; tangential -= direction) {
//                    int yPos = deltaX != 0 ? perpendicular : tangential;
//                    int xPos = deltaX != 0 ? tangential : perpendicular;
//                    int targetYPos = yPos;
//                    int targetXPos = xPos;
//                    if(boardCopy[yPos][xPos]==0) continue;
//                    int i=(deltaX != 0 ? xPos : yPos)+direction;
//                    int stopSign = direction > 0 ? board.length : -1;
//                    for(; i != stopSign; i+=direction) {
//                        int row = deltaX != 0 ? yPos : i;
//                        int column = deltaX != 0 ? i : xPos;
//                        if(boardCopy[row][column] != 0 && boardCopy[row][column] != board[yPos][xPos]) break;
//                        if(deltaX != 0) {
//                            targetXPos = i;
//                        } else {
//                            targetYPos = i;
//                        }
//
//                    }
//                    // x and y are now the block position, tx ty are where the block is sliding into
//                    if(( deltaX != 0 && targetXPos == xPos) || (deltaY != 0 && targetYPos == yPos)) continue;
//                    else if(boardCopy[targetYPos][targetXPos] == board[yPos][xPos]) {
//                        prevBoard[targetYPos][targetXPos][0] = boardCopy[targetYPos][targetXPos];
//                        boardCopy[targetYPos][targetXPos] *= 2;
//                        score += boardCopy[targetYPos][targetXPos];
//                        moved = true;
//                    } else if((deltaX != 0 && targetXPos != xPos) || (deltaY != 0 && targetYPos != yPos)) {
//                        prevBoard[targetYPos][targetXPos][0] = 1;
//                        boardCopy[targetYPos][targetXPos] = boardCopy[yPos][xPos];
//                        moved = true;
//                    }
//                    if(moved) {
//                        prevBoard[targetYPos][targetXPos][1] = yPos;
//                        prevBoard[targetYPos][targetXPos][2] = xPos;
//                        boardCopy[yPos][xPos] = 0;
//                    }
//                }
//            }
//        }
//        if (!moved) return null;
//        animStart = frameCount;
//        return boardCopy;
//    }
//
//
//

}

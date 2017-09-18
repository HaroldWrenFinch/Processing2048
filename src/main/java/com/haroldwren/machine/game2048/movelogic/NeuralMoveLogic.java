package com.haroldwren.machine.game2048.movelogic;

import com.haroldwren.machine.game2048.runner.GameLogic;
import org.encog.mathutil.Equilateral;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class NeuralMoveLogic {
    private BasicNetwork network;
    private static final String networkFileName = "file/temp/2048_500000_train.eg";
    private static final Equilateral equilateral = new Equilateral(4, 1, 0);
    /**
     * create neuralMoveLogic
     */
    public NeuralMoveLogic() {
        File networkFile = new File("/tmp/2048_40_train.eg");
        try {
            InputStream inputStream = new FileInputStream(networkFile);
            network = (BasicNetwork) EncogDirectoryPersistence.loadObject(networkFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double log2(double num) {
        if(num==0) {
            return 0.0;
        }
        return Math.log(num)/Math.log(2);
    }

    /**
     * do the logic
     *
     * @param network
     */
    public void doNeuralLogic(GameLogic gameLogic, MLRegression network) {
        double oszto = log2(2048);

        double[] neuralInput = {
                log2(gameLogic.board[0][0])/oszto, log2(gameLogic.board[0][1])/oszto,
                    log2(gameLogic.board[0][2])/oszto, log2(gameLogic.board[0][3])/oszto,
                log2(gameLogic.board[1][0])/oszto, log2(gameLogic.board[1][1])/oszto,
                    log2(gameLogic.board[1][2])/oszto, log2(gameLogic.board[1][3])/oszto,
                log2(gameLogic.board[2][0])/oszto, log2(gameLogic.board[2][1])/oszto,
                    log2(gameLogic.board[2][2])/oszto, log2(gameLogic.board[2][3])/oszto,
                log2(gameLogic.board[3][0])/oszto, log2(gameLogic.board[3][1])/oszto,
                    log2(gameLogic.board[3][2])/oszto, log2(gameLogic.board[3][3])/oszto,
        };
        BasicMLData input = new BasicMLData(neuralInput);

        MLData output;
        Boolean fromFile;
        if(network != null) {
            if(network.getClass() == NEATNetwork.class) {
                output = this.compute((NEATNetwork) network, input);
                fromFile = false;
            } else {
                output = this.compute((BasicNetwork) network, input);
                fromFile = false;
            }
        } else {
            output = this.compute(this.network, input);
            fromFile = true;
        }

        int success = equilateral.decode(output.getData());

        if(success == 0) {
            if(!gameLogic.move(1, 0) && fromFile) {
                if(!gameLogic.move(0, 1)) {
                    if(!gameLogic.move(-1, 0)) {
                        gameLogic.move(0, -1);
                    }
                }
            }
        } else if(success == 1) {
            if(!gameLogic.move(0, 1) && fromFile) {
                if(!gameLogic.move(1, 0)) {
                    if(!gameLogic.move(-1, 0)) {
                        gameLogic.move(0, -1);
                    }
                }
            }
        } else if(success == 2) {
            if(!gameLogic.move(-1, 0) && fromFile) {
                if(!gameLogic.move(1, 0)) {
                    if(!gameLogic.move(0, 1)) {
                        gameLogic.move(0, -1);
                    }
                }
            }
        } else {
            if(!gameLogic.move(0, -1) && fromFile) {
                if(!gameLogic.move(1, 0)) {
                    if(!gameLogic.move(0, 1)) {
                        gameLogic.move(-1, 0);
                    }
                }
            }
        }
    }

    /**
     * compute output, for input data
     *
     * @param network
     * @param input
     * @return
     */
    private MLData compute(NEATNetwork network,BasicMLData input) {
        return network.compute(input);
    }

    private MLData compute(BasicNetwork network,BasicMLData input) {
        return network.compute(input);
    }

}

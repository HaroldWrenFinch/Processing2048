package com.haroldwren.machine.game2048.movelogic;

import com.haroldwren.machine.game2048.runner.GameLogic;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.BasicNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class NeuralMoveLogic implements MoveLogic {
    private NEATNetwork network;
    private static final String networkFileName = "file/temp/2048_3400000_train.eg";

    /**
     * create neuralMoveLogic
     */
    public NeuralMoveLogic() {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(networkFileName);
        if(null!= resource) {
            File networkFile = new File(resource.getFile());
            PersistNEATPopulation pnp = new PersistNEATPopulation();
            try {
                InputStream inputStream = new FileInputStream(networkFile);
                NEATPopulation pop = (NEATPopulation) pnp.read(inputStream);
                network = (NEATNetwork)pop.getCODEC().decode(pop.getBestGenome());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * do the logic
     *
     * @param network
     */
    public void doNeuralLogic(GameLogic gameLogic, MLRegression network) {
        double oszto = 2;
        for(int i = 0; i< gameLogic.board.length;i++) {
            for(int j = 0; j< gameLogic.board.length;j++) {
                if(gameLogic.board[i][j]>oszto) {
                    oszto = gameLogic.board[i][j];
                }
            }
        }

        double[] neuralInput = {
                gameLogic.board[0][0]/oszto, gameLogic.board[0][1]/oszto, gameLogic.board[0][2]/oszto, gameLogic.board[0][3]/oszto,
                gameLogic.board[1][0]/oszto, gameLogic.board[1][1]/oszto, gameLogic.board[1][2]/oszto, gameLogic.board[1][3]/oszto,
                gameLogic.board[2][0]/oszto, gameLogic.board[2][1]/oszto, gameLogic.board[2][2]/oszto, gameLogic.board[2][3]/oszto,
                gameLogic.board[3][0]/oszto, gameLogic.board[3][1]/oszto, gameLogic.board[3][2]/oszto, gameLogic.board[3][3]/oszto,
        };
        BasicMLData input = new BasicMLData(neuralInput);

        MLData output;
        if(network != null) {
            if(network.getClass() == NEATNetwork.class) {
                output = this.compute((NEATNetwork) network, input);
            } else {
                output = this.compute((BasicNetwork) network, input);
            }
        } else {
            output = this.compute(this.network, input);
        }
        double neuralOuput0 = output.getData(0);
        double neuralOuput1 = output.getData(1);
        double neuralOuput2 = output.getData(2);
        double neuralOuput3 = output.getData(3);

        if(neuralOuput0>=neuralOuput1 && neuralOuput0>=neuralOuput2 && neuralOuput0>=neuralOuput3) {
            gameLogic.move(1, 0);
        } else if(neuralOuput1>=neuralOuput0 && neuralOuput1>=neuralOuput2 && neuralOuput1>=neuralOuput3) {
            gameLogic.move(-1, 0);
        } else if(neuralOuput2>=neuralOuput0 && neuralOuput2>=neuralOuput1 && neuralOuput2>=neuralOuput3) {
            gameLogic.move(0, 1);
        } else {
            gameLogic.move(0, -1);
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

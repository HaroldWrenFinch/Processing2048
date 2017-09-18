package com.haroldwren.machine.game2048.neural;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.MethodFactory;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Game2048NeuralNetworkLogic {
    public static final double MAX_ITERATION = 12000.0;
    private static final double SUCCESS_RATE = 1;
    private static final double MIN_SCORE = MAX_ITERATION * SUCCESS_RATE;
    private static final String NETWORK_FILE_NAME = "2048";
    private static final String DEFAULT_FILE_EXTENSION = "eg";
    private static final int POPULATION_SIZE = 500;
    private static final int OUTPUT_NEURON_SIZE = 3;
    private static final Boolean WRITE_PERCENTS_TO_FILE = true;
    public static double maxScore = 0L;

    public static BasicNetwork createNetwork() {
        ElmanPattern pattern = new ElmanPattern();
        pattern.setInputNeurons(16);
        pattern.addHiddenLayer(32);
        pattern.setOutputNeurons(OUTPUT_NEURON_SIZE);
        pattern.setActivationFunction(new ActivationSigmoid());
        BasicNetwork network = (BasicNetwork)pattern.generate();
        network.reset();
        return network;
    }

    public void init() throws IOException {
        BasicNetwork network = createNetwork();
        CalculateScore score = new Game2048NeuralScore(null);
        MLTrain train = new MLMethodGeneticAlgorithm(new MethodFactory(){
            @Override
            public MLMethod factor() {
                final BasicNetwork result = createNetwork();
                ((MLResettable)result).reset();
                return result;
            }},score,POPULATION_SIZE);

        Boolean[] savedTrain = new Boolean[] {false, false, false, false, false, false, false, false, false, false};
        int savedTrainNO = 0;
        double error = 0;
        System.out.println("Epoch #");
        do {
            train.iteration();
            int iteration = train.getIteration();
            error = train.getError();
            System.out.println("Epoch #" + iteration + " Score:" + error);
            if(WRITE_PERCENTS_TO_FILE) {
                if(!savedTrain[savedTrainNO] && (error > (MIN_SCORE * (savedTrainNO+1) * 0.1))) {
                    savedTrain[savedTrainNO] = true;
                    writeToDisk((BasicNetwork) train.getMethod(), false, "2048_"+(savedTrainNO+1)+"0_percent");
                    if(savedTrainNO < savedTrain.length -1) {
                        savedTrainNO++;
                    }
                }
                if(iteration % 10 == 0) {
                    writeToDisk(network, false, "2048_"+ iteration +"_train");
                }
            }
        } while(error < MIN_SCORE);

        network = (BasicNetwork)train.getMethod();
        System.out.println("Input: " + network.getInputCount());
        System.out.println("Output: " + network.getOutputCount());

        writeToDisk(network, true, "perfect");
        //EncogUtility.evaluate(network, evaluateSet);
    }

    public void writeToDisk(BasicNetwork network, boolean shutdown, String fileName) throws IOException {
        File networkFile = File.createTempFile(NETWORK_FILE_NAME+fileName, DEFAULT_FILE_EXTENSION);

        try {
            EncogDirectoryPersistence.saveObject(networkFile, network) ;
            if(fileName != null && !fileName.isEmpty()) {
                Path source = Paths.get(networkFile.toString());
                Files.move(source, source.resolveSibling(fileName+"."+DEFAULT_FILE_EXTENSION));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(shutdown) {
            Encog.getInstance().shutdown();
        }
    }

    public void writeToDisk(NEATPopulation pop, boolean shutdown, String fileName) throws IOException {
        File networkFile = File.createTempFile(NETWORK_FILE_NAME, DEFAULT_FILE_EXTENSION);

        PersistNEATPopulation pnp = new PersistNEATPopulation();
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(networkFile);
            pnp.save(outputStream, pop); //pop is your NEATPopulation object
            outputStream.flush();
            outputStream.close();
            if(fileName != null && !fileName.isEmpty()) {
                Path source = Paths.get(networkFile.toString());
                Files.move(source, source.resolveSibling(fileName+"."+DEFAULT_FILE_EXTENSION));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(shutdown) {
            Encog.getInstance().shutdown();
        }
    }

}

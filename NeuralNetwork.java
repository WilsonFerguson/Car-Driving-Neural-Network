public class NeuralNetwork {

    float[] inputLayer;
    Layer[] hiddenLayers;
    Layer outputLayer;

    public NeuralNetwork(int... layerSizes) {
        inputLayer = new float[layerSizes[0]];
        hiddenLayers = new Layer[layerSizes.length - 2];
        for (int i = 0; i < hiddenLayers.length; i++) {
            hiddenLayers[i] = new Layer(layerSizes[i + 1], layerSizes[i]);
        }
        outputLayer = new Layer(layerSizes[layerSizes.length - 1], layerSizes[layerSizes.length - 2]);
    }

    public NeuralNetwork(int inputs, int numHiddens, int outputs) {
        inputLayer = new float[inputs];
        hiddenLayers = new Layer[numHiddens];
        outputLayer = new Layer(outputs, inputs);
    }

    public void load() {
        for (int i = 0; i < hiddenLayers.length; i++) {
            hiddenLayers[i].loadWeights(i);
            hiddenLayers[i].loadBiases(i);
        }
        outputLayer.loadWeights();
        outputLayer.loadBiases();
    }

    public float[] feedForward(float[] inputs) {
        float[] outputs = inputLayer;
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = inputs[i];
        }
        for (int i = 0; i < hiddenLayers.length; i++) {
            outputs = hiddenLayers[i].feedForward(outputs);
        }
        outputs = outputLayer.feedForward(outputs);
        return outputs;
    }

    public void mutate(float mutationRate) {
        for (int i = 0; i < hiddenLayers.length; i++) {
            hiddenLayers[i].mutate(mutationRate);
        }
        outputLayer.mutate(mutationRate);
    }

    public NeuralNetwork crossover(NeuralNetwork other) {
        NeuralNetwork child = new NeuralNetwork(inputLayer.length, hiddenLayers.length, outputLayer.size);
        for (int i = 0; i < hiddenLayers.length; i++) {
            child.hiddenLayers[i] = hiddenLayers[i].crossover(other.hiddenLayers[i]);
        }
        child.outputLayer = outputLayer.crossover(other.outputLayer);
        return child;
    }

    public NeuralNetwork clone() {
        NeuralNetwork clone = new NeuralNetwork(inputLayer.length, hiddenLayers.length, outputLayer.size);
        for (int i = 0; i < hiddenLayers.length; i++) {
            clone.hiddenLayers[i] = hiddenLayers[i].clone();
        }
        clone.outputLayer = outputLayer.clone();
        return clone;
    }

    public float[] getInputs() {
        return inputLayer;
    }

    public Layer[] getHiddenLayers() {
        return hiddenLayers;
    }

    public Layer getOutputLayer() {
        return outputLayer;
    }

    public void save() {
        for (int i = 0; i < hiddenLayers.length; i++) {
            hiddenLayers[i].save(i);
        }
        outputLayer.save();
    }

}

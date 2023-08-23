import library.core.*;

public class Layer extends PComponent {

    float[] neurons;
    float[] biases;
    float[][] weights;

    public int size;

    public Layer(int neuronCount, int prevNeuronCount) {
        neurons = new float[neuronCount];
        biases = new float[neuronCount];
        weights = new float[neuronCount][prevNeuronCount];

        for (int i = 0; i < neuronCount; i++) {
            biases[i] = random(-1, 1);
            for (int j = 0; j < prevNeuronCount; j++) {
                weights[i][j] = random(-1, 1);
            }
        }
        size = neuronCount;
    }

    public float[] feedForward(float[] inputs) {
        for (int i = 0; i < neurons.length; i++) {
            float sum = 0;
            for (int j = 0; j < inputs.length; j++) {
                sum += inputs[j] * weights[i][j];
            }
            sum += biases[i];
            neurons[i] = sigmoid(sum);
        }
        return neurons;
    }

    public float sigmoid(float x) {
        return 1 / (1 + (float) Math.exp(-x));
    }

    public void mutate(float mutationRate) {
        for (int i = 0; i < biases.length; i++) {
            if (random(1) < mutationRate) {
                biases[i] = random(-1, 1);
            }
        }
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                if (random(1) < mutationRate) {
                    weights[i][j] = random(-1, 1);
                }
            }
        }
    }

    // Uniform crossover
    public Layer crossover(Layer other) {
        Layer child = new Layer(neurons.length, weights[0].length);
        for (int i = 0; i < biases.length; i++) {
            if (random(1) < 0.5) {
                child.biases[i] = biases[i];
            } else {
                child.biases[i] = other.biases[i];
            }
        }
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                if (random(1) < 0.5) {
                    child.weights[i][j] = weights[i][j];
                } else {
                    child.weights[i][j] = other.weights[i][j];
                }
            }
        }
        return child;
    }

    public Layer clone() {
        Layer clone = new Layer(neurons.length, weights[0].length);
        for (int i = 0; i < biases.length; i++) {
            clone.biases[i] = biases[i];
        }
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                clone.weights[i][j] = weights[i][j];
            }
        }
        return clone;
    }

    public float[] getNeurons() {
        return neurons;
    }

    public void save(int index) {
        saveStrings(toArray(biases), "Brain/HiddenBias" + index + ".txt");
        saveStrings(toArray(weights), "Brain/HiddenWeight" + index + ".txt");
    }

    public void save() {
        saveStrings(toArray(biases), "Brain/OutputBias.txt");
        saveStrings(toArray(weights), "Brain/OutputWeight.txt");
    }

    public String[] toArray(float[] array) {
        String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            stringArray[i] = Float.toString(array[i]);
        }
        return stringArray;
    }

    public String[] toArray(float[][] array) {
        String[] stringArray = new String[array.length * array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                stringArray[i * array[i].length + j] = Float.toString(array[i][j]);
            }
        }
        return stringArray;
    }

    public void loadBiases(int index) {
        String[] stringArray = loadStrings("Brain/HiddenBias" + index + ".txt");
        for (int i = 0; i < biases.length; i++) {
            biases[i] = Float.parseFloat(stringArray[i]);
        }
    }

    public void loadBiases() {
        String[] stringArray = loadStrings("Brain/OutputBias.txt");
        for (int i = 0; i < biases.length; i++) {
            biases[i] = Float.parseFloat(stringArray[i]);
        }
    }

    public void loadWeights(int index) {
        String[] stringArray = loadStrings("Brain/HiddenWeight" + index + ".txt");
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = Float.parseFloat(stringArray[i * weights[i].length + j]);
            }
        }
    }

    public void loadWeights() {
        String[] stringArray = loadStrings("Brain/OutputWeight.txt");
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = Float.parseFloat(stringArray[i * weights[i].length + j]);
            }
        }
    }

}

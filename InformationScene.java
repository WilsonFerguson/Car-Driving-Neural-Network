import java.util.ArrayList;
import library.core.*;

public class InformationScene extends Scene {

    private MainScene mainScene;

    // Buttons
    private SceneButton mainButton;
    private SceneButton trackEditorButton;

    Graph fitnessGraph;

    public InformationScene(Simulation sim, String name) {
        super(sim, name); // not sure where i learned super, but it's cool

        mainScene = (MainScene) Scene.getScene("Main");

        trackEditorButton = new SceneButton(sim,
                new PVector(width - Settings.navButtonSize.x - 5, height - Settings.navButtonSize.y - 5),
                Settings.navButtonSize,
                "Track");
        mainButton = new SceneButton(sim,
                new PVector(width - Settings.navButtonSize.x * 2 - 10, height - Settings.navButtonSize.y - 5),
                Settings.navButtonSize,
                "Main");

        trackEditorButton.setBackground(200, 0, 0);
        mainButton.setBackground(0, 200, 0);
    }

    public void draw() {
        if (!active)
            return;

        int generation = mainScene.generation;
        int maxFitness = (int) mainScene.maxFitness;
        int currentFitness = (int) mainScene.currentFitness;
        int currentDrivers = mainScene.currentDrivers;
        int completedLaps = mainScene.completedLaps;
        int maxCompletedLaps = mainScene.maxCompletedLaps;
        float learningRate = mainScene.learningRate;

        float speed = mainScene.speed;

        fitnessGraph = mainScene.fitnessGraph.copy();
        fitnessGraph.setPos(new PVector(18, 500));
        fitnessGraph.setSize(new PVector(900, 500));

        // Average fitness of previous generation

        // Background
        background(0);

        // General information
        fill(255);
        textSize(50);
        text("Generation #" + generation, 10, 30);
        text("Max Fitness: " + maxFitness, 10, 80);
        text("Current Fitness: " + currentFitness, 10, 130);
        text("Current Drivers: " + currentDrivers, 10, 180);
        text("Max Completed Laps: " + maxCompletedLaps, 10, 230);
        text("Completed Laps: " + completedLaps, 10, 280);
        text("Learning Rate: " + Helper.roundString(learningRate, 3), 10, 330);
        text("Speed: " + Helper.roundString(speed, 0) + " mph", 10, 380);

        // Fitness graph
        fitnessGraph.draw();

        // Neural network display
        drawNeuralNetwork();

        // Run time
        String time = "";
        int seconds = millis() / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;

        if (hours < 10)
            time += "0";

        time += hours + ":";

        if (minutes < 10)
            time += "0";

        time += minutes + ":";

        if (seconds < 10)
            time += "0";

        time += seconds;

        text(time, 10, height - 50);

        // Buttons
        trackEditorButton.draw();
        mainButton.draw();
    }

    public void mousePressed() {
        if (!active)
            return;

        NeuralNetwork brain = fitnessGraph.getNeuralNetworkFromMouse();
        if (brain == null)
            return;

        mainScene.Reset();
        for (Driver driver : mainScene.drivers) {
            driver.brain = brain.clone();
        }
    }

    public void drawNeuralNetwork() {
        NeuralNetwork brain = null;
        Driver driver = null;
        // Go through all the current drivers until we find one that is not dead
        for (Driver d : mainScene.drivers) {
            if (!d.dead) {
                driver = d;
                brain = d.brain;
                break;
            }
        }

        if (brain == null)
            return;

        push();

        float[] inputs = brain.getInputs();
        Layer[] hiddenLayers = brain.getHiddenLayers();
        Layer outputLayer = brain.getOutputLayer();

        float diagramWidth = width / 2; // Width of the neural network display
        float diagramHeight = height / 2; // Height of the neural network display
        float x = width - diagramWidth - 10; // Left side of the neural network display

        int numLayers = hiddenLayers.length + 2;

        float padding = 10; // Padding between each layer
        float layerWidth = (diagramWidth - padding * 2 - (numLayers - 1) * padding) / numLayers; // Width of each layer
        float layerHeight = diagramHeight - padding * 2; // Height of biggest layer

        int maxHeight = 0; // The maximum number of neurons in a layer
        for (Layer layer : hiddenLayers) {
            maxHeight = max(maxHeight, layer.getNeurons().length);
        }
        maxHeight = max(maxHeight, outputLayer.getNeurons().length);
        maxHeight = max(maxHeight, inputs.length);

        float neuronRadius = min(layerWidth, layerHeight / maxHeight) / 2; // Radius of each neuron
        float distBetweenNeurons = layerHeight / maxHeight; // Distance between each neuron

        ArrayList<Float>[] neuronHeights = new ArrayList[numLayers]; // The height of each neuron in each layer
        Float[] layerX = new Float[numLayers]; // The x position of each layer

        ellipseMode(CENTER);

        // Draw the input layer
        float neuronX = x + padding + layerWidth / 2;
        layerX[0] = neuronX;
        for (int i = 0; i < inputs.length; i++) {
            float neuronY = diagramHeight / 2; // Middle of the layer
            neuronY -= (inputs.length - 1) * distBetweenNeurons / 2; // Move the layer up so that the first neuron is at
                                                                     // the top
            neuronY += i * distBetweenNeurons; // Move the neuron down to the correct position

            if (neuronHeights[0] == null)
                neuronHeights[0] = new ArrayList<Float>();

            neuronHeights[0].add(neuronY);
        }

        // Draw the hidden layers
        for (int i = 0; i < hiddenLayers.length; i++) {
            Layer layer = hiddenLayers[i];
            neuronX += layerWidth + padding;
            layerX[i + 1] = neuronX;
            for (int j = 0; j < layer.getNeurons().length; j++) {
                float neuronY = diagramHeight / 2; // Middle of the layer
                neuronY -= (layer.getNeurons().length - 1) * distBetweenNeurons / 2; // Move the layer up so that the
                                                                                     // first
                                                                                     // neuron is at the top
                neuronY += j * distBetweenNeurons; // Move the neuron down to the correct position

                if (neuronHeights[i + 1] == null)
                    neuronHeights[i + 1] = new ArrayList<Float>();

                neuronHeights[i + 1].add(neuronY);
            }
        }

        // Draw the output layer
        neuronX += layerWidth + padding;
        layerX[numLayers - 1] = neuronX;
        for (int i = 0; i < outputLayer.getNeurons().length; i++) {
            float neuronY = diagramHeight / 2; // Middle of the layer
            neuronY -= (outputLayer.getNeurons().length - 1) * distBetweenNeurons / 2; // Move the layer up so that the
                                                                                       // first
                                                                                       // neuron is at the top
            neuronY += i * distBetweenNeurons; // Move the neuron down to the correct position

            if (neuronHeights[numLayers - 1] == null)
                neuronHeights[numLayers - 1] = new ArrayList<Float>();

            neuronHeights[numLayers - 1].add(neuronY);
        }

        float[] brainInputs = driver.car.sensors; // The inputs to the neural network

        // sim.push();
        // sim.fill(20);
        // sim.noStroke();
        // sim.rectMode(sim.CORNER);
        // sim.rect(x, 0, diagramWidth, diagramHeight, 50);
        // sim.pop();

        // Draw the connections (weights) and neurons
        drawConnections(hiddenLayers, outputLayer, neuronHeights, x, padding, layerWidth);

        drawNeurons(brain, inputs, brainInputs, x, padding, layerWidth, neuronHeights, neuronRadius, numLayers,
                hiddenLayers,
                outputLayer);

        // for (int i = 0; i < numLayers; i++) {
        // ArrayList<Float> layer = neuronHeights[i];
        // for (int j = 0; j < layer.size(); j++) {
        // float xPos = layerX[i];
        // float yPos = layer.get(j);

        // sim.noStroke();
        // if (i == 0) {
        // sim.fill(0, 0, 255);
        // } else if (i < numLayers - 1) {
        // sim.fill(0, 255, 0);
        // } else {
        // sim.fill(255, 0, 0);
        // }
        // sim.ellipse(xPos, yPos, neuronRadius, neuronRadius);
        // }
        // }

        pop();
    }

    private void drawConnections(Layer[] hiddenLayers, Layer outputLayer, ArrayList<Float>[] neuronHeights, float x,
            float padding, float layerWidth) {
        for (int i = 1; i < neuronHeights.length - 1; i++) { // Hidden layers
            ArrayList<Float> layer1 = neuronHeights[i - 1];
            ArrayList<Float> layer2 = neuronHeights[i];

            float[][] weights = hiddenLayers[i - 1].weights;

            for (int j = 0; j < layer1.size(); j++) {
                float neuron1X = x + padding + layerWidth / 2 + (i - 1) * (layerWidth + padding);
                float neuron1Y = layer1.get(j);
                for (int k = 0; k < layer2.size(); k++) {
                    float neuron2X = x + padding + layerWidth / 2 + i * (layerWidth + padding);
                    float neuron2Y = layer2.get(k);

                    float weight = weights[k][j];

                    // Blue if weight is negative
                    // Green if weight is positive
                    if (weight < 0) {
                        stroke(0, 0, 255);
                    } else {
                        stroke(0, 255, 0);
                    }
                    strokeWeight(abs(weight) * 5);
                    line(neuron1X, neuron1Y, neuron2X, neuron2Y);
                }
            }
        }

        // Output layer
        ArrayList<Float> layer1 = neuronHeights[neuronHeights.length - 2];
        ArrayList<Float> layer2 = neuronHeights[neuronHeights.length - 1];

        float[][] weights = outputLayer.weights;

        for (int j = 0; j < layer1.size(); j++) {
            for (int k = 0; k < layer2.size(); k++) {
                float neuron1X = x + padding + layerWidth / 2 + (neuronHeights.length - 2) * (layerWidth + padding);
                float neuron1Y = layer1.get(j);
                float neuron2X = x + padding + layerWidth / 2 + (neuronHeights.length - 1) * (layerWidth + padding);
                float neuron2Y = layer2.get(k);

                float weight = weights[k][j];

                // Blue if weight is negative
                // Green if weight is positive
                if (weight < 0) {
                    stroke(0, 0, 255);
                } else {
                    stroke(0, 255, 0);
                }
                strokeWeight(abs(weight) * 5);
                line(neuron1X, neuron1Y, neuron2X, neuron2Y);
            }
        }
    }

    private void drawNeurons(NeuralNetwork brain, float[] inputs, float[] brainInputs, float x, float padding,
            float layerWidth,
            ArrayList<Float>[] neuronHeights, float neuronRadius, int numLayers, Layer[] hiddenLayers,
            Layer outputLayer) {
        // Input layer
        float[] inputStrengths = new float[inputs.length];
        float maxInput = 0;
        for (int i = 0; i < inputs.length; i++) {
            maxInput = max(maxInput, brainInputs[i]);
        }
        if (Settings.scaleDownInputsInDiagram) {
            for (int i = 0; i < inputs.length; i++) {
                inputStrengths[i] = brainInputs[i] / maxInput; // Scale the inputs to be between 0 and 1
            }
        } else {
            for (int i = 0; i < inputs.length; i++) {
                inputStrengths[i] = brainInputs[i];
            }
        }

        for (int i = 0; i < inputs.length; i++) {
            float xPos = x + padding + layerWidth / 2;
            float yPos = neuronHeights[0].get(i);

            fill(255 * inputStrengths[i]);
            noStroke();
            circle(xPos, yPos, neuronRadius);
        }

        // Hidden layers
        for (int i = 0; i < hiddenLayers.length; i++) {
            Layer layer = hiddenLayers[i];
            float[] outputs = layer.feedForward(inputStrengths);
            for (int j = 0; j < outputs.length; j++) {
                float xPos = x + padding + layerWidth / 2 + (i + 1) * (layerWidth + padding);
                float yPos = neuronHeights[i + 1].get(j);

                fill(255 * outputs[j]);
                noStroke();
                circle(xPos, yPos, neuronRadius);
            }
            inputStrengths = outputs;
        }

        // Output layer
        float[] outputs = brain.feedForward(brainInputs);
        // float[] outputs = outputLayer.feedForward(inputStrengths);

        if (Settings.scaleDownOutputsInDiagram) {
            int maxIndex = 0;
            float maxOutput = 0;
            for (int i = 0; i < outputs.length; i++) {
                if (outputs[i] > outputs[maxIndex]) {
                    maxIndex = i;
                    maxOutput = outputs[i];
                }
            }
            // Scale outputs to either 0 or 1 or scale them all down
            for (int i = 0; i < outputs.length; i++) {
                // if (i == maxIndex) {
                // outputs[i] = 1;
                // } else {
                // outputs[i] = 0;
                // }

                outputs[i] /= maxOutput;
            }
        }
        for (int i = 0; i < outputs.length; i++) {
            float xPos = x + padding + layerWidth / 2 + (numLayers - 1) * (layerWidth + padding);
            float yPos = neuronHeights[numLayers - 1].get(i);

            fill(255 * outputs[i]);
            noStroke();
            circle(xPos, yPos, neuronRadius);
        }
    }
}

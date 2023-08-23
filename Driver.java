import java.util.ArrayList;
import library.core.*;

public class Driver extends PComponent {

    Simulation sim;

    NeuralNetwork brain;
    Car car;

    public float fitness = 0;

    boolean dead = false;

    ArrayList<PVector> posHistory;
    ArrayList<Float> rotationHistory;

    int completedLaps = 0;
    int previousIndex = 0;
    float lastLap;

    public Driver(Simulation sim, int... layerSizes) {
        this.sim = sim;

        brain = new NeuralNetwork(layerSizes);
        car = new Car(sim, Settings.spawnPosition.x, Settings.spawnPosition.y, Settings.carSize.x, Settings.carSize.y);

        posHistory = new ArrayList<PVector>();
        rotationHistory = new ArrayList<Float>();

        lastLap = millis();
    }

    public void load() {
        brain.load();
    }

    public void draw() {
        car.draw();
    }

    public void update() {
        car.update();
        car.calculateSensors();

        posHistory.add(car.pos.copy());
        rotationHistory.add(car.vel.heading() + HALF_PI);

        float[] inputs = car.sensors;

        if (Settings.scaleDownInputs) {
            float maxInput = 0;
            for (int i = 0; i < inputs.length; i++) {
                maxInput = max(maxInput, inputs[i]);
            }

            for (int i = 0; i < inputs.length; i++) {
                inputs[i] /= maxInput;
            }
        }

        float[] outputs = brain.feedForward(inputs);

        int highestIndex = 0;
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] > outputs[highestIndex]) {
                highestIndex = i;
            }
        }
        if (highestIndex == 0)
            car.turnLeft();
        else if (highestIndex == 1)
            car.turnRight();
        // else do nothing

        if (crashed()) {
            dead = true;
            return;
        }
        // Find index of closest path PVector in track
        int index = previousIndex;
        float minDist = Float.MAX_VALUE; // github copilot
        for (int i = previousIndex; i < sim.mainScene.track.path.size(); i++) {
            float dist = PVector.dist(car.pos, sim.mainScene.track.path.get(i));
            if (dist < minDist) {
                minDist = dist;
                index = i;
            }
        }
        // Smaller the distance, the better (distances increase as index increases)
        float dist = sim.mainScene.track.pathDistances.get(index);

        // How this works makes it so that when it loops it will drop down in fitness a
        // lot for a second
        fitness = dist * Settings.fitnessIncrease;
        fitness += completedLaps * sim.mainScene.track.totalDistance * Settings.fitnessIncrease;

        if (PVector.dist(car.pos, sim.mainScene.track.path.get(0)) < 50 && millis() - lastLap > 3000) {
            completedLaps++;
            previousIndex = 0;
            index = 0;
            lastLap = millis();
        }

        previousIndex = index;
        if (previousIndex == sim.mainScene.track.path.size() - 1) {
            previousIndex = 0;
        }
    }

    public boolean crashed() {
        for (Wall wall : sim.mainScene.track.walls) {
            if (wall.intersects(car)) {
                return true;
            }
        }
        return false;
    }

    public void learn(NeuralNetwork other) {
        brain = brain.crossover(other);
        brain.mutate(sim.mainScene.learningRate);
    }

    public void mutate() {
        brain.mutate(sim.mainScene.learningRate);
    }

    public Driver clone() {
        Driver clone = new Driver(sim, brain.inputLayer.length, brain.hiddenLayers.length, brain.outputLayer.size);
        clone.brain = brain.clone();
        return clone;
    }

}

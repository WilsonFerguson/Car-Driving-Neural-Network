import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import library.core.*;

public class MainScene extends Scene {

    Track track;
    ArrayList<Driver> drivers;

    ArrayList<Driver> previousGeneration; // Previous generation of drivers

    int generation = 1;
    int maxFitness = 0;

    float learningRate = Settings.initLearningRate;

    // Stagnant -> spike learning rate (only happens when fitness<learningRateDecay)
    int learningRateSpikeThreshold = 5; // If fitness has been stagnant for this many generations, increase the learning
                                        // rate
    float changedNeeded = 50; // If all the past fitnesses are in this range, we need to spike the learning
                              // rate
    float learningRateSpike = 0.013f; // Increase the learning rate by this much

    float currentFitness = 0;
    int currentDrivers = Settings.numDrivers;
    int completedLaps = 0;
    int maxCompletedLaps = 0;

    // Ghost of the best driver
    ArrayList<PVector> ghostPositions;
    ArrayList<Float> ghostRotations;
    int index = 0;

    Graph fitnessGraph;

    float speed; // How fast the fastest driver is going

    int[] brain = { Settings.numSensors, 7, 5, 3 }; // 7, 7, 5, 3

    // Buttons
    SceneButton informationButton;
    SceneButton trackEditorButton;

    public MainScene(Simulation sim, String name) {
        super(sim, name);

        track = new Track(sim);

        drivers = new ArrayList<Driver>();
        for (int i = 0; i < Settings.numDrivers; i++) {
            drivers.add(new Driver(sim, brain));
            if (Settings.loadExpert)
                drivers.get(i).load();
        }

        previousGeneration = new ArrayList<Driver>();

        ghostPositions = new ArrayList<PVector>();
        ghostRotations = new ArrayList<Float>();

        fitnessGraph = new Graph(sim, new PVector(18, 822.0f), new PVector(457.5f, 298.5f));

        trackEditorButton = new SceneButton(sim,
                new PVector(width - Settings.navButtonSize.x - 5, height - Settings.navButtonSize.y - 5),
                Settings.navButtonSize,
                "Track");
        informationButton = new SceneButton(sim,
                new PVector(width - Settings.navButtonSize.x * 2 - 10, height - Settings.navButtonSize.y - 5),
                Settings.navButtonSize,
                "Info");

        trackEditorButton.setBackground(200, 0, 0);
        informationButton.setBackground(0, 200, 0);
    }

    public void draw() {
        if (active) {
            background(0);

            // soooooooo slow
            // loadPixels();
            // for (int i = 0; i < pixels.length; i++) {
            // pixels[i] = pixels[i];
            // }
            // updatePixels();

            if (Settings.showInformation) {
                fill(255);
                textSize(50);
                text("Generation #" + generation, 10, 30);
                text("Max Fitness: " + (int) maxFitness, 10, 80);
                text("Current Fitness: " + (int) currentFitness, 10, 130);
                text("Current Drivers: " + currentDrivers, 10, 180);
                text("Completed Laps: " + completedLaps, 10, 230);
            }

            if (Settings.showRunTime) {
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

                fill(255);
                textSize(50);
                text(time, 10, height - 50);

                track.draw();
            }

            // Buttons
            trackEditorButton.draw();
            informationButton.draw();
        }

        speed = 0; // Reset the speed
        boolean allDead = true;
        currentDrivers = 0;
        currentFitness = 0;
        for (int i = drivers.size() - 1; i >= 0; i--) {
            Driver driver = drivers.get(i);

            if (driver.dead)
                continue;

            driver.update();
            if (active)
                driver.draw();

            currentFitness = max(currentFitness, driver.fitness);
            completedLaps = max(completedLaps, driver.completedLaps);
            speed = max(speed, driver.car.speed);

            currentDrivers++;
            allDead = false; // At least one driver is alive
        }

        // Show ghost
        if (ghostPositions.size() > 0 && active) {
            push();
            if (index < ghostPositions.size()) {
                translate(ghostPositions.get(index).x + Settings.carSize.x / 2,
                        ghostPositions.get(index).y + Settings.carSize.y / 2);
                rotate(ghostRotations.get(index));
                fill(0, 0, 255, 100);
                rect(0, 0, Settings.carSize.x, Settings.carSize.y);
            }
            pop();

        }
        index++;

        if (allDead) {
            // All drivers are dead, create a new generation
            nextGeneration();
        }
    }

    public void nextGeneration() {
        generation++;
        currentFitness = 0;

        maxCompletedLaps = max(maxCompletedLaps, completedLaps);
        completedLaps = 0;

        index = 0;

        // Sort drivers by their fitness score
        Collections.sort(drivers, new Comparator<Driver>() {
            @Override
            public int compare(Driver d1, Driver d2) {
                return (int) d2.fitness - (int) d1.fitness;
            }
        });

        float latestFitness = drivers.get(0).fitness; // Best fitness of this generation
        latestFitness = (float) (Math.round(latestFitness * 100.0) / 100.0);

        // Save best driver's brain
        if (latestFitness > maxFitness && (!Settings.loadExpert || Settings.shouldUpdateExpert))
            drivers.get(0).brain.save();

        maxFitness = (int) max(maxFitness, latestFitness); // Update max fitness

        fitnessGraph.addValue((int) latestFitness); // Add latest fitness to graph
        fitnessGraph.addNeuralNetwork(drivers.get(0).brain); // Add latest brain to graph

        // Set previous generation to this generation
        previousGeneration.clear();
        for (Driver driver : drivers)
            previousGeneration.add(driver);

        // Update learning rate
        if (latestFitness < Settings.learningRateDecay) {
            learningRate = map(latestFitness, 0, Settings.learningRateDecay,
                    Settings.initLearningRate, Settings.endLearningRate);

            // Spike the learning rate if it has been stagnant for a while
            if (fitnessGraph.yValues.size() > learningRateSpikeThreshold) {
                boolean shouldSpike = true;
                for (int i = fitnessGraph.yValues.size() - 1; i >= fitnessGraph.yValues.size()
                        - learningRateSpikeThreshold; i--) {
                    if (abs(fitnessGraph.yValues.get(i) - fitnessGraph.yValues.get(i - 1)) > changedNeeded) {
                        shouldSpike = false;
                        break;
                    }
                }
                if (shouldSpike)
                    learningRate += learningRateSpike;
            }
        } else {
            learningRate = Settings.endLearningRate;
        }

        // Save ghost of best driver
        if (latestFitness >= maxFitness) {
            ghostPositions = drivers.get(0).posHistory;
            ghostRotations = drivers.get(0).rotationHistory;
        }

        // Scale fitnesses to be between 0 and 1
        float totalFitness = 0;
        for (Driver driver : drivers) {
            totalFitness += driver.fitness;
        }

        for (Driver driver : drivers) {
            driver.fitness /= totalFitness;
        }

        // Create a new generation of drivers
        ArrayList<Driver> newDrivers = new ArrayList<Driver>();
        for (int i = 0; i < Settings.numDrivers; i++) {
            if (Settings.crossover) {

                Driver parent1 = selectParent();
                Driver parent2 = selectParent();

                Driver child = parent1.clone();

                if (Settings.shouldLearn)
                    child.learn(parent2.brain);

                newDrivers.add(child);

            } else {
                // Select a parent based on fitness
                Driver parent = selectParent();

                // Create a child based on the parent
                Driver child = parent.clone();

                // Mutate the child
                if (Settings.shouldLearn) // Don't mutate the expert
                    child.mutate();

                // Add the child to the new generation
                newDrivers.add(child);
            }
        }

        // Replace the old generation with the new generation
        for (int i = 0; i < Settings.numDrivers; i++) {
            drivers.set(i, newDrivers.get(i));
        }
    }

    public Driver selectParent() {
        // Select a random number between 0 and 1
        float r = random();

        // Iterate through all drivers
        for (Driver driver : drivers) {
            // Subtract the driver's fitness from the random number
            r -= driver.fitness;

            // If the random number is less than 0, return the driver
            if (r < 0) {
                return driver;
            }
        }

        // If no driver was selected, return the last driver
        return drivers.get(drivers.size() - 1);
    }

    public void Reset() {
        drivers.clear();
        previousGeneration.clear();
        ghostPositions.clear();
        ghostRotations.clear();
        fitnessGraph = new Graph(sim, new PVector(18, 822.0f), new PVector(457.5f, 298.5f));

        generation = 0;
        maxFitness = 0;
        learningRate = Settings.initLearningRate;
        currentFitness = 0;
        maxCompletedLaps = 0;
        speed = 0;
        index = 0;

        for (int i = 0; i < Settings.numDrivers; i++) {
            drivers.add(new Driver(sim, brain));
            if (Settings.loadExpert)
                drivers.get(i).load();
        }
    }

    public void keyPressed() {
        if (key == 's') {
            saveBrain();
            println("Saved brain");
        }
    }

    public void saveBrain() {
        int highestFitness = 0;
        NeuralNetwork bestBrain = null;
        for (Driver driver : drivers) {
            if (driver.fitness > highestFitness) {
                highestFitness = (int) driver.fitness;
                bestBrain = driver.brain;
            }
        }
        bestBrain.save();
    }
}
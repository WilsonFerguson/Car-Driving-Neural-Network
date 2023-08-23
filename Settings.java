import library.core.*;

class Settings {

    // Expert
    public static boolean loadExpert = false;
    public static boolean shouldLearn = true; // Turn this off if you don't want them to mutate
    public static boolean shouldUpdateExpert = false; // If we have loaded the expert, should it overwrite the save

    // Drivers
    public static int numDrivers = 100; // 100
    public static PVector carSize = new PVector(20, 50);
    public static PVector spawnPosition = new PVector(240.5, 511.5);

    // Sensors
    public static float sensorAngle = 40; // 48
    public static float sensorLength = 1000; // 1000
    public static int numSensors = 7; // 3, 5, or 7

    // Physics
    public static float maxSpeed = 3;
    public static float accelerationSpeed = 0.1f;
    public static float resistance = 0.99f;
    public static float turningResistance = 0.99f; // Resistance when turning
    public static float turnSpeed = 0.04f;

    // Learning Rate
    public static float initLearningRate = 0.11f;
    public static float endLearningRate = 0.05f;
    public static int learningRateDecay = 22000; // Reaches end learning rate at this fitness level (5000? or 1200)

    public static float fitnessIncrease = 1f; // 9.01: Multiplier for new method, adder for old method

    public static boolean crossover = false; // If false, only mutate (crossover seems to be much worse)

    // Main Scene Draw Settings
    public static boolean showInformation = false;
    public static boolean showRunTime = true;

    // Navigation buttons
    public static PVector navButtonSize = new PVector(200, 100);

    // Neural Network Settings
    public static boolean scaleDownInputs = false; // Divide the inputs by the max input to scale them down to 0-1
    public static boolean scaleDownInputsInDiagram = true; // Scale down the inputs in the diagram
    public static boolean scaleDownOutputsInDiagram = false; // Scale down the outputs in the diagram
}
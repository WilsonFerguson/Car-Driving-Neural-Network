import java.util.ArrayList;
import library.core.*;

public class TrackEditorScene extends Scene {

    ArrayList<PVector> leftPoints;
    ArrayList<PVector> rightPoints;
    ArrayList<PVector> path; // Distance path

    ArrayList<Wall> leftWalls;
    ArrayList<Wall> rightWalls;

    float threshold = 15; // Distance between current and previous point
    float trackWidth = 75;

    // Buttons
    SceneButton informationButton;
    SceneButton mainButton;
    Button saveButton;
    Button downloadButton;

    boolean drawing = false; // Is the user drawing the track?

    public TrackEditorScene(Simulation sim, String name) {
        super(sim, name);

        leftPoints = new ArrayList<PVector>();
        rightPoints = new ArrayList<PVector>();
        path = new ArrayList<PVector>();

        leftWalls = new ArrayList<Wall>();
        rightWalls = new ArrayList<Wall>();

        mainButton = new SceneButton(sim,
                new PVector(width - Settings.navButtonSize.x - 5, height - Settings.navButtonSize.y - 5),
                Settings.navButtonSize,
                "Main");
        informationButton = new SceneButton(sim,
                new PVector(width - Settings.navButtonSize.x * 2 - 10, height - Settings.navButtonSize.y - 5),
                Settings.navButtonSize,
                "Info");

        mainButton.setBackground(200, 0, 0);
        informationButton.setBackground(0, 200, 0);

        saveButton = new Button(new PVector(10, height - Settings.navButtonSize.y - 5), Settings.navButtonSize,
                "Save");
        downloadButton = new Button(
                new PVector(10 + Settings.navButtonSize.x + 5, height - Settings.navButtonSize.y - 5),
                PVector.add(Settings.navButtonSize, new PVector(50, 0)), "Download");

        saveButton.setBackground(0, 0, 200);
        downloadButton.setBackground(0, 0, 200);

        saveButton.setTextColor(255, 255, 255);
        downloadButton.setTextColor(255, 255, 255);
    }

    public void setup() {
        sim.switchedToTrackEditor = true;
    }

    public void draw() {
        if (!active)
            return;

        if (drawing && (mouse.x < width - Settings.navButtonSize.x * 2
                || mouse.y < height - Settings.navButtonSize.y)) {
            float dist = 0;
            if (path.size() > 0)
                dist = PVector.dist(mouse, path.get(path.size() - 1));
            else
                dist = threshold + 10;

            if (dist > threshold) {
                PVector dir = PVector.sub(mouse, pmouse);

                if (path.size() > 0)
                    dir = PVector.sub(mouse, path.get(path.size() - 1));

                dir.normalize();

                // Rotate both ways and add points
                dir.rotate(-HALF_PI);
                dir.mult(trackWidth / 2);
                PVector left = PVector.add(mouse, dir);

                dir.normalize();
                dir.rotate(PI);
                dir.mult(trackWidth / 2);
                PVector right = PVector.add(mouse, dir);

                leftPoints.add(left);
                rightPoints.add(right);
                path.add(mouse.copy());

                if (leftPoints.size() > 2) {
                    Wall leftWall = new Wall(leftPoints.get(leftPoints.size() - 2),
                            leftPoints.get(leftPoints.size() - 1));
                    Wall rightWall = new Wall(rightPoints.get(rightPoints.size() - 2),
                            rightPoints.get(rightPoints.size() - 1));

                    leftWalls.add(leftWall);
                    rightWalls.add(rightWall);

                    leftWall.draw();
                    rightWall.draw();
                }
            }
        }

        // Buttons
        mainButton.draw();
        informationButton.draw();

        saveButton.draw();
        if (saveButton.mouseDown) {
            updateTrack();
            sim.mainScene.Reset();
        }

        downloadButton.draw();
        if (downloadButton.mouseDown) {
            sim.mainScene.track.saveTrack();
        }
    }

    public void updateTrack() {
        Track track = sim.mainScene.track;

        if (leftWalls.size() == 0)
            return;

        track.walls.clear();
        track.pathDistances.clear();
        track.path.clear();
        track.totalDistance = 0;

        for (Wall wall : leftWalls) {
            track.walls.add(wall);
        }
        Wall lastWall = new Wall(leftPoints.get(leftPoints.size() - 1), leftPoints.get(1));
        track.walls.add(lastWall);
        leftWalls.add(lastWall);

        for (Wall wall : rightWalls) {
            track.walls.add(wall);
        }
        lastWall = new Wall(rightPoints.get(rightPoints.size() - 1), rightPoints.get(1));
        track.walls.add(lastWall);
        rightWalls.add(lastWall);

        // Path
        for (int i = 0; i < path.size(); i++) {
            track.path.add(path.get(i));
        }
        track.calculatePathDistances();
    }

    public void Reset() {
        leftPoints.clear();
        rightPoints.clear();
        path.clear();

        leftWalls.clear();
        rightWalls.clear();

        // Drawing
        background(0);

        // draw Tracks/track.png

        // Draw car spawn location
        fill(0, 150, 0);
        rectMode(CENTER);
        noStroke();
        rect(Settings.spawnPosition, Settings.carSize);
    }

    public void keyReleased() {
        if (keyString.equals("Space")) {
            drawing = !drawing;
        }
    }
}

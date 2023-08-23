import java.util.ArrayList;
import library.core.*;

public class Track extends PComponent {

    Simulation sim;

    ArrayList<Wall> walls;
    ArrayList<PVector> path; // The path the car should follow
    ArrayList<Float> pathDistances; // The distance from the point of each path to the end
    float totalDistance; // The total distance of the path

    public Track(Simulation sim) {
        this.sim = sim;

        walls = new ArrayList<Wall>();
        path = new ArrayList<PVector>();
        pathDistances = new ArrayList<Float>();

        loadTrack();
        calculatePathDistances();
    }

    public void draw() {
        for (Wall wall : walls) {
            wall.draw();
        }

        // Draw finish line
        push();
        stroke(0, 150, 0);
        strokeWeight(5);
        line(walls.get(0).start, walls.get(walls.size() - 1).end);
        pop();
        // have each distance PVector have walls associated with it that should be
        // checked
        // by the cars (forward sensors ig should always check them all)
    }

    public void calculatePathDistances() {
        float dist = 0;
        for (int i = path.size() - 1; i >= 0; i--) {
            if (i == path.size() - 1) {
                pathDistances.add(0f);
            } else {
                dist += path.get(i).dist(path.get(i + 1));
                pathDistances.add(dist);
            }
        }
        totalDistance = dist;
    }

    public void savePath() {
        String[] stringArray = new String[path.size()];
        for (int i = 0; i < path.size(); i++) {
            stringArray[i] = path.get(i).toString(); // (35.0f, 35.0f)
        }
        saveStrings(stringArray, "Tracks/path.txt");
    }

    public void saveWalls() {
        String[] stringArray = new String[walls.size()];
        for (int i = 0; i < walls.size(); i++) {
            stringArray[i] = walls.get(i).toString(); // (x1, y1, x2, y2)
        }
        saveStrings(stringArray, "Tracks/walls.txt");
    }

    public void loadPath() {
        String[] stringArray = loadStrings("Tracks/path.txt");
        for (int i = 0; i < stringArray.length; i++) {
            String[] split = split(stringArray[i], ',');
            float x = Float.parseFloat(split[0].substring(1));
            float y = Float.parseFloat(split[1].substring(0, split[1].length() - 1));
            path.add(new PVector(x, y));
        }
    }

    public void loadWalls() {
        String[] stringArray = loadStrings("Tracks/walls.txt");
        for (int i = 0; i < stringArray.length; i++) {
            String[] split = split(stringArray[i], ',');
            float x1 = Float.parseFloat(split[0].substring(1));
            float y1 = Float.parseFloat(split[1]);
            float x2 = Float.parseFloat(split[2]);
            float y2 = Float.parseFloat(split[3].substring(0, split[3].length() - 1));
            walls.add(new Wall(x1, y1, x2, y2));
        }
    }

    public void saveTrack() {
        savePath();
        saveWalls();
    }

    public void loadTrack() {
        loadPath();
        loadWalls();
    }

}

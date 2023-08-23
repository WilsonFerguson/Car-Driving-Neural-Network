import java.util.ArrayList;
import library.core.*;

public class Graph extends PComponent {

    Simulation sim;

    private PVector pos;
    private PVector size;

    ArrayList<Integer> yValues;
    float minYValue = -1;
    float maxYValue = 1;

    ArrayList<NeuralNetwork> neuralNetworks; // Best neural network at each generation

    public Graph(Simulation sim, PVector pos, PVector size) {
        this.sim = sim;
        this.pos = pos;
        this.size = size;

        // no idea where i learned arraylists from
        yValues = new ArrayList<Integer>();
        neuralNetworks = new ArrayList<NeuralNetwork>();
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public void setSize(PVector size) {
        this.size = size;
    }

    public void addValue(int value) {
        yValues.add(value);
        if (minYValue == -1 || value < minYValue) {
            minYValue = value;
        }
        if (maxYValue == 1 || value > maxYValue) {
            maxYValue = value;
        }
    }

    public void addNeuralNetwork(NeuralNetwork neuralNetwork) {
        neuralNetworks.add(neuralNetwork);
    }

    public void draw() {
        push();
        stroke(255);
        strokeWeight(3);

        for (int i = 0; i < yValues.size() - 1; i++) {
            float x = map(i, 0, yValues.size() - 1, pos.x, pos.x + size.x);
            float y = map(yValues.get(i), minYValue, maxYValue, pos.y + size.y,
                    pos.y);
            float x2 = map(i + 1, 0, yValues.size() - 1, pos.x, pos.x + size.x);
            float y2 = map(yValues.get(i + 1), minYValue, maxYValue, pos.y + size.y,
                    pos.y);

            if (yValues.get(i) < yValues.get(i + 1)) {
                stroke(0, 255, 0);
            } else if (yValues.get(i) > yValues.get(i + 1)) {
                stroke(255, 0, 0);
            } else {
                stroke(255);
            }
            line(x, y, x2, y2);
        }
        if (yValues.size() > 0) {
            textSize(30);
            fill(255);
            textAlign(LEFT);
            float y = map(yValues.get(yValues.size() - 1), minYValue, maxYValue,
                    pos.y + size.y, pos.y);
            text(yValues.get(yValues.size() - 1), pos.x + size.x + 10, y - 15);
        }

        pop();

        // Show PVector and fitness at mouse position
        int mouseIndex = getIndexFromMouse();
        if (mouseIndex != -1) {
            float x = map(mouseIndex, 0, yValues.size() - 1, pos.x, pos.x + size.x);
            float y = map(yValues.get(mouseIndex), minYValue, maxYValue, pos.y + size.y, pos.y);

            push();
            textSize(30);
            fill(255);
            textAlign(LEFT);
            text(yValues.get(mouseIndex), x + 10, y - 15);
            noStroke();
            fill(255);
            ellipseMode(CENTER);
            circle(x, y, 10);
            pop();
        }
    }

    private int getIndexFromMouse() {
        if (yValues.size() > 0 && mouseX > pos.x && mouseX < pos.x + size.x && mouseY > pos.y
                && mouseY < pos.y + size.y) {
            int index = (int) map(mouseX, pos.x, pos.x + size.x, 0, yValues.size());
            return index;
        }
        return -1;
    }

    public NeuralNetwork getNeuralNetworkFromMouse() {
        int index = getIndexFromMouse();
        if (index != -1) {
            return neuralNetworks.get(index);
        }
        return null;
    }

    public Graph copy() {
        Graph g = new Graph(sim, pos.copy(), size.copy());
        g.yValues = (ArrayList<Integer>) yValues.clone();
        g.minYValue = minYValue;
        g.maxYValue = maxYValue;
        return g;
    }

}
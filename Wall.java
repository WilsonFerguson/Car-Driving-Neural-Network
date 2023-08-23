import library.core.*;

public class Wall extends PComponent {

    PVector start;
    PVector end;

    public Wall(PVector start, PVector end) {
        this.start = start.copy();
        this.end = end.copy();
    }

    public Wall(float x1, float y1, float x2, float y2) {
        this.start = new PVector(x1, y1);
        this.end = new PVector(x2, y2);
    }

    public void draw() {
        stroke(255);
        strokeWeight(2);
        line(start.x, start.y, end.x, end.y);
    }

    // https://stackoverflow.com/questions/20677795/how-do-i-compute-the-intersection-PVector-of-two-lines
    public PVector intersects(PVector start, PVector end) {
        float x1 = this.start.x;
        float y1 = this.start.y;
        float x2 = this.end.x;
        float y2 = this.end.y;
        float x3 = start.x;
        float y3 = start.y;
        float x4 = end.x;
        float y4 = end.y;

        // push();
        // stroke(0, 0, 255);
        // strokeWeight(2);
        // line(start, end);
        // pop();

        float x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
                / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        float y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
                / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

        if (x < Math.min(x1, x2) || x > Math.max(x1, x2) || x < Math.min(x3, x4) || x > Math.max(x3, x4)) {
            return null;
        }

        if (y < Math.min(y1, y2) || y > Math.max(y1, y2) || y < Math.min(y3, y4) || y > Math.max(y3, y4)) {
            return null;
        }

        return new PVector(x, y);
    }

    public boolean intersects(Car car) {
        PVector[] corners = car.getCorners();
        for (int i = 0; i < corners.length; i++) {
            PVector corner = corners[i];
            PVector nextCorner = corners[(i + 1) % corners.length];
            PVector intersection = intersects(corner, nextCorner);
            if (intersection != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * (x1, y1, x2, y2)
     */
    public String toString() {
        return "(" + start.x + ", " + start.y + ", " + end.x + ", " + end.y + ")";
    }

}
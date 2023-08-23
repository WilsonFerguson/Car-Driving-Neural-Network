import library.core.*;

public class Car extends PComponent {

    Simulation sim;

    PVector pos, vel, acc;
    PVector dim;

    public float[] sensors;

    float speed;

    boolean turning;

    public Car(Simulation sim, float x, float y, float w, float h) {
        this.sim = sim;

        this.pos = new PVector(x, y);
        this.dim = new PVector(w, h);
        this.vel = new PVector(0, -1);
        this.acc = PVector.zero();

        // idk where i learned this coding train?
        sensors = new float[Settings.numSensors];
    }

    public void draw() {

        push();
        fill(255, 0, 0);
        translate(pos.x + dim.x / 2, pos.y + dim.y / 2);
        rotate(vel.heading() + HALF_PI);
        rect(0, 0, dim.x, dim.y);
        pop();
    }

    public void update() {
        acc = vel.copy();
        acc.normalize();
        acc.mult(Settings.accelerationSpeed);

        vel.add(acc);
        vel.limit(Settings.maxSpeed);
        vel.mult(Settings.resistance);
        if (turning)
            vel.mult(Settings.turningResistance);

        pos.add(vel);

        acc.mult(0);

        calculateSensors();

        // Calculate speed (18 feet is length of f1 car)
        speed = vel.mag() / dim.y; // Percentage of car length every frame
        speed *= 60; // Percentage of car length every second
        speed *= 60; // Percentage of car length every minute
        speed *= 60; // Percentage of car length every hour
        speed *= 18; // Feet per hour
        speed *= 0.000189394; // Miles per hour (github copilot found this)

        turning = false;
    }

    public void calculateSensors() {
        sensors = new float[Settings.numSensors];

        PVector frontCenter = PVector.add(getCorners()[0], getCorners()[1]);
        frontCenter.div(2);

        if (Settings.numSensors == 7) {
            // Left
            sensors[0] = createSensor(-HALF_PI, frontCenter);
            sensors[1] = createSensor(-Settings.sensorAngle, frontCenter);
            sensors[2] = createSensor(-Settings.sensorAngle / 3, frontCenter);
            // Front
            sensors[3] = createSensor(-PI, frontCenter); // PI to cancel out the rotation
            // Right
            sensors[4] = createSensor(Settings.sensorAngle / 3, frontCenter);
            sensors[5] = createSensor(Settings.sensorAngle, frontCenter);
            sensors[6] = createSensor(HALF_PI, frontCenter);
        }
        if (Settings.numSensors == 5) {
            // Left
            sensors[0] = createSensor(-Settings.sensorAngle, frontCenter);
            sensors[1] = createSensor(-HALF_PI, frontCenter);
            // Front
            sensors[2] = createSensor(-PI, frontCenter); // PI to cancel out the rotation
            // Right
            sensors[3] = createSensor(HALF_PI, frontCenter);
            sensors[4] = createSensor(Settings.sensorAngle, frontCenter);
        }

        if (Settings.numSensors == 3) {
            // Left
            sensors[0] = createSensor(-Settings.sensorAngle, frontCenter);
            // Front
            sensors[1] = createSensor(-PI, frontCenter); // PI to cancel out the rotation
            // Right
            sensors[2] = createSensor(Settings.sensorAngle, frontCenter);
        }

    }

    public float createSensor(float angle, PVector center) {
        PVector start = vel.copy();
        start.normalize();
        start.rotate(angle + PI);
        start.add(center);
        PVector end = PVector.mult(PVector.sub(start, center), Settings.sensorLength);
        end.add(center);
        return calculateSensor(start.copy(), end.copy());
    }

    public float calculateSensor(PVector start, PVector end) {
        float sensor = 0;
        for (int i = 0; i < sim.mainScene.track.walls.size(); i++) {
            PVector intersection = sim.mainScene.track.walls.get(i).intersects(start, end);
            if (intersection != null) {
                if (sensor == 0 || sensor > PVector.dist(pos, intersection)) {
                    sensor = PVector.dist(pos, intersection);
                }
            }
        }
        return sensor;
    }

    public void applyForce(PVector force) {
        acc.add(force);
    }

    public void turnLeft() {
        vel.rotate(-Settings.turnSpeed);
        turning = true;
    }

    public void turnRight() {
        vel.rotate(Settings.turnSpeed);
        turning = true;
    }

    public PVector getCorner(PVector original, float rotation) {
        PVector corner = original.copy();

        float mag = corner.mag();

        corner.normalize();
        corner.rotate(rotation);

        corner.mult(mag);
        corner.add(pos);
        corner.add(dim.x / 2, dim.y / 2);
        return corner;
    }

    public PVector[] getCorners() {
        float heading = vel.heading() + HALF_PI;

        PVector[] corners = new PVector[4];
        corners[0] = getCorner(new PVector(-dim.x / 2, -dim.y / 2), heading);
        corners[1] = getCorner(new PVector(dim.x / 2, -dim.y / 2), heading);
        corners[2] = getCorner(new PVector(dim.x / 2, dim.y / 2), heading);
        corners[3] = getCorner(new PVector(-dim.x / 2, dim.y / 2), heading);

        return corners;
    }

    public void drawCorners() {
        PVector[] corners = getCorners();
        for (int i = 0; i < corners.length; i++) {
            fill(0, 255, 0);
            ellipseMode(CENTER);
            ellipse(corners[i].x, corners[i].y, 10, 10);
        }
    }

}

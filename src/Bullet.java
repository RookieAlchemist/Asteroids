// Bullet.java
import java.awt.*;

/**
 * Class for dealing with Bullets. Bullets have an x, y location, velocity and
 * move with slight acceleration. Like regular bullets, if they hit something,
 * they destroy it. Also like regular bullets, they do not travel forever, this
 * is accomplished with the maximum distance variable, and distance methods. The
 * difference between this class and all the others is that the code for
 * wrapping around the screen is in a different method, rather than inside the
 * move method. This is because bullets from aliens should not wrap around the
 * screen, that would make defending against them too hard and not fun.
 * 
 * @author Abdullah
 */
public class Bullet {
    private double x, y;
    private double velocity;
    private double angle;
    private double acceleration = 0.2;
    private static final double MAX_DISTANCE = Tools.distance(0, 0, 0.5 * Panel.WIDTH, 0.5 * Panel.HEIGHT); // The maximum distance a bullet can travel.
    private final int SIZE = 4;
    private double og_x, og_y; // Used to calculate how far the bullet has travelled.
    private double distance_travelled = 0;

    // Simple constructor.
    public Bullet(double x, double y, double velocity, double angle) {
        this.x = x;
        this.y = y;
        this.velocity = velocity + 15;
        this.angle = angle;
        og_x = x;
        og_y = y;
    }

    // Alternate constructor that allows for passing in a Point instead of the
    // components of the coordinate separately.
    public Bullet(Point position, double velocity, double angle) {
        this(position.getX(), position.getY(), velocity, angle);
    }

    // Moves the bullet.
    public void move() {
        velocity += (acceleration);
        x += velocity * Tools.cos(angle);
        y -= velocity * Tools.sin(angle);
    }

    // Reorients the bullet if it has travelled outside of the screen bounds.
    public void wrapAround() {
        if (x < -1)
            translate(Panel.WIDTH + 2, 0);
        if (x > Panel.WIDTH + 1)
            translate(-(Panel.WIDTH + 2), 0);
        if (y < -1)
            translate(0, Panel.HEIGHT + 2);
        if (y > Panel.HEIGHT + 1)
            translate(0, -(Panel.HEIGHT + 2));
    }

    // Translates the x and the y coordinate.
    private void translate(int dx, int dy) {
        distance_travelled += Tools.distance(x, y, og_x, og_y);
        x += dx;
        y += dy;
        og_x = x;
        og_y = y;
    }

    // Getters.
    public double x() { return x; }
    public double y() { return y; }

    // Calculates how far the bullet has travelled.
    public double distance_travelled() {
        return Tools.distance(x, y, og_x, og_y) + distance_travelled;
    }

    // Checks if the bullet has travlled further than it is supposed to.
    public boolean too_far() {
        return distance_travelled() > MAX_DISTANCE;
    }

    // Draws the bullet.
    public void draw(Graphics g) {
        g.fillOval(Tools.integer(x - SIZE / 2), Tools.integer(y - SIZE / 2), SIZE, SIZE);
    }

}
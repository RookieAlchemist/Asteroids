// Explosion.java
import java.awt.*;

/**
 * A simple class for an explosion animation. Each explosion consists of random
 * particles that move away from an initial point, meaning the only information
 * stored for each piece is the angle is shares with the site of destruction.
 * Explosions have a size field, unlike the other classes, this class' size just
 * denotes the grandeur of the explosion, i.e., how many particles it has and
 * how long it takes to fizzle out.
 */
public class Explosion {
    private double x, y;
    private int size;
    private int[] pieces;
    public double time = 0;

    // Simple constructor.
    public Explosion(double x, double y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        pieces = new int[size];
        for (int i = 0; i < size; i++) {
            pieces[i] = Tools.random_int(360);
        }
    }

    // "Moves" the explosion, but really just forwards the time axis.
    public void move() {
        time++;
    }

    // Determines the explosion has fizzled out.
    public boolean fizzled_out() {
        return time > size * 3;
    }

    // Draws the explosion.
    public void draw(Graphics g) {
        for (int angle : pieces) {
            g.drawOval(
                    Tools.integer(x - time * Tools.cos(angle)),
                    Tools.integer(y - time * Tools.sin(angle)),
                    2,
                    2);
        }
    }
}

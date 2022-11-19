// SpaceRock.java
import java.awt.*;
import java.util.ArrayList;

/**
 * A class for Asteroids. To avoid having a file called "Asteroid.java" in the
 * same project as "Asteroids.java", I changed the class name to SpaceRock,
 * because just "Rock" is excessively uncool. Asteroids have type, they can
 * either be large, medium or small. This is accomplished with a variable
 * {@code type}, stored as an integer. If a large or medium asteroid is
 * destroyed, it splits into two smaller asteroids. The body of the asteroid is
 * a polygon, with its points being stored as integer arrays holding its modulus
 * and argument; for more information, see the {@code point()} method in the
 * {@linkplain Tools} class. Asteroids have random speeds and angles.
 */
public class SpaceRock {
    private double x, y;
    public static final int small = 1, medium = 2, large = 3;
    private int type;
    private int size;
    private double speed;
    private int angle = Tools.random_int(360);
    private int num_points;
    private int[][] points;

    // Simple constructor.
    public SpaceRock(double x, double y, int type) {
        this.type = type;
        speed = Tools.random_double(4 / type, 7 / type);
        if (type == large) {
            if (Tools.chance(2)) {
                this.x = Tools.chance(2) ? 0 : Panel.WIDTH;
                this.y = Tools.random_int(Panel.HEIGHT);
            } else {
                this.x = Tools.random_int(Panel.WIDTH);
                this.y = Tools.chance(2) ? 0 : Panel.HEIGHT;
            }
        } else {
            this.x = x;
            this.y = y;
        }
        size = type * 25;

        num_points = Tools.random_int(6, 12);
        points = new int[num_points][];

        int initial_angle = 0;
        for (int i = 0; i < num_points; i++) {
            int plus_some = Tools.random_int(20, 20 + 360 / num_points);
            points[i] = new int[] {
                    Tools.integer(Tools.random_double(size * 0.8, size)),
                    Tools.random_int(initial_angle, initial_angle + plus_some) };
            initial_angle += plus_some;
        }
    }

    // Constructor for when a basic large asteroid is wanted.
    public SpaceRock() {
        this(0, 0, large);
    }

    // Getter.
    public int type() {
        return type;
    }

    // Returns the body of the asteroid.
    public Polygon body() {
        int[] x_points = new int[points.length];
        int[] y_points = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x_points[i] = Tools.integer(x + points[i][0] * Tools.cos(points[i][1] + angle));
            y_points[i] = Tools.integer(y - points[i][0] * Tools.sin(points[i][1] + angle));
        }
        return new Polygon(x_points, y_points, points.length);
    }

    // Moves the asteroid.
    public void move() {
        x += speed * Tools.cos(angle);
        y -= speed * Tools.sin(angle);
        if (x < -size)
            x += Panel.WIDTH + 2 * size;
        if (x > Panel.WIDTH + size)
            x -= Panel.WIDTH + 2 * size;
        if (y < -size)
            y += Panel.HEIGHT + 2 * size;
        if (y > Panel.HEIGHT + size)
            y -= Panel.HEIGHT + 2 * size;
    }

    // Checks collision with a bullet.
    public boolean hits(Bullet b) {
        return body().contains(b.x(), b.y());
    }

    // Splits an asteroid in two, returns an ArrayList of the new asteroids.
    public ArrayList<SpaceRock> split() {
        ArrayList<SpaceRock> list = new ArrayList<SpaceRock>();
        if (type != small) {
            list.add(new SpaceRock(x, y, type - 1));
            list.add(new SpaceRock(x, y, type - 1));
        }
        return list;
    }

    // Draws the asteroid.
    public void draw(Graphics g) {
        g.drawPolygon(body());
    }

}
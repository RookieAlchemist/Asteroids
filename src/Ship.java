// Ship.java
import java.awt.*;
import java.awt.event.*;

/**
 * The class that deals with the main player's ship. The ship's body is a
 * polygon, with the original points being stored as integer arrays of each
 * point's modulus and argument, for more information see the {@code point()}
 * method in {@linkplain Tools}. Ships have an immunity property, the handling
 * of which is mainly dealt with the Panel class. This class interacts with all
 * other physical objects, it has a method that checks if the ship is hitting
 * any other object, overloaded for bullets, aliens and asteroids.
 * 
 * @author Abdullah
 */
public class Ship {
    private double x = Panel.WIDTH / 2, y = Panel.HEIGHT / 2;
    private int angle = 90;
    private static final double acceleration = 0.7, deceleration = 0.95;
    private static final int size = 50;
    private double vX = 0, vY = 0;
    private static final int TERMINAL_VELOCITY = 30;
    private boolean isImmune = false;

    private static int[][] points = new int[][] {
            { 30, 90 - 90 }, { 15, 0 - 90 }, { 20, 315 - 90 }, { 0, 0 - 90 }, { 20, 225 - 90 }, { 15, 180 - 90 }
    };

    // Getters and Setters.
    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public boolean isImmune() {
        return isImmune;
    }

    public void makeImmune(boolean b) {
        isImmune = b;
    }

    /** Takes in the points and returns a Polygon object */
    private Polygon body() {
        int[] x_points = new int[points.length];
        int[] y_points = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            Point p = Tools.point(x, y, points[i], angle);
            x_points[i] = p.x;
            y_points[i] = p.y;
        }
        return new Polygon(x_points, y_points, points.length);
    }

    // Checks collision with an asteroid.
    public boolean hits(SpaceRock spr) {
        for (int[] point : points) {
            Polygon rock_body = spr.body();
            if (rock_body.contains(Tools.point(x, y, point, angle)))
                return true;
        }
        return false;
    }

    // Checks collision with a bullet.
    public boolean hits(Bullet b) {
        return body().contains(b.x(), b.y());
    }

    // Checks collision with an Alien.
    public boolean hits(Alien a) {
        return body().intersects(a.body());
    }

    // Shoots a bullet, returns the bullet shot.
    public Bullet shoot() {
        return new Bullet(Tools.point(x, y, points[0], angle), Math.hypot(vX, vY), angle);
    }

    // Move function called by papa move in Panel.
    public void move(boolean[] keys) {
        if (keys[KeyEvent.VK_A])
            angle += 10;
        if (keys[KeyEvent.VK_D])
            angle -= 10;

        if (keys[KeyEvent.VK_W]) {
            if (Math.hypot(vX, vY) < TERMINAL_VELOCITY) {
                vX += acceleration * Tools.cos(angle);
                vY += acceleration * Tools.sin(angle);
            }
        }
        vX *= deceleration;
        vY *= deceleration;
        x += vX;
        y -= vY;

        if (x < -size)
            x += Panel.WIDTH + 2 * size;
        if (x > Panel.WIDTH + size)
            x -= Panel.WIDTH + 2 * size;
        if (y < -size)
            y += Panel.HEIGHT + 2 * size;
        if (y > Panel.HEIGHT + size)
            y -= Panel.HEIGHT + 2 * size;
    }

    // Draws the ship.
    public void draw(Graphics g) {
        if (isImmune)
            g.fillPolygon(body());
        else
            g.drawPolygon(body());
    }
}
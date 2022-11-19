// Alien.java
import java.awt.*;
import javax.swing.ImageIcon;

/**
 * Alien's are quite similar to ships, but they are not controlled by the
 * player. They move around the screen and shoot bullets, and unlike the
 * original game, they move towards, and shoot towards, the direction of the
 * ship; AI baby. Unlike ship's, the body is given by a rectangle, rather than a
 * polygon.
 */
public class Alien {
    private static final double speed = Tools.random_int(3, 5);
    private int width, height;
    public static final int small = 0, big = 1;
    private final Image img;
    private double x, y;
    private int type;
    private double angle;
    private int img_width, img_height;

    public Rectangle body() {
        return new Rectangle(Tools.integer(x - width / 2), Tools.integer(y - height / 2), width, height);
    }

    public Alien() {
        type = Tools.random_int(small, big);
        width = type == small ? 50 : 80;
        height = type == small ? 45 : 75;
        // The reason why the following two variables are slightly bigger is to account
        // for the inaccurate approximation of the alien image to a plain rectangle.
        // Scaling it up better fills in the gaps.
        img_width = Tools.integer(width * 1.2);
        img_height = Tools.integer(height * 1.2);
        img = new ImageIcon("images/saucer.png").getImage().getScaledInstance(img_width, img_height,
                Image.SCALE_SMOOTH);

        // All the cases where the alien can spawn from.
        int locationCase = Tools.random_int(3);
        int TOP = 0, LEFT = 1, BOTTOM = 2, RIGHT = 3;
        if (locationCase == TOP) {
            y = -height;
            x = Tools.random_int(Panel.WIDTH);
        } else if (locationCase == LEFT) {
            y = Tools.random_int(Panel.HEIGHT);
            x = -width;
        } else if (locationCase == BOTTOM) {
            y = Panel.HEIGHT;
            x = Tools.random_int(Panel.WIDTH);
        } else if (locationCase == RIGHT) {
            y = Tools.random_int(Panel.HEIGHT);
            x = Panel.WIDTH;
        }
        angle = Tools.random_double(0, 360);
    }

    // Getter.
    public int type() {
        return type;
    }

    // Checks collision with a bullet.
    public boolean hits(Bullet b) {
        return body().contains(b.x(), b.y());
    }

    // Shoots a bullet.
    public Bullet shoot(double x, double y) {
        return new Bullet(this.x, this.y, speed - 10, Math.toDegrees(Math.atan2(this.y - y, x - this.x)));
    }

    // Moves the ship.
    public void move(double x, double y) {
        angle = Math.toDegrees(Math.atan2(this.y - y, x - this.x));
        this.x += speed * Tools.cos(angle);
        this.y -= speed * Tools.sin(angle);

        // Reorients the alien if it has travelled outside of the screen's bounds.
        if (x < -width)
            x += Panel.WIDTH + width;
        if (x > Panel.WIDTH)
            x -= Panel.WIDTH;
        if (y < -height)
            y += Panel.HEIGHT + height;
        if (y > Panel.HEIGHT)
            y -= Panel.HEIGHT - height;
    }

    // Draws the ship.
    public void draw(Graphics g) {
        g.drawImage(
            img,
            Tools.integer(x - img_width / 2),
            Tools.integer(y - img_height / 2),
            img_width,
            img_height,
            null);
    }

}
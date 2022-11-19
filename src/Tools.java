// Tools.java
import java.awt.Point;
import java.io.*;
import java.util.Scanner;

/**
 * Emulates a toolbox, as in it has useful tools inside.
 * 
 * @author Abdullah
 */
public class Tools {

    // Types casts a double into an integer, but does it properly.
    public static int integer(double n) {
        return (int) Math.round(n);
    }

    /**
     * In trigonometry, the cosine is a function that gives the horizontal
     * component of any point on a unit circle.
     * 
     * @param theta An angle in degrees
     * @return The cosine of theta
     */
    public static double cos(double theta) {
        return Math.cos(Math.toRadians(theta));
    }

    /**
     * The sine is a horizontal shift of the cosine, and gives the vertical
     * component of any point on a unit circle
     * 
     * @param theta
     * @return The sine of theta
     */
    public static double sin(double theta) {
        return cos(90 - theta);
    }

    // Computes the distance between two points, taking in their x and y
    // coordinates.
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    /**
     * Returns a random integer within an inclusive range.
     * 
     * @param low
     * @param high
     * @return int
     */
    public static int random_int(int low, int high) {
        return (int) (random_double(low, high));
    }

    /**
     * Returns a random integer from 0 to some upper bound.
     * 
     * @param high
     * @return int
     */
    public static int random_int(int high) {
        return random_int(0, high);
    }

    /**
     * Returns a random double within an inclusive range.
     * 
     * @param low
     * @param high
     * @return double
     */
    public static double random_double(double low, double high) {
        return Math.random() * (high - low + 1) + low;
    }

    /**
     * Returns true 1/n times, used to add some randomness or to account for simple
     * cases. For example if you want an event to occur a 25% of the time, you can
     * do {@code if (chance(4)) ... }
     */
    public static boolean chance(int probability) {
        return random_int(1, probability) == 1;
    }

    /**
     * For my own convenience, I designed the ship and asteroids off of points in
     * polar coordinates. Throughout the program, there are int[] coded to be
     * [modulus, argument]. This method takes in that int[] and converts to the
     * builtin Point class, while offsetting that value by some starting x, y and
     * angle values.
     * 
     * @param x
     * @param y
     * @param data
     * @param angle
     * @return A point with rounded x and y coordinates.
     * @see Ship
     * @see SpaceRock
     */
    public static Point point(double x, double y, int[] data, int angle) {
        return new Point(
                integer(x + data[0] * cos(data[1] + angle)),
                integer(y - data[0] * sin(data[1] + angle)));
    }

    // For when the angle is not to be taken into consideration.
    public static Point point(double x, double y, int[] data) {
        return point(x, y, data, 0);
    }

    // Gets the high score.
    public static int getHighScore() {
        try {
            Scanner input = new Scanner(new BufferedReader(new FileReader("data/highScore.txt")));
            int score = input.hasNextInt() ? input.nextInt() : 0;
            input.close();
            return score;
        } catch (IOException e) {
            throw new RuntimeException("Error getting the high score", e);
        }
    }

    // Gets the highest scorer.
    public static String getHighScorer() {
        try {
            Scanner input = new Scanner(new BufferedReader(new FileReader("data/highScorer.txt")));
            String name = input.hasNext() ? input.nextLine() : "Anonymous Player";
            input.close();
            return name;
        } catch (IOException e) {
            throw new RuntimeException("Error getting the high scorer.", e);
        }
    }

    // Updates the high score and the highest scorer.
    public static void updateHighScore(int score, String playerName) {
        try {
            if (getHighScore() < score) {
                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("data/highScore.txt")));
                output.print(score);
                output.close();
                output = new PrintWriter(new BufferedWriter(new FileWriter("data/highScorer.txt")));
                output.print(playerName == null ? "Anonymous Player" : playerName);
                output.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating the info.", e);
        }
    }

}
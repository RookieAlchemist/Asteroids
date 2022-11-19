// Asteroids.java
// Abdullah Mustafa
// Just some template code.

import javax.swing.JFrame;

public class Asteroids extends JFrame {
    Panel game = new Panel();

    public Asteroids() {
        super("Asteroids");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(game);
        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static void main(String[] p) {
        new Asteroids();
    }
}
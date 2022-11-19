// Panel.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * This class is all the game logic. The game has a home screen with some
 * information, and the game screen where you actually play Asteroids. Game
 * difficulty is determined by the level you are on. Level 1 serves as an
 * introduction and to get familiar with the game, you have a set number of
 * asteroids and no aliens. In levels 2 and over you can get additional
 * asteroids and aliens. For score, destroying asteroids and aliens increases
 * your score. The smaller the object, the more points rewarded. You also get
 * more points for farther shots. Upon playing, you input your name to see how
 * you compare against the highest scorer, stored in files in the data folder.
 * That data is retrieved upon running the game, and is updates every time you
 * lose. You play as the main Ship, move around with the W, A and D keys, and
 * shoot with the space bar. For shooting mechanisms, I have not allowed
 * continuous shooting by holding down the space bar, as that makes things
 * boring; I have set the reload speed to 200ms. You die when you hit an
 * asteroid, an alien, or an alien's bullet; if you have enough lives you may
 * re-spawn. Upon re-spawning, you become immune for a short time. Immunity is
 * partially controlled by the ship class, but also a counter in this class. You
 * are also rewarded immunity by killing an alien. Note that a key difference in
 * this version of the game is that aliens do not destroy asteroids, be it by
 * collision or by shooting them down; this is because that is lame.
 * 
 * @author Abdullah
 */
public class Panel extends JPanel implements ActionListener, KeyListener, MouseListener {
    public static final int WIDTH = 800, HEIGHT = 600; // Constants controlling screen size.
    private boolean[] keys = new boolean[KeyEvent.KEY_LAST * 5];
    Timer timer;
    private int level = 1;
    private int lives = 3;
    private int score = 0;

    private String playerName;
    private String highScorer = Tools.getHighScorer();
    private int highScore = Tools.getHighScore();

    private int shotTime = 0; // Used in congregation with the variable below to add a shooting delay.
    private static final int readyToShoot = 10;
    private boolean hasShot = false; // Prevents you from spamming the space bar.
    private int immuneCounter = 0; // Counter for immunity.

    // All the classes being stored.
    private Ship ship = new Ship();
    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    // The reason why enemy bullets are stored in different ArrayList's is because
    // they will be interacting differently than standard bullets.
    private ArrayList<Bullet> enemyBullets = new ArrayList<Bullet>();
    private ArrayList<SpaceRock> spaceRocks = new ArrayList<SpaceRock>();
    private ArrayList<Alien> aliens = new ArrayList<Alien>();
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();

    private static final int HOME = 1, GAME = 2;
    private int screen = HOME;

    // Colour, image and sound effect constants.
    private static final Image homeAnimation = new ImageIcon("images/intro.gif").getImage();
    private static final Image title = new ImageIcon("images/title.png").getImage();
    private static final SoundEffect homeMusic = new SoundEffect("sounds/magic space.wav");
    private static final SoundEffect battleMusic = new SoundEffect("sounds/Lines of Code.wav");
    public static final Color white = new Color(232, 233, 243);
    public static final Color blue = new Color(35, 211, 217);
    public static final Color red = new Color(216, 34, 107);

    // Basic Constructor.
    public Panel() {
        for (int i = 0; i < 5; i++) {
            spaceRocks.add(new SpaceRock());
        }
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        addMouseListener(this);
        timer = new Timer(20, this);
        timer.start();
    }

    // Re-spawn's the player after dying.
    private void re_spawn() {
        lives--;
        if (lives == 0) { // Resets the visuals after completely dying.
            screen = HOME;
            Tools.updateHighScore(score, playerName);
            ship = new Ship();
            ship = new Ship();
            bullets = new ArrayList<Bullet>();
            enemyBullets = new ArrayList<Bullet>();
            spaceRocks = new ArrayList<SpaceRock>();
            aliens = new ArrayList<Alien>();
            explosions = new ArrayList<Explosion>();
            for (int i = 0; i < 3; i++) {
                spaceRocks.add(new SpaceRock());
            }
            return;
        }
        immuneCounter = 100;
        ship = new Ship();
    }

    // Levels up.
    private void newLevel() {
        level++;
        bullets.clear();
        spaceRocks.clear();
        aliens.clear();
        enemyBullets.clear();
        for (int i = 0; i < 3 * level; i++) {
            spaceRocks.add(new SpaceRock());
        }
    }

    // Master move function, moves all the individual objects, and handles
    // interactions.
    private void move() {
        if (score > highScore) {
            highScore = score;
            highScorer = playerName;
        }
        if (spaceRocks.size() == 0 && aliens.size() == 0) {
            newLevel();
        }

        // Handles immunity.
        immuneCounter--;
        if (immuneCounter > 0) {
            ship.makeImmune(true);
        } else {
            ship.makeImmune(false);
            immuneCounter = 0;
        }
        ship.move(keys);

        if (level > 1 && Tools.chance(1500 / level))
            spaceRocks.add(new SpaceRock());
        if (level > 1 && Tools.chance(1100 / level))
            aliens.add(new Alien());

        if (shotTime == readyToShoot && keys[KeyEvent.VK_SPACE] && !hasShot) {
            shotTime = 0;
            hasShot = true;
            bullets.add(ship.shoot());
        }
        if (shotTime < readyToShoot)
            shotTime++;
        for (int b = bullets.size() - 1; b >= 0; b--) {
            bullets.get(b).move();
            bullets.get(b).wrapAround();
            if (bullets.get(b).too_far()) {
                bullets.remove(b);
            }
        }
        ArrayList<Bullet> junkBullets = new ArrayList<Bullet>();
        for (Bullet eb : enemyBullets) {
            eb.move();
            if (!ship.isImmune() && ship.hits(eb)) {
                re_spawn();
                explosions.add(new Explosion(ship.x(), ship.y(), 10));
                junkBullets.add(eb);
            }
            if (eb.too_far()) {
                junkBullets.add(eb);
            }
        }
        enemyBullets.removeAll(junkBullets);
        ArrayList<SpaceRock> trash = new ArrayList<SpaceRock>();
        ArrayList<SpaceRock> to_add = new ArrayList<SpaceRock>();

        // Moves the asteroid.
        for (SpaceRock spr : spaceRocks) {
            spr.move();
            for (int b = bullets.size() - 1; b >= 0; b--) {
                if (spr.hits(bullets.get(b))) {
                    to_add.addAll(spr.split());
                    trash.add(spr);
                    explosions.add(new Explosion(bullets.get(b).x(), bullets.get(b).y(), 5));
                    score += Tools.integer(0.05 * bullets.get(b).distance_travelled()); // Adds score based on how far
                                                                                        // the bullet has travelled.
                    bullets.remove(b);
                    if (spr.type() == SpaceRock.large) {
                        score += 20;
                    } else if (spr.type() == SpaceRock.medium) {
                        score += 50;
                    } else {
                        score += 75;
                    }
                }
            }
            if (!ship.isImmune() && ship.hits(spr)) {
                trash.add(spr);
                explosions.add(new Explosion(ship.x(), ship.y(), 5));
                re_spawn();
            }
        }
        spaceRocks.addAll(to_add);
        ArrayList<Alien> killed = new ArrayList<Alien>();
        for (Alien a : aliens) {
            a.move(ship.x(), ship.y());
            if (Tools.chance(50)) {
                enemyBullets.add(a.shoot(ship.x(), ship.y()));
            }
            for (int b = bullets.size() - 1; b >= 0; b--) {
                if (a.hits(bullets.get(b))) {
                    killed.add(a);
                    score += Tools.integer(0.1 * bullets.get(b).distance_travelled());
                    explosions.add(new Explosion(bullets.get(b).x(), bullets.get(b).y(), 8));
                    bullets.remove(b);
                    immuneCounter += 50;
                    score += a.type() == Alien.big ? 120 : 180;
                }
            }
            if (!ship.isImmune() && ship.hits(a)) {
                explosions.add(new Explosion(ship.x(), ship.y(), 11));
                re_spawn(); // hitting an alien ship doesn't kill the alien.
            }
        }
        spaceRocks.removeAll(trash);
        aliens.removeAll(killed);
        for (int exp = explosions.size() - 1; exp >= 0; exp--) {
            explosions.get(exp).move();
            if (explosions.get(exp).fizzled_out()) {
                explosions.remove(exp);
            }
        }
    }

    // Template code.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (screen == GAME)
            move();
        repaint();
    }

    // Draws everything.
    @Override
    public void paint(Graphics g) {
        g.setFont(new Font("BankGothic Lt BT", Font.PLAIN, 20));
        if (screen == HOME) {
            // Images and GIFs.
            g.drawImage(homeAnimation, 0, 0, WIDTH, HEIGHT, null);
            g.drawImage(title, 0, 0, WIDTH, HEIGHT, null);
            battleMusic.stop();
            homeMusic.loop();
            g.setColor(white);
            g.drawString("Click Anywhere to Play!", 10, 20);
            // The text at the bottom left.
            // Yes, overcomplicated approach, but I would rather do this than worry about
            // cases.
            ArrayList<String> info = new ArrayList<String>();
            if (highScore > 0) {
                info.add(String.format("High Score: %d (%s)", highScore, highScorer));
            }
            if (score > 0) {
                info.add(String.format("Score: %d (%s)", score, playerName));
            }
            for (int i = 0; i < info.size(); i++) {
                g.drawString(info.get(i), 10, HEIGHT - 10 - 20 * i);
            }
            g.drawString("Move around with the W, A and D keys", WIDTH - 434, 20);
            g.drawString("shoot with the space bar", WIDTH - 287, 40);
            g.drawString("By Abdullah Mustafa", WIDTH - 250, HEIGHT - 10);
            return;
        }
        homeMusic.stop();
        battleMusic.loop();
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(white);
        // Draws all the objects.
        ship.draw(g);
        for (Bullet b : bullets) {
            b.draw(g);
        }
        for (Bullet eb : enemyBullets) {
            eb.draw(g);
        }
        for (SpaceRock spr : spaceRocks) {
            spr.draw(g);
        }
        for (Alien a : aliens) {
            a.draw(g);
        }
        for (Explosion exp : explosions) {
            exp.draw(g);
        }
        g.drawString(String.format("%d %s Remaining", lives, lives == 1 ? "Life" : "Lives"), 10, 20);
        g.drawString(String.format("Level %d", level), 10, 40);
        if (highScore > 0) {
            g.drawString(String.format("Score: %d", score), 10, HEIGHT - 30);
            g.drawString(String.format("High Score: %d (%s)", highScore, highScorer), 10, HEIGHT - 10);
        } else {
            g.drawString(String.format("Score: %d (%s)", score, playerName), 10, HEIGHT - 10);
        }

        // Bars at the top right, show your shot time and how much time you have left
        // for immunity.
        g.setColor(shotTime == readyToShoot ? blue : white);
        g.fillRect(WIDTH - 10 - 5 * shotTime, 10, 5 * shotTime, 5);
        if (immuneCounter > 0) {
            g.setColor(white);
            g.fillRect(WIDTH - 10 - immuneCounter, 20, immuneCounter, 5);
        }
    }

    // Template code.
    @Override
    public void mouseClicked(MouseEvent e) {
        // JOptionPane code was mainly given from
        // https://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html.
        if (screen == HOME) { // User enters their name.
            playerName = JOptionPane.showInputDialog("Enter Your Name", playerName);
            if (playerName == null)
                return;
            if (playerName.equals(""))
                playerName = "Anonymous Player";
            screen = GAME;
            lives = 3;
            level = 1;
            score = 0;
            return;
        }
    }

    // Template code.
    @Override
    public void keyPressed(KeyEvent ke) {
        keys[ke.getKeyCode()] = true;
    }

    // Template code.
    @Override
    public void keyReleased(KeyEvent ke) {
        keys[ke.getKeyCode()] = false;
        if (KeyEvent.getKeyText(ke.getKeyCode()).equals("Space"))
            hasShot = false;
    }

    @Override public void keyTyped(KeyEvent ke) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}

}
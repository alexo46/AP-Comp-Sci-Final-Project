// Alex Oliva
// 6/3/2022

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import java.awt.event.KeyAdapter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Game extends JFrame {
    public static final int STARTING_DOTS = 2; // number of dots present on start
    public static final int RENDER_TIME = 250; // time per game update
    public static final int DOT_SIZE = 30; // size of the dot
    public static final int DOT_DISTANCE = 25; // distance the dot travels per step
    public static final int MAX_DOTS = 500; // maximum amount of dots
    public static final String HIGH_SCORE_PATH = "HighScore.txt"; // path to high score file
 
    private Direction currrentDirection = new Direction(0,-1); // direction the snake is traveling
    private boolean running = false; // true = game is running, false = hasn't started/ended
    private Random rand = new Random(); // object from Random Class

    private ArrayList<Position> positionList = new ArrayList<>(); // array that contains the previous positions of each dot
    private ArrayList<Dot> dots = new ArrayList<>(); // array that contains the list of dots used to make up the snake

    private JLabel head; // first dot (red dot), of the snake.
    private JLabel highScoreLbl; // high score label
    private JLabel currentScoreLbl; // current score label
    private JPanel gamePanel; // JPanel element
    private JLabel apple; // current apple on the board

    public int highScore;
    public int currentScore = 0;

    // spawns an apple on the board
    private void spawnApple() {
        int x = rand.nextInt(60,gamePanel.getWidth()-60);
        int y = rand.nextInt(60,gamePanel.getHeight()-60);

        // initalizes apple variable
        apple = new JLabel();
        apple.setBounds(x, y, 40, 40);

        // sets image to apple label 
        BufferedImage img;
        try {
            img = ImageIO.read(new File("imgs\\apple.png"));
            Image dimg = img.getScaledInstance(apple.getWidth(), apple.getHeight(), Image.SCALE_SMOOTH);
            apple.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gamePanel.add(apple);
        gamePanel.repaint();
    }

    // updates the current score and high score
    private void updateScore() throws IOException {
        currentScore = dots.size();
        if (currentScore > highScore) { // if the current score is greater than high score
            highScore = currentScore;
            try(FileWriter writer = new FileWriter(HIGH_SCORE_PATH)) { // write to high score file new high score
                writer.write(String.valueOf(highScore));
            }
            highScoreLbl.setText("High Score: " + highScore); // set high score label
        }
        currentScoreLbl.setText("Current Score: " + currentScore); // set current score label
    }

    // reference: https://zetcode.com/javagames/collision/
    // checks if label is colliding with different label
    private boolean isCollision(JLabel lb1, JLabel lb2) {
        return lb1.getBounds().intersects(lb2.getBounds());
    }
    
    // checks if the head is colliding with self or wall
    private boolean isCollidingWithSelfOrWall() {
      // checks to see if snake collides with itself
      for (int i = 2; i<dots.size(); i++) {
          if (head.getBounds().intersects(dots.get(i).getLabel().getBounds())) {
              System.out.println("hit self");
              return true;
          }
      }
        
      // checks to see if snake is within the game panel boundaries
      if (head.getX() <= 0 || head.getX() >= gamePanel.getWidth()-DOT_SIZE/2) {
            System.out.println("Out of bounds for x");
            return true;
      } else if (head.getY() <= 0 || head.getY() >= gamePanel.getHeight()) {
            System.out.println("Out of bounds for y");
            return true;
      }

      return false;
    }

    // ran when game over
    private void gameOver() {
        running = false; // stops updating game
    
        // creates new game over label
        JLabel gameOverLbl = new JLabel("Game Over!");
        gameOverLbl.setForeground(new Color(0, 0, 0));
        gameOverLbl.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLbl.setVerticalAlignment(SwingConstants.CENTER);
        gameOverLbl.setFont(new Font("Century Gothic", Font.BOLD, 20));
        gameOverLbl.setBounds(-32, 0, 602, 47);
        gamePanel.add(gameOverLbl);
    }

    // creates snake before game is ran
    private void constructSnake() {
        Position startingPos = new Position(268, 273); // snake head's starting position
        positionList.add(startingPos);

        head = new JLabel(); // creates head of the snake
        head.setName("head");
        head.setBounds((int)startingPos.getX(), (int)startingPos.getY(), DOT_SIZE, DOT_SIZE);
        head.setForeground(new Color(235,235,235));

        // reference: https://stackoverflow.com/questions/16343098/resize-a-picture-to-fit-a-jlabel
        // sets snake's image to the red circle image.
        BufferedImage img;
        try {
            img = ImageIO.read(new File("imgs\\RedCircle.png"));
            Image dimg = img.getScaledInstance(head.getWidth(), head.getHeight(), Image.SCALE_SMOOTH);
            head.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        head.setOpaque(false);

        gamePanel.add(head);

        for (int i = 1; i <= STARTING_DOTS; i++) { // constructs the body of the snake
            Position pos = new Position(268, 273 + (DOT_DISTANCE * i)); // dot position
            Dot dot = new Dot((int) pos.getX(), (int) pos.getY(), DOT_SIZE); // dot object

            gamePanel.add(dot.getLabel()); // adds dot object to game panel
            gamePanel.repaint(); // allows for dot to be visible on gamePanel
            dots.add(dot); // adds dot to dot arraylist
            positionList.add(pos); // adds position to positionList
        }
    }

    // constructor that loads the Gui
    public Game() {
        addKeyListener(new TAdapter()); // listens for keys pressed on the window
        setTitle("Snake Game");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 608, 720);

        // content pane that is responsbile for holdng the GUI content
        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(255,255,255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        JLabel title = new JLabel("Snake Game"); // game title at the top of the screen
        title.setForeground(new Color(0, 0, 0));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Century Gothic", Font.BOLD, 20));
        title.setBounds(0, 0, 602, 47);
        contentPane.add(title);
  
        gamePanel = new JPanel(); // panel that holds all the game GUIs
        gamePanel.setBackground(new Color(235,235,235));
        gamePanel.setBounds(32, 105, 535, 546);
        gamePanel.setLayout(null);
        contentPane.add(gamePanel);

        highScoreLbl = new JLabel("High Score: 0"); // initalizes high score label
        highScoreLbl.setForeground(new Color(0, 0, 0));
        highScoreLbl.setHorizontalAlignment(SwingConstants.LEFT);
        highScoreLbl.setFont(new Font("Century Gothic", Font.BOLD, 20));
        highScoreLbl.setBounds(30, 30, 602, 47);
        contentPane.add(highScoreLbl);

        currentScoreLbl = new JLabel("Current Score: 0"); // initalizes current score label
        currentScoreLbl.setForeground(new Color(0, 0, 0));
        currentScoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        currentScoreLbl.setFont(new Font("Century Gothic", Font.BOLD, 20));
        currentScoreLbl.setBounds(-30, 30, 602, 47);
        contentPane.add(currentScoreLbl);

        setContentPane(contentPane);
        
        // updating high score label and variable
        try {
            File highScoreFile = new File(HIGH_SCORE_PATH);
            if (highScoreFile.createNewFile()) { // creates a new high score file if one doesn't exist and sets highscore to 0
               try (FileWriter writer = new FileWriter(HIGH_SCORE_PATH)) {
                writer.write("0");
                highScore = 0;
               }
            } else {
                Scanner scan = new Scanner(highScoreFile); // else it reads the high score file and sets the highscore variable to the file contents
                int score = scan.nextInt();
                highScore = score;
                scan.close();
            }
        } catch (IOException e) {
        e.printStackTrace();
        }
        highScoreLbl.setText("High Score: " + highScore); // updates the highscore label
    }

    // is called when the game starts and handles movement
    public void run() throws IOException, InterruptedException {
        constructSnake(); // constructs the snake when the game is ran
        spawnApple(); // spawns an apple in a random area within gamePanel
        running = true; // allows game to run

        while (running) { // while running do. when run
            Thread.sleep(RENDER_TIME); 
            int headX = head.getX(); // head x position
            int headY = head.getY(); // head y position

            Position headPosition = new Position(headX + (currrentDirection.getX()*DOT_DISTANCE), headY + (currrentDirection.getY()*DOT_DISTANCE)); // new head position of snake
            head.setBounds((int) headPosition.getX(), (int) headPosition.getY(), DOT_SIZE, DOT_SIZE); // sets the head position of the snake

            for (int i = 0; i<dots.size(); i++) {
                JLabel dot = dots.get(i).getLabel();
                Position pos = positionList.get(i); // gets the positionList position at index = i
                dot.setBounds((int) pos.getX(),(int) pos.getY(), DOT_SIZE, DOT_SIZE); // sets the position of dot to pos
            }

            positionList.add(0, headPosition); // adds the new head position to the positionList index at 0

            if (isCollision(head, apple)) { // checks if head collides with apple
                gamePanel.remove(apple); // removes the apple
                updateScore(); // updates high and current scores
                
                Position dotPos = positionList.get(dots.size()+1); // gets dot position for new dot
                Dot dot = new Dot((int)dotPos.getX(), (int)dotPos.getY(), DOT_SIZE); // creates new dot
                dots.add(dot); // adds to array list
                gamePanel.add(dot.getLabel()); // adds to gamePanel
                spawnApple(); // spawns a new apple elsewhere
                gamePanel.repaint(); // allows for player to see the apple. ( apple doesn't appear without running gamePanel.repaint(); )
            }
            
            if (isCollidingWithSelfOrWall()) { // checks to see if the snake is colliding with itself, or if it is out of the gamePanel boundaries.
              gameOver(); // game over method
            }
        }
    }

    // reference: https://zetcode.com/javagames/movingsprites/
    // listens for key pressed events
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            int key = event.getKeyCode();
           if (running) {
                if (key == KeyEvent.VK_RIGHT && currrentDirection.getX() != 1) { // if key = right arrow then set direction to move right
                    currrentDirection.set(1,0);
                } else if (key == KeyEvent.VK_LEFT && currrentDirection.getX() != -1) { // if key = left arrow then set direction to move left
                    currrentDirection.set(-1,0);
                } else if (key == KeyEvent.VK_UP && currrentDirection.getY() != -1) { // if key = up arrow then set direction to move up
                    currrentDirection.set(0,-1);
                } else if (key == KeyEvent.VK_DOWN && currrentDirection.getY() != 1) { // if key = down arrow then set direction to move down
                    currrentDirection.set(0,1);
                }
            }
        }
    }
}

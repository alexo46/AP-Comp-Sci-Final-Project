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
    public static final int STARTING_DOTS = 2;
    public static final int RENDER_TIME = 100;
    public static final int DOT_SIZE = 30;
    public static final int DOT_DISTANCE = 25;
    public static final int MAX_DOTS = 500;
    public static final String HIGH_SCORE_PATH = "HighScore.txt";

    private Direction currrentDirection = new Direction(0,-1);
    private boolean running = false;
    private Random rand = new Random();

    private ArrayList<Position> positionList = new ArrayList<>();
    private ArrayList<Dot> dots = new ArrayList<>();

    private JLabel head;
    private JLabel highScoreLbl;
    private JLabel currentScoreLbl;
    private JPanel gamePanel;
    private JLabel apple;

    public int highScore;
    public int currentScore = 0;

    // spawns an apple on the board
    private void spawnApple() {
        int x = rand.nextInt(60,gamePanel.getWidth()-60);
        int y = rand.nextInt(60,gamePanel.getHeight()-60);

        apple = new JLabel();
        apple.setBounds(x, y, 40, 40);

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
        if (currentScore > highScore) {
            highScore = currentScore;
            try(FileWriter writer = new FileWriter(HIGH_SCORE_PATH)) {
                writer.write(String.valueOf(highScore));
            }
            highScoreLbl.setText("High Score: " + highScore);
        }
        currentScoreLbl.setText("Current Score: " + currentScore);
    }

    // reference: https://zetcode.com/javagames/collision/
    // checks if label is colliding with different label
    private boolean isCollision(JLabel lb1, JLabel lb2) {
        return lb1.getBounds().intersects(lb2.getBounds());
    }
    
    // checks if the head is colliding with self or wall
    private boolean isCollidingWithSelfOrWall() {
      for (int i = 2; i<dots.size(); i++) {
          if (head.getBounds().intersects(dots.get(i).getLabel().getBounds())) {
              System.out.println("hit self");
              return true;
          }
      }
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
        running = false;

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
        Position startingPos = new Position(268, 273);
        positionList.add(startingPos);

        head = new JLabel();
        head.setName("head");
        head.setBounds((int)startingPos.getX(), (int)startingPos.getY(), DOT_SIZE, DOT_SIZE);
        head.setForeground(new Color(235,235,235));

        // reference: https://stackoverflow.com/questions/16343098/resize-a-picture-to-fit-a-jlabel
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

        for (int i = 1; i <= STARTING_DOTS; i++) {
            Position pos = new Position(268, 273 + (DOT_DISTANCE * i));
            Dot dot = new Dot((int) pos.getX(), (int) pos.getY(), DOT_SIZE);

            gamePanel.add(dot.getLabel());
            gamePanel.repaint();
            dots.add(dot);
            positionList.add(pos);
        }
    }

    // constructor that loads the Gui
    public Game() {
        addKeyListener(new TAdapter());
        setTitle("Snake Game");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 608, 720);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(255,255,255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);

        JLabel title = new JLabel("Snake Game");
        title.setForeground(new Color(0, 0, 0));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Century Gothic", Font.BOLD, 20));
        title.setBounds(0, 0, 602, 47);
        contentPane.add(title);
  
        gamePanel = new JPanel();
        gamePanel.setBackground(new Color(235,235,235));
        gamePanel.setBounds(32, 105, 535, 546);
        gamePanel.setLayout(null);
        contentPane.add(gamePanel);

        highScoreLbl = new JLabel("High Score: 0");
        highScoreLbl.setForeground(new Color(0, 0, 0));
        highScoreLbl.setHorizontalAlignment(SwingConstants.LEFT);
        highScoreLbl.setFont(new Font("Century Gothic", Font.BOLD, 20));
        highScoreLbl.setBounds(30, 30, 602, 47);
        contentPane.add(highScoreLbl);

        currentScoreLbl = new JLabel("Current Score: 0");
        currentScoreLbl.setForeground(new Color(0, 0, 0));
        currentScoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        currentScoreLbl.setFont(new Font("Century Gothic", Font.BOLD, 20));
        currentScoreLbl.setBounds(-30, 30, 602, 47);
        contentPane.add(currentScoreLbl);

        setContentPane(contentPane);

        try {
            File highScoreFile = new File(HIGH_SCORE_PATH);
            if (highScoreFile.createNewFile()) {
               try (FileWriter writer = new FileWriter(HIGH_SCORE_PATH)) {
                writer.write("0");
                highScore = 0;
               }
            } else {
                Scanner scan = new Scanner(highScoreFile);
                int score = scan.nextInt();
                highScore = score;
                scan.close();
            }
        } catch (IOException e) {
        e.printStackTrace();
        }
        highScoreLbl.setText("High Score: " + highScore);
    }

    // is called when the game starts and handles movement
    public void run() throws IOException, InterruptedException {
        constructSnake();
        spawnApple();
        running = true;

        while (running) {
            Thread.sleep(250);
            int headX = head.getX();
            int headY = head.getY();

            Position headPosition = new Position(headX + (currrentDirection.getX()*DOT_DISTANCE), headY + (currrentDirection.getY()*DOT_DISTANCE));
            head.setBounds((int) headPosition.getX(), (int) headPosition.getY(), DOT_SIZE, DOT_SIZE);

            for (int i = 0; i<dots.size(); i++) {
                JLabel dot = dots.get(i).getLabel();
                Position pos = positionList.get(i);
                dot.setBounds((int) pos.getX(),(int) pos.getY(), DOT_SIZE, DOT_SIZE);
            }

            positionList.add(0, headPosition);

            if (isCollision(head, apple)) {
                gamePanel.remove(apple);
                updateScore();
                
                Position dotPos = positionList.get(dots.size()+1);
                Dot dot = new Dot((int)dotPos.getX(), (int)dotPos.getY(), DOT_SIZE);
                dots.add(dot);
                gamePanel.add(dot.getLabel());
                spawnApple();
                gamePanel.repaint();
            }
            
            if (isCollidingWithSelfOrWall()) {
              gameOver();
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
                if (key == KeyEvent.VK_RIGHT && currrentDirection.getX() != 1) {
                    currrentDirection.set(1,0);
                } else if (key == KeyEvent.VK_LEFT && currrentDirection.getX() != -1) {
                    currrentDirection.set(-1,0);
                } else if (key == KeyEvent.VK_UP && currrentDirection.getY() != -1) {
                    currrentDirection.set(0,-1);
                } else if (key == KeyEvent.VK_DOWN && currrentDirection.getY() != 1) {
                    currrentDirection.set(0,1);
                }
            }
        }
    }
}

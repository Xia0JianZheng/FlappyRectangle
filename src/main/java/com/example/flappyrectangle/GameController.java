package com.example.flappyrectangle;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameController implements Initializable {

    AnimationTimer gameLoop;
    @FXML
    private AnchorPane gameScreen;
    @FXML
    private Rectangle bird;

    @FXML
    private Button startButton;
    @FXML
    private Text score;
    @FXML
    private Text level;


    private double gameSpeed = 0;
    private int gameTime = 0;
    private int scoreCount = 0;
    private int levelcount = 1;

    double yDelta = 0.02;
    double time = 0;
    int jumpHeight = 100;
    Random random = new Random();

    private int spawnTime = 500;

    private boolean gameStarted = false;

    ArrayList<Rectangle> obstacles = new ArrayList<>();
    ArrayList<Rectangle> rectangles = new ArrayList<>();

    Media jumpSound = new Media(getClass().getResource("assets/flap.wav").toString());
    Media deadSound = new Media(getClass().getResource("assets/fail.wav").toString());


    public void initialize(URL url, ResourceBundle resourceBundle) {
        load();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if(gameStarted) {
                    update();
                }
            }
        };
    }

    @FXML
    void startGame() {

        startButton.setDisable(true);
        gameScreen.requestFocus();
        gameTime = 0;
        time = 0;
        scoreCount = 0;
        levelcount = 0;
        jumpHeight = 75;
        spawnTime = 500;
        gameSpeed = 0;
        score.setText(String.valueOf(0));
        level.setText(String.valueOf(1));
        gameStarted = true;
        gameLoop.start();
    }

    @FXML
    void pressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            jump();
            MediaPlayer mediaPlayer_jump = new MediaPlayer(jumpSound);
            mediaPlayer_jump.play();
        }
    }


    private void jump() {
        if(gameStarted) {
            if (bird.getLayoutY() + bird.getY() <= jumpHeight) {
                moveBirdY(-(bird.getLayoutY() + bird.getY()));
                time = 0;
                return;
            }

        moveBirdY(-jumpHeight);
        time = 0;
        }
    }

    private void update() {

        time++;
        gameTime++;
        moveBirdY(yDelta * time);
        if (gameTime % spawnTime == 0) {
            createObstacles();
        }
        moveObstacles(obstacles);

        updatePoint();

        if (collisionDetection()) {
            MediaPlayer mediaPlayer_dead = new MediaPlayer(deadSound);
            mediaPlayer_dead.play();
            gameOver();
        }

        if (isBirdDead()) {
            MediaPlayer mediaPlayer_dead = new MediaPlayer(deadSound);
            mediaPlayer_dead.play();
            gameOver();
        }

    }

    private void load() {
        System.out.println("Game Starting");
    }

    private void moveBirdY(double positionChange) {
        bird.setY(bird.getY() + positionChange);
    }

    private boolean isBirdDead() {
        double birdY = bird.getLayoutY() + bird.getY();
        return birdY >= gameScreen.getHeight();
    }

    private void gameOver() {
        bird.setY(0);
        gameScreen.getChildren().removeAll(rectangles);
        gameLoop.stop();
        startButton.setDisable(false);
        gameStarted = false;
        obstacles.clear();
        rectangles.clear();
    }

    private void createObstacles() {
        int width = 75;
        double xPos = gameScreen.getWidth() - 50;

        double space = 200;
        double recTopHeight = random.nextInt((int) (gameScreen.getHeight() - space - 100)) + 50;
        System.out.println(recTopHeight);
        double recBottomheight = gameScreen.getHeight() - space - recTopHeight;


        Rectangle obstacleTop = new Rectangle(xPos, 0, width, recTopHeight);
        Rectangle obstacleBottom = new Rectangle(xPos, recTopHeight + space, width, recBottomheight);
        obstacleTop.setFill(Color.GREEN);
        obstacleBottom.setFill(Color.GREEN);
        gameScreen.getChildren().addAll(obstacleTop, obstacleBottom);
        obstacles.add(obstacleTop);
        obstacles.add(obstacleBottom);
        rectangles.add(obstacleTop);
        rectangles.add(obstacleBottom);

    }

    private void updatePoint(){
        for (Rectangle rectangle : obstacles) {
            if (bird.getBoundsInParent().getMaxX() > rectangle.getBoundsInParent().getMaxX() && !rectangle.getProperties().containsKey("scored")) {
                rectangle.getProperties().put("scored", true);
                scoreCount++;
                score.setText(String.valueOf(scoreCount));
                if(scoreCount % 5 == 0){
                    LevelUp();
                }
            }
        }
    }


    private void LevelUp(){
        levelcount+=1;
        gameSpeed += 0.25;
        spawnTime -= 15;
        level.setText(String.valueOf(levelcount));
    }

    private void moveObstacles(ArrayList<Rectangle> obstacles) {

        ArrayList<Rectangle> outOfScreen = new ArrayList<>();

        for (Rectangle rectangle : obstacles) {
            moveRectangle(rectangle, -0.75 + (-gameSpeed));

            if (rectangle.getX() <= -rectangle.getWidth()) {
                outOfScreen.add(rectangle);
            }
        }
        obstacles.remove(outOfScreen);
        gameScreen.getChildren().removeAll(outOfScreen);
    }

    private void moveRectangle(Rectangle rectangle, double amount) {
        rectangle.setX(rectangle.getX() + amount);
    }

    private boolean collisionDetection() {
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getBoundsInLocal().intersects(bird.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

}

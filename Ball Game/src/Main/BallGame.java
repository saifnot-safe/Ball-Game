package Main;

import Ball.Ball;
import Scenes.IntroScene;
import UI.GameStats;
import Utils.SoundManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * BallGame.java
 * Main class for the game
 * Handles frame size and game object setup
 */

public class BallGame extends Application {

public static final double WIDTH = 900;
public static final double HEIGHT = 500;
public static final double FPS_TIME = 0.016;

private StackPane root = new StackPane();
private Pane gamePane = new Pane();
private Pane introPane = new Pane();
private SoundManager sounds = new SoundManager();
private Ball ball;
private GameStats statsDisplay;
private IntroScene intro;


    @Override
    public void start(Stage primaryStage) {

        statsDisplay = new GameStats(gamePane);

        ball = new Ball(20, WIDTH/2.2, 500, 0.1, 0.25, Color.BLACK, gamePane, sounds, statsDisplay);

        intro = new IntroScene(root, introPane, sounds);

        root.getChildren().add(gamePane);
        root.getChildren().add(introPane);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        Image icon = new Image(getClass().getResourceAsStream("/Assets/logo.png"));
        primaryStage.getIcons().add(icon);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Ball Game");
        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
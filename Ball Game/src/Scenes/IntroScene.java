package Scenes;

import Main.BallGame;
import Utils.SoundManager;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * IntroScene.java
 * Starting game scene
 * Creates aniation for the game intro
 */

public class IntroScene {

    private StackPane root;
    private Pane introPane;
    private Image logo = new Image(getClass().getResource("/Assets/Eyes2.png").toExternalForm());
    private ImageView logoView = new ImageView(logo);
    private Timeline blinkAnim;
    private Timeline blinkInterval;
    private int startHeight = 100;
    private int blinkHeight = 500;
    private Group eyes;
    private Rectangle eyelid = new Rectangle(400, startHeight, Color.BLACK);
    private Rectangle background = new Rectangle(BallGame.WIDTH, BallGame.HEIGHT, Color.BLACK);
    private PauseTransition pause = new PauseTransition(Duration.seconds(0.75));
    private SoundManager sounds;

    public IntroScene(StackPane root, Pane introPane, SoundManager sounds) {
        this.root = root;
        this.introPane = introPane;

        setupIntro();
        initEyelids();
        initBlinking();

        // Blink animation when clicked
        introPane.setOnMouseClicked(e -> {
            blinkAnim = new Timeline(
                    new KeyFrame(Duration.seconds(0.25), new KeyValue(eyelid.heightProperty(), blinkHeight, Interpolator.EASE_BOTH)));
            blinkAnim.play();
            blinkInterval.stop();

            blinkAnim.setOnFinished(ev-> {
                pause.play();
                pause.setOnFinished(eve-> {
                    sounds.playChime(6);
                    root.getChildren().remove(introPane);
                });

            });

        });

    }

    /**
     * Setups up intro graphics and objects
     */
    public void setupIntro() {
        introPane.getChildren().add(background);
        logoView.setFitWidth(BallGame.WIDTH);
        logoView.setFitHeight(BallGame.HEIGHT);
        logoView.setPreserveRatio(true);
        logoView.setY(0);
        logoView.setX(0);

       eyes = new Group(logoView, eyelid);
        introPane.getChildren().add(eyes);

    }


    public void initEyelids() {
        eyelid.setX(250);
        eyelid.setY(0);
        eyelid.setTranslateX(0.5);
        eyelid.setTranslateY(0.5);
        introPane.getChildren().add(eyelid);

    }

    public void initBlinking() {
        // Brings eyelids down for blinking anim
         blinkAnim = new Timeline(
                 new KeyFrame(Duration.seconds(0.25), new KeyValue(eyelid.heightProperty(), blinkHeight, Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.seconds(0.75), new KeyValue(eyelid.heightProperty(), startHeight, Interpolator.EASE_BOTH)));

         // Schedules the next blink with a small chance of double blinking
        blinkAnim.setOnFinished(e -> {
            if (Math.random() < 0.2)
                scheduleNextBlink(0.5);
            else
                scheduleNextBlink();});

        scheduleNextBlink();
    }

    /**
     * Schedules a blink within a random interval
     */
    private void scheduleNextBlink() {
        double randomInterval = 3 + Math.random() * 7;
        scheduleNextBlink(randomInterval);
    }

    /**
     * Schedules a blink within a custom interval
     * @param customDelaySeconds is the custom interval
     */
    private void scheduleNextBlink(double customDelaySeconds) {

        blinkInterval = new Timeline (
                new KeyFrame(Duration.seconds(customDelaySeconds), e -> {
                    blinkAnim.play();})
        ); // Decides when to play blinkAnim using the custom interval

        blinkInterval.setCycleCount(1);
        blinkInterval.play();

    }

}

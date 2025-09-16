package Ball;
import Main.BallGame;
import UI.GameStats;
import Utils.SoundManager;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

/**
 * Ball.java
 * Creates the player's ball object
 * Handles graphics and animation
 */

public class Ball {
    private static final double EFFECT_START_RADIUS = 5;
    private double radius;
    private Paint color;
    private DoubleProperty eyeVertical = new SimpleDoubleProperty(0);
    private DoubleProperty eyeHorizontal = new SimpleDoubleProperty(0);
    private BooleanProperty cd = new SimpleBooleanProperty();
    private Circle ball;
    private BallPhysics ballPhysics;
    private Pane gamePane;
    private Rectangle forceZone = new Rectangle(0, 0, BallGame.WIDTH, BallGame.HEIGHT); //creating a transparent rectangle to detect clicks
    private Rectangle leftEye;
    private Rectangle rightEye;
    private Arc eyelids;
    private Timeline blinkAnim;
    private Timeline blinkInterval;
    private int colorIndex = 0;
    private final List<Paint> effectColors= Arrays.asList(
            Color.web("#ffc1cc"),
            Color.web("#a0e7e5"),
            Color.web("#b4f8c8"),
            Color.web("#fbe7c6"),
            Color.web("#ffaebc")
    );
    private Circle clickEffect = new Circle(EFFECT_START_RADIUS, effectColors.get(colorIndex));
    private int chimeIndex = 0;
    private SoundManager sounds;
    private GameStats statsDisplay;

    public Ball(double radius, double x, double y, double gravity, double friction, Paint color, Pane gamePane, SoundManager sounds, GameStats statsDisplay) {
        this.gamePane = gamePane;
        this.radius = radius;
        this.color = color;
        this.sounds = sounds;
        this.statsDisplay = statsDisplay;

        ball = new Circle(radius, color);
        ballPhysics = new BallPhysics(radius, x, y, gravity, friction, statsDisplay);

        ball.centerXProperty().bind(ballPhysics.getPosX());
        ball.centerYProperty().bind(ballPhysics.getPosY());
        // Bind ball position so there is no need to update it in the loop now

        initEyes();
        initBlinking();
        initMovementTimeline();
        bounceClickHandler();

        Group ballGroup = new Group(ball, leftEye, rightEye, eyelids);

        gamePane.getChildren().add(ballGroup);
    }

    private void initEyes() {
        leftEye = new Rectangle(radius/6.66, radius/2.22, Color.WHITE);
        rightEye = new Rectangle(radius/6.66, radius/2.22, Color.WHITE);
        eyelids = new Arc();
        eyelids.setRadiusX(radius/1.25);
        eyelids.setRadiusY(radius/1.66);
        eyelids.setFill(color);
        eyelids.setStartAngle(0);
        eyelids.setLength(180);
        eyelids.setType(ArcType.ROUND);
        eyelids.setVisible(false);
    }

    private void initMovementTimeline() {
        Timeline movementTimeline = new Timeline(new KeyFrame(Duration.seconds(BallGame.FPS_TIME), e -> {
           ballPhysics.updatePhysics();
           chimeHandler();
           eyeAnimHandler();

            leftEye.setX(ballPhysics.getPosX().get() - radius/2.85 + eyeHorizontal.get());
            leftEye.setY(ballPhysics.getPosY().get()- radius/4 + eyeVertical.get());
            rightEye.setX(ballPhysics.getPosX().get() + radius/4 + eyeHorizontal.get());
            rightEye.setY(ballPhysics.getPosY().get() - radius/4 + eyeVertical.get());
            // Sets the updated positions for rhe ball and eyes and eyelids
        }));
        movementTimeline.setCycleCount(Animation.INDEFINITE);
        movementTimeline.play();

    }

    private void initBlinking() {
        blinkAnim = new Timeline (
                new KeyFrame(Duration.seconds(0.25),
                        new KeyValue(eyelids.translateYProperty(), radius/1.56, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.75),
                        new KeyValue(eyelids.translateYProperty(), 0))
        );

        blinkAnim.setCycleCount(1);
        blinkAnim.setRate(1);
        blinkAnim.setOnFinished(e -> {eyelids.setVisible(true);
            if (Math.random() < 0.2)
                scheduleNextBlink(0.5);
            else
                scheduleNextBlink();});
        scheduleNextBlink();
    }

    /**
     * Handles bounce mechanic that allows user to move the ball
     */
    private void bounceClickHandler() {
        forceZone.setFill(Color.TRANSPARENT);
        gamePane.getChildren().add(forceZone);
        Timeline cooldown = new Timeline(new KeyFrame(Duration.seconds(0.5), ev ->
                cd.set(false)));
        Timeline effectTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(clickEffect.radiusProperty(), 30),
                new KeyValue(clickEffect.opacityProperty(), 0)));

            forceZone.setOnMouseClicked(e -> {
                if (!cd.get()) {
                    cd.set(true);
                    cooldown.play();
                    // Bounce cooldown

                    sounds.playChime(chimeIndex);
                    chimeIndex++;
                    if (chimeIndex >= sounds.chimes.size())
                        chimeIndex = sounds.chimes.size() - 1;
                    // Plays chime sound effect

                    clickEffect.setFill(effectColors.get(colorIndex));
                    colorIndex++;
                    if (colorIndex >= effectColors.size())
                        colorIndex = 0;
                    // Creates colour effect that goes through different colours in order

                    clickEffect.setCenterX(e.getX());
                    clickEffect.setCenterY(e.getY());
                    gamePane.getChildren().add(clickEffect);

                    effectTimeline.play();
                    effectTimeline.setOnFinished(ev -> {clickEffect.setRadius(EFFECT_START_RADIUS); clickEffect.setOpacity(1);
                        gamePane.getChildren().remove(clickEffect); effectTimeline.stop();});

                    double clickX = e.getX();
                    double clickY = e.getY();
                    // Where the user clicked
                    double dX = ballPhysics.getPosX().get() - clickX;
                    double dY = ballPhysics.getPosY().get() - clickY;
                    double distance = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
                    // Distance from user's click to the ball
                    double directionX = dX / distance;
                    double directionY = dY / distance;
                    // Direction from the user's click towards the ball
                    double force = 5 * Math.exp(-0.005 * distance);
                    // Calculates force based off distance from click to ball
                    ballPhysics.bounce(0.1, directionX * force, directionY * force);
                    // Creates bounce based off of direction and distance
                }
            });
    }

    /**
     * Handles chimes and graphic effects when bouncing the ball
     */
    private void chimeHandler() {
        if (ballPhysics.hitGround()) {
            if (chimeIndex > 3) {
                Circle errorEffect = new Circle(30, Color.RED);
                errorEffect.setOpacity(0.5);
                errorEffect.setCenterX(ballPhysics.getPosX().get());
                errorEffect.setCenterY(ballPhysics.getPosY().get()+5);
                gamePane.getChildren().add(errorEffect);
                // Creates an error effect if the user breaks his bounce streak
                Timeline errorEffectTimeline = new Timeline (new KeyFrame(Duration.seconds(1),
                        new KeyValue(errorEffect.radiusProperty(), 50),
                        new KeyValue(errorEffect.opacityProperty(), 0)));
                errorEffectTimeline.play();
                errorEffectTimeline.setOnFinished(ev -> {gamePane.getChildren().remove(errorEffect); errorEffectTimeline.stop();});
            } // Error effect anim
            chimeIndex = 0; // Resets chime scale if ball hits ground
        }
    }

    /**
     * Handles eye anim and movement
     */
    private void eyeAnimHandler() {
        if (ballPhysics.getVelocityY().get() < 0 && eyeVertical.get() > -5)
            eyeVertical.set(eyeVertical.get() - 0.5);
        if (ballPhysics.getVelocityY().get() > 0 && eyeVertical.get() < 4)
            eyeVertical.set(eyeVertical.get() + 0.5);
        // Vertical eye movement
        if (Math.abs(ballPhysics.getVelocityY().get()) < 5)
            eyeVertical.set(eyeVertical.get()*0.5);
        // Brings eyes to normal position when velocity is low

        if (ballPhysics.getVelocityX().get() < 0 && eyeHorizontal.get() > -2)
            eyeHorizontal.set(eyeHorizontal.get() - 0.5);
        if (ballPhysics.getVelocityX().get() > 0 && eyeHorizontal.get() < 3)
            eyeHorizontal.set(eyeHorizontal.get()+ 0.5);
        // Horizontal eye movement
        if (Math.abs(ballPhysics.getVelocityX().get()) < 1)
            eyeHorizontal.set(eyeHorizontal.get() - eyeHorizontal.get() * 0.2);
        // Brings eyes to normal position when velocity is low

        eyelids.setCenterX(ballPhysics.getPosX().get());
        if (eyeVertical.get() > 0)
            eyelids.setCenterY(ballPhysics.getPosY().get() - radius/2.5 + eyeVertical.get());
        else
            eyelids.setCenterY(ballPhysics.getPosY().get() - radius/2.5);
        // Sets eyelid position
    }

    private void scheduleNextBlink() {
        double randomInterval = 3 + Math.random() * 7;
        scheduleNextBlink(randomInterval);
    }

    private void scheduleNextBlink(double customDelaySeconds) {
        blinkInterval = new Timeline (
                new KeyFrame(Duration.seconds(customDelaySeconds), e -> {eyelids.setVisible(true);
                    blinkAnim.play();})
        );
        blinkInterval.setCycleCount(1);
        blinkInterval.play();
    }

}

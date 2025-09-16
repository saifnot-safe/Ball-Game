package Ball;

import Main.BallGame;
import UI.GameStats;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

/**
 * BallPhysics.java
 * Handles ball physics and collision mechanics
 */
public class BallPhysics {

    private final DoubleProperty posX, posY, velocityX, velocityY;
    private final double gravity, friction;
    private final double radius;
    private GameStats statsDisplay;
    private double bounces = 0;

    public BallPhysics(double radius, double x, double y, double gravity, double friction, GameStats statsDisplay) {
        this.radius = radius;
        this.gravity = gravity;
        this.friction = friction;
        this.posX = new SimpleDoubleProperty(x);
        this.posY = new SimpleDoubleProperty(y);
        this.velocityX = new SimpleDoubleProperty(5);
        this.velocityY = new SimpleDoubleProperty(0);
        this.statsDisplay = statsDisplay;
    }

    public void updatePhysics() {
        groundCollisionHandler();
        wallCollisionHandler();
        velocityY.set(velocityY.get() + gravity);

        posX.set(posX.get() + velocityX.get());
        posY.set(posY.get() + velocityY.get());
        statsDisplay.updateVelocity(velocityX.get(), velocityY.get());
    }

    /**
     * Bounces the ball
     * @param easeTime is the time the bounce transition takes
     * @param bounceX is the bounce offset in the x direction
     * @param bounceY is the bounce offset in the y direction
     */
    public void bounce(double easeTime, double bounceX, double bounceY) {
        double targetX = velocityX.get() + bounceX;
        double targetY = velocityY.get() + bounceY;
        Timeline bounceTimeline = new Timeline(new KeyFrame(Duration.seconds(easeTime), new KeyValue(velocityX, targetX, Interpolator.EASE_BOTH),
                new KeyValue(velocityY, targetY, Interpolator.EASE_BOTH)));
        bounceTimeline.play();
        // Transitions old veclotiy towards the target
        bounces++;
        statsDisplay.updateBounces(bounces);
        // Updates bounce stats

    }

    public boolean hitGround() {
        return posY.get() >= BallGame.HEIGHT - radius-30;
    }

    public DoubleProperty getPosX() {
        return posX;
    }

    public DoubleProperty getPosY() {
        return posY;
    }

    public DoubleProperty getVelocityX() {
        return velocityX;
    }

    public DoubleProperty getVelocityY() {
        return velocityY;
    }

    private void wallCollisionHandler() {
        if (posX.get() <= radius+1 || posX.get() >= BallGame.WIDTH - radius-1) // Checks if ball is touching side walls, +-1 added for clipping
            velocityX.set(velocityX.get()*-1); // Reverses velocity after hitting the wall with damping
    }

    private void groundCollisionHandler() {
        if (hitGround()) {
            posY.set(BallGame.HEIGHT - radius-30); // Clamps ball to ground
            velocityY.set(velocityY.get()*-0.8); // Reverses velocity after hitting the ground with damping
            if (Math.abs(velocityX.get()) < friction)
                velocityX.set(0);
            // Prevents infinite friction velocity
            else if (velocityX.get() > 0)
                velocityX.set(velocityX.get() - friction);
            else
                velocityX.set(velocityX.get() + friction);
            // Adds friction
            bounces = 0;
            statsDisplay.updateBounces(bounces);
            // Resets consecutive bounces if ball hits floor

        }
    }



    }


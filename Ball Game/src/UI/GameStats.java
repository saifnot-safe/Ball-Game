package UI;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * GameStats.java
 * Displays Game Stat User Interface
 */

public class GameStats {

    private double ballVelocity = 0;
    private double ballBounces = 0;
    private Pane gamePane;
    private Text velocityDisplay;
    private Text bounceDisplay;

    public GameStats() {

    }

    public GameStats(Pane gamePane) {
        this.gamePane = gamePane;
        displayStats();
    }

    private void displayStats() {

        velocityDisplay = new Text("Velocity: " + ballVelocity);
        velocityDisplay.setFont(Font.font(20));
        velocityDisplay.setX(50);
        velocityDisplay.setY(50);
        velocityDisplay.setFill(Color.BLACK);

        bounceDisplay = new Text("Bounces: " + ballBounces);
        bounceDisplay.setX(50);
        bounceDisplay.setY(80);
        bounceDisplay.setFill(Color.BLACK);
        bounceDisplay.setFont(Font.font(20));

        gamePane.getChildren().add(velocityDisplay);
        gamePane.getChildren().add(bounceDisplay);

    }

    /**
     * Updates velocity UI
     * @param velocityX is the X velocity
     * @param velocityY is the Y velocity
     */
    public void updateVelocity(double velocityX, double velocityY) {
        ballVelocity = Math.sqrt(velocityX*velocityX + velocityY*velocityY); // Calculates velocity using the X and Y components
        velocityDisplay.setText("Velocity: " + String.format("%.0f", ballVelocity));
    }

    /**
     * Updates consecutive bounce UI
     * @param bounces is the number of consecutive bounced
     */
    public void updateBounces(double bounces) {
        ballBounces = bounces;
        bounceDisplay.setText("Bounces: " + String.format("%.0f", ballBounces));
    }


}

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.App;
import view.PlayerListSelect;

/**
 * This GUI Application allows staff members of the Crossroads Inn Discord GW2 Community to create training squads for training days.
 * It solves the squad making problem as a Constraint Satisfaction Problem.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Main extends Application {
    // Values = 1024      512           256       128         64       32            16               8         4      2       1
    // Roles = "cTank", "chrono supp", "qFB", "alacrigade", "druid", "heal FB", "heal Renegade", "other heal", "bs", "pdps", "cdps"

    // Example player1 can play cTank, qFB and pdps = 0b10100000010
    // To check if player 1 can dps: (player 1 & 3) > 0
    // This applies the dps bit mask with an and bitwise operation. If any bit remain set, the result will be greater than 0 and confirm that player 1 can play dps.

    public static final String APP_NAME, VERSION;
    static {
        APP_NAME = "Squad Maker";
        VERSION = "1.0";
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        App root = new App();
        root.setAndInitCenter(new PlayerListSelect());

        Scene scene = new Scene(root, 1200, 800, true);

        primaryStage.setScene(scene);
        primaryStage.setTitle(APP_NAME);
        primaryStage.show();
    }
}

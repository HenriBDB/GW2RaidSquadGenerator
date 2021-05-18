package com.crossroadsinn;

import com.crossroadsinn.settings.Settings;
import com.crossroadsinn.settings.Roles;
import com.crossroadsinn.settings.Squads;
import com.crossroadsinn.components.ThemeListener;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import com.crossroadsinn.view.App;

import java.io.File;
import java.util.ArrayList;

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
    private static Style THEME;
    private static Scene scene;
    private static final ArrayList<ThemeListener> themeListeners;
    private static Stage primaryStage;

    static {
        APP_NAME = "Squad Maker";
        VERSION = "1.0";
        THEME = Style.LIGHT;
        themeListeners = new ArrayList<>();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        App root = new App();
        scene = new Scene(root, 1200, 800, true);

        Main.primaryStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.setTitle(APP_NAME);
        Settings.init();
        Roles.init();
        Squads.init();
        root.navigateHome();
        primaryStage.show();
    }

    /**
     * Update theme to Dark/Light mode and update all stored themed nodes.
     * @param darkMode whether dark mode or light mode.
     */
    public static void updateTheme(boolean darkMode) {
        if (darkMode) THEME = Style.DARK;
        else THEME = Style.LIGHT;

        JMetro jMetro = new JMetro(THEME);
        jMetro.setScene(scene);
        for (ThemeListener listener : themeListeners) listener.updateTheme();
    }

    public static Style getTheme() {
        return THEME;
    }

    public static ArrayList<ThemeListener> getThemeListeners() {
        return themeListeners;
    }

    public static Stage getPrimaryStage() {
        return Main.primaryStage;
    }
}

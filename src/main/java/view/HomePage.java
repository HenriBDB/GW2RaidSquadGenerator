package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * A simple home page with a background image from GW2 official wallpapers.
 *
 * @author Eren Bole.8720
 */
public class HomePage extends BorderPane implements AppContent {

    public HomePage(App parent) {
        setId("home-page");
        Label title = new Label("GW2 Raid Squad Generator");
        title.getStyleClass().add("home-page-title");
        setCenter(title);

        Button getStarted = new Button("Get Started");
        getStarted.setOnAction(e -> parent.navigatePlayerListSelect());
        Label footnotes = new Label("Created by Eren Bole.8720 for Crossroads Inn Raid Training discord community.");
        footnotes.getStyleClass().add("footnotes");
        VBox bottom = new VBox(20);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(getStarted, footnotes);
        bottom.setPadding(new Insets(5));
        setBottom(bottom);
    }

    @Override
    public void init() {

    }
}

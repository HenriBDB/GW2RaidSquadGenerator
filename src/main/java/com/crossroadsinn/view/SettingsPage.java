package com.crossroadsinn.view;

import com.crossroadsinn.components.SignupSheetLinkView;
import com.crossroadsinn.datatypes.LinkPair;
import com.crossroadsinn.settings.Settings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.controlsfx.control.ToggleSwitch;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple page showing settings that can be changed
 * including theme and links to online player CSVs.
 *
 * @author Eren Bole.8720
 */
public class SettingsPage extends ScrollPane implements AppContent {

    StackPane content;
    VBox progressBar;
    Label saveMsg;

    public SettingsPage() {
        fitToWidthProperty().set(true);

        progressBar = new VBox();
        ProgressBar bar = new ProgressBar();
        bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.getChildren().add(bar);
        progressBar.setAlignment(Pos.CENTER);

        content = new StackPane();
        content.setPadding(new Insets(0,0,30,0));
        content.setAlignment(Pos.TOP_CENTER);
        VBox settings = new VBox(30);
        settings.setMaxWidth(800);

        VBox linkSection = makeSection("Player Sheet Links");
        Label traineeLinksLabel = new Label("Trainee Links");
        SignupSheetLinkView traineeLinksView = new SignupSheetLinkView(Settings.TRAINEE_LINKS);
        Button addTraineeLink = new Button("Add Trainee Sheet Link");
        addTraineeLink.setOnAction(e -> Settings.TRAINEE_LINKS.add(new LinkPair<>("Name", "www.link.com")));

        Region region = new Region(); region.setPrefHeight(10);
        Label commLinksLabel = new Label("Commander Links");
        SignupSheetLinkView commLinksView = new SignupSheetLinkView(Settings.COMM_LINKS);
        Button addCommLink = new Button("Add Commander Sheet Link");
        addCommLink.setOnAction(e -> Settings.COMM_LINKS.add(new LinkPair<>("Name", "www.link.com")));
        linkSection.getChildren().addAll(traineeLinksLabel, traineeLinksView, addTraineeLink,
                region, commLinksLabel, commLinksView, addCommLink);

        VBox themeSection = makeSection("Theme and Appearance");
        ToggleSwitch themeToggle = new ToggleSwitch("Dark");
        themeToggle.setPadding(new Insets(0));
        themeToggle.setSelected(Settings.isDarkMode());
        themeToggle.selectedProperty().addListener((e, oldVal, newVal) -> Settings.setDarkMode(newVal));
        Label lightLabel = new Label("Light");
        HBox themeChoice = new HBox(20);
        themeChoice.getChildren().addAll(lightLabel, themeToggle);
        themeSection.getChildren().addAll(themeChoice);

        VBox saveSection = makeSection("Save Settings");
        saveSection.setAlignment(Pos.CENTER);
        Button saveBtn = new Button("Save");
        saveBtn.setDefaultButton(true);
        saveBtn.setOnAction(e -> saveSettings());
        saveMsg = new Label();
        saveSection.getChildren().addAll(saveBtn, saveMsg);

        settings.getChildren().addAll(linkSection, themeSection, saveSection);
        content.getChildren().add(settings);
        setContent(content);
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }

    @Override
    public void init() {
        saveMsg.setText("");
    }

    /**
     * Creates a section header with an all-caps title and horizontal separator adjacent.
     * Returns a VBox that can be populated with the section's content.
     * @param title The name of the section.
     * @return The section node.
     */
    private VBox makeSection(String title) {
        Label titleLabel = new Label(title.toUpperCase());
        Separator line = new Separator(Orientation.HORIZONTAL);
        HBox.setHgrow(line, Priority.ALWAYS);
        HBox titleBox = new HBox(10);
        titleBox.getChildren().addAll(titleLabel, line);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0,0,15,0));

        VBox section = new VBox(10);
        section.getChildren().add(titleBox);
        return section;
    }

    /**
     * Store applied settings indefinitely on system using the Settings class.
     */
    private void saveSettings() {
        content.setDisable(true);
        content.getChildren().add(progressBar);

        Settings.saveSettings();

        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        saveMsg.setText("Successfully saved at: " + time.format(formatter));

        content.getChildren().remove(progressBar);
        content.setDisable(false);
    }
}

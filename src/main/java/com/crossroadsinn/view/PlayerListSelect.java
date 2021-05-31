package com.crossroadsinn.view;

import com.crossroadsinn.Main;
import com.crossroadsinn.components.ThemedIcon;
import com.crossroadsinn.settings.Settings;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;
import com.crossroadsinn.signups.Commander;
import com.crossroadsinn.signups.Player;
import com.crossroadsinn.signups.SignupsParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Selection Screen to select commander and trainee sign-up sheets.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class PlayerListSelect extends VBox implements AppContent {

    private final App parent;
    private final Label uploadCommMsg = new Label(), uploadTraineeMsg = new Label();
    private final Button next = new Button("Save Changes");
    private final GridPane uploadTable;
    private final StackPane uploadTablePane;
    private final VBox progressBar, traineeLinksContainer, commLinksContainer;
    private Thread dlThread;

    private ArrayList<Player> traineeList;
    private ArrayList<Commander> commanderList;

    public PlayerListSelect(App parent) {
        this.parent = parent;
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 10, 20, 10));
        setSpacing(20);

        progressBar = new VBox(10);
        ProgressBar bar = new ProgressBar();
        bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        Button cancelProcess = new Button("Cancel");
        cancelProcess.setOnAction(e -> {
            if (dlThread != null) {
                dlThread.interrupt();
                dlThread = null;
                resume();
            }
        });
        progressBar.getChildren().addAll(bar, cancelProcess);
        progressBar.setAlignment(Pos.CENTER);

        Button uploadTrainees = new Button("Upload Trainee List");
        uploadTrainees.setOnAction(e -> uploadPlayerCSV(null, false));
        uploadTrainees.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button uploadCommanders = new Button("Upload Commander and Aide List");
        uploadCommanders.setOnAction(e -> uploadPlayerCSV(null, true));
        uploadCommanders.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        traineeLinksContainer = new VBox(10);
        commLinksContainer = new VBox(10);

        ThemedIcon fileIcon = new ThemedIcon("images/FileIconLight.png", "images/FileIconDark.png");
        ThemedIcon downloadIcon = new ThemedIcon("images/DownloadIconLight.png", "images/DownloadIconDark.png");
        ThemedIcon commanderTagIcon = new ThemedIcon("images/CommanderTagIconLight.png", "images/CommanderTagIconDark.png");
        ThemedIcon traineeIcon = new ThemedIcon("images/TraineeIconLight.png", "images/TraineeIconDark.png");
        Main.getThemeListeners().add(fileIcon);
        Main.getThemeListeners().add(downloadIcon);
        Main.getThemeListeners().add(commanderTagIcon);
        Main.getThemeListeners().add(traineeIcon);

        uploadTable = new GridPane();
        uploadTable.setHgap(10); uploadTable.setVgap(10);
        uploadTable.setAlignment(Pos.CENTER);
        uploadTable.add(fileIcon, 1, 0);
        uploadTable.add(downloadIcon, 2, 0);
        uploadTable.add(traineeIcon, 0, 1);
        uploadTable.add(commanderTagIcon, 0, 2);
        uploadTable.add(uploadTrainees, 1, 1);
        uploadTable.add(traineeLinksContainer, 2, 1);
        uploadTable.add(uploadCommanders, 1, 2);
        uploadTable.add(commLinksContainer, 2, 2);

        RowConstraints vGrow = new RowConstraints();
        vGrow.setVgrow(Priority.ALWAYS);
        uploadTable.getRowConstraints().addAll(new RowConstraints(), vGrow, vGrow);
        ColumnConstraints hGrow = new ColumnConstraints();
        hGrow.setHgrow(Priority.ALWAYS);
        hGrow.setHalignment(HPos.CENTER);
        uploadTable.getColumnConstraints().addAll(new ColumnConstraints(), hGrow, hGrow);

        uploadTablePane = new StackPane(uploadTable);
        VBox.setVgrow(uploadTablePane, Priority.ALWAYS);
        uploadTablePane.setAlignment(Pos.CENTER);

        getChildren().addAll(uploadTablePane, uploadTraineeMsg, uploadCommMsg, next);
        next.setDisable(true);
        next.setOnAction(e -> selectCommanders());

        getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }

    /**
     * Sets the list of commanders and provides user feedback on the validation.
     * @param commanderList The list of commanders to set.
     */
    public void setCommanderList(ArrayList<Commander> commanderList) {
        if (commanderList == null) {
            uploadCommMsg.setText("Invalid file.");
        } else if (commanderList.isEmpty()) {
            uploadCommMsg.setText("No available commanders.");
        } else {
            this.commanderList = commanderList;
            uploadCommMsg.setText(String.format("Successfully uploaded %d commanders and aides.", commanderList.size()));
        }
    }

    /**
     * Sets the list of trainees and provides user feedback on the validation.
     * @param traineeList The list of trainees to set.
     */
    public void setTraineeList(ArrayList<Player> traineeList) {
        this.traineeList = traineeList;
        if (traineeList == null) {
            uploadTraineeMsg.setText("Invalid file.");
        } else if (traineeList.isEmpty()) {
            uploadTraineeMsg.setText("No available trainees");
        } else {
            uploadTraineeMsg.setText(String.format("Successfully uploaded %d trainees.", traineeList.size()));
        }
    }

    /**
     * Initialise the selection screen with any
     * previously stored commander or trainee lists.
     */
    public void init() {
        if (parent.getCommanderList() != null) setCommanderList(parent.getCommanderList());
        if (parent.getTraineeList() != null) setTraineeList(parent.getTraineeList());
        traineeLinksContainer.getChildren().clear();
        if (Settings.TRAINEE_LINKS.isEmpty())
            traineeLinksContainer.getChildren().add(makeCenteredLabel("Add Links to Trainee Sheets in Settings"));
        Settings.TRAINEE_LINKS.forEach(e ->
                traineeLinksContainer.getChildren().add(makeLinkButton(e.getKey(), e.getValue(), false)));
        commLinksContainer.getChildren().clear();
        if (Settings.COMM_LINKS.isEmpty())
            commLinksContainer.getChildren().add(makeCenteredLabel("Add Links to Commander Sheets in Settings"));
        Settings.COMM_LINKS.forEach(e ->
                commLinksContainer.getChildren().add(makeLinkButton(e.getKey(), e.getValue(), true)));
        update();
    }

    /**
     * Once a commander list and trainee list has been uploaded,
     * go to the next page to select which commanders will be present.
     */
    private void selectCommanders() {
        if (commanderList == null || traineeList == null) return;
        parent.setCommanderList(commanderList);
        parent.setTraineeList(traineeList);
        parent.navigateCommanderSelect();
    }

    /**
     * Update next button after uploading CSV.
     * If both trainees and commanders have been uploaded,
     * allow user to go to next page by enabling next button.
     */
    private void update() {
        next.setDisable(commanderList == null || traineeList == null);
    }

    /**
     * Allow user to select a csv file containing a list of sign-ups.
     */
    private void uploadPlayerCSV(String link, boolean forCommanders) {
        uploadTable.setDisable(true);
        uploadTablePane.getChildren().add(progressBar);
        boolean fromLink = link != null;

        try {
            if (fromLink) {
                Task<InputStreamReader> linkDL = new Task<>() {
                    @Override
                    protected InputStreamReader call() throws Exception {
                        URL fileLink = new URL(link);
                        try {
                            return new InputStreamReader(fileLink.openStream(), StandardCharsets.UTF_8);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                };
                dlThread = new Thread(linkDL);
                linkDL.setOnSucceeded(e -> parsePlayerFile(linkDL.getValue(), forCommanders));
                dlThread.start();
            } else {
                FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
                FileChooser csvChooser = new FileChooser();
                csvChooser.getExtensionFilters().add(csvFilter);
                File input = csvChooser.showOpenDialog(this.getScene().getWindow());
                // If input = null, operation was canceled.
                if (input != null) parsePlayerFile(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8), forCommanders);
                else {
                    resume();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsePlayerFile(InputStreamReader fileStream, boolean forCommanders) {

        if(fileStream != null) { // Try parsing sign-ups csv.
            SignupsParser signupsParser = new SignupsParser();
            ArrayList<Player> players = signupsParser.parse(fileStream);
            if (forCommanders) uploadCommanders(players);
            else uploadTrainees(players);
        }
        else if (forCommanders) uploadCommMsg.setText("File not found or internet issues.");
        else uploadTraineeMsg.setText("File not found or internet issues.");
        resume();
    }

    private void uploadCommanders(ArrayList<Player> commanders) {
        // Keep only commanders and aides.
        if (commanders != null) setCommanderList(commanders.stream()
                    .filter(p -> p.getTier().toLowerCase().equals("commander") ||
                            p.getTier().toLowerCase().equals("aide"))
                    .map(Commander::new).collect(Collectors.toCollection(ArrayList::new)));
        else setCommanderList(null);
        update();
    }

    private void uploadTrainees(ArrayList<Player> trainees) {
        // Keep only trainees with an assigned tier between 0 and 3 included.
        if (trainees != null) trainees = trainees.stream()
                .filter(p -> p.getTier().matches("^([0123]|(trainee))$"))
                .collect(Collectors.toCollection(ArrayList::new));
        setTraineeList(trainees);
        update();
    }

    private Button makeLinkButton(String text, String link, boolean forCommanders) {
        Button btn = new Button();
        btn.setGraphic(makeCenteredLabel(text));
        btn.setOnAction(e -> uploadPlayerCSV(link, forCommanders));
        VBox.setVgrow(btn, Priority.ALWAYS);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return btn;
    }

    /**
     * Creates a simple label with a given text that is centered,
     * grows always and has no max size.
     * @param text The text for the label.
     * @return The centered label.
     */
    private Label makeCenteredLabel(String text) {
        Label centeredLabel = new Label(text);
        HBox.setHgrow(centeredLabel, Priority.ALWAYS);
        VBox.setVgrow(centeredLabel, Priority.ALWAYS);
        centeredLabel.setAlignment(Pos.CENTER);
        centeredLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return centeredLabel;
    }

    private void resume() {
        uploadTablePane.getChildren().remove(progressBar);
        uploadTable.setDisable(false);
    }
}

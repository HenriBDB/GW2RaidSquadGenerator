package view;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import signups.Player;
import signups.SignupsParser;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Selection Screen to select commander and trainee sign-up sheets.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class PlayerListSelect extends VBox implements AppContent {

    private static final String ALERT_ERROR, ALERT_INVALID_FILE, ALERT_NO_FILE;
    static {
        ALERT_ERROR = "Error";
        ALERT_INVALID_FILE = "File does not contain any valid sign-ups.";
        ALERT_NO_FILE = "No file has been selected.";
    }
    Label uploadCommMsg = new Label(), uploadTraineeMsg = new Label();
    Button uploadCommanders = new Button("Upload Commander and Aide List");
    Button uploadTrainees = new Button("Upload Trainee List");
    Button next = new Button("Save Changes");

    ArrayList<Player> commanderList, traineeList;

    public PlayerListSelect() {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        HBox commies = new HBox(10);
        commies.getChildren().addAll(uploadCommanders, uploadCommMsg);
        commies.setAlignment(Pos.CENTER);
        HBox trainees = new HBox(10);
        trainees.getChildren().addAll(uploadTrainees, uploadTraineeMsg);
        trainees.setAlignment(Pos.CENTER);

        getChildren().addAll(trainees, commies, next);
        next.setDisable(true);
        uploadTrainees.setOnAction(e -> uploadTraineeCSV());
        uploadCommanders.setOnAction(e -> uploadCommanderCSV());
        next.setOnAction(e -> selectCommanders());
    }

    /**
     * Sets the list of commanders and provides user feedback on the validation.
     * @param commanderList The list of commanders to set.
     */
    public void setCommanderList(ArrayList<Player> commanderList) {
        if (commanderList == null || commanderList.isEmpty()) {
            this.commanderList = null;
            uploadCommMsg.setText("Invalid file.");
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
        if (traineeList == null || traineeList.isEmpty()) {
            this.traineeList = null;
            uploadTraineeMsg.setText("Invalid file.");
        } else {
            this.traineeList = traineeList;
            uploadTraineeMsg.setText(String.format("Successfully uploaded %d trainees.", traineeList.size()));
        }
    }

    /**
     * Initialise the selection screen with any
     * previously stored commander or trainee lists.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.getCommanderList() != null) setCommanderList(parent.getCommanderList());
        if (parent.getTraineeList() != null) setTraineeList(parent.getTraineeList());
        update();
    }

    /**
     * Once a commander list and trainee list has been uploaded,
     * go to the next page to select which commanders will be present.
     */
    private void selectCommanders() {
        if (commanderList == null || traineeList == null) return;
        App parent = (App) getParent();
        parent.setCommanderList(commanderList);
        parent.setTraineeList(traineeList);
        parent.setAndInitCenter(new CommanderSelect());
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
     * @return The list of sign-ups.
     */
    private ArrayList<Player> uploadPlayerCSV() {
        FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
        FileChooser csvChooser = new FileChooser();
        csvChooser.getExtensionFilters().add(csvFilter);

        File input = csvChooser.showOpenDialog(this.getScene().getWindow());
        if(input != null) { // Try parsing sign-ups csv.
            SignupsParser signupsParser = new SignupsParser();
            ArrayList<Player> players = signupsParser.parse(input);
            if (players.isEmpty()) {
                // Selected file does not contain any valid sign-ups.
                Alert alertBadFile = new Alert(Alert.AlertType.ERROR);
                alertBadFile.setTitle(ALERT_ERROR);
                alertBadFile.setHeaderText(null);
                alertBadFile.setContentText(ALERT_INVALID_FILE);
                alertBadFile.show();
            } else {
                return players;
            }
        } else { // cancel was clicked - alert
            Alert alertNoFile = new Alert(Alert.AlertType.ERROR);
            alertNoFile.setTitle(ALERT_ERROR);
            alertNoFile.setHeaderText(null);
            alertNoFile.setContentText(ALERT_NO_FILE);
            alertNoFile.show();
        }
        return null;
    }

    private void uploadCommanderCSV() {
        ArrayList<Player> commanderList = uploadPlayerCSV();
        // Keep only commanders and aides.
        if (commanderList != null) commanderList = commanderList.stream()
                .filter(p -> p.getTier().toLowerCase().equals("commander") ||
                p.getTier().toLowerCase().equals("aide")).collect(Collectors.toCollection(ArrayList::new));
        setCommanderList(commanderList);
        update();
    }

    private void uploadTraineeCSV() {
        ArrayList<Player> traineeList = uploadPlayerCSV();
        // Keep only trainees with an assigned tier between 0 and 3 included.
        if (traineeList != null) traineeList = traineeList.stream()
                .filter(p -> p.getTier().matches("[0123]"))
                .collect(Collectors.toCollection(ArrayList::new));
        setTraineeList(traineeList);
        update();
    }
}

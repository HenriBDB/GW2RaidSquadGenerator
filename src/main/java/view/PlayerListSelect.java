package view;

import app.Main;
import components.ThemedIcon;
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
import signups.Commander;
import signups.Player;
import signups.SignupsParser;

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

    private static final String COMM_LINK, TUE_LINK, THU_LINK, SAT_LINK;
    static {
        COMM_LINK = "https://docs.google.com/spreadsheets/d/1p7KrDZ1F65-9EZblf5aeHgAsUnnRFGFJLI97oZExRxM/export?format=csv&gid=1874569681";
        TUE_LINK = "https://docs.google.com/spreadsheets/d/1p7KrDZ1F65-9EZblf5aeHgAsUnnRFGFJLI97oZExRxM/export?format=csv&gid=1377878105";
        THU_LINK = "https://docs.google.com/spreadsheets/d/1p7KrDZ1F65-9EZblf5aeHgAsUnnRFGFJLI97oZExRxM/export?format=csv&gid=1414462221";
        SAT_LINK = "https://docs.google.com/spreadsheets/d/1p7KrDZ1F65-9EZblf5aeHgAsUnnRFGFJLI97oZExRxM/export?format=csv&gid=903269235";
    }
    App parent;
    Label uploadCommMsg = new Label(), uploadTraineeMsg = new Label();
    Button next = new Button("Save Changes");
    GridPane uploadTable;
    StackPane uploadTablePane;
    VBox progressBar;

    ArrayList<Player> traineeList;
    ArrayList<Commander> commanderList;

    public PlayerListSelect(App parent) {
        this.parent = parent;
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 10, 20, 10));
        setSpacing(20);

        progressBar = new VBox();
        ProgressBar bar = new ProgressBar();
        bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.getChildren().add(bar);
        progressBar.setAlignment(Pos.CENTER);

        Button tueSignups = new Button("Tue");
        tueSignups.setOnAction(e -> uploadPlayerCSV(TUE_LINK, false));
        VBox.setVgrow(tueSignups, Priority.ALWAYS);
        tueSignups.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button thuSignups = new Button("Thu");
        thuSignups.setOnAction(e -> uploadPlayerCSV(THU_LINK, false));
        VBox.setVgrow(thuSignups, Priority.ALWAYS);
        thuSignups.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button satSignups = new Button("Sat");
        satSignups.setOnAction(e -> uploadPlayerCSV(SAT_LINK, false));
        VBox.setVgrow(satSignups, Priority.ALWAYS);
        satSignups.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button uploadTrainees = new Button("Upload Trainee List");
        uploadTrainees.setOnAction(e -> uploadPlayerCSV(null, false));
        uploadTrainees.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button uploadCommLink = new Button("Use Commander Online Sheet");
        uploadCommLink.setOnAction(e -> uploadPlayerCSV(COMM_LINK, true));
        uploadCommLink.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button uploadCommanders = new Button("Upload Commander and Aide List");
        uploadCommanders.setOnAction(e -> uploadPlayerCSV(null, true));
        uploadCommanders.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox traineeLinksContainer = new VBox(10);
        traineeLinksContainer.setPadding(new Insets(0));
        traineeLinksContainer.getChildren().addAll(tueSignups, thuSignups, satSignups);

        HBox commanderUploads = new HBox(10);
        HBox traineeUploads = new HBox(10);

        commanderUploads.getChildren().addAll(uploadCommanders, uploadCommLink);
        traineeUploads.getChildren().addAll(uploadTrainees, traineeLinksContainer);

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
        uploadTable.add(uploadCommLink, 2, 2);

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
                        return new InputStreamReader(fileLink.openStream(), StandardCharsets.UTF_8);
                    }
                };
                Thread thread = new Thread(linkDL);
                linkDL.setOnSucceeded(e -> parsePlayerFile(linkDL.getValue(), forCommanders));
                thread.start();
            } else {
                FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
                FileChooser csvChooser = new FileChooser();
                csvChooser.getExtensionFilters().add(csvFilter);
                File input = csvChooser.showOpenDialog(this.getScene().getWindow());
                // If input = null, operation was canceled.
                if (input != null) parsePlayerFile(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8), forCommanders);
                else {
                    uploadTablePane.getChildren().remove(progressBar);
                    uploadTable.setDisable(false);
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
        uploadTablePane.getChildren().remove(progressBar);
        uploadTable.setDisable(false);
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
                .filter(p -> p.getTier().matches("[0123]"))
                .collect(Collectors.toCollection(ArrayList::new));
        setTraineeList(trainees);
        update();
    }
}

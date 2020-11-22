package com.crossroadsinn.view;

import com.crossroadsinn.components.PlayerListView;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import jfxtras.styles.jmetro.JMetroStyleClass;
import com.crossroadsinn.problem.SquadSolution;
import com.crossroadsinn.signups.Player;
import com.crossroadsinn.signups.SquadSaver;

import java.time.LocalDate;
import java.util.List;

/**
 * Page that displays a saved composition of squads.
 * Can navigate between saved compositions and export them to CSV.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SavedCompositions extends VBox implements AppContent {

    private static final String NO_SAVED_COMPS;
    static {
        NO_SAVED_COMPS = "There are currently no saved compositions...";
    }
    App parent;
    List<SquadSolution> squadComps;
    HBox squadView, squadCompLinks;
    Label compName, saveMsg;
    Button saveBtn;
    DatePicker saveDate;
    int current;

    public SavedCompositions(App parent) {
        super(10);
        this.parent = parent;
        setPadding(new Insets(0, 10, 0 ,10));
        setAlignment(Pos.CENTER);
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }

    public void init() {
        current = -1;
        getChildren().clear();
        if (parent.getSavedSolutions() == null || parent.getSavedSolutions().isEmpty()) {
            Label noComp = new Label(NO_SAVED_COMPS);
            getChildren().add(noComp);
        } else {
            squadComps = parent.getSavedSolutions();
            compName = new Label("");
            squadView = new HBox(10);
            squadCompLinks = new HBox(10);
            squadCompLinks.setAlignment(Pos.CENTER);
            squadView.setAlignment(Pos.CENTER);
            VBox.setVgrow(squadView, Priority.ALWAYS);
            for (int i=0; i < squadComps.size(); ++i) {
                Button showCompBtn = new Button(squadComps.get(i).getName());
                int finalI = i;
                showCompBtn.setOnAction(e -> displayComp(finalI));
                squadCompLinks.getChildren().add(showCompBtn);
            }
            displayComp(squadComps.size()-1);

            Region region = new Region();
            VBox.setVgrow(region, Priority.ALWAYS);

            saveDate = new DatePicker();
            // https://stackoverflow.com/questions/48238855/how-to-disable-past-dates-in-datepicker-of-javafx-scene-builder
            saveDate.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) < 0 );
                }
            });
            saveMsg = new Label();
            saveBtn = new Button("Save Squad Composition to CSV");
            saveBtn.setOnAction(e -> saveToCSV());
            HBox bottomPane = new HBox(10);
            bottomPane.setPadding(new Insets(10));
            bottomPane.getChildren().addAll(saveDate, saveBtn, saveMsg);
            bottomPane.setAlignment(Pos.CENTER);

            getChildren().clear();
            getChildren().addAll(squadCompLinks, compName, squadView, bottomPane);
        }
    }

    /**
     * Navigate to a given composition.
     * @param index The index of that composition in the saved list.
     */
    private void displayComp(int index) {
        if (index != current) {
            current = index;
            SquadSolution toDisplay = squadComps.get(index);
            compName.setText(toDisplay.getName());
            squadView.getChildren().clear();
            for (List<Player> squad : toDisplay.getSquads()) {
                PlayerListView view = new PlayerListView(FXCollections.observableList(squad));
                HBox.setHgrow(view, Priority.ALWAYS);
                VBox.setVgrow(view, Priority.ALWAYS);
                squadView.getChildren().add(view);
            }
        }
    }

    /**
     * Save all the stored compositions to CSV.
     */
    private void saveToCSV() {
        if (saveDate.getValue() == null) saveMsg.setText("Please select a date.");
        else {
            saveBtn.setDisable(true);
            saveMsg.setText("Saving to CSV...");
            boolean saveSuccessful = SquadSaver.exportToCSV(squadComps, parent.getTraineeList(), saveDate.getValue().toString());
            if (saveSuccessful) saveMsg.setText("Successfully saved to CSV.");
            else saveMsg.setText("Did not save to CSV.");
            saveBtn.setDisable(false);
        }
    }
}

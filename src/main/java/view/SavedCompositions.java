package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import problem.SquadSolution;
import signups.Player;
import signups.SquadSaver;

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
    List<SquadSolution> squadComps;
    HBox squadView, squadCompLinks;
    Label compName, saveMsg;
    Button saveBtn;
    int current = -1;

    public SavedCompositions() {
        super(10);
        setAlignment(Pos.TOP_CENTER);
    }

    public void init() {
        App parent = (App) getParent();
        if (parent.getSavedSolutions() == null || parent.getSavedSolutions().isEmpty()) {
            getChildren().add(new Label(NO_SAVED_COMPS));
        } else {
            squadComps = parent.getSavedSolutions();
            compName = new Label("");
            squadView = new HBox(10);
            squadCompLinks = new HBox(10);
            squadCompLinks.setAlignment(Pos.CENTER);
            squadView.setAlignment(Pos.CENTER);
            for (int i=0; i < squadComps.size(); ++i) {
                Button showCompBtn = new Button(squadComps.get(i).getName());
                int finalI = i;
                showCompBtn.setOnAction(e -> displayComp(finalI));
                squadCompLinks.getChildren().add(showCompBtn);
            }
            displayComp(squadComps.size()-1);

            Region region = new Region();
            VBox.setVgrow(region, Priority.ALWAYS);

            saveMsg = new Label();
            saveBtn = new Button("Save Squad Composition to CSV");
            saveBtn.setOnAction(e -> saveToCSV());
            HBox bottomPane = new HBox(10);
            bottomPane.setPadding(new Insets(10));
            bottomPane.getChildren().addAll(saveBtn, saveMsg);
            bottomPane.setAlignment(Pos.CENTER);

            getChildren().clear();
            getChildren().addAll(squadCompLinks, compName, squadView, region, bottomPane);
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
                squadView.getChildren().add(new PlayerListView(FXCollections.observableList(squad)));
            }
        }
    }

    /**
     * Save all the stored compositions to CSV.
     */
    private void saveToCSV() {
        App parent = (App) getParent();
        saveBtn.setDisable(true);
        saveMsg.setText("Saving to CSV...");
        SquadSaver.saveCompsToCSV(squadComps, parent.getTraineeList());
        saveMsg.setText("Successfully saved to CSV.");
        saveBtn.setDisable(false);
    }
}

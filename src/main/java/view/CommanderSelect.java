package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import signups.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Selection Screen to select which commanders will be participating in the training.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class CommanderSelect extends BorderPane implements AppContent{

    Label confirmMsg;
    VBox commanderCheckboxes = new VBox(10);
    CommanderTable commanderTable;


    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.getCommanderList() != null && parent.getTraineeList() != null) {
            commanderCheckboxes.setAlignment(Pos.CENTER_LEFT);
            ScrollPane commieListPane = new ScrollPane();
            commieListPane.setContent(commanderCheckboxes);
            commieListPane.setPadding(new Insets(0, 10, 0, 10));

            StackPane content = new StackPane();
            if (parent.getCommanderList() != null) {
                commanderTable = new CommanderTable(FXCollections.observableList(parent.getCommanderList()));
            }
            content.getChildren().addAll(commanderTable);
            content.setPadding(new Insets(10));

            setCenter(content);

            confirmMsg = new Label();
            Button confirmBtn = new Button("Confirm");
            confirmBtn.setOnAction(e -> confirmChoices());

            HBox bottomPane = new HBox(10);
            bottomPane.setPadding(new Insets(10));
            bottomPane.getChildren().addAll(confirmBtn, confirmMsg);
            bottomPane.setAlignment(Pos.CENTER);
            setBottom(bottomPane);
        }
    }

    /**
     * Confirm choice of commanders and aides and move on to planning the Squad.
     */
    private void confirmChoices() {
        App parent = (App) getParent();
        if (commanderTable.getItems().stream().anyMatch(p -> p.getChosenRoles().get() != 0)) {
            ArrayList<Player> selectedCommanders = commanderTable.getItems()
                    .stream().filter(p -> p.getChosenRoles().get() != 0)
                    .map(c -> {
                        Player p = new Player(c);
                        p.setRoles(c.getChosenRoles().get());
                        return p;
                    }).collect(Collectors.toCollection(ArrayList::new));
            parent.setSelectedCommanderList(selectedCommanders);
            parent.setAndInitCenter(new Solving());
        } else {
            confirmMsg.setText("Please select at least one commander.");
        }
    }
}

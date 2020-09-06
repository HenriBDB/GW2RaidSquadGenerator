package com.crossroadsinn.view;

import com.crossroadsinn.components.CommanderTable;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import jfxtras.styles.jmetro.JMetroStyleClass;
import com.crossroadsinn.signups.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Selection Screen to select which commanders will be participating in the training.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class CommanderSelect extends BorderPane implements AppContent{

    App parent;
    Label confirmMsg;
    VBox commanderCheckboxes = new VBox(10);
    CommanderTable commanderTable;

    public CommanderSelect(App parent) {
        this.parent = parent;
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        commanderCheckboxes.setAlignment(Pos.CENTER_LEFT);
        ScrollPane commieListPane = new ScrollPane();
        commieListPane.setContent(commanderCheckboxes);
        commieListPane.setPadding(new Insets(0, 10, 0, 10));
        commanderTable = new CommanderTable(FXCollections.observableArrayList());
        commanderTable.getStyleClass().add("alternating-row-colors");

        StackPane content = new StackPane();

        content.getChildren().addAll(commanderTable);
        content.setPadding(new Insets(10));

        setCenter(content);

        confirmMsg = new Label();
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(e -> {
            if (getParent() != null) confirmChoices();
        });

        HBox bottomPane = new HBox(10);
        bottomPane.setPadding(new Insets(10));
        bottomPane.getChildren().addAll(confirmBtn, confirmMsg);
        bottomPane.setAlignment(Pos.CENTER);
        setBottom(bottomPane);
    }

    /**
     * Initialise the view with data from the parent.
     * Should only be called by an App parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.getCommanderList() != null) {
            commanderTable.getItems().clear();
            commanderTable.getItems().addAll(parent.getCommanderList());
        }
    }

    /**
     * Confirm choice of commanders and aides and move on to planning the Squad.
     */
    private void confirmChoices() {
        try {
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
                parent.navigateSolving();
            } else {
                confirmMsg.setText("Please select at least one commander.");
            }
        } catch (ClassCastException e) {
            confirmMsg.setText("An error occurred...");
        }
    }
}

package view;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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

    VBox commanderCheckboxes = new VBox(10);
    ListView<String> selectedCommies = new ListView<>();
    ListView<String> selectedAides = new ListView<>();

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.commanderList != null && parent.traineeList != null) {
            populateCommieList();
            commanderCheckboxes.setAlignment(Pos.CENTER_LEFT);
            ScrollPane commieListPane = new ScrollPane();
            commieListPane.setContent(commanderCheckboxes);
            commieListPane.setPadding(new Insets(0, 10, 0, 10));

            VBox selectedPlayers = new VBox(10);
            selectedPlayers.getChildren().addAll(new Label("Selected Commanders: "), selectedCommies,
                    new Label("Selected Aides: "), selectedAides);

            HBox content = new HBox(10);
            content.setAlignment(Pos.CENTER);
            content.getChildren().addAll(commieListPane, new Separator(Orientation.VERTICAL), selectedPlayers);
            content.setPadding(new Insets(10));

            Button confirmBtn = new Button("Confirm");
            confirmBtn.setOnAction(e -> confirmChoices());

            setCenter(content);
            StackPane bottomPane = new StackPane();
            bottomPane.setPadding(new Insets(10));
            bottomPane.getChildren().add(confirmBtn);
            setBottom(bottomPane);
        }
    }

    /**
     * Confirm choice of commanders and aides and move on to planning the Squad.
     */
    private void confirmChoices() {
        App parent = (App) getParent();
        parent.selectedCommanderList = commanderCheckboxes.getChildren()
                .stream().filter(e -> e instanceof PlayerCheckBox)
                .filter(e -> ((PlayerCheckBox) e).isSelected())
                .map(e -> ((PlayerCheckBox) e).getPlayer())
                .collect(Collectors.toCollection(ArrayList::new));
        parent.setAndInitCenter(new Solving());
    }

    /**
     * Populate the VBox with a list of checkboxes
     * associated to each commander/aide provided.
     */
    private void populateCommieList() {
        App parent = (App) getParent();
        commanderCheckboxes.getChildren().clear();
        // Add commanders:
        commanderCheckboxes.getChildren().add(new Label("Select Commanders: "));
        commanderCheckboxes.getChildren().addAll(parent.commanderList.stream()
                .filter(c -> c.getTier().toLowerCase().contains("commander"))
                .map(c -> {
                    PlayerCheckBox pc = new PlayerCheckBox(c);
                    pc.setOnAction(this::playerCheckboxOnClick);
                    return pc;
                })
                .collect(Collectors.toList()));
        commanderCheckboxes.getChildren().add(new Region());
        // Add aides:
        commanderCheckboxes.getChildren().add(new Label("Select Aides: "));
        commanderCheckboxes.getChildren().addAll(parent.commanderList.stream()
                .filter(c -> c.getTier().toLowerCase().contains("aide"))
                .map(c -> {
                    PlayerCheckBox pc = new PlayerCheckBox(c);
                    pc.setOnAction(this::playerCheckboxOnClick);
                    return pc;
                })
                .collect(Collectors.toList()));
    }

    /**
     * If commander or aide is selected, add him/her to the appropriate list.
     * If deselected, remove him/her from the appropriate list.
     * @param e The CheckBox action event.
     */
    private void playerCheckboxOnClick(Event e) {
        PlayerCheckBox pc = (PlayerCheckBox) e.getSource();
        boolean isCommander = pc.getPlayer().getTier().toLowerCase().contains("commander");
        if (pc.isSelected()) {
            if (isCommander) selectedCommies.getItems().add(pc.getPlayer().getGw2Account());
            else selectedAides.getItems().add(pc.getPlayer().getGw2Account());
        }
        else {
            if (isCommander) selectedCommies.getItems().remove(pc.getPlayer().getGw2Account());
            else selectedAides.getItems().remove(pc.getPlayer().getGw2Account());
        }
    }

}

/**
 * Wrapper checkbox that contains a player object.
 */
class PlayerCheckBox extends CheckBox {

    Player player;

    public PlayerCheckBox(Player player) {
        this.player = player;
        setText(player.getGw2Account());
    }

    public Player getPlayer() {
        return player;
    }
}

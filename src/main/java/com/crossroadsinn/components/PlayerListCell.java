package com.crossroadsinn.components;

import com.crossroadsinn.settings.Settings;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import com.crossroadsinn.signups.Player;

/**
 * Custom ListView cell that stores and displays a player.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class PlayerListCell extends ListCell<Player> {

    // Icons to use for the roleAssignAndReset button.
    static Image CROSS_ICON, PERSON_ICON;
    static {
        CROSS_ICON = new Image(Settings.getAssetFilePath("images/CrossIcon.png"));
        PERSON_ICON = new Image(Settings.getAssetFilePath("images/PersonIcon.png"));
    }

    Player player;
    Button roleAssignAndReset = new Button();
    String roleFilter;

    public PlayerListCell() {
        this(null);
    }

    public PlayerListCell(String roleFilter) {
        makeDraggable();
        roleAssignAndReset.setOnAction(e -> toggleRoleAssignAndReset());
        roleAssignAndReset.setPrefSize(16, 16);
        roleAssignAndReset.getStyleClass().add("transparent");
        this.roleFilter = roleFilter;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);
        if (empty) {
            setTooltip(null);
            setText(null);
            setGraphic(null);
            setId("");
        } else {
            this.player = player;
            this.setTooltip(new Tooltip(getTooltipContent()));
            setGraphic(getCellGraphic());
            updateStyle();
        }
    }

    public void updateStyle() {
        if (player != null) {
            if (roleFilter == null || player.getSimpleRoleList().stream().anyMatch(r -> r.equals(roleFilter))) {
                if (player.getTier().toLowerCase().equals("commander")) setId("comm-visible");
                else if (player.getTier().toLowerCase().equals("aide")) setId("aide-visible");
                else setId("player-visible");
            } else {
                if (player.getTier().toLowerCase().equals("commander")) setId("comm-filtered");
                else if (player.getTier().toLowerCase().equals("aide")) setId("aide-filtered");
                else setId("player-filtered");
            }
        }
    }

    /**
     * Generates the graphical content of a player
     * cell with that cell's player's data.
     * @return the graphic to display in the cell.
     */
    private HBox getCellGraphic() {
        if (player == null) return null;
        // Update Role assign and reset button graphic.
        if (player.getAssignedRole() != null) roleAssignAndReset.setGraphic(new ImageView(CROSS_ICON));
        else roleAssignAndReset.setGraphic(new ImageView(PERSON_ICON));
        HBox graphic = new HBox(10);
        Label playerName = new Label(player.toString());
        playerName.setStyle("-fx-text-fill: inherit;");
        graphic.getChildren().addAll(roleAssignAndReset, playerName);
        return graphic;
    }

    /**
     * If player is already assigned a role, clear it.
     * Otherwise, show role assignment window to allow
     * user to assign a role to that player.
     */
    private void toggleRoleAssignAndReset() {
        if (player != null) {
            if (player.getAssignedRole() == null) {
                if (RoleSelectPopUp.assignPlayerRolePopup(player)) setGraphic(getCellGraphic());
            } else player.setAssignedRole(0);
            setGraphic(getCellGraphic());
        }
    }

    /**
     * Generate tooltip content:
     * Player discord names
     * Tier: [0123]
     * Roles...
     * @return The string to use as tooltip.
     */
    private String getTooltipContent() {
        String roles = String.join(", ", player.getRoleList());
        String name = player.getDiscordName().isBlank() ? player.getGw2Account() : player.getDiscordName();
        return String.format("%s\nTier: %s\n%s", name, player.getTier(), roles);
    }

    /**
     * Allow dragging cards around ListViews.
     */
    private void makeDraggable() {
        setOnDragDetected(event -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(player.toString());
            dragboard.setContent(content);
            dragboard.setDragView(new ImageView(snapshot(null, null)).getImage());
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() instanceof PlayerListCell && event.getGestureSource() != this) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragDropped(event -> {
            boolean done = false;
            if (event.getGestureSource() instanceof PlayerListCell && event.getGestureSource() != this) {
                PlayerListCell dragged = (PlayerListCell) event.getGestureSource();
                Player pDragged = dragged.getPlayer();
                ObservableList<Player> parentItems = this.getListView().getItems();

                dragged.getListView().getItems().remove(pDragged);
                int indexOfThis = parentItems.indexOf(this.getPlayer());
                if (indexOfThis == -1) {
                    // Dragged to empty cell, drop in bottom of list view.
                    parentItems.add(pDragged);
                } else {
                    // Drop dragged player over this cell.
                    parentItems.add(indexOfThis, pDragged);
                }
                done = true;
            }
            event.setDropCompleted(done);
            event.consume();
        });

        setOnDragDone(Event::consume);
    }
}

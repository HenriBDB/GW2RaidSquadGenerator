package com.crossroadsinn.components;

import com.crossroadsinn.problem.entities.Roles;
import com.crossroadsinn.settings.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import com.crossroadsinn.problem.entities.Player;
import java.util.Comparator;
import java.util.List;

/**
 * Custom ListView to display and store a list of players.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class PlayerListView extends ListView<Player> {

    public PlayerListView() {
        this(FXCollections.observableArrayList());
    }

    public PlayerListView(ObservableList<Player> items) {
        super(items);
        setCellFactory(p -> new PlayerListCell());
        setDraggingFeatures();
        getStylesheets().add(Settings.getAssetFilePath("style/list-view-custom.css"));
        setMaxWidth(500);
    }

    public static void sortPlayerList(List<Player> playerList) {
        playerList.sort(Comparator.comparingInt(PlayerListView::getRoleOrder));
        playerList.sort(Comparator.comparingInt(PlayerListView::getRankOrder));
    }

    private static int getRankOrder(Player player) {
        if (player.getTier().toLowerCase().equals("commander")) return 0;
        else if (player.getTier().toLowerCase().equals("aide")) return 1;
        else return 2;
    }

    private static int getRoleOrder(Player player) {
        if (player.getAssignedRole() == null) return Roles.getAllRoles().size();
        return Roles.getAllRoles().indexOf(player.getAssignedRole());
    }

    /**
     * Allow dragging of PlayerListCells into this ListView.
     */
    private void setDraggingFeatures() {
        setOnDragOver(event -> {
            if (event.getGestureSource() instanceof PlayerListCell) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragDropped(event -> {
            boolean done = false;
            if (event.getGestureSource() instanceof PlayerListCell) {
                PlayerListCell dragged = (PlayerListCell) event.getGestureSource();
                getItems().add(dragged.getPlayer());
                dragged.getListView().getItems().remove(dragged.getPlayer());
                done = true;
            }
            event.setDropCompleted(done);
            event.consume();
        });

        setOnDragDone(Event::consume);
    }
}

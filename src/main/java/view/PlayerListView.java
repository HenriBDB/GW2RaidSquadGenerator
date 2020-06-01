package view;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import signups.Player;

public class PlayerListView extends ListView<Player> {

    public PlayerListView() {
        setCellFactory(p -> new PlayerListCell());
        setDraggingFeatures();
    }

    public PlayerListView(ObservableList<Player> items) {
        super(items);
        setCellFactory(p -> new PlayerListCell());
        setDraggingFeatures();
    }

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
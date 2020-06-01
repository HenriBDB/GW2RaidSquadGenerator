package view;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import signups.Player;

public class PlayerListCell extends ListCell<Player> {

    Player player;

    public PlayerListCell() {
        makeDraggable();
    }

    public Player getPlayer() {
        return player;
    }

    public void updateItem(Player player, boolean empty) {
        super.updateItem(player, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            this.player = player;
            setText(player.toString());
        }
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
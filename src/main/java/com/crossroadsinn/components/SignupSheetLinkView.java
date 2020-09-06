package com.crossroadsinn.components;

import com.crossroadsinn.datatypes.LinkPair;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import jfxtras.styles.jmetro.JMetroStyleClass;

/**
 * ListView made for string pairs that displays 2 text fields
 * that can be used to edit the string pairs.
 *
 * @author Eren Bole.8720
 */
public class SignupSheetLinkView extends ListView<LinkPair<String, String>> {

    public SignupSheetLinkView(ObservableList<LinkPair<String, String>> elements) {
        super(elements);
        prefHeightProperty().bind(Bindings.size(elements).multiply(46));
        setMinHeight(45);
        // Disable selection.
        setSelectionModel(new MultipleSelectionModel<>() {
            public ObservableList<Integer> getSelectedIndices() {
                return FXCollections.emptyObservableList();
            }
            public ObservableList<LinkPair<String, String>> getSelectedItems() {
                return FXCollections.emptyObservableList();
            }
            public void selectIndices(int index, int... indices) {}
            public void selectAll() {}
            public void selectFirst() {}
            public void selectLast() {}
            public void clearAndSelect(int index) {}
            public void select(int index) {}
            public void select(LinkPair<String, String> obj) {}
            public void clearSelection(int index) {}
            public void clearSelection() {}
            public boolean isSelected(int index) {
                return false;
            }
            public boolean isEmpty() {
                return false;
            }
            public void selectPrevious() {}
            public void selectNext() { }
        });
        getStyleClass().addAll("no-scroll-bar");
        setCellFactory(e -> new LinkCell());
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}

class LinkCell extends ListCell<LinkPair<String, String>> {

    TextField linkName, linkValue;
    HBox cell;

    public LinkCell() {
        linkName = new TextField();
        linkValue = new TextField();
        linkName.textProperty().addListener((e, oldVal, newVal) ->
                getItem().setKey(newVal));
        linkValue.textProperty().addListener((e, oldVal, newVal) ->
                getItem().setValue(newVal));
        HBox.setHgrow(linkName, Priority.ALWAYS);
        HBox.setHgrow(linkValue, Priority.ALWAYS);
        ImageView crossIcon = new ImageView(new Image("images/CrossIcon.png"));
        Button removeLink = new Button();
        removeLink.setGraphic(crossIcon);
        removeLink.getStyleClass().add("transparent");
        removeLink.setOnAction(e -> {
            getListView().getItems().remove(getItem());
        });
        cell = new HBox(10);
        cell.getChildren().addAll(linkName, linkValue, removeLink);
    }

    @Override
    protected void updateItem(LinkPair<String, String> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            linkName.setText(item.getKey());
            linkValue.setText(item.getValue());
            setGraphic(cell);
        }
    }
}


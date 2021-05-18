package com.crossroadsinn.components;

import com.crossroadsinn.settings.Squad;
import com.crossroadsinn.settings.Squads;
import com.crossroadsinn.settings.Settings;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class SquadsTableCheckbox extends TableCell<Squad, Boolean> {
    CheckBox checkBox = new CheckBox();
    HBox container = new HBox();

    public SquadsTableCheckbox() {
        super();
        checkBox.setPadding(new Insets(0, 5, 0, 5));
        container.getChildren().add(checkBox);
        container.setAlignment(Pos.CENTER);

        checkBox.setOnAction(e -> {
            if (getTableRow() != null) {
                Squad squad = getTableRow().getItem();
                if (checkBox.isSelected()) {
                    squad.setEnabled();
                } else {
                    squad.setDisabled();
                }
            }
        });
    }

    @Override
    protected void updateItem(Boolean enabled, boolean empty) {
        super.updateItem(enabled, empty);
        if (empty || getTableRow() == null) {
            setGraphic(null);
        } else {
            setGraphic(container);
            Squad squad = getTableRow().getItem();
            if (squad != null) {
                checkBox.setSelected(squad.getEnabled());
            }
        }
        setText(null);
    }
}
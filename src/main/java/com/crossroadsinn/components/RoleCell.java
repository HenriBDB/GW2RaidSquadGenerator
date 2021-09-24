package com.crossroadsinn.components;

import com.crossroadsinn.problem.entities.Role;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import com.crossroadsinn.problem.entities.Commander;

import java.util.HashSet;
import java.util.Set;

/**
 * A table cell for a CommanderTable that represents a role for that commander.
 * The checkbox it contains can be selected/deselected to (un)choose that role
 * for the associated commander.
 * @author Eren Bole.8720
 * @version 1.1
 */
public class RoleCell extends TableCell<Commander, Set<Role>> {

    CheckBox checkBox = new CheckBox();
    HBox container = new HBox();
    Set<Role> roleMask;

    public RoleCell(Set<Role> roleMask) {
        super();
        this.roleMask = roleMask;
        checkBox.setPadding(new Insets(0, 5, 0, 5));
        container.getChildren().add(checkBox);
        container.setAlignment(Pos.CENTER);

        checkBox.setOnAction(e -> {
            if (getTableRow() != null) {
                Commander player = getTableRow().getItem();
                if (checkBox.isSelected()) {
                    player.getChosenRoles().addAll(roleMask);
                } else {
                    player.getChosenRoles().removeAll(roleMask);
                }
            }
        });
    }

    @Override
    protected void updateItem(Set<Role> rolesSelected, boolean empty) {
        super.updateItem(rolesSelected,empty);
        if (empty || getTableRow() == null) {
            setGraphic(null);
        } else {
            setGraphic(container);
            Commander player = getTableRow().getItem();
            if (player != null) {
                checkBox.setDisable(player.getRoles().stream().noneMatch(role -> roleMask.contains(role)));
                checkBox.setSelected(player.getRoles().containsAll(roleMask));
            }
        }
        setText(null);
    }
}

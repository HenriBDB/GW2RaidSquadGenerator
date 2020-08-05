package Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import signups.Commander;

/**
 * A table cell for a CommanderTable that represents a role for that commander.
 * The checkbox it contains can be selected/deselected to (un)choose that role
 * for the associated commander.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class RoleCell extends TableCell<Commander, Number> {

    CheckBox checkBox = new CheckBox();
    int roleMask;

    public RoleCell(int roleMask) {
        super();
        this.roleMask = roleMask;
        checkBox.setPadding(new Insets(0, 5, 0, 5));
        setAlignment(Pos.CENTER);

        checkBox.setOnAction(e -> {
            if (getTableRow() != null) {
                Commander player = getTableRow().getItem();
                if (checkBox.isSelected()) {
                    player.setChosenRoles(player.getChosenRoles().get() | (roleMask & player.getRoles()));
                } else {
                    player.setChosenRoles(player.getChosenRoles().get() & (~roleMask));
                }
            }
        });
    }

    @Override
    protected void updateItem(Number rolesSelected, boolean empty) {
        super.updateItem(rolesSelected,empty);
        if (empty || getTableRow() == null) {
            setGraphic(null);
        } else {
            setGraphic(checkBox);
            Commander player = getTableRow().getItem();
            if (player != null) {
                checkBox.setDisable((player.getRoles() & roleMask) == 0);
                checkBox.setSelected((player.getChosenRoles().get() & (roleMask & player.getRoles())) > 0);
            }
        }
        setText(null);
    }
}

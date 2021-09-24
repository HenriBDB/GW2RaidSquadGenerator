package com.crossroadsinn.components;

import com.crossroadsinn.problem.entities.Role;
import com.crossroadsinn.problem.entities.Roles;
import com.crossroadsinn.settings.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.crossroadsinn.problem.entities.Commander;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates a table of commanders where each column is populated with
 * a checkbox and linked to a role allowing enabling and disabling
 * that role for the commander of the row.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class CommanderTable extends TableView<Commander> {

    public CommanderTable(ObservableList<Commander> items) {
        super(items);
        init();
        getStylesheets().add(Settings.getAssetFilePath("style/table-custom.css"));
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Creates the columns and associates the cell factories.
     */
    private void init() {
        TableColumn<Commander, Set<Role>> checkAll = new TableColumn<>();
        checkAll.setCellValueFactory(new PropertyValueFactory<>("chosenRoles"));
        checkAll.setCellFactory(p -> new RoleCell(new HashSet<>(Roles.getAllRoles())));
        checkAll.setPrefWidth(40);

        TableColumn<Commander, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		getColumns().addAll(checkAll, name);
		
		for(Role role:Roles.getAllRoles()) {
			if (!role.getCommRole()) continue;
			TableColumn<Commander, Set<Role>> col = new TableColumn<>(role.getRoleName());
            checkAll.setCellValueFactory(new PropertyValueFactory<>("chosenRoles"));
			col.setCellFactory(p -> {
			    Set<Role> roleSet = new HashSet<>();
			    roleSet.add(role);
                return new RoleCell(new HashSet<Role>(roleSet));
            });
			getColumns().add(col);
		}
    }

}
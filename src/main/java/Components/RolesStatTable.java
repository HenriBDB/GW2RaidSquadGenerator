package Components;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RolesStatTable extends TableView<RoleStatRow> {

    public RolesStatTable(ObservableList<RoleStatRow> stats) {
        super(stats);
        init();
        setPrefWidth(275);
    }

    private void init() {
        TableColumn<RoleStatRow, String> name = new TableColumn<>("Role");
        name.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        TableColumn<RoleStatRow, Integer> assigned = new TableColumn<>("Assigned");
        assigned.setCellValueFactory(new PropertyValueFactory<>("assigned"));

        TableColumn<RoleStatRow, Integer> left = new TableColumn<>("Left");
        left.setCellValueFactory(new PropertyValueFactory<>("left"));

        getColumns().addAll(name, assigned, left);
    }


}

package components;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RolesStatTable extends TableView<RoleStatRow> {

    public RolesStatTable(ObservableList<RoleStatRow> stats) {
        super(stats);
        init();
//        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setMaxWidth(405);
    }

    private void init() {
        TableColumn<RoleStatRow, String> name = new TableColumn<>("Role");
        name.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        name.setMinWidth(175);

        TableColumn<RoleStatRow, Integer> assigned = new TableColumn<>("Assigned");
        assigned.setCellValueFactory(new PropertyValueFactory<>("assigned"));
        assigned.setMinWidth(120);

        TableColumn<RoleStatRow, Integer> left = new TableColumn<>("Left");
        left.setCellValueFactory(new PropertyValueFactory<>("left"));
        left.setMinWidth(100);

        getColumns().addAll(name, assigned, left);
    }


}

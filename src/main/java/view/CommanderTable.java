package view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import signups.Commander;

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
    }

    /**
     * Creates the columns and associates the cell factories.
     */
    private void init() {
        TableColumn<Commander, Number> checkAll = new TableColumn<>();
        checkAll.setCellValueFactory(f -> f.getValue().getChosenRoles());
        checkAll.setCellFactory(p -> new RoleCell(2047));
        checkAll.setPrefWidth(40);

        TableColumn<Commander, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("discordName"));

        TableColumn<Commander, Number> tank = new TableColumn<>("Tank");
        tank.setCellValueFactory(f -> f.getValue().getChosenRoles());
        tank.setCellFactory(p -> new RoleCell(1024));

        TableColumn<Commander, Number> cSupp = new TableColumn<>("Chrono Supp");
        cSupp.setCellValueFactory(f -> f.getValue().getChosenRoles());
        cSupp.setCellFactory(p -> new RoleCell(512));

        TableColumn<Commander, Number> druid = new TableColumn<>("Druid");
        druid.setCellValueFactory(f -> f.getValue().getChosenRoles());
        druid.setCellFactory(p -> new RoleCell(64));

        TableColumn<Commander, Number> qFB = new TableColumn<>("Quickness FB");
        qFB.setCellValueFactory(f -> f.getValue().getChosenRoles());
        qFB.setCellFactory(p -> new RoleCell(256));

        TableColumn<Commander, Number> hFB = new TableColumn<>("Heal FB");
        hFB.setCellValueFactory(f -> f.getValue().getChosenRoles());
        hFB.setCellFactory(p -> new RoleCell(32));

        TableColumn<Commander, Number> healRene = new TableColumn<>("Heal Renegade");
        healRene.setCellValueFactory(f -> f.getValue().getChosenRoles());
        healRene.setCellFactory(p -> new RoleCell(16));

        TableColumn<Commander, Number> alacrigade = new TableColumn<>("Alacrigade");
        alacrigade.setCellValueFactory(f -> f.getValue().getChosenRoles());
        alacrigade.setCellFactory(p -> new RoleCell(128));

        TableColumn<Commander, Number> offheal = new TableColumn<>("Offheal");
        offheal.setCellValueFactory(f -> f.getValue().getChosenRoles());
        offheal.setCellFactory(p -> new RoleCell(8));

        TableColumn<Commander, Number> bs = new TableColumn<>("Banners");
        bs.setCellValueFactory(f -> f.getValue().getChosenRoles());
        bs.setCellFactory(p -> new RoleCell(4));

        TableColumn<Commander, Number> dps = new TableColumn<>("DPS");
        dps.setCellValueFactory(f -> f.getValue().getChosenRoles());
        dps.setCellFactory(p -> new RoleCell(3));

        getColumns().addAll(checkAll, name, tank, cSupp, druid, qFB, hFB, healRene, alacrigade, offheal, bs, dps);
    }

}
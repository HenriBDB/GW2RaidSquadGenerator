package Components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RoleStatRow {

    IntegerProperty left = new SimpleIntegerProperty();
    IntegerProperty assigned = new SimpleIntegerProperty();
    StringProperty roleName = new SimpleStringProperty();

    public RoleStatRow(String roleName) {
        this(roleName, 0, 0);
    }

    public RoleStatRow(String roleName, int left, int assigned) {
        this.left.set(left);
        this.assigned.set(assigned);
        this.roleName.set(roleName);
    }

    public void decrementLeft() {
        left.set(left.get() - 1);
    }

    public void incrementLeft() {
        left.set(left.get() + 1);
    }

    public void decrementAssigned() {
        assigned.set(assigned.get() - 1);
    }

    public void incrementAssigned() {
        assigned.set(assigned.get() + 1);
    }

    public int getLeft() {
        return left.get();
    }

    public IntegerProperty leftProperty() {
        return left;
    }

    public int getAssigned() {
        return assigned.get();
    }

    public IntegerProperty assignedProperty() {
        return assigned;
    }

    public String getRoleName() {
        return roleName.get();
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }
}

package com.crossroadsinn.components;

import com.crossroadsinn.problem.entities.Role;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import com.crossroadsinn.problem.entities.Player;
import com.crossroadsinn.Main;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pop up window with a drop-down menu allowing
 * users to assign a role to a player.
 *
 * @author Eren Bole.8720
 */
public class RoleSelectPopUp {

    /**
     * Creates a combo box with all the roles a
     * given player signed up as in options.
     * @param player the given player.
     * @return the ComboBox with role names.
     */
    private static ComboBox<Role> getDropDownSelector(Player player, String filteredRole) {
//        ComboBox<Role> dropDownSelector = new ComboBox<>();
//		for (Role role:player.getRoleObj()) {
//			dropDownSelector.getItems().add(role);
//		}
//        if (filteredRole == null || Arrays.stream(player.getRoleList()).noneMatch(Objects.requireNonNull(filteredRole)::equals)) {
//            dropDownSelector.getSelectionModel().selectFirst();
//        }
//        else {
//			//try to find a role with the same name
//			for (Role role:player.getRoleObj()) {
//				if (role.getRoleName() == filteredRole) {
//					dropDownSelector.getSelectionModel().select(role);
//					break;
//				} else dropDownSelector.getSelectionModel().selectFirst();
//			}
//        }
//        return dropDownSelector;
        return null;
    }

    /**
     * Popup window where user can select a role for a
     * player and update that player's assigned role.
     * @param player the given player.
     * @return false if cancelled, true if player updated.
     */
    public static boolean assignPlayerRolePopup(Player player, String filteredRole) {
        AtomicBoolean isRoleSelected = new AtomicBoolean(false);
        Stage popup = new Stage();

        ComboBox<Role> roleSelector = getDropDownSelector(player, filteredRole);

        HBox choices = new HBox(10);
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> popup.close());
        Button select = new Button("Select");
        select.setOnAction(e -> {
            Role role = roleSelector.getSelectionModel().getSelectedItem();
            if (role != null) {
                player.setAssignedRole(role);
                isRoleSelected.set(true);
            }
            popup.close();
        });
        choices.getChildren().addAll(cancel, select);
        choices.setAlignment(Pos.CENTER);

        VBox popupContent = new VBox(10);
        popupContent.getChildren().addAll(roleSelector, choices);
        popupContent.setAlignment(Pos.CENTER);

        double width = 200;
        double height = 100;

        // Center on screen: https://stackoverflow.com/questions/29350181/how-to-center-a-window-properly-in-java-fx
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        popup.setX((screenBounds.getWidth() - width) / 2);
        popup.setY((screenBounds.getHeight() - height) / 2);

        Scene scene = new Scene(popupContent, width, height);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        popup.setScene(scene);
        popup.showAndWait();

        return isRoleSelected.get();
    }
}
package view;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import signups.Player;

import java.util.ArrayList;

/**
 * Wrapper class for the BorderPane holding the App.
 * Contains the list of commanders and trainees being used.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class App extends BorderPane {

    // Since these variables should be accessible and modifiable by any node in this BorderPane,
    // there is no point in keeping them private.
    public ArrayList<Player> commanderList, selectedCommanderList, traineeList;

    public void setAndInitCenter(AppContent content) {
        setCenter(((Node) content));
        content.init();
    }
}

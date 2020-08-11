package app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import datatypes.LinkPair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.prefs.Preferences;

/**
 * A static class that stores app settings and preferences.
 * Uses the Java Preference API to store those preferences on the system.
 * Stores for user only.
 *
 * @author Eren Bole.8720
 */
public class Settings {

    private static final Preferences USER_PREFERENCES;
    public static final ObservableList<LinkPair<String, String>> TRAINEE_LINKS, COMM_LINKS;
    private static boolean DARK_MODE;
    private static final String TRAINEE_LINKS_KEY, COMM_LINKS_KEY, DARK_MODE_KEY;
    static {
        USER_PREFERENCES = Preferences.userNodeForPackage(Main.class);
        TRAINEE_LINKS = FXCollections.observableArrayList();
        COMM_LINKS = FXCollections.observableArrayList();
        TRAINEE_LINKS_KEY = "TRAINEE_LINKS";
        COMM_LINKS_KEY = "COMMANDER_LINKS";
        DARK_MODE_KEY = "DARK_MODE";
    }

    public static void init() {
        setLinksFromPreferences(TRAINEE_LINKS, TRAINEE_LINKS_KEY);
        setLinksFromPreferences(COMM_LINKS, COMM_LINKS_KEY);
        DARK_MODE = USER_PREFERENCES.getBoolean(DARK_MODE_KEY, false);
        Main.updateTheme(DARK_MODE);
    }

    /**
     * Store the preferences on the system.
     */
    public static void saveSettings() {
        updatePreferenceLinkList(TRAINEE_LINKS, TRAINEE_LINKS_KEY);
        updatePreferenceLinkList(COMM_LINKS, COMM_LINKS_KEY);
        USER_PREFERENCES.putBoolean(DARK_MODE_KEY, DARK_MODE);
    }

    public static void setDarkMode(boolean darkMode) {
        DARK_MODE = darkMode;
        Main.updateTheme(darkMode);
    }

    /**
     * Store a list of String pairs in JSON format on the system.
     * @param linksList The list to store.
     * @param preferenceKey The key to use when storing in system preferences.
     */
    private static void updatePreferenceLinkList(List<LinkPair<String, String>> linksList, String preferenceKey) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(linksList);
        } catch (JsonProcessingException ignore) {
        } finally {
            USER_PREFERENCES.put(preferenceKey, json);
        }
    }

    /**
     * Get system preferences and store them in the class fields.
     * @param linksList List to update.
     * @param preferenceKey The key referring to that list in the system.
     */
    private static void setLinksFromPreferences(List<LinkPair<String, String>> linksList, String preferenceKey) {
        linksList.clear();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonLinks = USER_PREFERENCES.get(preferenceKey, "");
        try {
            List<LinkPair<String, String>> linksListTmp = objectMapper.readValue(jsonLinks, new TypeReference<>() {});
            linksList.addAll(linksListTmp);
        } catch (JsonProcessingException ignored) {

        }
    }

    public static boolean isDarkMode() {
        return DARK_MODE;
    }
}

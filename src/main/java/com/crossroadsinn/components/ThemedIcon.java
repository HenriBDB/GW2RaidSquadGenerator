package com.crossroadsinn.components;

import com.crossroadsinn.Main;
import com.crossroadsinn.settings.Settings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxtras.styles.jmetro.Style;

/**
 * An image view containing an icon that can update with the theme.
 *
 * @author Eren Bole.8720
 */
public class ThemedIcon extends ImageView implements ThemeListener {

    String lightURL, darkURL;

    /**
     * Create the ImageView and provide the urls for the different themes.
     */
    public ThemedIcon(String lightURL, String darkURL) {
        super();
        this.lightURL = Settings.getAssetFilePath(lightURL);
        this.darkURL = Settings.getAssetFilePath(darkURL);
        updateTheme();
    }

    public void updateTheme() {
        if (Main.getTheme() == Style.LIGHT) setImage(new Image(lightURL));
        else if (Main.getTheme() == Style.DARK) setImage(new Image(darkURL));
        else setImage(null);
    }
}

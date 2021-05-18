module com.crossroadsinn.squadmaker {
    requires javafx.graphics;
    requires javafx.base;
    requires org.jfxtras.styles.jmetro;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.prefs;
    requires opencsv;
    requires org.controlsfx.controls;
    exports com.crossroadsinn.datatypes;
    exports com.crossroadsinn.signups;
    exports com.crossroadsinn.components;
    exports com.crossroadsinn.problem;
    exports com.crossroadsinn.settings;
    exports com.crossroadsinn;
}
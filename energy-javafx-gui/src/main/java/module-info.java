module energy.javafx_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens energy.javafx_gui to javafx.fxml;
    opens energy.javafx_gui.dto to com.fasterxml.jackson.databind, javafx.base;
    exports energy.javafx_gui;
    exports energy.javafx_gui.controller;
    opens energy.javafx_gui.controller to javafx.fxml;
}
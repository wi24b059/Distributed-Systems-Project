module energy.javafx_gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens energy.javafx_gui to javafx.fxml;
    exports energy.javafx_gui;
}
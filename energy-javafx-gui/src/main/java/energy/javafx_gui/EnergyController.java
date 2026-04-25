package energy.javafx_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EnergyController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
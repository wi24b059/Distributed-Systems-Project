package energy.javafx_gui.controller;

import energy.javafx_gui.dto.CurrentEnergyDto;
import energy.javafx_gui.dto.HistoricalEnergyDto;
import energy.javafx_gui.service.EnergyApiService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class EnergyController {
    private final EnergyApiService energyApiService = new EnergyApiService();
    private final ObservableList<HistoricalEnergyDto> historicalData =
            FXCollections.observableArrayList();

    @FXML
    private Label hourLabel;

    @FXML
    private Label communityDepletedLabel;

    @FXML
    private Label gridPortionLabel;

    @FXML
    private TextField startField;

    @FXML
    private TextField endField;

    @FXML
    private TableView<HistoricalEnergyDto> historicalTable;

    @FXML
    private TableColumn<HistoricalEnergyDto, String> hourColumn;

    @FXML
    private TableColumn<HistoricalEnergyDto, Double> communityProducedColumn;

    @FXML
    private TableColumn<HistoricalEnergyDto, Double> communityUsedColumn;

    @FXML
    private TableColumn<HistoricalEnergyDto, Double> gridUsedColumn;

    @FXML
    public void initialize() {
        hourColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHour()));

        communityProducedColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCommunity_produced()));

        communityUsedColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCommunity_used()));

        gridUsedColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getGrid_used()));

        historicalTable.setItems(historicalData);

        startField.setPromptText("2025-01-10T06:00:00");
        endField.setPromptText("2025-01-10T14:00:00");
    }

    @FXML
    protected void onRefreshCurrentData() {
        try {
            CurrentEnergyDto currentEnergyDto = energyApiService.getCurrentEnergyData();

            hourLabel.setText(currentEnergyDto.getHour());
            communityDepletedLabel.setText(String.format("%.2f %%", currentEnergyDto.getCommunity_depleted()));
            gridPortionLabel.setText(String.format("%.2f %%", currentEnergyDto.getGrid_portion()));
        } catch (Exception exception) {
            showError("Could not load current energy data.");
        }
    }

    @FXML
    protected void onShowHistoricalData() {
        try {
            String start = startField.getText();
            String end = endField.getText();

            if (start == null || start.isBlank() || end == null || end.isBlank()) {
                showError("Please enter start and end datetime in the format yyyy-MM-ddTHH:mm:ss.");
                return;
            }

            List<HistoricalEnergyDto> result =
                    energyApiService.getHistoricalEnergyData(start, end);

            historicalData.setAll(result);
        } catch (Exception exception) {
            showError("Could not load historical energy data.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
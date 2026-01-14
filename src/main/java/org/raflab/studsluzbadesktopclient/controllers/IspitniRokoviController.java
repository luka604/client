package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.dtos.IspitniRokDTO;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.springframework.stereotype.Component;

@Component
public class IspitniRokoviController {

    private final IspitniRokService ispitniRokService;
    private final MainView mainView;

    @FXML
    private TableView<IspitniRokDTO> tabelaIspitniRokovi;

    @FXML
    private TextField nazivTf;

    @FXML
    private DatePicker pocetakDp;

    @FXML
    private DatePicker krajDp;

    @FXML
    private Label statusLabel;

    private IspitniRokDTO selectedRok;

    public IspitniRokoviController(IspitniRokService ispitniRokService, MainView mainView) {
        this.ispitniRokService = ispitniRokService;
        this.mainView = mainView;
    }

    @FXML
    public void initialize() {
        loadIspitniRokovi();

        tabelaIspitniRokovi.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRok = newSelection;
            }
        });
    }

    private void loadIspitniRokovi() {
        ispitniRokService.getAllIspitniRokoviAsync()
                .collectList()
                .subscribe(
                        rokovi -> Platform.runLater(() -> {
                            tabelaIspitniRokovi.setItems(FXCollections.observableArrayList(rokovi));
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju ispitnih rokova: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handlePrikaziIspite() {
        if (selectedRok == null) {
            showError("Molimo izaberite ispitni rok.");
            return;
        }

        IspitiController.setSelectedIspitniRokId(selectedRok.getId());
        mainView.changeRoot("ispiti");
    }

    @FXML
    public void handleNoviRok() {
        nazivTf.clear();
        pocetakDp.setValue(null);
        krajDp.setValue(null);
        statusLabel.setText("");
    }

    @FXML
    public void handleDodajRok() {
        if (nazivTf.getText() == null || nazivTf.getText().trim().isEmpty()) {
            showError("Naziv ispitnog roka je obavezan.");
            return;
        }
        if (pocetakDp.getValue() == null) {
            showError("Datum pocetka je obavezan.");
            return;
        }
        if (krajDp.getValue() == null) {
            showError("Datum kraja je obavezan.");
            return;
        }
        if (pocetakDp.getValue().isAfter(krajDp.getValue())) {
            showError("Datum pocetka mora biti pre datuma kraja.");
            return;
        }

        IspitniRokDTO dto = new IspitniRokDTO();
        dto.setNaziv(nazivTf.getText().trim());
        dto.setPocetak(pocetakDp.getValue());
        dto.setKraj(krajDp.getValue());

        ispitniRokService.createAsync(dto)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Ispitni rok uspesno dodat!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            loadIspitniRokovi();
                            handleNoviRok();
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri dodavanju ispitnog roka: " + error.getMessage());
                        })
                );
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

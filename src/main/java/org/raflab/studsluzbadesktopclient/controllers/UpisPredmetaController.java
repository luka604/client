package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.dtos.*;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.services.SkolskaGodinaService;
import org.raflab.studsluzbadesktopclient.services.SlusaPredmetService;
import org.springframework.stereotype.Component;

@Component
public class UpisPredmetaController {

    private final SlusaPredmetService slusaPredmetService;
    private final SkolskaGodinaService skolskaGodinaService;
    private final PredmetService predmetService;

    @FXML
    private TextField brojIndeksaTf;

    @FXML
    private ComboBox<PredmetDTO> predmetCb;

    @FXML
    private ComboBox<SkolskaGodinaDTO> skolskaGodinaCb;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField pretraziIndeksTf;

    @FXML
    private TableView<SlusaPredmetDTO> tabelaPredmeti;

    public UpisPredmetaController(SlusaPredmetService slusaPredmetService,
                                   SkolskaGodinaService skolskaGodinaService,
                                   PredmetService predmetService) {
        this.slusaPredmetService = slusaPredmetService;
        this.skolskaGodinaService = skolskaGodinaService;
        this.predmetService = predmetService;
    }

    @FXML
    public void initialize() {
        loadSkolskeGodine();
        loadPredmeti();
    }

    private void loadSkolskeGodine() {
        skolskaGodinaService.getAllAsync()
                .collectList()
                .subscribe(
                        godine -> Platform.runLater(() -> {
                            skolskaGodinaCb.setItems(FXCollections.observableArrayList(godine));
                            if (!godine.isEmpty()) {
                                skolskaGodinaCb.getSelectionModel().selectFirst();
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju skolskih godina: " + error.getMessage());
                        })
                );
    }

    private void loadPredmeti() {
        predmetService.getAllPredmetiAsync()
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() -> {
                            predmetCb.setItems(FXCollections.observableArrayList(predmeti));
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju predmeta: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handleUpisiStudenta() {
        String brojIndeksa = brojIndeksaTf.getText();
        if (brojIndeksa == null || brojIndeksa.trim().isEmpty()) {
            showError("Molimo unesite broj indeksa.");
            return;
        }

        PredmetDTO selectedPredmet = predmetCb.getValue();
        if (selectedPredmet == null) {
            showError("Molimo izaberite predmet.");
            return;
        }

        SkolskaGodinaDTO selectedGodina = skolskaGodinaCb.getValue();
        if (selectedGodina == null) {
            showError("Molimo izaberite skolsku godinu.");
            return;
        }

        SlusaPredmetRequestDTO dto = new SlusaPredmetRequestDTO();
        dto.setBrojIndeksa(brojIndeksa.trim());
        dto.setSifraPredmeta(selectedPredmet.getSifra());
        dto.setSkolskaGodinaId(selectedGodina.getId());

        slusaPredmetService.upisiStudentaNaPredmetAsync(dto)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Student " + brojIndeksa + " uspesno upisan na predmet " + selectedPredmet.getNaziv() + "!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            brojIndeksaTf.clear();
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handlePretraziPredmete() {
        String brojIndeksa = pretraziIndeksTf.getText();
        if (brojIndeksa == null || brojIndeksa.trim().isEmpty()) {
            showError("Molimo unesite broj indeksa za pretragu.");
            return;
        }

        slusaPredmetService.getPredmetiZaStudentaAsync(brojIndeksa.trim())
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() -> {
                            tabelaPredmeti.setItems(FXCollections.observableArrayList(predmeti));
                            if (predmeti.isEmpty()) {
                                statusLabel.setText("Student ne slusa nijedan predmet.");
                                statusLabel.setStyle("-fx-text-fill: orange;");
                            } else {
                                statusLabel.setText("Pronadjeno " + predmeti.size() + " predmeta.");
                                statusLabel.setStyle("-fx-text-fill: green;");
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
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

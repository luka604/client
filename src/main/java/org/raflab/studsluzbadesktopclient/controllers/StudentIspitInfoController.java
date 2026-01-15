package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.raflab.studsluzbadesktopclient.dtos.*;
import org.raflab.studsluzbadesktopclient.services.*;
import org.springframework.stereotype.Component;

@Component
public class StudentIspitInfoController {

    private final PredmetService predmetService;
    private final SkolskaGodinaService skolskaGodinaService;
    private final PredIspitneObavezeService predIspitneObavezeService;
    private final IzlazakNaIspitService izlazakNaIspitService;

    @FXML
    private TextField brojIndeksaTf;

    @FXML
    private ComboBox<PredmetDTO> predmetCombo;

    @FXML
    private ComboBox<SkolskaGodinaDTO> skolskaGodinaCombo;

    @FXML
    private TableView<PredispitniPoeniDTO> tabelaPredispitne;

    @FXML
    private TableView<DetaljiPolaganjaDTO> tabelaPolaganja;

    @FXML
    private Label lblUkupnoPredispitni;

    @FXML
    private Label lblBrojPolaganja;

    @FXML
    private Label statusLabel;

    public StudentIspitInfoController(PredmetService predmetService,
                                      SkolskaGodinaService skolskaGodinaService,
                                      PredIspitneObavezeService predIspitneObavezeService,
                                      IzlazakNaIspitService izlazakNaIspitService) {
        this.predmetService = predmetService;
        this.skolskaGodinaService = skolskaGodinaService;
        this.predIspitneObavezeService = predIspitneObavezeService;
        this.izlazakNaIspitService = izlazakNaIspitService;
    }

    @FXML
    public void initialize() {
        loadPredmeti();
        loadSkolskeGodine();
    }

    private void loadPredmeti() {
        predmetService.getAllPredmetiAsync()
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() -> {
                            predmetCombo.setItems(FXCollections.observableArrayList(predmeti));
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju predmeta: " + error.getMessage());
                        })
                );
    }

    private void loadSkolskeGodine() {
        skolskaGodinaService.getAllAsync()
                .collectList()
                .subscribe(
                        godine -> Platform.runLater(() -> {
                            skolskaGodinaCombo.setItems(FXCollections.observableArrayList(godine));
                            // Select active year by default
                            for (SkolskaGodinaDTO g : godine) {
                                if (g.getAktivna() != null && g.getAktivna()) {
                                    skolskaGodinaCombo.setValue(g);
                                    break;
                                }
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju skolskih godina: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handlePrikaziPredispitne() {
        String brojIndeksa = brojIndeksaTf.getText();
        if (brojIndeksa == null || brojIndeksa.trim().isEmpty()) {
            showError("Molimo unesite broj indeksa.");
            return;
        }

        PredmetDTO predmet = predmetCombo.getValue();
        if (predmet == null) {
            showError("Molimo izaberite predmet.");
            return;
        }

        SkolskaGodinaDTO skolskaGodina = skolskaGodinaCombo.getValue();
        if (skolskaGodina == null) {
            showError("Molimo izaberite skolsku godinu.");
            return;
        }

        predIspitneObavezeService.getPredispitniPoeniAsync(
                        brojIndeksa.trim(),
                        predmet.getId(),
                        skolskaGodina.getId())
                .collectList()
                .subscribe(
                        poeni -> Platform.runLater(() -> {
                            tabelaPredispitne.setItems(FXCollections.observableArrayList(poeni));

                            int ukupno = poeni.stream()
                                    .filter(p -> p.getOsvojeniPoeni() != null)
                                    .mapToInt(PredispitniPoeniDTO::getOsvojeniPoeni)
                                    .sum();
                            lblUkupnoPredispitni.setText("Ukupno predispitnih poena: " + ukupno);
                            lblUkupnoPredispitni.setStyle("-fx-text-fill: blue;");
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handlePrikaziBrojPolaganja() {
        String brojIndeksa = brojIndeksaTf.getText();
        if (brojIndeksa == null || brojIndeksa.trim().isEmpty()) {
            showError("Molimo unesite broj indeksa.");
            return;
        }

        PredmetDTO predmet = predmetCombo.getValue();
        if (predmet == null) {
            showError("Molimo izaberite predmet.");
            return;
        }

        izlazakNaIspitService.getBrojPolaganjaAsync(brojIndeksa.trim(), predmet.getSifra())
                .subscribe(
                        rezultat -> Platform.runLater(() -> {
                            lblBrojPolaganja.setText("Student " + rezultat.getBrojIndeksa() +
                                    " je polagao predmet " + rezultat.getPredmetNaziv() +
                                    " " + rezultat.getBrojPolaganja() + " puta.");
                            lblBrojPolaganja.setStyle("-fx-text-fill: blue;");

                            if (rezultat.getDetaljiPolaganja() != null) {
                                tabelaPolaganja.setItems(FXCollections.observableArrayList(rezultat.getDetaljiPolaganja()));
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handleZatvori() {
        Stage stage = (Stage) brojIndeksaTf.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.raflab.studsluzbadesktopclient.dtos.PrijavaIspitaRequestDTO;
import org.raflab.studsluzbadesktopclient.dtos.StudentPrijavaDTO;
import org.raflab.studsluzbadesktopclient.services.PrijavaIspitaService;
import org.springframework.stereotype.Component;

@Component
public class PrijavljeniStudentiController {

    private final PrijavaIspitaService prijavaIspitaService;

    private static Long ispitId;
    private static String ispitNaziv;
    private static Long ispitniRokId;

    @FXML
    private Label lblNaslov;

    @FXML
    private TableView<StudentPrijavaDTO> tabelaPrijavljeni;

    @FXML
    private TextField brojIndeksaTf;

    @FXML
    private Label statusLabel;

    public PrijavljeniStudentiController(PrijavaIspitaService prijavaIspitaService) {
        this.prijavaIspitaService = prijavaIspitaService;
    }

    public static void setIspitId(Long id) {
        ispitId = id;
    }

    public static void setIspitNaziv(String naziv) {
        ispitNaziv = naziv;
    }

    public static void setIspitniRokId(Long id) {
        ispitniRokId = id;
    }

    @FXML
    public void initialize() {
        lblNaslov.setText("Prijavljeni studenti za ispit: " + ispitNaziv);
        loadPrijavljeniStudenti();
    }

    private void loadPrijavljeniStudenti() {
        prijavaIspitaService.getPrijavljeniStudentiAsync(ispitId)
                .collectList()
                .subscribe(
                        studenti -> Platform.runLater(() -> {
                            tabelaPrijavljeni.setItems(FXCollections.observableArrayList(studenti));
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju prijavljenih studenata: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handlePrijaviStudenta() {
        String brojIndeksa = brojIndeksaTf.getText();
        if (brojIndeksa == null || brojIndeksa.trim().isEmpty()) {
            showError("Molimo unesite broj indeksa.");
            return;
        }

        PrijavaIspitaRequestDTO dto = new PrijavaIspitaRequestDTO();
        dto.setBrojIndeksa(brojIndeksa.trim());
        dto.setIspitId(ispitId);
        dto.setIspitniRokId(ispitniRokId);

        prijavaIspitaService.prijavaIspitaAsync(dto)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Student uspesno prijavljen!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            brojIndeksaTf.clear();
                            loadPrijavljeniStudenti();
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handleZatvori() {
        Stage stage = (Stage) lblNaslov.getScene().getWindow();
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

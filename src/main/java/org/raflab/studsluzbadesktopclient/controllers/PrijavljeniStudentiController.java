package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dto.request.*;
import dto.response.*;
import lombok.Setter;
import org.raflab.studsluzbadesktopclient.services.IzlazakNaIspitService;
import org.raflab.studsluzbadesktopclient.services.PrijavaIspitaService;
import org.springframework.stereotype.Component;

@Component
public class PrijavljeniStudentiController {

    private PrijavaIspitaService prijavaIspitaService;
    private IzlazakNaIspitService izlazakNaIspitService;

    @Setter
    private static Long ispitId;
    @Setter
    private static String ispitNaziv;
    @Setter
    private static Long ispitniRokId;

    @FXML
    private Label lblNaslov;

    @FXML
    private TableView<StudentPrijavaDTO> tabelaPrijavljeni;

    @FXML
    private TextField brojIndeksaTf;

    @FXML
    private TextField brojPoenaTf;

    @FXML
    private TextField napomenaTf;

    @FXML
    private Label statusLabel;

    public PrijavljeniStudentiController(PrijavaIspitaService prijavaIspitaService,
                                         IzlazakNaIspitService izlazakNaIspitService) {
        this.prijavaIspitaService = prijavaIspitaService;
        this.izlazakNaIspitService = izlazakNaIspitService;
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
    public void handleUnosIzlaska() {
        StudentPrijavaDTO selectedStudent = tabelaPrijavljeni.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Molimo selektujte studenta iz tabele.");
            return;
        }

        String brojPoenaStr = brojPoenaTf.getText();
        if (brojPoenaStr == null || brojPoenaStr.trim().isEmpty()) {
            showError("Molimo unesite broj poena.");
            return;
        }

        int brojPoena;
        try {
            brojPoena = Integer.parseInt(brojPoenaStr.trim());
        } catch (NumberFormatException e) {
            showError("Broj poena mora biti ceo broj.");
            return;
        }

        if (brojPoena < 0 || brojPoena > 70) {
            showError("Broj poena mora biti izmedju 0 i 70.");
            return;
        }

        IzlazakNaIspitRequestDTO dto = new IzlazakNaIspitRequestDTO();
        dto.setPrijavaId(selectedStudent.getPrijavaId());
        dto.setBrojPoena(brojPoena);
        dto.setNapomena(napomenaTf.getText());

        izlazakNaIspitService.unosIzlaskaAsync(dto)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Izlazak uspesno unet za studenta " + selectedStudent.getBrojIndeksa() + "!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            brojPoenaTf.clear();
                            napomenaTf.clear();
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

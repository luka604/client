package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import dto.response.StudentPodaciDTO;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.StudentPodaciService;
import org.springframework.stereotype.Component;

@Component
public class PretragaStudenataController {

    private StudentPodaciService studentPodaciService;
    private MainView mainView;

    private int currentPage = 0;
    private int totalPages = 0;
    private static final int PAGE_SIZE = 10;

    private String currentIme = null;
    private String currentPrezime = null;

    @FXML
    private TextField imeTf;

    @FXML
    private TextField prezimeTf;

    @FXML
    private TextField srednjaSkolaNazivTf;

    @FXML
    private TableView<StudentPodaciDTO> tabelaStudenti;

    @FXML
    private Label lblPage;

    @FXML
    private Label statusLabel;

    public PretragaStudenataController(StudentPodaciService studentPodaciService,
                                        MainView mainView) {
        this.studentPodaciService = studentPodaciService;
        this.mainView = mainView;
    }

    @FXML
    public void initialize() {
        // Initial load
    }

    private void loadStudenti() {
        studentPodaciService.pretragaStudenataAsync(currentIme, currentPrezime, currentPage, PAGE_SIZE)
                .subscribe(
                        page -> Platform.runLater(() -> {
                            tabelaStudenti.setItems(FXCollections.observableArrayList(page.getContent()));
                            totalPages = page.getTotalPages();
                            lblPage.setText("Stranica " + (currentPage + 1) + " od " + Math.max(1, totalPages));
                            statusLabel.setText("Pronadjeno " + page.getTotalElements() + " studenata.");
                            statusLabel.setStyle("-fx-text-fill: green;");
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handlePrevious() {
        if (currentPage > 0) {
            currentPage--;
            loadStudenti();
        }
    }

    @FXML
    public void handleNext() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadStudenti();
        }
    }

    @FXML
    public void handleOtvoriProfil() {
        StudentPodaciDTO selected = tabelaStudenti.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Molimo izaberite studenta iz tabele.");
            return;
        }

        if (selected.getBrojIndeksa() == null || selected.getBrojIndeksa().isEmpty()) {
            showError("Izabrani student nema broj indeksa.");
            return;
        }

        StudentProfilController.setSelectedBrojIndeksa(selected.getBrojIndeksa());
        mainView.changeRoot("studentProfil");
    }

    @FXML
    public void handlePretragaPoSrednjojSkoli() {
        String nazivSkole = srednjaSkolaNazivTf.getText();
        if (nazivSkole == null || nazivSkole.trim().isEmpty()) {
            showError("Molimo unesite naziv srednje skole.");
            return;
        }

        studentPodaciService.getStudentiBySrednjaSkolaNazivAsync(nazivSkole.trim())
                .collectList()
                .subscribe(
                        studenti -> Platform.runLater(() -> {
                            tabelaStudenti.setItems(FXCollections.observableArrayList(studenti));
                            statusLabel.setText("Pronadjeno " + studenti.size() + " studenata iz skole: " + nazivSkole);
                            statusLabel.setStyle("-fx-text-fill: green;");
                            lblPage.setText("Svi rezultati");
                            totalPages = 1;
                            currentPage = 0;
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handlePretraga() {
        currentIme = imeTf.getText();
        currentPrezime = prezimeTf.getText();
        currentPage = 0;

        if ((currentIme == null || currentIme.isEmpty()) && (currentPrezime == null || currentPrezime.isEmpty())) {
            currentIme = null;
            currentPrezime = null;
        }

        loadStudenti();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.MainView;
import dto.response.PredmetDTO;
import dto.response.StudijskiProgramDTO;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.services.StudijskiProgramService;
import org.springframework.stereotype.Component;


@Component
public class StudijskiProgramiController {

    private final StudijskiProgramService studijskiProgramService;
    private final PredmetService predmetService;
    private final MainView mainView;

    @FXML
    private TableView<StudijskiProgramDTO> tabelaStudijskiProgrami;

    @FXML
    private TableView<PredmetDTO> tabelaPredmeti;

    @FXML
    private Label lblPredmetiNaslov;


    public StudijskiProgramiController(StudijskiProgramService studijskiProgramService,
                                        PredmetService predmetService,
                                        MainView mainView) {
        this.studijskiProgramService = studijskiProgramService;
        this.predmetService = predmetService;
        this.mainView = mainView;
    }

    @FXML
    public void initialize() {
        loadStudijskiProgrami();
    }

    private void loadStudijskiProgrami() {
        studijskiProgramService.getAllStudijskiProgramiAsync()
                .collectList()
                .subscribe(
                        programi -> Platform.runLater(() -> {
                            tabelaStudijskiProgrami.setItems(FXCollections.observableArrayList(programi));
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju studijskih programa: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handlePrikaziPredmete() {
        StudijskiProgramDTO selectedProgram =
                tabelaStudijskiProgrami.getSelectionModel().getSelectedItem();

        if (selectedProgram == null) {
            showError("Molimo izaberite studijski program.");
            return;
        }

        lblPredmetiNaslov.setText("Predmeti na programu: " + selectedProgram.getNaziv());

        predmetService.getPredmetiByStudijskiProgramAsync(selectedProgram.getOznaka())
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() ->
                                tabelaPredmeti.setItems(FXCollections.observableArrayList(predmeti))
                        ),
                        error -> Platform.runLater(() ->
                                showError("Greska pri ucitavanju predmeta: " + error.getMessage())
                        )
                );
    }

    @FXML
    public void handleDodajPredmet() {
        StudijskiProgramDTO selectedProgram =
                tabelaStudijskiProgrami.getSelectionModel().getSelectedItem();

        if (selectedProgram == null) {
            showError("Molimo izaberite studijski program.");
            return;
        }

        DodajPredmetController.setStudijskiProgramOznaka(selectedProgram.getOznaka());
        mainView.openModal("dodajPredmet", "Dodaj predmet na " + selectedProgram.getNaziv(), 450, 400);

        handlePrikaziPredmete();
    }

    @FXML
    public void handleProsecnaOcena() {
        PredmetDTO selectedPredmet =
                tabelaPredmeti.getSelectionModel().getSelectedItem();

        if (selectedPredmet == null) {
            showError("Molimo izaberite predmet.");
            return;
        }

        ProsecnaOcenaPredmetController.setPredmetSifra(selectedPredmet.getSifra());
        ProsecnaOcenaPredmetController.setPredmetNaziv(selectedPredmet.getNaziv());
        mainView.openModal("prosecnaOcenaPredmet", "Prosecna ocena - " + selectedPredmet.getNaziv(), 500, 450);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

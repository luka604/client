package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.MainView;
import dto.request.IspitRequestDTO;
import dto.response.IspitDTO;
import dto.response.IspitniRokDTO;
import dto.response.PredmetDTO;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@Component
public class IspitiController {

    private IspitService ispitService;
    private IspitniRokService ispitniRokService;
    private PredmetService predmetService;
    private MainView mainView;

    private static Long selectedIspitniRokId;

    @FXML
    private ComboBox<IspitniRokDTO> ispitniRokCombo;

    @FXML
    private TableView<IspitDTO> tabelaIspiti;

    @FXML
    private ComboBox<PredmetDTO> predmetCombo;

    @FXML
    private DatePicker datumDp;

    @FXML
    private TextField vremeTf;

    @FXML
    private Label statusLabel;


    public IspitiController(IspitService ispitService, IspitniRokService ispitniRokService,
                            PredmetService predmetService, MainView mainView) {
        this.ispitService = ispitService;
        this.ispitniRokService = ispitniRokService;
        this.predmetService = predmetService;
        this.mainView = mainView;
    }


    @FXML
    public void initialize() {
        loadIspitniRokovi();
        loadPredmeti();
    }

    private void loadIspitniRokovi() {
        ispitniRokService.getAllIspitniRokoviAsync()
                .collectList()
                .subscribe(
                        rokovi -> Platform.runLater(() -> {
                            ispitniRokCombo.setItems(FXCollections.observableArrayList(rokovi));
                            if (selectedIspitniRokId != null) {
                                for (IspitniRokDTO rok : rokovi) {
                                    if (rok.getId().equals(selectedIspitniRokId)) {
                                        ispitniRokCombo.setValue(rok);
                                        loadIspiti(selectedIspitniRokId);
                                        break;
                                    }
                                }
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju ispitnih rokova: " + error.getMessage());
                        })
                );
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

    private void loadIspiti(Long ispitniRokId) {
        ispitService.getIspitiByIspitniRokAsync(ispitniRokId)
                .collectList()
                .subscribe(
                        ispiti -> Platform.runLater(() -> {
                            tabelaIspiti.setItems(FXCollections.observableArrayList(ispiti));
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju ispita: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handleIspitniRokSelected() {
        IspitniRokDTO selectedRok = ispitniRokCombo.getValue();
        if (selectedRok != null) {
            loadIspiti(selectedRok.getId());
        }
    }

    @FXML
    public void handlePrijavljeniStudenti() {
        IspitniRokDTO rok = requireSelectedRok();
        if (rok == null) return;

        IspitDTO selectedIspit = requireSelectedIspit();
        if (selectedIspit == null) return;

        PrijavljeniStudentiController.setIspitId(selectedIspit.getId());
        PrijavljeniStudentiController.setIspitNaziv(selectedIspit.getPredmetNaziv());
        PrijavljeniStudentiController.setIspitniRokId(rok.getId());

        mainView.openModal(
                "prijavljeniStudenti",
                "Prijavljeni studenti - " + selectedIspit.getPredmetNaziv(),
                600, 500
        );
    }

    @FXML
    public void handleRezultati() {
        IspitniRokDTO rok = requireSelectedRok();
        if (rok == null) return;
        IspitDTO selectedIspit = requireSelectedIspit();
        if (selectedIspit == null) return;
        RezultatiIspitaController.setSelectedIspitId(selectedIspit.getId());
        RezultatiIspitaController.setSelectedIspitniRokId(rok.getId());
        mainView.changeRoot("rezultatiIspita");
    }

    @FXML
    public void handleDodajIspit() {
        if (ispitniRokCombo.getValue() == null) {
            showError("Molimo izaberite ispitni rok.");
            return;
        }
        if (predmetCombo.getValue() == null) {
            showError("Molimo izaberite predmet.");
            return;
        }
        if (datumDp.getValue() == null) {
            showError("Molimo izaberite datum odrzavanja.");
            return;
        }
        if (vremeTf.getText() == null || vremeTf.getText().trim().isEmpty()) {
            showError("Molimo unesite vreme pocetka.");
            return;
        }

        LocalTime vreme;
        try {
            vreme = LocalTime.parse(vremeTf.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            showError("Neispravno vreme. Unesite u formatu HH:MM (npr. 09:00).");
            return;
        }

        IspitRequestDTO dto = new IspitRequestDTO();
        dto.setIspitniRokId(ispitniRokCombo.getValue().getId());
        dto.setPredmetSifra(predmetCombo.getValue().getSifra());
        dto.setDatumOdrzavanja(datumDp.getValue());
        dto.setVremePocetka(vreme);
        dto.setZakljucan(false);

        ispitService.createAsync(dto)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Ispit uspesno dodat!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            loadIspiti(ispitniRokCombo.getValue().getId());
                            clearForm();
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri dodavanju ispita: " + error.getMessage());
                        })
                );
    }

    private void clearForm() {
        predmetCombo.setValue(null);
        datumDp.setValue(null);
        vremeTf.clear();
    }

    private IspitniRokDTO requireSelectedRok() {
        IspitniRokDTO rok = ispitniRokCombo.getValue();
        if (rok == null) {
            showError("Molimo izaberite ispitni rok.");
        }
        return rok;
    }

    private IspitDTO requireSelectedIspit() {
        IspitDTO selected = tabelaIspiti.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Molimo izaberite ispit.");
        }
        return selected;
    }

    public static void setSelectedIspitniRokId(Long id) {
        selectedIspitniRokId = id;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

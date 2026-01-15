package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.raflab.studsluzbadesktopclient.dtos.*;
import org.raflab.studsluzbadesktopclient.services.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentProfilController {

    private final StudentPodaciService studentPodaciService;
    private final PolozeniPredmetService polozeniPredmetService;
    private final StudentPredmetService studentPredmetService;
    private final UpisGodineService upisGodineService;
    private final ObnovaGodineService obnovaGodineService;
    private final UplataService uplataService;
    private final PredmetService predmetService;

    private String currentBrojIndeksa;
    private int pagePolozeni = 0;
    private int pageNepolozeni = 0;
    private int totalPagesPolozeni = 0;
    private int totalPagesNepolozeni = 0;
    private static final int PAGE_SIZE = 10;

    private List<PredmetDTO> allPredmeti = new ArrayList<>();

    @FXML private TextField brojIndeksaTf;
    @FXML private Label statusLabel;
    @FXML private TabPane tabPane;

    // Licni podaci
    @FXML private Label lblIme;
    @FXML private Label lblPrezime;
    @FXML private Label lblSrednjeIme;
    @FXML private Label lblPol;
    @FXML private Label lblEmail;
    @FXML private Label lblTelefon;
    @FXML private Label lblUspehSrednjaSkola;
    @FXML private Label lblUspehPrijemni;

    // Polozeni ispiti
    @FXML private TableView<PolozeniPredmetDTO> tabelaPolozeni;
    @FXML private Label lblPagePolozeni;

    // Nepolozeni ispiti
    @FXML private TableView<PredmetDTO> tabelaNepolozeni;
    @FXML private Label lblPageNepolozeni;

    // Upisane godine
    @FXML private ListView<UpisGodineDTO> listaUpisaneGodine;
    @FXML private Spinner<Integer> spinnerGodina;
    @FXML private TextField upisNapomenaTf;
    @FXML private ListView<PredmetDTO> listaPredmetiZaUpis;

    // Obnovljene godine
    @FXML private ListView<ObnovaGodineDTO> listaObnovljeneGodine;
    @FXML private Spinner<Integer> spinnerObnovaGodina;
    @FXML private TextField obnovaNapomenaTf;
    @FXML private ListView<PredmetDTO> listaPredmetiZaObnovu;
    @FXML private Label lblUkupnoEspb;

    // Uplate
    @FXML private Label lblPreostaloEUR;
    @FXML private Label lblPreostaloRSD;
    @FXML private Label lblUkupnoUplaceno;
    @FXML private Label lblSrednjiKurs;
    @FXML private TextField iznosUplateTf;

    public StudentProfilController(StudentPodaciService studentPodaciService,
                                    PolozeniPredmetService polozeniPredmetService,
                                    StudentPredmetService studentPredmetService,
                                    UpisGodineService upisGodineService,
                                    ObnovaGodineService obnovaGodineService,
                                    UplataService uplataService,
                                    PredmetService predmetService) {
        this.studentPodaciService = studentPodaciService;
        this.polozeniPredmetService = polozeniPredmetService;
        this.studentPredmetService = studentPredmetService;
        this.upisGodineService = upisGodineService;
        this.obnovaGodineService = obnovaGodineService;
        this.uplataService = uplataService;
        this.predmetService = predmetService;
    }

    @FXML
    public void initialize() {
        spinnerGodina.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));
        spinnerObnovaGodina.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));

        listaPredmetiZaUpis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaPredmetiZaObnovu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listaPredmetiZaObnovu.getSelectionModel().getSelectedItems().addListener((ListChangeListener<PredmetDTO>) c -> {
            updateEspbCount();
        });

        loadAllPredmeti();
    }

    private void loadAllPredmeti() {
        predmetService.getAllPredmetiAsync()
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() -> {
                            allPredmeti = predmeti;
                            listaPredmetiZaObnovu.setItems(FXCollections.observableArrayList(predmeti));
                        }),
                        error -> {}
                );
    }

    @FXML
    public void handleUcitajStudenta() {
        String brojIndeksa = brojIndeksaTf.getText();
        if (brojIndeksa == null || brojIndeksa.trim().isEmpty()) {
            showError("Molimo unesite broj indeksa.");
            return;
        }

        currentBrojIndeksa = brojIndeksa.trim();
        pagePolozeni = 0;
        pageNepolozeni = 0;

        loadStudentData();
        loadPolozeniIspiti();
        loadNepolozeniIspiti();
        loadUpisaneGodine();
        loadObnovljeneGodine();
        loadPreostaliIznos();
    }

    private void loadStudentData() {
        studentPodaciService.getStudentByBrojIndeksaAsync(currentBrojIndeksa)
                .subscribe(
                        student -> Platform.runLater(() -> {
                            lblIme.setText(student.getIme() != null ? student.getIme() : "-");
                            lblPrezime.setText(student.getPrezime() != null ? student.getPrezime() : "-");
                            lblSrednjeIme.setText(student.getSrednjeIme() != null ? student.getSrednjeIme() : "-");
                            lblPol.setText(student.getPol() != null ? student.getPol().toString() : "-");
                            lblEmail.setText(student.getEmail() != null ? student.getEmail() : "-");
                            lblTelefon.setText(student.getBrojTelefonaMobilni() != null ? student.getBrojTelefonaMobilni() : "-");
                            lblUspehSrednjaSkola.setText(student.getUspehSrednjaSkola() != null ? student.getUspehSrednjaSkola().toString() : "-");
                            lblUspehPrijemni.setText(student.getUspehPrijemni() != null ? student.getUspehPrijemni().toString() : "-");

                            statusLabel.setText("Student ucitan uspesno!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    private void loadPolozeniIspiti() {
        polozeniPredmetService.getPolozeniIspitiAsync(currentBrojIndeksa, pagePolozeni, PAGE_SIZE)
                .subscribe(
                        page -> Platform.runLater(() -> {
                            tabelaPolozeni.setItems(FXCollections.observableArrayList(page.getContent()));
                            totalPagesPolozeni = page.getTotalPages();
                            lblPagePolozeni.setText("Stranica " + (pagePolozeni + 1) + " od " + Math.max(1, totalPagesPolozeni));
                        }),
                        error -> Platform.runLater(() -> {
                            tabelaPolozeni.setItems(FXCollections.observableArrayList());
                        })
                );
    }

    private void loadNepolozeniIspiti() {
        studentPredmetService.getNepolozeniIspitiAsync(currentBrojIndeksa, pageNepolozeni, PAGE_SIZE)
                .subscribe(
                        page -> Platform.runLater(() -> {
                            tabelaNepolozeni.setItems(FXCollections.observableArrayList(page.getContent()));
                            listaPredmetiZaUpis.setItems(FXCollections.observableArrayList(page.getContent()));
                            totalPagesNepolozeni = page.getTotalPages();
                            lblPageNepolozeni.setText("Stranica " + (pageNepolozeni + 1) + " od " + Math.max(1, totalPagesNepolozeni));
                        }),
                        error -> Platform.runLater(() -> {
                            tabelaNepolozeni.setItems(FXCollections.observableArrayList());
                        })
                );
    }

    private void loadUpisaneGodine() {
        upisGodineService.getUpisaneGodineAsync(currentBrojIndeksa)
                .collectList()
                .subscribe(
                        godine -> Platform.runLater(() -> {
                            listaUpisaneGodine.setItems(FXCollections.observableArrayList(godine));
                        }),
                        error -> Platform.runLater(() -> {
                            listaUpisaneGodine.setItems(FXCollections.observableArrayList());
                        })
                );
    }

    private void loadObnovljeneGodine() {
        obnovaGodineService.getObnovljeneGodineAsync(currentBrojIndeksa)
                .collectList()
                .subscribe(
                        godine -> Platform.runLater(() -> {
                            listaObnovljeneGodine.setItems(FXCollections.observableArrayList(godine));
                        }),
                        error -> Platform.runLater(() -> {
                            listaObnovljeneGodine.setItems(FXCollections.observableArrayList());
                        })
                );
    }

    private void loadPreostaliIznos() {
        uplataService.getPreostaliIznosAsync(currentBrojIndeksa)
                .subscribe(
                        iznos -> Platform.runLater(() -> {
                            lblPreostaloEUR.setText(iznos.getPreostaloEUR() + " EUR");
                            lblPreostaloRSD.setText(iznos.getPreostaloRSD() + " RSD");
                            lblUkupnoUplaceno.setText(iznos.getUkupnoUplacenoEUR() + " EUR");
                            lblSrednjiKurs.setText(iznos.getSrednjiKurs() + " RSD/EUR");
                        }),
                        error -> Platform.runLater(() -> {
                            lblPreostaloEUR.setText("-");
                            lblPreostaloRSD.setText("-");
                            lblUkupnoUplaceno.setText("-");
                            lblSrednjiKurs.setText("-");
                        })
                );
    }

    @FXML
    public void handlePreviousPolozeni() {
        if (pagePolozeni > 0) {
            pagePolozeni--;
            loadPolozeniIspiti();
        }
    }

    @FXML
    public void handleNextPolozeni() {
        if (pagePolozeni < totalPagesPolozeni - 1) {
            pagePolozeni++;
            loadPolozeniIspiti();
        }
    }

    @FXML
    public void handlePreviousNepolozeni() {
        if (pageNepolozeni > 0) {
            pageNepolozeni--;
            loadNepolozeniIspiti();
        }
    }

    @FXML
    public void handleNextNepolozeni() {
        if (pageNepolozeni < totalPagesNepolozeni - 1) {
            pageNepolozeni++;
            loadNepolozeniIspiti();
        }
    }

    @FXML
    public void handleUpisGodine() {
        if (currentBrojIndeksa == null) {
            showError("Prvo ucitajte studenta.");
            return;
        }

        Integer godina = spinnerGodina.getValue();
        String napomena = upisNapomenaTf.getText();

        List<Long> predmetiIds = listaPredmetiZaUpis.getSelectionModel().getSelectedItems()
                .stream()
                .map(PredmetDTO::getId)
                .collect(Collectors.toList());

        UpisGodineRequestDTO request = new UpisGodineRequestDTO();
        request.setGodinaKojuUpisuje(godina);
        request.setNapomena(napomena);
        request.setPreneseniPredmetiIds(predmetiIds);

        upisGodineService.upisGodineAsync(currentBrojIndeksa, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Godina uspesno upisana!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            loadUpisaneGodine();
                            upisNapomenaTf.clear();
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handleObnovaGodine() {
        if (currentBrojIndeksa == null) {
            showError("Prvo ucitajte studenta.");
            return;
        }

        Integer godina = spinnerObnovaGodina.getValue();
        String napomena = obnovaNapomenaTf.getText();

        List<PredmetDTO> selectedPredmeti = new ArrayList<>(listaPredmetiZaObnovu.getSelectionModel().getSelectedItems());

        int ukupnoEspb = selectedPredmeti.stream()
                .mapToInt(PredmetDTO::getEspb)
                .sum();

        if (ukupnoEspb > 60) {
            showError("Maksimalni zbir ESPB poena moze biti 60! Trenutno: " + ukupnoEspb);
            return;
        }

        if (selectedPredmeti.isEmpty()) {
            showError("Morate izabrati bar jedan predmet.");
            return;
        }

        List<Long> predmetiIds = selectedPredmeti.stream()
                .map(PredmetDTO::getId)
                .collect(Collectors.toList());

        ObnovaGodineRequestDTO request = new ObnovaGodineRequestDTO();
        request.setGodinaKojuObnavlja(godina);
        request.setNapomena(napomena);
        request.setPredmetiIds(predmetiIds);

        obnovaGodineService.obnovaGodineAsync(currentBrojIndeksa, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Godina uspesno obnovljena!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            loadObnovljeneGodine();
                            obnovaNapomenaTf.clear();
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    @FXML
    public void handleDodajUplatu() {
        if (currentBrojIndeksa == null) {
            showError("Prvo ucitajte studenta.");
            return;
        }

        String iznosStr = iznosUplateTf.getText();
        if (iznosStr == null || iznosStr.trim().isEmpty()) {
            showError("Molimo unesite iznos uplate.");
            return;
        }

        BigDecimal iznos;
        try {
            iznos = new BigDecimal(iznosStr.trim());
        } catch (NumberFormatException e) {
            showError("Neispravan format iznosa.");
            return;
        }

        UplataRequestDTO request = new UplataRequestDTO();
        request.setIznosRSD(iznos);

        uplataService.dodajUplatuAsync(currentBrojIndeksa, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Uplata uspesno dodata! Iznos: " + result.getIznosEUR() + " EUR");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            loadPreostaliIznos();
                            iznosUplateTf.clear();
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    private void updateEspbCount() {
        int ukupno = listaPredmetiZaObnovu.getSelectionModel().getSelectedItems()
                .stream()
                .mapToInt(PredmetDTO::getEspb)
                .sum();
        lblUkupnoEspb.setText(String.valueOf(ukupno));
        if (ukupno > 60) {
            lblUkupnoEspb.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblUkupnoEspb.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import dto.request.*;
import dto.response.*;
import org.raflab.studsluzbadesktopclient.dtos.PolozeniPredmetReportDTO;
import org.raflab.studsluzbadesktopclient.navigation.NavigationHistory;
import org.raflab.studsluzbadesktopclient.services.*;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Setter
public class StudentProfilController {

    private final StudentPodaciService studentPodaciService;
    private final PolozeniPredmetService polozeniPredmetService;
    private final StudentPredmetService studentPredmetService;
    private final UpisGodineService upisGodineService;
    private final ObnovaGodineService obnovaGodineService;
    private final UplataService uplataService;
    private final PredmetService predmetService;
    @Setter
    private static String selectedBrojIndeksa;
    private final NavigationHistory navigationHistory;

    private String currentBrojIndeksa;
    private int pagePolozeni = 0;
    private int pageNepolozeni = 0;
    private int totalPagesPolozeni = 0;
    private int totalPagesNepolozeni = 0;
    private static final int PAGE_SIZE = 10;

    private List<PredmetDTO> allPredmeti = new ArrayList<>();
    private StudentPodaciDTO currentStudentData;

    @FXML private TextField brojIndeksaTf;
    @FXML private Label statusLabel;
    @FXML private TabPane tabPane;


    @FXML private Label lblIme;
    @FXML private Label lblPrezime;
    @FXML private Label lblSrednjeIme;
    @FXML private Label lblPol;
    @FXML private Label lblEmail;
    @FXML private Label lblTelefon;
    @FXML private Label lblUspehSrednjaSkola;
    @FXML private Label lblUspehPrijemni;
    @FXML private Label lblUkupnoEspbProfil;
    @FXML private Label lblProsecnaOcena;


    @FXML private TableView<PolozeniPredmetDTO> tabelaPolozeni;
    @FXML private Label lblPagePolozeni;


    @FXML private TableView<PredmetDTO> tabelaNepolozeni;
    @FXML private Label lblPageNepolozeni;


    @FXML private ListView<UpisGodineDTO> listaUpisaneGodine;
    @FXML private Spinner<Integer> spinnerGodina;
    @FXML private TextField upisNapomenaTf;
    @FXML private ListView<PredmetDTO> listaPredmetiZaUpis;
    @FXML private ListView<PredmetDTO> listaNoviPredmeti;


    @FXML private ListView<ObnovaGodineDTO> listaObnovljeneGodine;
    @FXML private Spinner<Integer> spinnerObnovaGodina;
    @FXML private TextField obnovaNapomenaTf;
    @FXML private ListView<PredmetDTO> listaPredmetiZaObnovu;
    @FXML private Label lblUkupnoEspb;


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
                                    PredmetService predmetService,
                                    NavigationHistory navigationHistory) {
        this.studentPodaciService = studentPodaciService;
        this.polozeniPredmetService = polozeniPredmetService;
        this.studentPredmetService = studentPredmetService;
        this.upisGodineService = upisGodineService;
        this.obnovaGodineService = obnovaGodineService;
        this.uplataService = uplataService;
        this.predmetService = predmetService;
        this.navigationHistory = navigationHistory;
    }

    @FXML
    public void initialize() {
        spinnerGodina.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));
        spinnerObnovaGodina.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1));

        listaPredmetiZaUpis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaNoviPredmeti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaPredmetiZaObnovu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        spinnerGodina.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadPredmetiZaGodinu(newVal);
        });

        listaPredmetiZaObnovu.getSelectionModel().getSelectedItems().addListener((ListChangeListener<PredmetDTO>) c -> {
            updateEspbCount();
        });

        loadAllPredmeti();

        // Check if navigated from search with pre-selected student
        String selectedBrojIndeksa = navigationHistory.consumeSelectedBrojIndeksa();
        if (selectedBrojIndeksa != null && !selectedBrojIndeksa.isEmpty()) {
            brojIndeksaTf.setText(selectedBrojIndeksa);
            handleUcitajStudenta();
        }
    }

    private void loadAllPredmeti() {
        predmetService.getAllPredmetiAsync()
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() -> {
                            allPredmeti = predmeti;
                            listaPredmetiZaObnovu.setItems(FXCollections.observableArrayList(predmeti));
                            // Load subjects for the currently selected year
                            loadPredmetiZaGodinu(spinnerGodina.getValue());
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
        loadProsecnaOcenaAndEspb();
    }

    private void loadStudentData() {
        studentPodaciService.getStudentByBrojIndeksaAsync(currentBrojIndeksa)
                .subscribe(
                        student -> Platform.runLater(() -> {
                            currentStudentData = student;
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
                            currentStudentData = null;
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

    private void loadProsecnaOcenaAndEspb() {
        polozeniPredmetService.getAllPolozeniIspitiAsync(currentBrojIndeksa)
                .collectList()
                .subscribe(
                        polozeniIspiti -> Platform.runLater(() -> {
                            if (polozeniIspiti == null || polozeniIspiti.isEmpty()) {
                                lblUkupnoEspbProfil.setText("0");
                                lblProsecnaOcena.setText("-");
                                return;
                            }

                            int ukupnoEspb = polozeniIspiti.stream()
                                    .filter(p -> p.getPredmet() != null && p.getPredmet().getEspb() != null)
                                    .mapToInt(p -> p.getPredmet().getEspb())
                                    .sum();

                            double prosecnaOcena = polozeniIspiti.stream()
                                    .filter(p -> p.getOcena() != null && p.getOcena() >= 6)
                                    .mapToInt(PolozeniPredmetDTO::getOcena)
                                    .average()
                                    .orElse(0.0);

                            lblUkupnoEspbProfil.setText(String.valueOf(ukupnoEspb));
                            lblProsecnaOcena.setText(String.format("%.2f", prosecnaOcena));
                        }),
                        error -> Platform.runLater(() -> {
                            lblUkupnoEspbProfil.setText("-");
                            lblProsecnaOcena.setText("-");
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


        List<Long> preneseniPredmetiIds = listaPredmetiZaUpis.getSelectionModel().getSelectedItems()
                .stream()
                .map(PredmetDTO::getId)
                .collect(Collectors.toList());


        List<Long> noviPredmetiIds = listaNoviPredmeti.getSelectionModel().getSelectedItems()
                .stream()
                .map(PredmetDTO::getId)
                .collect(Collectors.toList());

        UpisGodineRequestDTO request = new UpisGodineRequestDTO();
        request.setGodinaKojuUpisuje(godina);
        request.setNapomena(napomena);
        request.setPreneseniPredmetiIds(preneseniPredmetiIds);
        request.setNoviPredmetiIds(noviPredmetiIds);

        upisGodineService.upisGodineAsync(currentBrojIndeksa, request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            statusLabel.setText("Godina uspesno upisana sa " +
                                (noviPredmetiIds.size() + preneseniPredmetiIds.size()) + " predmeta!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            loadUpisaneGodine();
                            loadNepolozeniIspiti();
                            upisNapomenaTf.clear();
                            listaNoviPredmeti.getSelectionModel().clearSelection();
                            listaPredmetiZaUpis.getSelectionModel().clearSelection();
                        }),
                        error -> Platform.runLater(() -> {
                            statusLabel.setText("Greska: " + error.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        })
                );
    }

    private void loadPredmetiZaGodinu(Integer godina) {
        if (godina == null) return;

        int semestar1 = (godina - 1) * 2 + 1;
        int semestar2 = godina * 2;

        List<PredmetDTO> predmetiZaGodinu = allPredmeti.stream()
                .filter(p -> p.getSemestar() != null &&
                        p.getSemestar() >= semestar1 && p.getSemestar() <= semestar2)
                .collect(Collectors.toList());

        listaNoviPredmeti.setItems(FXCollections.observableArrayList(predmetiZaGodinu));
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

    @FXML
    public void handleGenerisiUverenjeOStudiranju() {
        if (currentBrojIndeksa == null || currentStudentData == null) {
            showError("Prvo ucitajte studenta.");
            return;
        }

        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/uverenjeOStudiranju.jrxml");
            if (reportStream == null) {
                showError("Sablon izvestaja nije pronadjen.");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("studentIme", currentStudentData.getIme());
            parameters.put("studentPrezime", currentStudentData.getPrezime());
            parameters.put("studentSrednjeIme", currentStudentData.getSrednjeIme());
            parameters.put("brojIndeksa", currentBrojIndeksa);
            parameters.put("studijskiProgram", "Informatika i racunarstvo");
            parameters.put("godinaStudija", getGodinaStudijaFromUpisaneGodine());
            parameters.put("skolskaGodina", getSkolskaGodina());
            parameters.put("datumIzdavanja", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
            parameters.put("ukupnoEspb", calculateTotalEspb());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            String fileName = "uverenje_o_studiranju_" + currentBrojIndeksa.replace("/", "_") + ".pdf";
            File pdfFile = new File(System.getProperty("java.io.tmpdir"), fileName);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }

            statusLabel.setText("Uverenje o studiranju generisano: " + pdfFile.getName());
            statusLabel.setStyle("-fx-text-fill: green;");

        } catch (JRException e) {
            showError("Greska pri generisanju uverenja: " + e.getMessage());
        } catch (Exception e) {
            showError("Greska pri otvaranju PDF-a: " + e.getMessage());
        }
    }

    @FXML
    public void handleGenerisiUverenjePolozeniIspiti() {
        if (currentBrojIndeksa == null || currentStudentData == null) {
            showError("Prvo ucitajte studenta.");
            return;
        }

        try {
            List<PolozeniPredmetDTO> polozeniIspiti = polozeniPredmetService.getAllPolozeniIspiti(currentBrojIndeksa);

            if (polozeniIspiti == null || polozeniIspiti.isEmpty()) {
                showError("Student nema polozenih ispita.");
                return;
            }

            List<PolozeniPredmetReportDTO> reportData = polozeniIspiti.stream()
                    .map(PolozeniPredmetReportDTO::fromPolozeniPredmetDTO)
                    .sorted(Comparator.comparing(PolozeniPredmetReportDTO::getGodinaStudija)
                            .thenComparing(p -> p.getPredmetNaziv() != null ? p.getPredmetNaziv() : ""))
                    .collect(Collectors.toList());

            InputStream reportStream = getClass().getResourceAsStream("/reports/uverenjePolozeniIspiti.jrxml");
            if (reportStream == null) {
                showError("Sablon izvestaja nije pronadjen.");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            int ukupnoEspb = reportData.stream()
                    .mapToInt(p -> p.getEspb() != null ? p.getEspb() : 0)
                    .sum();

            double prosecnaOcena = reportData.stream()
                    .filter(p -> p.getOcena() != null && p.getOcena() >= 6)
                    .mapToInt(PolozeniPredmetReportDTO::getOcena)
                    .average()
                    .orElse(0.0);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("studentIme", currentStudentData.getIme());
            parameters.put("studentPrezime", currentStudentData.getPrezime());
            parameters.put("studentSrednjeIme", currentStudentData.getSrednjeIme());
            parameters.put("brojIndeksa", currentBrojIndeksa);
            parameters.put("studijskiProgram", "Informatika i racunarstvo");
            parameters.put("datumIzdavanja", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
            parameters.put("ukupnoEspb", ukupnoEspb);
            parameters.put("prosecnaOcena", prosecnaOcena);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            String fileName = "uverenje_polozeni_ispiti_" + currentBrojIndeksa.replace("/", "_") + ".pdf";
            File pdfFile = new File(System.getProperty("java.io.tmpdir"), fileName);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }

            statusLabel.setText("Uverenje o polozenim ispitima generisano: " + pdfFile.getName());
            statusLabel.setStyle("-fx-text-fill: green;");

        } catch (JRException e) {
            showError("Greska pri generisanju uverenja: " + e.getMessage());
        } catch (Exception e) {
            showError("Greska pri ucitavanju podataka: " + e.getMessage());
        }
    }

    private String getGodinaStudijaFromUpisaneGodine() {
        if (listaUpisaneGodine.getItems() != null && !listaUpisaneGodine.getItems().isEmpty()) {
            int maxGodina = listaUpisaneGodine.getItems().stream()
                    .mapToInt(UpisGodineDTO::getGodinaKojuUpisuje)
                    .max()
                    .orElse(1);
            return maxGodina + ". godina";
        }
        return "1. godina";
    }

    private String getSkolskaGodina() {
        int currentYear = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        if (month >= 10) {
            return currentYear + "/" + (currentYear + 1);
        } else {
            return (currentYear - 1) + "/" + currentYear;
        }
    }

    private Integer calculateTotalEspb() {
        try {
            List<PolozeniPredmetDTO> polozeniIspiti = polozeniPredmetService.getAllPolozeniIspiti(currentBrojIndeksa);
            if (polozeniIspiti != null) {
                return polozeniIspiti.stream()
                        .filter(p -> p.getPredmet() != null && p.getPredmet().getEspb() != null)
                        .mapToInt(p -> p.getPredmet().getEspb())
                        .sum();
            }
        } catch (Exception e) {
            // ignore
        }
        return 0;
    }
}

package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.dtos.IspitDTO;
import org.raflab.studsluzbadesktopclient.dtos.IspitniRokDTO;
import org.raflab.studsluzbadesktopclient.dtos.ProsecnaOcenaDTO;
import org.raflab.studsluzbadesktopclient.dtos.RezultatIspitaDTO;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RezultatiIspitaController {

    private final IspitService ispitService;
    private final IspitniRokService ispitniRokService;
    private final MainView mainView;

    private static Long selectedIspitId;
    private static Long selectedIspitniRokId;

    @FXML
    private ComboBox<IspitniRokDTO> ispitniRokCombo;

    @FXML
    private ComboBox<IspitDTO> ispitCombo;

    @FXML
    private Label lblIspitInfo;

    @FXML
    private TableView<RezultatIspitaDTO> tabelaRezultati;

    @FXML
    private Label statusLabel;

    @FXML
    private Label lblProsecnaOcena;

    private List<RezultatIspitaDTO> currentRezultati;
    private IspitDTO currentIspit;

    public RezultatiIspitaController(IspitService ispitService, IspitniRokService ispitniRokService, MainView mainView) {
        this.ispitService = ispitService;
        this.ispitniRokService = ispitniRokService;
        this.mainView = mainView;
    }

    public static void setSelectedIspitId(Long id) {
        selectedIspitId = id;
    }

    public static void setSelectedIspitniRokId(Long id) {
        selectedIspitniRokId = id;
    }

    @FXML
    public void initialize() {
        loadIspitniRokovi();
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

    private void loadIspiti(Long ispitniRokId) {
        ispitService.getIspitiByIspitniRokAsync(ispitniRokId)
                .collectList()
                .subscribe(
                        ispiti -> Platform.runLater(() -> {
                            ispitCombo.setItems(FXCollections.observableArrayList(ispiti));
                            if (selectedIspitId != null) {
                                for (IspitDTO ispit : ispiti) {
                                    if (ispit.getId().equals(selectedIspitId)) {
                                        ispitCombo.setValue(ispit);
                                        loadRezultati(selectedIspitId);
                                        break;
                                    }
                                }
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju ispita: " + error.getMessage());
                        })
                );
    }

    private void loadRezultati(Long ispitId) {
        ispitService.getRezultatiIspitaAsync(ispitId)
                .collectList()
                .subscribe(
                        rezultati -> Platform.runLater(() -> {
                            currentRezultati = rezultati;
                            tabelaRezultati.setItems(FXCollections.observableArrayList(rezultati));

                            IspitDTO ispit = ispitCombo.getValue();
                            if (ispit != null) {
                                currentIspit = ispit;
                                lblIspitInfo.setText("Predmet: " + ispit.getPredmetNaziv() +
                                        " | Datum: " + ispit.getDatumOdrzavanja() +
                                        " | Prijavljeno studenata: " + rezultati.size());
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            showError("Greska pri ucitavanju rezultata: " + error.getMessage());
                        })
                );
    }

    @FXML
    public void handleIspitniRokSelected() {
        IspitniRokDTO selectedRok = ispitniRokCombo.getValue();
        if (selectedRok != null) {
            loadIspiti(selectedRok.getId());
            ispitCombo.setValue(null);
            tabelaRezultati.getItems().clear();
            lblIspitInfo.setText("");
        }
    }

    @FXML
    public void handleIspitSelected() {
        IspitDTO selectedIspit = ispitCombo.getValue();
        if (selectedIspit != null) {
            loadRezultati(selectedIspit.getId());
        }
    }

    @FXML
    public void handlePrikaziProsecnuOcenu() {
        IspitDTO selectedIspit = ispitCombo.getValue();
        if (selectedIspit == null) {
            showError("Molimo izaberite ispit.");
            return;
        }

        try {
            ProsecnaOcenaDTO prosecna = ispitService.getProsecnaOcena(selectedIspit.getId());
            StringBuilder sb = new StringBuilder();
            sb.append("Prosecna ocena: ").append(String.format("%.2f", prosecna.getProsecnaOcena()));
            sb.append(" | Broj studenata koji su polozili: ").append(prosecna.getBrojStudenata());

            if (prosecna.getDistribucijaOcena() != null && !prosecna.getDistribucijaOcena().isEmpty()) {
                sb.append(" | Distribucija: ");
                prosecna.getDistribucijaOcena().forEach((ocena, broj) ->
                        sb.append("Ocena ").append(ocena).append(": ").append(broj).append(" | "));
            }

            lblProsecnaOcena.setText(sb.toString());
            lblProsecnaOcena.setStyle("-fx-text-fill: blue; -fx-font-size: 14px; -fx-font-weight: bold;");
        } catch (Exception e) {
            showError("Greska pri ucitavanju prosecne ocene: " + e.getMessage());
        }
    }

    @FXML
    public void handleStampajZapisnik() {
        if (currentRezultati == null || currentRezultati.isEmpty()) {
            showError("Nema rezultata za stampanje.");
            return;
        }

        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/zapisnikIspita.jrxml");
            if (reportStream == null) {
                showError("Izvestaj nije pronadjen.");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("predmetNaziv", currentIspit.getPredmetNaziv());
            parameters.put("datumIspita", currentIspit.getDatumOdrzavanja() != null ?
                    currentIspit.getDatumOdrzavanja().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")) : "");
            parameters.put("ispitniRok", ispitniRokCombo.getValue().getNaziv());
            parameters.put("datumStampe", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(currentRezultati);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            String fileName = "zapisnik_" + currentIspit.getPredmetNaziv().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
            File pdfFile = new File(System.getProperty("java.io.tmpdir"), fileName);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }

            statusLabel.setText("Zapisnik generisan: " + pdfFile.getName());
            statusLabel.setStyle("-fx-text-fill: green;");

        } catch (JRException e) {
            showError("Greska pri generisanju zapisnika: " + e.getMessage());
        } catch (Exception e) {
            showError("Greska pri otvaranju PDF-a: " + e.getMessage());
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

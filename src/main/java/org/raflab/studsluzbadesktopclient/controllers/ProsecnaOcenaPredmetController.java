package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.raflab.studsluzbadesktopclient.dtos.OcenaDistribucijaDTO;
import org.raflab.studsluzbadesktopclient.dtos.ProsecnaOcenaDTO;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProsecnaOcenaPredmetController {

    private final PredmetService predmetService;

    private static String predmetSifra;
    private static String predmetNaziv;

    @FXML
    private Label lblPredmetNaziv;

    @FXML
    private TextField godinaOdTf;

    @FXML
    private TextField godinaDoTf;

    @FXML
    private Label lblProsecnaOcena;

    @FXML
    private Label lblBrojStudenata;

    @FXML
    private TableView<OcenaDistribucijaDTO> tabelaDistribucija;

    private ProsecnaOcenaDTO currentData;

    public ProsecnaOcenaPredmetController(PredmetService predmetService) {
        this.predmetService = predmetService;
    }

    public static void setPredmetSifra(String sifra) {
        predmetSifra = sifra;
    }

    public static void setPredmetNaziv(String naziv) {
        predmetNaziv = naziv;
    }

    @FXML
    public void initialize() {
        lblPredmetNaziv.setText("Prosecna ocena na predmetu: " + predmetNaziv);
        godinaOdTf.setText("2015");
        godinaDoTf.setText("2024");
    }

    @FXML
    public void handleIzracunaj() {
        try {
            int godinaOd = Integer.parseInt(godinaOdTf.getText().trim());
            int godinaDo = Integer.parseInt(godinaDoTf.getText().trim());

            if (godinaOd > godinaDo) {
                showError("Godina od mora biti manja ili jednaka godini do.");
                return;
            }

            predmetService.getProsecnaOcenaAsync(predmetSifra, godinaOd, godinaDo)
                    .subscribe(
                            data -> Platform.runLater(() -> {
                                currentData = data;
                                displayData(data);
                            }),
                            error -> Platform.runLater(() -> {
                                showError("Greska pri dobijanju prosecne ocene: " + error.getMessage());
                            })
                    );
        } catch (NumberFormatException e) {
            showError("Molimo unesite ispravne godine.");
        }
    }

    private void displayData(ProsecnaOcenaDTO data) {
        if (data.getProsecnaOcena() != null) {
            lblProsecnaOcena.setText(String.format("%.2f", data.getProsecnaOcena()));
        } else {
            lblProsecnaOcena.setText("-");
        }

        if (data.getBrojStudenata() != null) {
            lblBrojStudenata.setText(String.valueOf(data.getBrojStudenata()));
        } else {
            lblBrojStudenata.setText("0");
        }

        if (data.getDistribucijaOcena() != null) {
            List<OcenaDistribucijaDTO> distribucija = new ArrayList<>();
            for (Map.Entry<Integer, Long> entry : data.getDistribucijaOcena().entrySet()) {
                distribucija.add(new OcenaDistribucijaDTO(entry.getKey(), entry.getValue()));
            }
            distribucija.sort((a, b) -> Integer.compare(b.getOcena(), a.getOcena()));
            tabelaDistribucija.setItems(FXCollections.observableArrayList(distribucija));
        }
    }

    @FXML
    public void handleStampaj() {
        if (currentData == null) {
            showError("Nema podataka za stampanje. Prvo izracunajte prosecnu ocenu.");
            return;
        }

        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/prosecnaOcena.jrxml");
            if (reportStream == null) {
                showError("Izvestaj nije pronadjen.");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("predmetNaziv", predmetNaziv);
            parameters.put("godinaOd", godinaOdTf.getText());
            parameters.put("godinaDo", godinaDoTf.getText());
            parameters.put("prosecnaOcena", currentData.getProsecnaOcena() != null ?
                    String.format("%.2f", currentData.getProsecnaOcena()) : "-");
            parameters.put("brojStudenata", currentData.getBrojStudenata() != null ?
                    currentData.getBrojStudenata().toString() : "0");

            List<OcenaDistribucijaDTO> distribucija = new ArrayList<>();
            if (currentData.getDistribucijaOcena() != null) {
                for (Map.Entry<Integer, Long> entry : currentData.getDistribucijaOcena().entrySet()) {
                    distribucija.add(new OcenaDistribucijaDTO(entry.getKey(), entry.getValue()));
                }
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(distribucija);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            showError("Greska pri generisanju izvestaja: " + e.getMessage());
        }
    }

    @FXML
    public void handleZatvori() {
        Stage stage = (Stage) lblPredmetNaziv.getScene().getWindow();
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

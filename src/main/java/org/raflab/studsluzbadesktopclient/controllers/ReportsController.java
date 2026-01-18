package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.raflab.studsluzbadesktopclient.MainView;
import dto.response.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@Component
public class ReportsController {

    private final StudentService studentService;

    @FXML
    private BorderPane reportsPane;

    ReportsController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void handleIzvestajSviStudenti() {
        try {
            List<StudentDTO> studenti = studentService.sviStudenti();
            if (studenti == null || studenti.isEmpty()) {
                showError("Nema studenata za izvestaj.");
                return;
            }
            generateReport(studenti, "/reports/sviStudenti.jrxml", "sviStudenti.pdf");
        } catch (Exception e) {
            showError("Greska pri generisanju izvestaja: " + e.getMessage());
        }
    }

    @FXML
    TextField godinaUpisaTf;
    public void handleIzvestajSviStudentiPoGodiniUpisa() {
        try {
            if (godinaUpisaTf.getText() == null || godinaUpisaTf.getText().isEmpty()) {
                showError("Molimo unesite godinu upisa.");
                return;
            }
            List<StudentDTO> studenti = studentService.searchStudentsByGodinaUpisa(Integer.parseInt(godinaUpisaTf.getText()));
            if (studenti == null || studenti.isEmpty()) {
                showError("Nema studenata za izabranu godinu upisa.");
                return;
            }
            generateReport(studenti, "/reports/sviStudenti.jrxml", "sviStudentiPoGodiniUpisa.pdf");
        } catch (NumberFormatException e) {
            showError("Godina upisa mora biti broj.");
        } catch (Exception e) {
            showError("Greska pri generisanju izvestaja: " + e.getMessage());
        }
    }

    @FXML
    TextField studProgTf;
    public void handleIzvestajSviStudentiPoStudProg() {
        try {
            if (studProgTf.getText() == null || studProgTf.getText().isEmpty()) {
                showError("Molimo unesite studijski program.");
                return;
            }
            List<StudentDTO> studenti = studentService.searchStudentsByStudProg(studProgTf.getText());
            if (studenti == null || studenti.isEmpty()) {
                showError("Nema studenata za izabrani studijski program.");
                return;
            }
            generateReport(studenti, "/reports/sviStudenti.jrxml", "sviStudentiPoStudProg.pdf");
        } catch (Exception e) {
            showError("Greska pri generisanju izvestaja: " + e.getMessage());
        }
    }

    public void generateReport(List<StudentDTO> studenti, String reportPath, String defaultFileName) throws JRException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sacuvaj izvestaj");
        fileChooser.setInitialFileName(defaultFileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF fajlovi", "*.pdf"));

        Stage stage = (Stage) reportsPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            JasperReport report = JasperCompileManager.compileReport(MainView.class.getResourceAsStream(reportPath));
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(studenti);
            JasperPrint jp = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);
            JasperExportManager.exportReportToPdfFile(jp, file.getAbsolutePath());
            showInfo("Izvestaj uspesno sacuvan: " + file.getAbsolutePath());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Uspeh");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

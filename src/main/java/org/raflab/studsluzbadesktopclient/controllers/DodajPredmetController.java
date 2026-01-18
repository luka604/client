package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dto.request.PredmetRequestDTO;
import lombok.Setter;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.springframework.stereotype.Component;

@Component
public class DodajPredmetController {
    private final PredmetService predmetService;

    @FXML
    private TextField sifraTf;
    @FXML
    private TextField nazivTf;
    @FXML
    private TextArea opisTa;
    @FXML
    private TextField espbTf;
    @FXML
    private TextField semestarTf;
    @FXML
    private TextField fondPredavanjaTf;
    @FXML
    private TextField fondVezbiTf;
    @FXML
    private CheckBox obavezanCb;
    @FXML
    private Label errorLabel;

    @Setter
    private static String studijskiProgramOznaka;

    public DodajPredmetController(PredmetService predmetService) {
        this.predmetService = predmetService;
    }

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    public void handleSacuvaj() {
        errorLabel.setText("");

        if (!validateInput()) {
            return;
        }

        try {
            PredmetRequestDTO dto = new PredmetRequestDTO();
            dto.setSifra(sifraTf.getText().trim());
            dto.setNaziv(nazivTf.getText().trim());
            dto.setOpis(opisTa.getText() != null ? opisTa.getText().trim() : "");
            dto.setEspb(Integer.parseInt(espbTf.getText().trim()));
            dto.setSemestar(Integer.parseInt(semestarTf.getText().trim()));
            dto.setFondPredavanja(Integer.parseInt(fondPredavanjaTf.getText().trim()));
            dto.setFondVezbi(Integer.parseInt(fondVezbiTf.getText().trim()));
            dto.setObavezan(obavezanCb.isSelected());

            predmetService.addToStudijskiProgramAsync(studijskiProgramOznaka, dto)
                    .subscribe(
                            result -> Platform.runLater(() -> {
                                showSuccess("Predmet uspesno dodat!");
                                closeWindow();
                            }),
                            error -> Platform.runLater(() -> {
                                errorLabel.setText("Greska: " + error.getMessage());
                            })
                    );
        } catch (NumberFormatException e) {
            errorLabel.setText("Neispravni brojevi u poljima ESPB, Semestar, Fond predavanja ili Fond vezbi.");
        }
    }

    @FXML
    public void handleOtkazi() {
        closeWindow();
    }

    private boolean validateInput() {
        if (sifraTf.getText() == null || sifraTf.getText().trim().isEmpty()) {
            errorLabel.setText("Sifra predmeta je obavezna.");
            return false;
        }
        if (nazivTf.getText() == null || nazivTf.getText().trim().isEmpty()) {
            errorLabel.setText("Naziv predmeta je obavezan.");
            return false;
        }
        if (espbTf.getText() == null || espbTf.getText().trim().isEmpty()) {
            errorLabel.setText("ESPB bodovi su obavezni.");
            return false;
        }
        if (semestarTf.getText() == null || semestarTf.getText().trim().isEmpty()) {
            errorLabel.setText("Semestar je obavezan.");
            return false;
        }
        if (fondPredavanjaTf.getText() == null || fondPredavanjaTf.getText().trim().isEmpty()) {
            errorLabel.setText("Fond predavanja je obavezan.");
            return false;
        }
        if (fondVezbiTf.getText() == null || fondVezbiTf.getText().trim().isEmpty()) {
            errorLabel.setText("Fond vezbi je obavezan.");
            return false;
        }

        try {
            int espb = Integer.parseInt(espbTf.getText().trim());
            if (espb < 3 || espb > 8) {
                errorLabel.setText("ESPB mora biti izmedju 3 i 8.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("ESPB mora biti broj.");
            return false;
        }

        try {
            int semestar = Integer.parseInt(semestarTf.getText().trim());
            if (semestar < 1 || semestar > 8) {
                errorLabel.setText("Semestar mora biti izmedju 1 i 8.");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Semestar mora biti broj.");
            return false;
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) sifraTf.getScene().getWindow();
        stage.close();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Uspeh");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

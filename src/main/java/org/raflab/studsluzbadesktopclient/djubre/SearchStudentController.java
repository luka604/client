package org.raflab.studsluzbadesktopclient.djubre;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import dto.response.StudentDTO;
import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.navigation.NavigationHistory;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class SearchStudentController {

    private final StudentService studentService;
    private final MainView mainView;
    private final NavigationHistory navigationHistory;

    @FXML
    private TextField imeStudentaTf;

    @FXML
    private TableView<StudentDTO> tabelaStudenti;

    public SearchStudentController(StudentService studentService,
                                   MainView mainView,
                                   NavigationHistory navigationHistory) {
        this.studentService = studentService;
        this.mainView = mainView;
        this.navigationHistory = navigationHistory;
    }

    public void handleSearchStudent(ActionEvent actionEvent) {
        if(imeStudentaTf.getText().isEmpty())
            tabelaStudenti.setItems(FXCollections.observableArrayList(studentService.sviStudenti()));
        else{
            Flux<StudentDTO> flux = studentService.searchStudentsAsync(imeStudentaTf.getText());
            //Mono predstavlja 0 ili 1 element.
            flux.collectList() // pretvara Flux u Mono<List<StudentDTO>>
                    .subscribe(
                            list -> {
                                // Ovo se izvršava kada stigne rezultat
                                tabelaStudenti.setItems(FXCollections.observableArrayList(list));
                                System.out.println("Rezultat je stigao.");
                            },
                            error -> {
                                System.out.println(error.getMessage());
                            }
                    );
            System.out.println("Nakon search operacije.");
        }
    }

    @FXML
    public void handleOtvoriProfil() {
        StudentDTO selected = tabelaStudenti.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Molimo izaberite studenta iz tabele.");
            return;
        }

        if (selected.getBrojIndeksa() == null || selected.getBrojIndeksa().isEmpty()) {
            showError("Izabrani student nema broj indeksa.");
            return;
        }

        navigationHistory.setSelectedBrojIndeksa(selected.getBrojIndeksa());
        mainView.changeRoot("studentProfil");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
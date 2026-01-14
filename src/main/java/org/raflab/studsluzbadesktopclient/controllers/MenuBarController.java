package org.raflab.studsluzbadesktopclient.controllers;

import org.raflab.studsluzbadesktopclient.MainView;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

@Component
public class MenuBarController { 

	final MainView mainView;
	final StudentService studentService;

	@FXML
	private MenuBar menuBar;

    public MenuBarController(StudentService studentService, MainView mainView){
        this.studentService = studentService;
        this.mainView = mainView;
    }

	public void openSearchStudent() {
		mainView.changeRoot("searchStudent");
	}

	public void openNewStudent() {
		mainView.changeRoot("newStudent");
	}

	public void openEventsPage(){
		mainView.changeRoot("events");
	}

	public void openReportsPage(){
		mainView.changeRoot("reports");
	}

	public void openStudijskiProgrami() {
		mainView.changeRoot("studijskiProgrami");
	}

	public void openIspitniRokovi() {
		mainView.changeRoot("ispitniRokovi");
	}

	public void openIspiti() {
		mainView.changeRoot("ispiti");
	}

	public void openRezultatiIspita() {
		mainView.changeRoot("rezultatiIspita");
	}

	@FXML
    public void initialize() {

    }
}
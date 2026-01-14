package org.raflab.studsluzbadesktopclient;

import java.io.IOException;
import java.util.Objects;

import org.raflab.studsluzbadesktopclient.navigation.NavigationHistory;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class MainView {

    private final ContextFXMLLoader appFXMLLoader;
    private final NavigationHistory navigationHistory;
    private Scene scene;
    private boolean navigatingFromHistory = false;

    public MainView(ContextFXMLLoader appFXMLLoader, NavigationHistory navigationHistory) {
        this.appFXMLLoader = appFXMLLoader;
        this.navigationHistory = navigationHistory;
    }

    public Scene createScene() {
        try {
            FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/main.fxml"));
            BorderPane borderPane = loader.load();
            this.scene = new Scene(borderPane, 1000, 800);
            scene.getStylesheets().add(Objects.requireNonNull(MainView.class.getResource("/css/stylesheet.css")).toExternalForm());

            setupNavigationHandlers();

            navigationHistory.navigateTo("main");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.scene;
    }

    private void setupNavigationHandlers() {
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.BACK) {
                goBack();
                event.consume();
            } else if (event.getButton() == MouseButton.FORWARD) {
                goForward();
                event.consume();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                if (event.getCode() == KeyCode.OPEN_BRACKET) {
                    goBack();
                    event.consume();
                } else if (event.getCode() == KeyCode.CLOSE_BRACKET) {
                    goForward();
                    event.consume();
                }
            }
        });
    }

    public void goBack() {
        String previousPage = navigationHistory.goBack();
        if (previousPage != null) {
            navigatingFromHistory = true;
            loadFxml(previousPage);
            navigatingFromHistory = false;
        }
    }

    public void goForward() {
        String nextPage = navigationHistory.goForward();
        if (nextPage != null) {
            navigatingFromHistory = true;
            loadFxml(nextPage);
            navigatingFromHistory = false;
        }
    }

    private void loadFxml(String fxml) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeRoot(String fxml) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            scene.setRoot(loader.load());

            if (!navigatingFromHistory) {
                navigationHistory.navigateTo(fxml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node loadPane(String fxml) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openModal(String fxml) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            Parent parent = loader.load();
            Scene modalScene = new Scene(parent, 400, 300);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(modalScene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openModal(String fxml, String title) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            Parent parent = loader.load();
            Scene modalScene = new Scene(parent, 400, 300);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(modalScene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openModal(String fxml, String title, int width, int height) {
        FXMLLoader loader = appFXMLLoader.getLoader(MainView.class.getResource("/fxml/" + fxml + ".fxml"));
        try {
            Parent parent = loader.load();
            Scene modalScene = new Scene(parent, width, height);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(modalScene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean canGoBack() {
        return navigationHistory.canGoBack();
    }

    public boolean canGoForward() {
        return navigationHistory.canGoForward();
    }

    public NavigationHistory getNavigationHistory() {
        return navigationHistory;
    }
}

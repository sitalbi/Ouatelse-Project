package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

@Getter
@Setter
public abstract class BaseScreen {

    public static Image OUATELSE_ICON = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResource("images/ouatelse_icon.png")).toExternalForm());

    private Stage stage;

    public BaseScreen(String fxml, String title) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource(fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(title);

        try {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add("css/base.css");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStyleSheet(String cssFile) {
        this.getStage().getScene().getStylesheets().add(cssFile);
    }

    public void open() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}

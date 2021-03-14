package fr.s4e2.ouatelse.screens;

import fr.s4e2.ouatelse.Main;
import fr.s4e2.ouatelse.controllers.AuthStoreController;
import fr.s4e2.ouatelse.controllers.HomeController;
import fr.s4e2.ouatelse.objects.Store;
import fr.s4e2.ouatelse.objects.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

/**
 * Base screen of the software
 */
@Getter
@Setter
public abstract class BaseScreen {

    public static Image OUATELSE_ICON = new Image(Objects.requireNonNull(Main.class.getClassLoader().getResource("images/ouatelse_icon.png")).toExternalForm());

    private Stage stage;

    /**
     * Constructor
     *
     * @param fxml  the Scene
     * @param title the Title
     */
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
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Constructor
     *
     * @param fxml  the Scene
     * @param title the Title
     * @param user  the User
     */
    public BaseScreen(String fxml, String title, User user) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(title);

        try {
            Parent parent = loader.load();

            AuthStoreController authStoreController = loader.getController();
            authStoreController.setCurrentUser(user);

            Scene scene = new Scene(parent);
            stage.setScene(scene);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Constructor
     *
     * @param fxml  the Scene
     * @param title the Title
     * @param user  the User
     * @param store the Store
     */
    public BaseScreen(String fxml, String title, User user, Store store) {
        this.stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource(fxml));
        stage.getIcons().add(OUATELSE_ICON);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinHeight(650);
        stage.setMinWidth(825);
        stage.setTitle(title);

        try {
            Parent parent = loader.load();

            HomeController homeController = loader.getController();
            homeController.setCurrentUser(user);
            homeController.setCurrentStore(store);

            Scene scene = new Scene(parent);
            stage.setScene(scene);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * Allows to add a Cascading Style Sheet
     *
     * @param cssFile the CSS file to be added
     */
    public void addStyleSheet(String cssFile) {
        this.getStage().getScene().getStylesheets().add(cssFile);
    }

    /**
     * Allows to open the Stage
     */
    public void open() {
        stage.show();
    }

    /**
     * Allows to close the Stage
     */
    public void close() {
        stage.close();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import prac1.utils.FileUtils;

/**
 * FXML Controller class
 *
 * @author manel
 */
public class MainScreenController implements Initializable {

    Media media;
    MediaPlayer player;

    @FXML
    private Button repeatButton;
    @FXML
    private Button backfastforwardButton;
    @FXML
    private Button playButton;
    @FXML
    private Button fastforwardButton;
    @FXML
    private Button volumButton;
    @FXML
    private Slider sliderBar;
    @FXML
    private Button listPlayButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button randomButton;
    @FXML
    private MenuItem loadfileButton;

    // el Map on desarem les dades
    ObservableMap<String, Object> metaDades;

    /**
     * *
     * Inicialitza el controlador
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String path = FileUtils.getTestMP3(this);

        openMedia(path);

        //Este codigo habilita el slider volumen
        sliderBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                player.setVolume(sliderBar.getValue() * 0.01);
            }

        });
    }

    /**
     * *
     * Inicialitza el reproductor amb un fitxer MP3
     *
     * El format ha de ser de tipus URL
     *
     *
     */
    @FXML
    private void onAction_PlayButton(ActionEvent event) {
        // si l'anterior ha anat bé (media no és null), obtenim les metadades
        if (media != null) {
            metaDades = media.getMetadata();

            //txtArea1.appendText("METADADES: " + System.lineSeparator());
            //for (Map.Entry<String, Object> entrada : metaDades.entrySet()) {
            //    txtArea1.appendText("Clau -->" + entrada.getKey() + "    Valor ---> " + entrada.getValue() + System.lineSeparator());
            //}
            player.play();
        }
    }

    @FXML
    private void onAction_loadfileButton(ActionEvent event) {

        // desactivem el botó fins que el player estigui en estat READY 
        this.playButton.setDisable(true);

        Path file = FileUtils.getMP3Fromfile();

        if (file != null) {

            String mp3File = FileUtils.normalizeURLFormat(file.toString());

            //txtArea1.appendText("Obrint [" + mp3File + "]....");
            openMedia(mp3File);

            //btn_select.setDisable(true);
        }
    }

    private void openMedia(String path) {
        try {

            // actuaslitzem el recurs MP3
            this.media = new Media(path);

            // inicialitzem el reproductor
            this.player = new MediaPlayer(media);

            // un cop el reproductor està preparat, podem activar el botó per a procedir
            player.setOnReady(() -> {

                this.playButton.setDisable(false);

            });

        } catch (MediaException e) {

            this.media = null;

            this.player = null;

            System.out.println("ERROR obrint fitxer demo: " + path + ":" + e.toString());
        }
    }
}

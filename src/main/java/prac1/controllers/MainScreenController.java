/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import prac1.utils.FileUtils;

/**
 * FXML Controller class
 *
 * @author manel
 */
public class MainScreenController implements Initializable {

    ObservableMap<String, Object> metaDades;
    ObservableList<String> songs = FXCollections.observableArrayList();
    Media media = null;

    MediaPlayer player = null;
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
    @FXML
    private ListView<String> songListView;

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
        songListView.setItems(songs);

        songListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

            this.songListView.setItems(songs);
            deleteButton.setDisable(false);
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
    // Cargamos el archivo
    @FXML
    private void onAction_loadfileButton(ActionEvent event) {

        // desactivem el botó fins que el player estigui en estat READY 
        this.playButton.setDisable(true);

        Path file = FileUtils.getMP3Fromfile();

        if (file != null) {
            String mp3File = FileUtils.normalizeURLFormat(file.toString());
            openMedia(mp3File);
            songs.add(mp3File);
            playButton.setDisable(true);
        }
    }

    // Empezamos a reproducir la canción, además cogemos los metadatos en metaDades.
    @FXML
    private void onAction_PlayButton(ActionEvent event) {

        int selectedSongPosition = songListView.getSelectionModel().getSelectedIndex();
        if (selectedSongPosition > -1) {
        }

        if (media != null) {
            metaDades = media.getMetadata();
            player.play();
        }
    }

    @FXML
    private void onAction_deleteButton(ActionEvent event) {

        int selectedSongPosition = songListView.getSelectionModel().getSelectedIndex();

        if (selectedSongPosition > -1) {
            songs.remove(selectedSongPosition);
        }

        if (songs.isEmpty()) {
            deleteButton.setDisable(true);
        }

    }

    //Este metodo 
    private void openMedia(String path) {
        try {

            // actualitzem el recurs MP3
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

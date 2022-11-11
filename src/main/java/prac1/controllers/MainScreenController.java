/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import prac1.utils.FileUtils;

/**
 * FXML Controller class
 *
 * @author manel
 */
public class MainScreenController implements Initializable {

    ObservableMap<String, Object> metaDades;
    ObservableList<String> songs = FXCollections.observableArrayList();
    ArrayList<String> _path = new ArrayList<>();
    Media media = null;
    MediaPlayer player = null;
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private boolean progressbar = false;
    Image playing;
    Image pausing;
    //Image audioMuted;
    //Image audioNoMuted;

    @FXML
    private Button repeatButton;
    @FXML
    private Button backFastForwardButton;
    @FXML
    private Button playButton;
    @FXML
    private Button fastForwardButton;
    @FXML
    private Button volumButton;
    @FXML
    private Slider sliderBar;
    @FXML
    private Button deleteButton;
    @FXML
    private Button randomButton;
    @FXML
    private MenuItem loadFileButton;
    @FXML
    private ListView<String> songListView;
    @FXML
    private ImageView imagePlay;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private ImageView volumenImage;

    /**
     * *
     * Inicialitza el controlador
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        playButton.setDisable(true);
        songProgressBar.setVisible(false);

        songListView.setItems(songs);

        songListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("old" + oldValue);
            System.out.println("new" + newValue);
            System.out.println("obs" + observable);
            this.songListView.setItems(songs);

            openMedia(_path.get(songListView.getSelectionModel().getSelectedIndex()));

        });

        //Este codigo habilita el slider volumen
        sliderBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {

                player.setVolume(sliderBar.getValue() * 0.01);
            }
        });
    }

// Cargamos el archivo
    @FXML
    private void onAction_loadFileButton(ActionEvent event) {

        // desactivem el botó fins que el player estigui en estat READY 
        this.playButton.setDisable(true);

        Path file = FileUtils.getMP3Fromfile();

        if (file != null) {
            String mp3File = FileUtils.normalizeURLFormat(file.toString());
            openMedia(mp3File);
            playButton.setDisable(true);
        }
    }

    // Empezamos a reproducir la canción
    @FXML
    private void onAction_playButton(ActionEvent event) {

        // Comienza el progressBar
        beginTimer();

        switch (player.getStatus()) {
            case READY:
                player.play();
                pausing = new Image(FileUtils.getIcona(this, "pause.png"));
                imagePlay.setImage(pausing);
                break;

            case PLAYING:
                player.pause();
                pausing = new Image(FileUtils.getIcona(this, "play_1.png"));
                imagePlay.setImage(pausing);
                break;

            case PAUSED:
                player.play();
                playing = new Image(FileUtils.getIcona(this, "pause.png"));
                imagePlay.setImage(playing);
                break;
        }
    }

    @FXML
    private void onAction_deleteButton(ActionEvent event) {

        int selectedSongPosition = songListView.getSelectionModel().getSelectedIndex();

        if (selectedSongPosition > -1) {
            songs.remove(selectedSongPosition);
            _path.remove(selectedSongPosition);
            pausing = new Image(FileUtils.getIcona(this, "play_1.png"));
            imagePlay.setImage(pausing);
        }

        if (songs.isEmpty()) {
            deleteButton.setDisable(true);
            playButton.setDisable(true);
            player.stop();
            pausing = new Image(FileUtils.getIcona(this, "play_1.png"));
            imagePlay.setImage(pausing);
            media = null;
            songProgressBar.setProgress(0);
            player.seek(Duration.seconds(0.0));
        }
    }

    /**
     * Retrocedeix 5 segons la reproduccio
     *
     * @param event
     */
    @FXML
    private void onAction_backFastForwardButton(ActionEvent event) {

        //Obtenir temps de reproduccio actual i restar 5 segons
        Duration currentTime = this.player.getCurrentTime();
        currentTime = currentTime.subtract(Duration.seconds(5));
        this.player.seek(currentTime);
    }

    /**
     * Avança 5 segons la reproduccio
     *
     * @param event
     */
    @FXML
    private void onAction_fastForwardButton(ActionEvent event) {

        //Obtenir temps de reproduccio actual i afegir 5 segons
        Duration currentTime = this.player.getCurrentTime();
        currentTime = currentTime.add(Duration.seconds(5));
        this.player.seek(currentTime);
    }

    /**
     * Reorganitza aleatoriament la llista de reproduccio
     *
     * @param event
     */
    @FXML
    private void onAction_randomButton(ActionEvent event) {

        FXCollections.shuffle(songs);
    }

    private void openMedia(String path) {
        try {

            // actuaslitzem el recurs MP3
            this.media = new Media(path);

            // inicialitzem el reproductor
            this.player = new MediaPlayer(media);

            // un cop el reproductor està preparat, podem activar el botó per a procedir
            player.setOnReady(() -> {
                if (!_path.contains(path)) {
                    _path.add(path);
                    getMetadata();
                }

                this.playButton.setDisable(false);
            });
        } catch (MediaException e) {

            this.media = null;
            this.player = null;

            System.out.println("ERROR obrint fitxer demo: " + path + ":" + e.toString());
        }
    }

    private void getMetadata() {
        metaDades = media.getMetadata();

        if (!metaDades.isEmpty()) {

            if (metaDades.containsKey("title")) {
                songs.add(metaDades.get("title").toString());
            } else if (metaDades.containsKey("author")) {
                songs.add(metaDades.get("author").toString());
            } else {
                songs.add("Unknown artist");
            }
        } else {
            songs.add("Unknown artist");
        }
    }

    public void beginTimer() {

        timer = new Timer();
        task = new TimerTask() {

            public void run() {

                running = true;
                double current = player.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                //System.out.println(current / end);
                songProgressBar.setProgress(current / end);

                if (current / end == 1) {
                    cancelTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void cancelTimer() {

        running = false;
        timer.cancel();
    }

    @FXML
    void onAction_menuProgressBar(ActionEvent event) {
        //invierte del boolean de progressbar
        progressbar = !progressbar;

        if (progressbar) {
            songProgressBar.setVisible(true);
        } else {
            songProgressBar.setVisible(false);
        }

    }
}

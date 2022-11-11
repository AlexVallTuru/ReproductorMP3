/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.nio.file.Path;
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
import prac1.utils.Song;

/**
 * FXML Controller class
 *
 * @author manel
 */
public class MainScreenController implements Initializable {

    ObservableMap<String, Object> metaDades;
    ObservableList<String> songNames = FXCollections.observableArrayList();
    ObservableList<Song> songs = FXCollections.observableArrayList();
    Media media = null;
    MediaPlayer player = null;
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private boolean progressbar = false;
    Image playing;
    Image pausing;
    Song lastLoadedSong;

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

        songListView.setItems(songNames);

        songListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

            this.songListView.setItems(songNames);

            openMedia(songs.get(songListView.getSelectionModel().getSelectedIndex()).getSongPath());

        });

        //Este codigo habilita el slider volumen
        sliderBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            player.setVolume(sliderBar.getValue() * 0.01);
        });
    }

    /**
     * Obtiene la ruta del archivo que se carga, la envía a "openMedia", y se
     * queda esperando a que "media" esté listo, cuando está listo rellena los
     * atributos de Song y además informa en el ListView el título de la
     * canción.
     *
     * @param event
     */
    @FXML
    private void onAction_loadFileButton(ActionEvent event) {

        // desactivem el botó fins que el player estigui en estat READY 
        this.playButton.setDisable(true);

        Path file = FileUtils.getMP3Fromfile();
        
        if (file != null && file.toString().toLowerCase().endsWith(".mp3")) {
            String mp3File = FileUtils.normalizeURLFormat(file.toString());
            openMedia(mp3File);
            player.setOnReady(() -> {
                lastLoadedSong = getFilledSong(mp3File);
                songs.add(lastLoadedSong);
                songNames.add(lastLoadedSong.getSongName());
            });
            playButton.setDisable(true);
        }
    }

    /**
     * Empieza y pausa la reproduccion de la cancion seleccionada, cambiando el
     * icono del boton cuando es necesario
     *
     * @param event
     */
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

    /**
     * Obtiene el índice de la canción que tienes seleccionada y la elimina
     * tanto del ListView como de la lista de "songs". Además, si la canción que
     * has eliminado era la última canción deshabilita el botón de Play y limpia
     * el media.
     *
     * @param event
     */
    @FXML
    private void onAction_deleteButton(ActionEvent event) {

        int selectedSongPosition = songListView.getSelectionModel().getSelectedIndex();

        if (selectedSongPosition > -1) {
            songs.remove(selectedSongPosition);
            songNames.remove(selectedSongPosition);
            pausing = new Image(FileUtils.getIcona(this, "play_1.png"));
            imagePlay.setImage(pausing);
        }

        if (songNames.isEmpty()) {
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
     * Retrocede 5 segundos la reproduccion de la pista actual
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
     * Avanza 5 segundos la reproduccion de la pista actual
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
     * Reorganiza aleatoriamente la lista de reproducción siempre y cuando haya
     * al menos 3 canciones.
     *
     * @param event
     */
    @FXML
    private void onAction_randomButton(ActionEvent event) {

        if (songNames.size() <= 3) {
            FXCollections.shuffle(songs);
            songNames.clear();

            songs.forEach(s -> {
                songNames.add(s.getSongName());
            });
        }
    }

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

    /**
     * Genera un objeto de tipo "Song" y le asignamos los atributos "path",
     * "metaDades" y lo retornamos.
     *
     * @param path
     */
    private Song getFilledSong(String path) {

        //Creamos un nuevo "Song" y le seteamos el atributo path.
        Song newSong = new Song();
        newSong.setSongPath(path);

        metaDades = media.getMetadata();
        newSong.setMetaDades(metaDades);

        return newSong;
    }

    public void beginTimer() {

        timer = new Timer();
        task = new TimerTask() {

            @Override
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

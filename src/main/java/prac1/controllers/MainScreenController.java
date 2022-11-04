/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package prac1.controllers;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
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
    
    void on_botTestClic(ActionEvent event) {
        
        if (this.player != null)
            player.play();     

    }
  
    /***
     * Inicialitza el controlador
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
       String path  = FileUtils.getTestMP3(this);
        
       openMedia(path);
        
    }
    
    /***
     * Inicialitza el reproductor amb un fitxer MP3
     * 
     * El format ha de ser de tipus URL
     * 
     * 
     */
    private void openMedia(String path)
    {
        try{
            
            // actuaslitzem el recurs MP3
            this.media = new Media(path);

            // inicialitzem el reproductor
            this.player = new MediaPlayer(media);
            
        } catch (MediaException e) {
            
            System.out.println("ERROR obrint fitxer demo: " + path + ":" + e.toString());
        }
    }

    @FXML
    private void onActionRepeatButton(ActionEvent event) {
    }

    @FXML
    private void onActionBackfastforwardButton(ActionEvent event) {
    }

    @FXML
    private void onActionPlayButton(ActionEvent event) {
    }

    @FXML
    private void onAction_FastforwardButton(ActionEvent event) {
    }

    @FXML
    private void onAction_ReapeatButton(ActionEvent event) {
    }

    @FXML
    private void onAction_VolumenButton(ActionEvent event) {
    }

    @FXML
    private void onAction_SliderBar(MouseEvent event) {
    }

    @FXML
    private void onAction_ListPlayButton(ActionEvent event) {
    }

    @FXML
    private void onAction_DeleteButton(ActionEvent event) {
    }
}

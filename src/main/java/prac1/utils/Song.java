/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prac1.utils;

import javafx.collections.ObservableMap;

/**
 *
 * @author Kiwi
 */
public final class Song {

    private ObservableMap<String, Object> metaDades;
    private String songName;
    private String songPath;

    public Song() {
    }

    public Song(ObservableMap<String, Object> metaDades, String songPath) {
        this.setMetaDades(metaDades);
        this.setSongPath(songPath);
    }

    public ObservableMap<String, Object> getMetaDades() {
        return metaDades;
    }

    public void setMetaDades(ObservableMap<String, Object> metaDades) {
        this.metaDades = metaDades;
        setSongName(metaDades);
    }

    public String getSongName() {
        return songName;
    }

    private void setSongName(ObservableMap<String, Object> metaDades) {
        if (metaDades != null && !metaDades.isEmpty()) {

            if (metaDades.containsKey("title")) {
                this.songName = metaDades.get("title").toString();
            } else if (metaDades.containsKey("author")) {
                this.songName = metaDades.get("author").toString();
            } else {
                this.songName = "Unknown artist";
            }
        } else {
            this.songName = "Unknown artist";
        }
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

}

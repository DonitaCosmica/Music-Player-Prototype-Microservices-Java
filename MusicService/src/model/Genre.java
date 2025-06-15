package model;

import java.util.List;

public class Genre {
  private String genreId;
  private String name;
  private String description;
  private List<Song> songs;
  
  public Genre() {}

  public String getGenreId() { return genreId; }
  public void setGenreId(String genreId) { this.genreId = genreId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public List<Song> getSongs() { return songs; }
  public void setSongs(List<Song> songs) { this.songs = songs; }
  
  @Override
  public String toString() {
    return "ID: " + genreId + ", Name: " + name + "\n";
  }
}

package model;

import java.util.List;

public class Artist {
  private String artistId;
  private String name;
  private String bio;
  private Country originCountry;
  private String imageUrl;
  private List<Song> songs;

  public String getArtistId() { return artistId; }
  public void setArtistId(String artistId) { this.artistId = artistId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getBio() { return bio; }
  public void setBio(String bio) { this.bio = bio; }

  public Country getOriginCountry() { return originCountry; }
  public void setOriginCountry(Country country) { this.originCountry = country; }

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

  public List<Song> getSongs() { return songs; }
  public void setSongs(List<Song> songs) { this.songs = songs; }
  
  @Override
  public String toString() {
    return "Artist{" +
      "artistId='" + artistId + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}

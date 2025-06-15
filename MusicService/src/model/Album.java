package model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Album {
  private String albumId;
  private String title;
  private LocalDate releaseDate;
  private String coverArtUrl;
  private Artist primaryArtist;
  private int totalTracks;
  private String recordLabel;
  private List<Song> songs;

  public String getAlbumId() { return albumId; }
  public void setAlbumId(String albumId) { this.albumId = albumId; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public LocalDate getReleaseDate() { return releaseDate; }
  public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

  public String getCoverArtUrl() { return coverArtUrl; }
  public void setCoverArtUrl(String coverArtUrl) { this.coverArtUrl = coverArtUrl; }

  public Artist getPrimaryArtist() { return primaryArtist; }
  public void setPrimaryArtist(Artist primaryArtist) { this.primaryArtist = primaryArtist; }

  public int getTotalTracks() { return totalTracks; }
  public void setTotalTracks(int totalTracks) { this.totalTracks = totalTracks; }

  public String getRecordLabel() { return recordLabel; }
  public void setRecordLabel(String recordLabel) { this.recordLabel = recordLabel; }

  public List<Song> getSongs() { return songs; }
  public void setSongs(List<Song> songs) { this.songs = songs; }
  
  @Override
  public String toString() {
    return "Album{" +
      "albumId='" + albumId + '\'' +
      ", title='" + title + '\'' +
      ", releaseDate=" + releaseDate +
      ", coverArtUrl='" + coverArtUrl + '\'' +
      ", primaryArtist=" + (primaryArtist != null ? primaryArtist.getName() : "N/A") +
      ", totalTracks=" + totalTracks +
      ", recordLabel='" + recordLabel + '\'' +
      ", songs=" + (songs != null ? songs.stream().map(Song::getTitle).collect(Collectors.joining(", ")) : "N/A") +
    '}';
  }
}

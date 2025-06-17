package com.microservices.dao;

import java.time.LocalDate;
import java.util.List;

public class AlbumDAO {
  private String albumId;
  private String title;
  private LocalDate releaseDate;
  private String coverArtUrl;
  private ArtistDAO primaryArtist;
  private int totalTracks;
  private String recordLabel;
  private List<SongDAO> songs;

  public String getAlbumId() { return albumId; }
  public void setAlbumId(String albumId) { this.albumId = albumId; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public LocalDate getReleaseDate() { return releaseDate; }
  public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

  public String getCoverArtUrl() { return coverArtUrl; }
  public void setCoverArtUrl(String coverArtUrl) { this.coverArtUrl = coverArtUrl; }

  public ArtistDAO getPrimaryArtist() { return primaryArtist; }
  public void setPrimaryArtist(ArtistDAO primaryArtist) { this.primaryArtist = primaryArtist; }

  public int getTotalTracks() { return totalTracks; }
  public void setTotalTracks(int totalTracks) { this.totalTracks = totalTracks; }

  public String getRecordLabel() { return recordLabel; }
  public void setRecordLabel(String recordLabel) { this.recordLabel = recordLabel; }

  public List<SongDAO> getSongs() { return songs; }
  public void setSongs(List<SongDAO> songs) { this.songs = songs; }
  
  @Override
  public String toString() {
    return title;
  }
}

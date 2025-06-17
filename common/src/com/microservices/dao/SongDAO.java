package com.microservices.dao;

import java.util.List;

public class SongDAO {
  private String songId;
  private String title;
  private int durationSeconds;
  private String audioFilePath;
  private boolean isExplicit;
  private long playCount;
  private AlbumDAO album;
  private List<ArtistDAO> artists;
  private List<GenreDAO> genres;

  public String getSongId() { return songId; }
  public void setSongId(String songId) { this.songId = songId; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public int getDurationSeconds() { return durationSeconds; }
  public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

  public String getAudioFilePath() { return audioFilePath; }
  public void setAudioFilePath(String audioFilePath) { this.audioFilePath = audioFilePath; }

  public boolean isExplicit() { return isExplicit; }
  public void setExplicit(boolean isExplicit) { this.isExplicit = isExplicit; }

  public long getPlayCount() { return playCount; }
  public void setPlayCount(long playCount) { this.playCount = playCount; }

  public AlbumDAO getAlbum() { return album; }
  public void setAlbum(AlbumDAO album) { this.album = album; }

  public List<ArtistDAO> getArtists() { return artists; }
  public void setArtists(List<ArtistDAO> artists) { this.artists = artists; }

  public List<GenreDAO> getGenres() { return genres; }
  public void setGenres(List<GenreDAO> genres) { this.genres = genres; }
  
  @Override
  public String toString() {
    return title;
  }
}

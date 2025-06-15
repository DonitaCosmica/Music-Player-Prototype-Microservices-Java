package model;

import java.util.List;
import java.util.stream.Collectors;

public class Song {
  private String songId;
  private String title;
  private int durationSeconds;
  private String audioFilePath;
  private boolean isExplicit;
  private long playCount;
  private Album album;
  private List<Artist> artists;
  private List<Genre> genres;

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

  public Album getAlbum() { return album; }
  public void setAlbum(Album album) { this.album = album; }

  public List<Artist> getArtists() { return artists; }
  public void setArtists(List<Artist> artists) { this.artists = artists; }

  public List<Genre> getGenres() { return genres; }
  public void setGenres(List<Genre> genres) { this.genres = genres; }

  @Override
  public String toString() {
    return "Song{" +
      "songId='" + songId + '\'' +
      ", title='" + title + '\'' +
      ", album=" + (album != null ? album.getTitle() : "N/A") +
      ", artists=" + (artists != null ? artists.stream().map(Artist::getName).collect(Collectors.joining(", ")) : "N/A") +
      ", genres=" + (genres != null ? genres.stream().map(Genre::getName).collect(Collectors.joining(", ")) : "N/A") +
      '}';
  }
}

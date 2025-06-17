package main.java.com.musicplayer.ui.model;

import com.microservices.dao.ArtistDAO;
import com.microservices.dao.GenreDAO;
import com.microservices.dao.SongDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;

public class SongTableModel extends AbstractTableModel {
  private final String[] columnNames = { "ID", "Titulo", "Duracion (seg.)", "Album", "Artitsas", "Generos", "¿Explicita?" };
  private List<SongDAO> songs;
  
  public SongTableModel() {
    this.songs = new ArrayList<>();
  }
  
  public void setSongs(List<SongDAO> songs) {
    this.songs = songs;
    fireTableDataChanged();
  }
  
  public SongDAO getSongAt(int rowIndex) {
    return songs.get(rowIndex);
  }
  
  @Override
  public int getRowCount() {
    return songs.size();
  }
  
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }
  
  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }
  
  @Override
  public Object getValueAt(int rowindex, int columnIndex) {
    SongDAO song = songs.get(rowindex);
    return switch(columnIndex) {
      case 0 -> song.getSongId();
      case 1 -> song.getTitle();
      case 2 -> song.getDurationSeconds();
      case 3 -> song.getAlbum() != null ? song.getAlbum().getTitle() : "N/A";
      case 4 -> song.getArtists() != null 
        ? song.getArtists().stream().map(ArtistDAO::getName).collect(Collectors.joining(", ")) : "N/A";
      case 5 -> song.getGenres() != null 
        ? song.getGenres().stream().map(GenreDAO::getName).collect(Collectors.joining(", ")) : "N/A";
      case 6 -> song.isExplicit() ? "Si" : "No";
      default -> null;
    };
  }
  
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if(columnIndex == 2) return Integer.class;
    if(columnIndex == 6) return String.class;
    return String.class;
  }
  
  @Override
  public boolean isCellEditable(int index, int columnIndex) {
    return false;
  }
}

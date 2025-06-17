package main.java.com.musicplayer.ui.model;

import com.microservices.dao.AlbumDAO;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class AlbumTableModel extends AbstractTableModel {
  private final String[] columnNames = { "ID", "Título", "Fecha de Lanzamiento", "Artista Principal", "Número de Canciones", "Sello Discográfico", "URL Portada" };
  private List<AlbumDAO> albums;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  
  public AlbumTableModel() {
    this.albums = new ArrayList<>();
  }
  
  public void setAlbums(List<AlbumDAO> albums) {
    this.albums = albums;
    fireTableDataChanged();
  }
  
  public AlbumDAO getAlbumAt(int rowIndex) {
    return albums.get(rowIndex);
  }
  
  @Override
  public int getRowCount() {
    return albums.size();
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
    AlbumDAO album = albums.get(rowindex);
    return switch(columnIndex) {
      case 0 -> album.getAlbumId();
      case 1 -> album.getTitle();
      case 2 -> album.getReleaseDate() != null 
        ? album.getReleaseDate().format(DATE_FORMATTER) : "N/A";
      case 3 -> album.getPrimaryArtist()!= null 
        ? album.getPrimaryArtist().getName(): "N/A";
      case 4 -> album.getTotalTracks();
      case 5 -> album.getRecordLabel();
      case 6 -> album.getCoverArtUrl();
      default -> null;
    };
  }
  
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return switch(columnIndex) {
      case 2 -> String.class;
      case 4 -> Integer.class;
      default -> String.class;
    };
  }
  
  @Override
  public boolean isCellEditable(int index, int columnIndex) {
    return false;
  }
}

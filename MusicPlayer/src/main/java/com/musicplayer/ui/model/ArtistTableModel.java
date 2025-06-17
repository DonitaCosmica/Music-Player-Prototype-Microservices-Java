package main.java.com.musicplayer.ui.model;

import com.microservices.dao.ArtistDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ArtistTableModel extends AbstractTableModel {
  private final String[] columnNames = { "ID", "Nombre", "Biografia", "URL Imagen", "Pais de Origen" };
  private List<ArtistDAO> artists;
  
  public ArtistTableModel() {
    this.artists = new ArrayList<>();
  }
  
  public void setArtists(List<ArtistDAO> artists) {
    this.artists = artists;
    fireTableDataChanged();
  }
  
  public ArtistDAO getArtistAt(int rowIndex) {
    return artists.get(rowIndex);
  }
  
  @Override
  public int getRowCount() {
    return artists.size();
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
    ArtistDAO artist = artists.get(rowindex);
    return switch(columnIndex) {
      case 0 -> artist.getArtistId();
      case 1 -> artist.getName();
      case 2 -> artist.getBio();
      case 3 -> artist.getImageUrl();
      case 4 -> artist.getOriginCountry()!= null 
        ? artist.getOriginCountry().getName(): "N/A";
      default -> null;
    };
  }
  
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }
  
  @Override
  public boolean isCellEditable(int index, int columnIndex) {
    return false;
  }
}

package main.java.com.musicplayer.ui.model;

import com.microservices.dao.GenreDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class GenreTableModel extends AbstractTableModel {
  private final String[] columnNames = { "ID", "Nombre", "Descripcion" };
  private List<GenreDAO> genres;
  
  public GenreTableModel() {
    this.genres = new ArrayList<>();
  }
  
  public void setGenres(List<GenreDAO> genres) {
    this.genres = genres;
    fireTableDataChanged();
  }
  
  public GenreDAO getGenreAt(int rowIndex) {
    return genres.get(rowIndex);
  }
  
  @Override
  public int getRowCount() {
    return genres.size();
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
    GenreDAO genre = genres.get(rowindex);
    return switch(columnIndex) {
      case 0 -> genre.getGenreId();
      case 1 -> genre.getName();
      case 2 -> genre.getDescription();
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

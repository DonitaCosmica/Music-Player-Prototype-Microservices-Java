package main.java.com.musicplayer.ui.model;

import com.microservices.dao.CountryDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class CountryTableModel extends AbstractTableModel {
  private final String[] columnNames = { "ID", "Nombre", "Iso Code 2" };
  private List<CountryDAO> countries;
  
  public CountryTableModel() {
    this.countries = new ArrayList<>();
  }
  
  public void setCountries(List<CountryDAO> countries) {
    this.countries = countries;
    fireTableDataChanged();
  }
  
  public CountryDAO getCountryAt(int rowIndex) {
    return countries.get(rowIndex);
  }
  
  @Override
  public int getRowCount() {
    return countries.size();
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
    CountryDAO country = countries.get(rowindex);
    return switch(columnIndex) {
      case 0 -> country.getCountryId();
      case 1 -> country.getName();
      case 2 -> country.getIsoCode2();
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

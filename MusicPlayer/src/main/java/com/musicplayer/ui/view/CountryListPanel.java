package main.java.com.musicplayer.ui.view;

import com.microservices.dao.CountryDAO;
import com.microservices.response.ApiResponse;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import main.java.com.musicplayer.ui.model.CountryTableModel;
import main.java.com.musicplayer.ui.util.ApiClient;

public final class CountryListPanel extends JPanel {
  private JTable countryTable;
  private CountryTableModel tableModel;
  private final ApiClient apiClient;
  private final MainFrame parentFrame;
  
  public CountryListPanel(MainFrame parentFrame) {
    this.parentFrame = parentFrame;
    this.apiClient = new ApiClient();
    initUI();
    loadCountries();
  }
  
  private void initUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titleLabel = new JLabel("Gestion de Generos");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);
    
    tableModel = new CountryTableModel();
    countryTable = new JTable(tableModel);
    
    countryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    countryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
    countryTable.getColumnModel().getColumn(2).setPreferredWidth(100);
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    countryTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    countryTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    
    JScrollPane scrollPane = new JScrollPane(countryTable);
    add(scrollPane, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    JButton newButton = new JButton("Nuevo Pais");
    JButton editButton = new JButton("Editar Pais");
    JButton deleteButton = new JButton("Eliminar Pais");
    JButton refreshButton = new JButton("Actualizar Lista");
    
    newButton.addActionListener(ev -> handleNewCountry());
    editButton.addActionListener(ev -> handleEditCountry());
    deleteButton.addActionListener(ev -> handleDeleteCountry());
    refreshButton.addActionListener(ev -> loadCountries());
    
    buttonPanel.add(newButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }
  
  public void loadCountries() {
    try {
      ApiResponse<List<CountryDAO>> response = apiClient.getAllCountries();
      if(response.isSuccess() && response.getData() != null)
        tableModel.setCountries(response.getData());
      else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los paises: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de comunicacion", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private void handleNewCountry() {
    CountryFormDialog dialog = new CountryFormDialog(parentFrame, null);
    dialog.setVisible(true);
    if(dialog.isSaved()) loadCountries();
  }
  
  private void handleEditCountry() {
    int selectedRow = countryTable.getSelectedRow();
    if(selectedRow != -1) {
      CountryDAO selectedCountry = tableModel.getCountryAt(selectedRow);
      CountryFormDialog dialog = new CountryFormDialog(parentFrame, selectedCountry);
      dialog.setVisible(true);
      if(dialog.isSaved()) loadCountries();
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un pais para editar", "Ningun Pais Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleDeleteCountry() {
    int selectedRow = countryTable.getSelectedRow();
    if(selectedRow != -1) {
      CountryDAO selectedCountry = tableModel.getCountryAt(selectedRow);
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Estas seguro de que deseas eliminar el pais '" + selectedCountry.getName()+ "'?",
        "Confirmar Eliminacion",
        JOptionPane.YES_NO_OPTION
      );
      
      if(confirm == JOptionPane.YES_OPTION) {
        try {
          ApiResponse<Void> response = apiClient.deleteCountry(selectedCountry.getCountryId());
          if(response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Pais eliminado exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            loadCountries();
          } else
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el pais: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicacion", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un pais para eliminar.", "Ninguna artista Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
}

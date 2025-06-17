package main.java.com.musicplayer.ui.view;

import com.microservices.dao.GenreDAO;
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
import main.java.com.musicplayer.ui.model.GenreTableModel;
import main.java.com.musicplayer.ui.util.ApiClient;

public final class GenreListPanel extends JPanel {
  private JTable genreTable;
  private GenreTableModel tableModel;
  private final ApiClient apiClient;
  private final MainFrame parentFrame;
  
  public GenreListPanel(MainFrame parentFrame) {
    this.parentFrame = parentFrame;
    this.apiClient = new ApiClient();
    initUI();
    loadGenres();
  }
  
  private void initUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titleLabel = new JLabel("Gestion de Generos");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);
    
    tableModel = new GenreTableModel();
    genreTable = new JTable(tableModel);
    
    genreTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    genreTable.getColumnModel().getColumn(1).setPreferredWidth(150);
    genreTable.getColumnModel().getColumn(2).setPreferredWidth(250);
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    genreTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    genreTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    
    JScrollPane scrollPane = new JScrollPane(genreTable);
    add(scrollPane, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    JButton newButton = new JButton("Nuevo Genero");
    JButton editButton = new JButton("Editar Genero");
    JButton deleteButton = new JButton("Eliminar Genero");
    JButton refreshButton = new JButton("Actualizar Lista");
    
    newButton.addActionListener(ev -> handleNewGenre());
    editButton.addActionListener(ev -> handleEditGenre());
    deleteButton.addActionListener(ev -> handleDeleteGenre());
    refreshButton.addActionListener(ev -> loadGenres());
    
    buttonPanel.add(newButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }
  
  public void loadGenres() {
    try {
      ApiResponse<List<GenreDAO>> response = apiClient.getAllGenres();
      if(response.isSuccess() && response.getData() != null)
        tableModel.setGenres(response.getData());
      else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los generos: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de comunicacion", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private void handleNewGenre() {
    GenreFormDialog dialog = new GenreFormDialog(parentFrame, null);
    dialog.setVisible(true);
    if(dialog.isSaved()) loadGenres();
  }
  
  private void handleEditGenre() {
    int selectedRow = genreTable.getSelectedRow();
    if(selectedRow != -1) {
      GenreDAO selectedGenre = tableModel.getGenreAt(selectedRow);
      GenreFormDialog dialog = new GenreFormDialog(parentFrame, selectedGenre);
      dialog.setVisible(true);
      if(dialog.isSaved()) loadGenres();
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un genero para editar", "Ningun Genero Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleDeleteGenre() {
    int selectedRow = genreTable.getSelectedRow();
    if(selectedRow != -1) {
      GenreDAO selectedGenre = tableModel.getGenreAt(selectedRow);
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Estas seguro de que deseas eliminar el genero '" + selectedGenre.getName()+ "'?",
        "Confirmar Eliminacion",
        JOptionPane.YES_NO_OPTION
      );
      
      if(confirm == JOptionPane.YES_OPTION) {
        try {
          ApiResponse<Void> response = apiClient.deleteGenre(selectedGenre.getGenreId());
          if(response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Pais eliminado exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            loadGenres();
          } else
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el genero: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicacion", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un genero para eliminar.", "Ninguna artista Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
}

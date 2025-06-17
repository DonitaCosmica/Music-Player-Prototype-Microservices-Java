package main.java.com.musicplayer.ui.view;

import com.microservices.dao.ArtistDAO;
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
import main.java.com.musicplayer.ui.model.ArtistTableModel;
import main.java.com.musicplayer.ui.util.ApiClient;

public final class ArtistListPanel extends JPanel {
  private JTable artistTable;
  private ArtistTableModel tableModel;
  private final ApiClient apiClient = new ApiClient();
  private final MainFrame parentFrame;
  
  public ArtistListPanel(MainFrame parentFrame) {
    this.parentFrame = parentFrame;
    initUI();
    loadArtists();
  }
  
  private void initUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titleLabel = new JLabel("Gestion de Artistas");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);
    
    tableModel = new ArtistTableModel();
    artistTable = new JTable(tableModel);
    
    artistTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    artistTable.getColumnModel().getColumn(1).setPreferredWidth(150);
    artistTable.getColumnModel().getColumn(2).setPreferredWidth(250);
    artistTable.getColumnModel().getColumn(3).setPreferredWidth(150);
    artistTable.getColumnModel().getColumn(4).setPreferredWidth(100);
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    
    JScrollPane scrollPane = new JScrollPane(artistTable);
    add(scrollPane, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    JButton newButton = new JButton("Nuevo Artista");
    JButton editButton = new JButton("Editar Artista");
    JButton deleteButton = new JButton("Eliminar Artista");
    JButton refreshButton = new JButton("Actualizar Lista");
    
    newButton.addActionListener(ev -> handleNewArtist());
    editButton.addActionListener(ev -> handleEditArtist());
    deleteButton.addActionListener(ev -> handleDeleteArtist());
    refreshButton.addActionListener(ev -> loadArtists());
    
    buttonPanel.add(newButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }
  
  public void loadArtists() {
    try {
      ApiResponse<List<ArtistDAO>> response = apiClient.getAllArtists();
      if(response.isSuccess() && response.getData() != null)
        tableModel.setArtists(response.getData());
      else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los artitas: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de comunicacion", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private void handleNewArtist() {
    ArtistFormDialog dialog = new ArtistFormDialog(parentFrame, null);
    dialog.setVisible(true);
    if(dialog.isSaved()) loadArtists();
  }
  
  private void handleEditArtist() {
    int selectedRow = artistTable.getSelectedRow();
    if(selectedRow != -1) {
      ArtistDAO selectedArtist = tableModel.getArtistAt(selectedRow);
      ArtistFormDialog dialog = new ArtistFormDialog(parentFrame, selectedArtist);
      dialog.setVisible(true);
      if(dialog.isSaved()) loadArtists();
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un artista para editar", "Ningun Artista Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleDeleteArtist() {
    int selectedRow = artistTable.getSelectedRow();
    if(selectedRow != -1) {
      ArtistDAO selectedArtist = tableModel.getArtistAt(selectedRow);
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Estas seguro de que deseas eliminar el artista '" + selectedArtist.getName()+ "'?",
        "Confirmar Eliminacion",
        JOptionPane.YES_NO_OPTION
      );
      
      if(confirm == JOptionPane.YES_OPTION) {
        try {
          ApiResponse<Void> response = apiClient.deleteArtist(selectedArtist.getArtistId());
          if(response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Artista eliminado exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            loadArtists();
          } else
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el artista: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicacion", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un artista para eliminar.", "Ninguna artista Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
}

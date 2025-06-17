package main.java.com.musicplayer.ui.view;

import com.microservices.dao.AlbumDAO;
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
import main.java.com.musicplayer.ui.model.AlbumTableModel;
import main.java.com.musicplayer.ui.util.ApiClient;

public final class AlbumListPanel extends JPanel {
  private JTable albumTable;
  private AlbumTableModel tableModel;
  private final ApiClient apiClient = new ApiClient();
  private final MainFrame parentFrame;
  
  public AlbumListPanel(MainFrame parentFrame) {
    this.parentFrame = parentFrame;
    initUI();
    loadAlbums();
  }
  
  private void initUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titleLabel = new JLabel("Gestion de Albumes");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);
    
    tableModel = new AlbumTableModel();
    albumTable = new JTable(tableModel);
    
    albumTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    albumTable.getColumnModel().getColumn(1).setPreferredWidth(200);
    albumTable.getColumnModel().getColumn(2).setPreferredWidth(120);
    albumTable.getColumnModel().getColumn(3).setPreferredWidth(150);
    albumTable.getColumnModel().getColumn(4).setPreferredWidth(80);
    albumTable.getColumnModel().getColumn(5).setPreferredWidth(120);
    albumTable.getColumnModel().getColumn(6).setPreferredWidth(100);
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    albumTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    
    JScrollPane scrollPane = new JScrollPane(albumTable);
    add(scrollPane, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    JButton newButton = new JButton("Nuevo Album");
    JButton editButton = new JButton("Editar Album");
    JButton deleteButton = new JButton("Eliminar Album");
    JButton refreshButton = new JButton("Actualizar lista");
    
    newButton.addActionListener(ev -> handleNewAlbum());
    editButton.addActionListener(ev -> handleEditAlbum());
    deleteButton.addActionListener(ev -> handleDeleteAlbum());
    refreshButton.addActionListener(ev -> loadAlbums());
    
    buttonPanel.add(newButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }
  
  public void loadAlbums() {
    try {
      ApiResponse<List<AlbumDAO>> response = apiClient.getAllAlbums();
      if(response.isSuccess() && response.getData() != null)
        tableModel.setAlbums(response.getData());
      else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los albumes: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de comunicacion", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private void handleNewAlbum() {
    AlbumFormDialog dialog = new AlbumFormDialog(parentFrame, null);
    dialog.setVisible(true);
    if(dialog.isSaved()) loadAlbums();
  }
  
  private void handleEditAlbum() {
    int selectedRow = albumTable.getSelectedRow();
    if(selectedRow != -1) {
      AlbumDAO selectedAlbum = tableModel.getAlbumAt(selectedRow);
      AlbumFormDialog dialog = new AlbumFormDialog(parentFrame, selectedAlbum);
      dialog.setVisible(true);
      if(dialog.isSaved()) loadAlbums();
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un album para editar", "Ningun Album Seleccionada", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleDeleteAlbum() {
    int selectedRow = albumTable.getSelectedRow();
    if(selectedRow != -1) {
      AlbumDAO selectedAlbum = tableModel.getAlbumAt(selectedRow);
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Estas seguro de que deseas eliminar el album '" + selectedAlbum.getTitle() + "'?",
        "Confirmar Eliminacion",
        JOptionPane.YES_NO_OPTION
      );
      
      if(confirm == JOptionPane.YES_OPTION) {
        try {
          ApiResponse<Void> response = apiClient.deleteAlbum(selectedAlbum.getAlbumId());
          if(response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Album eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadAlbums();
          } else
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el album: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona un album para eliminar.", "Ninguna Canción Seleccionada", JOptionPane.WARNING_MESSAGE);
  }
}

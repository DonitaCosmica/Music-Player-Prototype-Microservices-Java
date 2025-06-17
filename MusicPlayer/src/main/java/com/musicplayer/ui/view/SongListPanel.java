package main.java.com.musicplayer.ui.view;

import com.microservices.dao.SongDAO;
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
import main.java.com.musicplayer.ui.model.SongTableModel;
import main.java.com.musicplayer.ui.util.ApiClient;

public final class SongListPanel extends JPanel {
  private JTable songTable;
  private SongTableModel tableModel;
  private final ApiClient apiClient = new ApiClient();
  private final MainFrame parentFrame;
  
  public SongListPanel(MainFrame parentFrame) {
    this.parentFrame = parentFrame;
    initUI();
    loadSongs();
  }
  
  private void initUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titleLabel = new JLabel("Gestion de Canciones");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(titleLabel, BorderLayout.NORTH);
    
    tableModel = new SongTableModel();
    songTable = new JTable(tableModel);
    
    songTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    songTable.getColumnModel().getColumn(1).setPreferredWidth(200);
    songTable.getColumnModel().getColumn(2).setPreferredWidth(80);
    songTable.getColumnModel().getColumn(3).setPreferredWidth(150);
    songTable.getColumnModel().getColumn(4).setPreferredWidth(180);
    songTable.getColumnModel().getColumn(5).setPreferredWidth(120);
    songTable.getColumnModel().getColumn(6).setPreferredWidth(60);
    
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    songTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
    
    JScrollPane scrollPane = new JScrollPane(songTable);
    add(scrollPane, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    JButton newButton = new JButton("Nueva Cancion");
    JButton editButton = new JButton("Editar Cancion");
    JButton deleteButton = new JButton("Eliminar Cancion");
    JButton refreshButton = new JButton("Actualizar lista");
    
    newButton.addActionListener(ev -> handleNewSong());
    editButton.addActionListener(ev -> handleEditSong());
    deleteButton.addActionListener(ev -> handleDeleteSong());
    refreshButton.addActionListener(ev -> loadSongs());
    
    buttonPanel.add(newButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }
  
  public void loadSongs() {
    try {
      ApiResponse<List<SongDAO>> response = apiClient.getAllSongs();
      if(response.isSuccess() && response.getData() != null)
        tableModel.setSongs(response.getData());
      else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar las canciones: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de comunicacion", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private void handleNewSong() {
    SongFormDialog dialog = new SongFormDialog(parentFrame, null);
    dialog.setVisible(true);
    if(dialog.isSaved()) loadSongs();
  }
  
  private void handleEditSong() {
    int selectedRow = songTable.getSelectedRow();
    if(selectedRow != -1) {
      SongDAO selectedSong = tableModel.getSongAt(selectedRow);
      SongFormDialog dialog = new SongFormDialog(parentFrame, selectedSong);
      dialog.setVisible(true);
      if(dialog.isSaved()) loadSongs();
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona una cancion para editar", "Ninguna Cancion Seleccionada", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleDeleteSong() {
    int selectedRow = songTable.getSelectedRow();
    if(selectedRow != -1) {
      SongDAO selectedSong = tableModel.getSongAt(selectedRow);
      int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Estas seguro de que deseas eliminar la cancion '" + selectedSong.getTitle() + "'?",
        "Confirmar Eliminacion",
        JOptionPane.YES_NO_OPTION
      );
      
      if(confirm == JOptionPane.YES_OPTION) {
        try {
          ApiResponse<Void> response = apiClient.deleteSong(selectedSong.getSongId());
          if(response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Canción eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadSongs();
          } else
            JOptionPane.showMessageDialog(this, "No se pudo eliminar la canción: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    } else
      JOptionPane.showMessageDialog(this, "Por favor, selecciona una canción para eliminar.", "Ninguna Canción Seleccionada", JOptionPane.WARNING_MESSAGE);
  }
}

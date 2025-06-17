package main.java.com.musicplayer.ui.view;

import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.response.ApiResponse;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.java.com.musicplayer.ui.util.ApiClient;

public class AlbumFormDialog extends JDialog {
  private JLabel formTitleLabel;
  private JTextField titleField;
  private JDateChooser releaseDateChooser;
  private JTextField recordLabelField;
  private JTextField coverImageUrlField;
  private JComboBox<ArtistDAO> primaryArtistComboBox;
  private final AlbumDAO albumToEdit;
  private final ApiClient apiClient;
  private boolean saved = false;
  
  public AlbumFormDialog(Frame owner, AlbumDAO albumToEdit) {
    super(owner, true);
    this.albumToEdit = albumToEdit;
    this.apiClient = new ApiClient();
    
    setTitle(albumToEdit == null ? "Nuevo Album" : "Editar Album");
    setSize(500, 450);
    setLocationRelativeTo(owner);
    initUI();
    loadComboBoxData();
    populateForm();
  }
  
  private void initUI() {
    setLayout(new BorderLayout(10, 10));
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    add(panel, BorderLayout.CENTER);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    formTitleLabel = new JLabel(albumToEdit == null ? "Crear Nuevo Album" : "Editar Album");
    formTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(formTitleLabel, gbc);

    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridy = 1;
    panel.add(new JLabel("Titulo:"), gbc);
    gbc.gridx = 1;
    titleField = new JTextField(30);
    panel.add(titleField, gbc);

    gbc.gridy = 2;
    gbc.gridx = 0;
    panel.add(new JLabel("Fecha Lanzamiento:"), gbc);
    gbc.gridx = 1;
    releaseDateChooser = new JDateChooser();
    releaseDateChooser.setDateFormatString("yyyy-MM-dd");
    panel.add(releaseDateChooser, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    panel.add(new JLabel("Sello Discografico:"), gbc);
    gbc.gridx = 1;
    recordLabelField = new JTextField(25);
    panel.add(recordLabelField, gbc);

    gbc.gridy = 4;
    gbc.gridx = 0;
    panel.add(new JLabel("URL Portada:"), gbc);
    gbc.gridx = 1;
    coverImageUrlField = new JTextField(25);
    panel.add(coverImageUrlField, gbc);

    gbc.gridy = 5;
    gbc.gridx = 0;
    panel.add(new JLabel("Artista Principal:"), gbc);
    gbc.gridx = 1;
    primaryArtistComboBox = new JComboBox<>();
    primaryArtistComboBox.setPreferredSize(new Dimension(200, primaryArtistComboBox.getPreferredSize().height));
    panel.add(primaryArtistComboBox, gbc);
    
    primaryArtistComboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean  cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof ArtistDAO artistDAO) setText(artistDAO.getName());
        else if(value == null) setText("Seleccione un artista...");
        return this;
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    JButton saveButton = new JButton("Guardar");
    JButton cancelButton = new JButton("Cancelar");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);

    saveButton.addActionListener(e -> handleSaveAlbum());
    cancelButton.addActionListener(e -> setVisible(false));
  }
  
  private void loadComboBoxData() {
    try {
      ApiResponse<List<ArtistDAO>> response = apiClient.getAllArtists();
      if(response.isSuccess() && response.getData() != null) {
        Vector<ArtistDAO> artists = new Vector();
        artists.add(null);
        artists.addAll(response.getData());
        primaryArtistComboBox.setModel(new DefaultComboBoxModel<>(artists));
      } else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los artistas: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void populateForm() {
    if(albumToEdit != null) {
      formTitleLabel.setText("Editar Album");
      titleField.setText(albumToEdit.getTitle());
      if(albumToEdit.getReleaseDate() != null)
        releaseDateChooser.setDate(Date.from(albumToEdit.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
      else
        releaseDateChooser.setDate(null);
      
      recordLabelField.setText(albumToEdit.getRecordLabel());
      coverImageUrlField.setText(albumToEdit.getCoverArtUrl());
      
      if(albumToEdit.getPrimaryArtist()!= null) {
        for(int i = 0; i < primaryArtistComboBox.getItemCount(); ++i) {
          ArtistDAO item = primaryArtistComboBox.getItemAt(i);
          if(item != null && item.getArtistId().equals(albumToEdit.getPrimaryArtist().getArtistId())) {
            primaryArtistComboBox.setSelectedIndex(i);
            break;
          }
        }
      }
    } else {
      formTitleLabel.setText("Nuevo Album");
      primaryArtistComboBox.setSelectedIndex(0);
      releaseDateChooser.setDate(null);
    }
  }
  
  private void handleSaveAlbum() {
    if(!validateInput()) return;
    
    AlbumDAO album = albumToEdit != null ? albumToEdit : new AlbumDAO();
    album.setTitle(titleField.getText().trim());
    Date selectedDate = releaseDateChooser.getDate();
    if (selectedDate != null)
      album.setReleaseDate(selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    else
      album.setReleaseDate(null);
        
    album.setRecordLabel(recordLabelField.getText().trim());
    album.setCoverArtUrl(coverImageUrlField.getText().trim());
    album.setPrimaryArtist((ArtistDAO) primaryArtistComboBox.getSelectedItem());
    album.setTotalTracks(albumToEdit != null ? album.getTotalTracks() : 0);

    try {
      ApiResponse<Void> response;
      if (albumToEdit == null)response = apiClient.createAlbum(album);
      else response = apiClient.updateAlbum(album);

      if (response.isSuccess()) {
        JOptionPane.showMessageDialog(this, response.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        saved = true;
        dispose();
      } else
        JOptionPane.showMessageDialog(this, response.getMessage(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private boolean validateInput() {
    StringBuilder errorMessage = new StringBuilder();
    if(titleField.getText() == null || titleField.getText().trim().isEmpty())
      errorMessage.append("El titulo no puede estar vacio.\n");
    if(releaseDateChooser.getDate() == null)
      errorMessage.append("La fecha de lanzamiento no puede estar vacia.\n");
    if(recordLabelField.getText() == null || recordLabelField.getText().trim().isEmpty())
      errorMessage.append("El sello discografico no puede estar vacio.\n");
    if(coverImageUrlField.getText() == null || coverImageUrlField.getText().trim().isEmpty())
      errorMessage.append("La URL de la portada no puede estar vacia\n");
    if(primaryArtistComboBox.getSelectedItem() == null)
      errorMessage.append("Debe seleccionar un artista principal\n");

    if (errorMessage.length() == 0) return true;
    else {
      JOptionPane.showMessageDialog(this, errorMessage.toString(), "Errores de Validacion", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }
  
  public boolean isSaved() {
    return saved;
  }
}

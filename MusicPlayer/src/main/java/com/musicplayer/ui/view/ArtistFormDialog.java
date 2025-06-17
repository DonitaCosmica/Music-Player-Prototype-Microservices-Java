package main.java.com.musicplayer.ui.view;

import com.microservices.dao.ArtistDAO;
import com.microservices.dao.CountryDAO;
import com.microservices.response.ApiResponse;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import main.java.com.musicplayer.ui.util.ApiClient;

public class ArtistFormDialog extends JDialog {
  private JLabel formTitleLabel;
  private JTextField nameField;
  private JTextArea bioField;
  private JTextField imageURLField;
  private JComboBox<CountryDAO> originCountryComboBox;
  private final ArtistDAO artistToEdit;
  private final ApiClient apiClient;
  private boolean saved = false;
  
  public ArtistFormDialog(Frame owner, ArtistDAO artistToEdit) {
    super(owner, true);
    this.artistToEdit = artistToEdit;
    this.apiClient = new ApiClient();
    
    setTitle(artistToEdit == null ? "Nuevo Artista" : "Editar Artista");
    setSize(500, 480);
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

    formTitleLabel = new JLabel(artistToEdit == null ? "Crear Nuevo Artista" : "Editar Artista");
    formTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(formTitleLabel, gbc);

    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridy = 1;
    gbc.gridx = 0;
    panel.add(new JLabel("Nombre:"), gbc);
    gbc.gridx = 1;
    nameField = new JTextField(30);
    panel.add(nameField, gbc);

    gbc.gridy = 2;
    gbc.gridx = 0;
    panel.add(new JLabel("Biografia:"), gbc);
    gbc.gridx = 1;
    bioField = new JTextArea(5, 30);
    bioField.setLineWrap(true);
    bioField.setWrapStyleWord(true);
    JScrollPane bioScrollPane = new JScrollPane(bioField);
    bioScrollPane.setPreferredSize(new Dimension(300, 100));
    panel.add(bioScrollPane, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    panel.add(new JLabel("URL Imagen:"), gbc);
    gbc.gridx = 1;
    imageURLField = new JTextField(25);
    panel.add(imageURLField, gbc);

    gbc.gridy = 4;
    gbc.gridx = 0;
    panel.add(new JLabel("URL Portada:"), gbc);
    gbc.gridx = 1;
    originCountryComboBox = new JComboBox<>();
    originCountryComboBox.setPreferredSize(new Dimension(200, originCountryComboBox.getPreferredSize().height));
    panel.add(originCountryComboBox, gbc);
    
    originCountryComboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean  cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof CountryDAO countryDAO) setText(countryDAO.getName());
        else if(value == null) setText("Seleccione un pais...");
        return this;
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    JButton saveButton = new JButton("Guardar");
    JButton cancelButton = new JButton("Cancelar");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);

    saveButton.addActionListener(ev -> handleSaveArtist());
    cancelButton.addActionListener(ev -> dispose());
  }
  
  private void loadComboBoxData() {
    try {
      ApiResponse<List<CountryDAO>> response = apiClient.getAllCountries();
      if(response.isSuccess() && response.getData() != null) {
        Vector<CountryDAO> countries = new Vector<>();
        countries.add(null);
        countries.addAll(response.getData());
        originCountryComboBox.setModel(new DefaultComboBoxModel<>(countries));
      } else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los paises: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicacion", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void populateForm() {
    if(artistToEdit != null) {
      formTitleLabel.setText("Editar Artista");
      nameField.setText(artistToEdit.getName());      
      bioField.setText(artistToEdit.getBio());
      imageURLField.setText(artistToEdit.getImageUrl());
      
      if(artistToEdit.getOriginCountry()!= null) {
        for(int i = 0; i < originCountryComboBox.getItemCount(); ++i) {
          CountryDAO item = originCountryComboBox.getItemAt(i);
          if(item != null && item.getCountryId().equals(artistToEdit.getOriginCountry().getCountryId())) {
            originCountryComboBox.setSelectedIndex(i);
            break;
          }
        }
      }
    } else {
      formTitleLabel.setText("Nuevo Artista");
      originCountryComboBox.setSelectedIndex(0);
    }
  }
  
  private void handleSaveArtist() {
    if(!validateInput()) return;
    
    ArtistDAO artist = artistToEdit != null ? artistToEdit : new ArtistDAO();
    artist.setName(nameField.getText().trim());        
    artist.setBio(bioField.getText().trim());
    artist.setImageUrl(imageURLField.getText().trim());
    artist.setOriginCountry((CountryDAO) originCountryComboBox.getSelectedItem());

    try {
      ApiResponse<Void> response;
      if (artistToEdit == null) response = apiClient.createArtist(artist);
      else response = apiClient.updateArtist(artist);

      if (response.isSuccess()) {
        JOptionPane.showMessageDialog(this, response.getMessage(), "Exito", JOptionPane.INFORMATION_MESSAGE);
        saved = true;
        dispose();
      } else
        JOptionPane.showMessageDialog(this, response.getMessage(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicacion", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
  
  private boolean validateInput() {
    StringBuilder errorMessage = new StringBuilder();
    if(nameField.getText() == null || nameField.getText().trim().isEmpty())
      errorMessage.append("El nombre no puede estar vacio.\n");
    if(bioField.getText() == null || bioField.getText().trim().isEmpty())
      errorMessage.append("La biografia no puede estar vacia.\n");
    if(imageURLField.getText() == null || imageURLField.getText().trim().isEmpty())
      errorMessage.append("La URL de imagen no puede estar vacio.\n");
    if(originCountryComboBox.getSelectedItem() == null)
      errorMessage.append("Debe seleccionar un pais de origen\n");

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

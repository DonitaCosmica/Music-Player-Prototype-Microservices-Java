package main.java.com.musicplayer.ui.view;

import com.microservices.dao.GenreDAO;
import com.microservices.response.ApiResponse;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import main.java.com.musicplayer.ui.util.ApiClient;

public class GenreFormDialog extends JDialog {
  private JLabel formTitleLabel;
  private JTextField nameField;
  private JTextArea descriprionField;
  private final GenreDAO genreToEdit;
  private final ApiClient apiClient;
  private boolean saved = false;
  
  public GenreFormDialog(Frame owner, GenreDAO genreToEdit) {
    super(owner, true);
    this.genreToEdit = genreToEdit;
    this.apiClient = new ApiClient();
    
    setTitle(genreToEdit == null ? "Nuevo Genero" : "Editar Genero");
    setSize(500, 380);
    setLocationRelativeTo(owner);
    initUI();
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

    formTitleLabel = new JLabel(genreToEdit == null ? "Crear Nuevo Genero" : "Editar Genero");
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
    panel.add(new JLabel("Descripcion:"), gbc);
    gbc.gridx = 1;
    descriprionField = new JTextArea(5, 30);
    descriprionField.setLineWrap(true);
    descriprionField.setWrapStyleWord(true);
    JScrollPane descriptionScrollPane = new JScrollPane(descriprionField);
    descriptionScrollPane.setPreferredSize(new Dimension(300, 100));
    panel.add(descriptionScrollPane, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    JButton saveButton = new JButton("Guardar");
    JButton cancelButton = new JButton("Cancelar");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);

    saveButton.addActionListener(ev -> handleSaveGenre());
    cancelButton.addActionListener(ev -> dispose());
  }
  
  private void populateForm() {
    if(genreToEdit != null) {
      formTitleLabel.setText("Editar Genero");
      nameField.setText(genreToEdit.getName());      
      descriprionField.setText(genreToEdit.getDescription());
    } else
      formTitleLabel.setText("Nuevo Genero");
  }
  
  private void handleSaveGenre() {
    if(!validateInput()) return;
    
    GenreDAO genre = genreToEdit != null ? genreToEdit : new GenreDAO();
    genre.setName(nameField.getText().trim());        
    genre.setDescription(descriprionField.getText().trim());

    try {
      ApiResponse<Void> response;
      if (genreToEdit == null) response = apiClient.createGenre(genre);
      else response = apiClient.updateGenre(genre);

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
    if(descriprionField.getText() == null || descriprionField.getText().trim().isEmpty())
      errorMessage.append("La descripcion no puede estar vacia.\n");

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

package main.java.com.musicplayer.ui.view;

import com.microservices.dao.CountryDAO;
import com.microservices.response.ApiResponse;
import java.awt.BorderLayout;
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
import javax.swing.JTextField;
import main.java.com.musicplayer.ui.util.ApiClient;

public class CountryFormDialog extends JDialog {
  private JLabel formTitleLabel;
  private JTextField nameField;
  private JTextField isoCode2Field;
  private final CountryDAO countryToEdit;
  private final ApiClient apiClient;
  private boolean saved = false;
  
  public CountryFormDialog(Frame owner, CountryDAO countryToEdit) {
    super(owner, true);
    this.countryToEdit = countryToEdit;
    this.apiClient = new ApiClient();
    
    setTitle(countryToEdit == null ? "Nuevo Pais" : "Editar Pais");
    setSize(500, 300);
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

    formTitleLabel = new JLabel(countryToEdit == null ? "Crear Nuevo Pais" : "Editar Pais");
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
    panel.add(new JLabel("Codigo Iso 2:"), gbc);
    gbc.gridx = 1;
    isoCode2Field = new JTextField(20);
    panel.add(isoCode2Field, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    JButton saveButton = new JButton("Guardar");
    JButton cancelButton = new JButton("Cancelar");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);

    saveButton.addActionListener(ev -> handleSaveCountry());
    cancelButton.addActionListener(ev -> dispose());
  }
  
  private void populateForm() {
    if(countryToEdit != null) {
      formTitleLabel.setText("Editar Pais");
      nameField.setText(countryToEdit.getName());      
      isoCode2Field.setText(countryToEdit.getIsoCode2());
    } else
      formTitleLabel.setText("Nuevo Pais");
  }
  
  private void handleSaveCountry() {
    if(!validateInput()) return;
    
    CountryDAO country = countryToEdit != null ? countryToEdit : new CountryDAO();
    country.setName(nameField.getText().trim());        
    country.setIsoCode2(isoCode2Field.getText().trim());

    try {
      ApiResponse<Void> response;
      if (countryToEdit == null) response = apiClient.createCountry(country);
      else response = apiClient.updateCountry(country);

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
    if(isoCode2Field.getText() == null || isoCode2Field.getText().trim().isEmpty())
      errorMessage.append("El codigo iso 2 no puede estar vacia.\n");

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

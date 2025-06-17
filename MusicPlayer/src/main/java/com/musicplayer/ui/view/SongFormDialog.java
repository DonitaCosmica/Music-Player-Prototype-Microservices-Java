package main.java.com.musicplayer.ui.view;

import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.dao.GenreDAO;
import com.microservices.dao.SongDAO;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import main.java.com.musicplayer.ui.util.ApiClient;

public class SongFormDialog extends JDialog {
  private JLabel formTitleLabel;
  private JTextField titleField, audioFilePathField;
  private JSpinner durationSpinner;
  private JCheckBox explicitCheckBox;
  private JComboBox<AlbumDAO> albumComboBox;
  private JList<ArtistDAO> artistsJList;
  private JList<GenreDAO> genresJList;
  private DefaultListModel<ArtistDAO> selectedArtistsModel;
  private DefaultListModel<GenreDAO> selectedGenresModel;
  private final SongDAO songToEdit;
  private final ApiClient apiClient;
  private boolean saved = false;
  private Player mp3Player;
  private boolean isPlaying = false;
  
  public SongFormDialog(Frame owner, SongDAO songToEdit) {
    super(owner, true);
    this.songToEdit = songToEdit;
    this.apiClient = new ApiClient();
    
    setTitle(songToEdit == null ? "Nueva Cancion" : "Editar Cancion");
    setSize(600, 650);
    setLocationRelativeTo(owner);
    initUI();
    loadFormData();
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

    formTitleLabel = new JLabel(songToEdit == null ? "Crear Nueva Cancion" : "Editar Cancion");
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
    panel.add(new JLabel("Titulo:"), gbc);
    gbc.gridx = 1;
    titleField = new JTextField(30);
    panel.add(titleField, gbc);

    gbc.gridy = 2;
    gbc.gridx = 0;
    panel.add(new JLabel("Duracion (seg.):"), gbc);
    gbc.gridx = 1;
    durationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setColumns(10);
    panel.add(durationSpinner, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    panel.add(new JLabel("Ruta Audio:"), gbc);
    gbc.gridx = 1;
    JPanel audioPanel = new JPanel(new BorderLayout(5, 0));
    audioFilePathField = new JTextField(25);
    JButton playButton = new JButton("Reproducir");
    audioPanel.add(audioFilePathField, BorderLayout.CENTER);
    audioPanel.add(playButton, BorderLayout.EAST);
    panel.add(audioPanel, gbc);

    gbc.gridy = 4;
    gbc.gridx = 0;
    panel.add(new JLabel("Explicita:"), gbc);
    gbc.gridx = 1;
    explicitCheckBox = new JCheckBox();
    panel.add(explicitCheckBox, gbc);

    gbc.gridy = 5;
    gbc.gridx = 0;
    panel.add(new JLabel("Album:"), gbc);
    gbc.gridx = 1;
    albumComboBox = new JComboBox<>();
    panel.add(albumComboBox, gbc);

    gbc.gridy = 6;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    panel.add(new JLabel("Artistas:"), gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    selectedArtistsModel = new DefaultListModel<>();
    artistsJList = new JList<>(selectedArtistsModel);
    artistsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane artistsScrollPane = new JScrollPane(artistsJList);
    artistsScrollPane.setPreferredSize(new Dimension(300, 80));
    panel.add(artistsScrollPane, gbc);
    
    artistsJList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof ArtistDAO artistDAO) setText(artistDAO.getName());
        return this;
      }
    });

    gbc.gridy = 7;
    gbc.gridx = 1;
    gbc.weighty = 0.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JPanel artistButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    JButton addArtistButton = new JButton("Añadir Artista");
    JButton removeArtistButton = new JButton("Quitar Artista");
    artistButtonsPanel.add(addArtistButton);
    artistButtonsPanel.add(removeArtistButton);
    panel.add(artistButtonsPanel, gbc);

    gbc.gridy = 8;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    panel.add(new JLabel("Generos:"), gbc);
    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    selectedGenresModel = new DefaultListModel<>();
    genresJList = new JList<>(selectedGenresModel);
    genresJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane genresScrollPane = new JScrollPane(genresJList);
    genresScrollPane.setPreferredSize(new Dimension(300, 80));
    panel.add(genresScrollPane, gbc);
    
    genresJList.setCellRenderer(new DefaultListCellRenderer(){
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof GenreDAO genreDAO) setText(genreDAO.getName());
        return this;
      }
    });

    gbc.gridy = 9;
    gbc.gridx = 1;
    gbc.weighty = 0.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JPanel genreButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    JButton addGenreButton = new JButton("Añadir Genero");
    JButton removeGenreButton = new JButton("Quitar Genero");
    genreButtonsPanel.add(addGenreButton);
    genreButtonsPanel.add(removeGenreButton);
    panel.add(genreButtonsPanel, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    JButton saveButton = new JButton("Guardar");
    JButton cancelButton = new JButton("Cancelar");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);

    addArtistButton.addActionListener(e -> handleAddArtist());
    removeArtistButton.addActionListener(e -> handleRemoveArtist());
    addGenreButton.addActionListener(e -> handleAddGenre());
    removeGenreButton.addActionListener(e -> handleRemoveGenre());
    saveButton.addActionListener(e -> handleSaveSong());
    playButton.addActionListener(ev -> toggleAudioPlayback(playButton));
    cancelButton.addActionListener(e -> {
      stopAudio();
      dispose();
    });
  }
  
  private void loadFormData() {
    try {
      ApiResponse<List<AlbumDAO>> response = apiClient.getAllAlbums();
      if(response.isSuccess() && response.getData() != null) {
        Vector<AlbumDAO> albums = new Vector<>();
        albums.add(null);
        albums.addAll(response.getData());
        albumComboBox.setModel(new DefaultComboBoxModel<>(albums));
      } else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los albumes: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      
      albumComboBox.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean  cellHasFocus) {
          super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          if(value instanceof AlbumDAO albumDAO) setText(albumDAO.getTitle());
          else if(value == null) setText("Seleccione un album...");
          return this;
        }
      });
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicacion", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void populateForm() {
    if(songToEdit != null) {
      formTitleLabel.setText("Editar Cancion");
      titleField.setText(songToEdit.getTitle());
      durationSpinner.setValue(songToEdit.getDurationSeconds());
      audioFilePathField.setText(songToEdit.getAudioFilePath());
      explicitCheckBox.setSelected(songToEdit.isExplicit());
      
      if(songToEdit.getAlbum() != null) {
        DefaultComboBoxModel<AlbumDAO> model = (DefaultComboBoxModel<AlbumDAO>) albumComboBox.getModel();
        for(int i = 0; i < model.getSize(); i++) {
          AlbumDAO item = model.getElementAt(i);
          if(item != null && item.getAlbumId() != null && item.getAlbumId().equals(songToEdit.getAlbum().getAlbumId())) {
            albumComboBox.setSelectedIndex(i);
            break;
          }
        }
      } else
        albumComboBox.setSelectedIndex(0);
      
      if(songToEdit.getArtists() != null)
        songToEdit.getArtists().forEach(selectedArtistsModel::addElement);
      
      if(songToEdit.getGenres() != null)
        songToEdit.getGenres().forEach(selectedGenresModel::addElement);
    } else {
      titleField.setText("");
      durationSpinner.setValue(0);
      audioFilePathField.setText("");
      explicitCheckBox.setSelected(false);
      albumComboBox.setSelectedIndex(0);
      selectedArtistsModel.clear();
      selectedGenresModel.clear();
    }
  }
  
  private void toggleAudioPlayback(JButton playButton) {
    String filePath = audioFilePathField.getText().trim();
    if (filePath.isEmpty()) {
      JOptionPane.showMessageDialog(this, "La ruta del archivo de audio está vacia.", "Error de Reproduccion", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (isPlaying) {
      stopAudio();
      playButton.setText("Reproducir");
    } else {
      try {
        new Thread(() -> {
          try {
            playAudioInThread(filePath);
          } catch (IOException | JavaLayerException ex) {
            SwingUtilities.invokeLater(() -> {
              JOptionPane.showMessageDialog(this, "Error al reproducir el MP3: " + ex.getMessage(), "Error de Audio MP3", JOptionPane.ERROR_MESSAGE);
              ex.printStackTrace();
              playButton.setText("Reproducir");
            });
          }
        }).start();
        playButton.setText("Detener");
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al iniciar la reproducción: " + e.getMessage(), "Error de Audio", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
      }
    }
  }
  
  private void playAudioInThread(String filePath) throws IOException, JavaLayerException {
    stopAudio();
    
    File audioFile = new File(filePath);
    if(!audioFile.exists())
      throw new IOException("El archivo de audio no existe: " + filePath);
    
    FileInputStream fis;
    try {
      fis = new FileInputStream(audioFile);
      mp3Player = new Player(fis);
      isPlaying = true;
      mp3Player.play();
    } finally {
      SwingUtilities.invokeLater(() -> {
        isPlaying = false;
        Component[] components = ((JPanel) audioFilePathField.getParent()).getComponents();
        for(Component component : components)
          if(component instanceof JButton button && button.getText().equals("Detener")) {
            button.setText("Reproducir");
            break;
          }
      });
    }
  }
  
  private void stopAudio() {
    if(mp3Player != null && isPlaying) {
      mp3Player.close();
      mp3Player = null;
    }
    
    isPlaying = false;
  }
  
  @Override
  public void dispose() {
    stopAudio();
    super.dispose();
  }
  
  private void handleAddArtist() {
    try {
      ApiResponse<List<ArtistDAO>> response = apiClient.getAllArtists();
      if(response.isSuccess() && response.getData() != null) {
        List<ArtistDAO> allArtists = new ArrayList<>(response.getData());
        allArtists.removeAll(
          Collections.list(selectedArtistsModel.elements())
            .stream().collect(Collectors.toList())
        );
        
        Vector<ArtistDAO> artists = new Vector<>();
        artists.add(null);
        artists.addAll(allArtists);
        
        JComboBox<ArtistDAO> artistChooser = new JComboBox<>(artists);
        artistChooser.setRenderer(new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof ArtistDAO artistDAO) setText(artistDAO.getName());
            else if(value == null) setText("Seleccione un artista...");
            return this;
          }
        });
        
        int result = JOptionPane.showConfirmDialog(this, artistChooser, "Seleccionar Artista",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if(result == JOptionPane.OK_OPTION && artistChooser.getSelectedItem() != null)
          selectedArtistsModel.addElement((ArtistDAO) artistChooser.getSelectedItem());
      } else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los artistas: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void handleRemoveArtist() {
    int selectedIndex = artistsJList.getSelectedIndex();
    if(selectedIndex != -1) selectedArtistsModel.remove(selectedIndex);
    else JOptionPane.showMessageDialog(this, "Por favor, selecciona un artista para quitar.", "Ningun Artista Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleAddGenre() {
    try {
      ApiResponse<List<GenreDAO>> response = apiClient.getAllGenres();
      if(response.isSuccess() && response.getData() != null) {
        List<GenreDAO> allGenres = new ArrayList<>(response.getData());
        allGenres.removeAll(
          Collections.list(selectedGenresModel.elements())
            .stream().collect(Collectors.toList())
        );
        
        Vector<GenreDAO> genres = new Vector<>();
        genres.add(null);
        genres.addAll(allGenres);
        
        JComboBox<GenreDAO> genreChooser = new JComboBox<>(genres);
        genreChooser.setRenderer(new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof GenreDAO genreDAO) setText(genreDAO.getName());
            else if(value == null) setText("Seleccione un genero...");
            return this;
          }
        });
        
        int result = JOptionPane.showConfirmDialog(this, genreChooser, "Seleccionar Genero",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if(result == JOptionPane.OK_OPTION && genreChooser.getSelectedItem() != null)
          selectedGenresModel.addElement((GenreDAO) genreChooser.getSelectedItem());
      } else
        JOptionPane.showMessageDialog(this, "No se pudieron cargar los generos: " + response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Error al conectar con el servidor: " + ex.getMessage(), "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void handleRemoveGenre() {
    int selectedIndex = genresJList.getSelectedIndex();
    if(selectedIndex != -1) selectedGenresModel.remove(selectedIndex);
    else JOptionPane.showMessageDialog(this, "Por favor, selecciona un genero para quitar.", "Ningun Genero Seleccionado", JOptionPane.WARNING_MESSAGE);
  }
  
  private void handleSaveSong() {
    if(!validateInput()) return;
    
    SongDAO song = songToEdit != null ? songToEdit : new SongDAO();
    song.setTitle(titleField.getText());
    song.setDurationSeconds((Integer) durationSpinner.getValue());
    song.setAudioFilePath(audioFilePathField.getText());
    song.setExplicit(explicitCheckBox.isSelected());
    song.setAlbum((AlbumDAO) albumComboBox.getSelectedItem());
    if(songToEdit == null) song.setPlayCount(0);
    
    song.setArtists(Collections.list(selectedArtistsModel.elements()));
    song.setGenres(Collections.list(selectedGenresModel.elements()));
    
    try {
      ApiResponse<Void> response;
      if(songToEdit == null) response = apiClient.createSong(song);
      else response = apiClient.updateSong(song);
      
      if(response.isSuccess()) {
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
      errorMessage.append("El título no puede estar vacío.\n");
    if((Integer) durationSpinner.getValue() < 0)
      errorMessage.append("La duracion no puede ser negativa\n");
    if(audioFilePathField.getText() == null || audioFilePathField.getText().trim().isEmpty())
      errorMessage.append("La ruta del archivo de audio no puede estar vacía.\n");
    if(albumComboBox.getSelectedItem() == null)
      errorMessage.append("Debe seleccionar un álbum.\n");
    if(selectedArtistsModel.isEmpty())
      errorMessage.append("Debe seleccionar al menos un artista.\n");
    if(selectedGenresModel.isEmpty())
      errorMessage.append("Debe seleccionar al menos un género.\n");
    if(errorMessage.length() == 0) return true;
    else {
      JOptionPane.showMessageDialog(this, errorMessage.toString(), "Errores de Validación", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }
  
  public boolean isSaved() {
    return saved;
  }
}

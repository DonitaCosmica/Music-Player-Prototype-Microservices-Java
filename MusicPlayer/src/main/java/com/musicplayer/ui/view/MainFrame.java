package main.java.com.musicplayer.ui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class MainFrame extends JFrame {
  private JPanel contentPanel;
  private JLabel currentSongLabel;
  private JButton playButton, pauseButton, stopbutton;
  private JSlider volumeSlider;
  
  public MainFrame() {
    setTitle("Reproductor y Gestor de Musica");
    setSize(1000, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    
    initMenuBar();
    initComponents();
    addListeners();
  }
  
  private void initMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("Archivo");
    JMenuItem exitItem = new JMenuItem("salir");
    exitItem.addActionListener(ev -> System.exit(0));
    fileMenu.add(exitItem);
    
    JMenu manageMenu = new JMenu("Gestionar");
    JMenuItem songsMenuItem = new JMenuItem("Canciones");
    songsMenuItem.addActionListener(ev -> showPanel(new SongListPanel(this)));
    manageMenu.add(songsMenuItem);
    
    JMenuItem artistsMenuItem = new JMenuItem("Artistas");
    artistsMenuItem.addActionListener(ev -> showPanel(new ArtistListPanel(this)));
    manageMenu.add(artistsMenuItem);
    
    JMenuItem albumsMenuItem = new JMenuItem("Albumes");
    albumsMenuItem.addActionListener(ev -> showPanel(new AlbumListPanel(this)));
    manageMenu.add(albumsMenuItem);
    
    JMenuItem genresMenuItem = new JMenuItem("Generos");
    genresMenuItem.addActionListener(ev -> showPanel(new GenreListPanel(this)));
    manageMenu.add(genresMenuItem);
    
    JMenuItem countriesMenuItem = new JMenuItem("Paises");
    countriesMenuItem.addActionListener(ev -> showPanel(new CountryListPanel(this)));
    manageMenu.add(countriesMenuItem);
    
    menuBar.add(fileMenu);
    menuBar.add(manageMenu);
    setJMenuBar(menuBar);
  }
  
  private void initComponents() {
    setLayout(new BorderLayout());
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    currentSongLabel = new JLabel("No hay cancion reproduciendose");
    topPanel.add(currentSongLabel);
    
    playButton = new JButton("Play");
    pauseButton = new JButton("Pause");
    stopbutton = new JButton("Stop");
    volumeSlider = new JSlider(0, 100, 50);
    
    topPanel.add(playButton);
    topPanel.add(pauseButton);
    topPanel.add(stopbutton);
    topPanel.add(new JLabel("Volumen"));
    topPanel.add(volumeSlider);
    add(topPanel, BorderLayout.NORTH);
    
    contentPanel = new JPanel();
    contentPanel.setLayout(new CardLayout());
    JPanel welcomePanel = new JPanel(new GridBagLayout());
    welcomePanel.add(new JLabel("Selecciona una opcion del menu 'Gestionar' para empezar"));
    contentPanel.add(welcomePanel, "Welcome");
    
    ((CardLayout)contentPanel.getLayout()).show(contentPanel, "Welcome");
    add(contentPanel, BorderLayout.CENTER);
    
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    bottomPanel.add(new JLabel("Mi Reproductor de Musica - Proyecto Final POE"));
    add(bottomPanel, BorderLayout.SOUTH);
  }
  
  private void addListeners() {
    playButton.addActionListener(ev -> handlePlay());
    pauseButton.addActionListener(ev -> handlePause());
    stopbutton.addActionListener(ev -> handleStop());
    volumeSlider.addChangeListener(ev -> {
      System.out.println("Volumen: " + volumeSlider.getValue());
    });
  }
  
  public void showPanel(JPanel panel) {
    contentPanel.removeAll();
    contentPanel.add(panel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
  }
  
  private void handlePlay() {
    currentSongLabel.setText("Reproduciendo: Cancion de Prueba...");
    System.out.println("Play (Swing): Reproduciendo audio...");
  }
  
  private void handlePause() {
    System.out.println("Pause (Swing): Audio en pausa");
  }
  
  private void handleStop() {
    currentSongLabel.setText("No hay cancion reproduciendose");
    System.out.println("Stop (Swing): Audio detenido");
  }
}

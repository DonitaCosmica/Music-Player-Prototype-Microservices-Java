package main.java.com.musicplayer.ui;

import javax.swing.SwingUtilities;
import main.java.com.musicplayer.ui.view.MainFrame;

public class MainApp {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame();
      mainFrame.setVisible(true);
    });
  }
}
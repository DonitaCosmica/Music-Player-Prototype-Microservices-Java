package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  private final int PORT = 5239;
  
  public void startServer() {
    try(ServerSocket ss = new ServerSocket(PORT)) {
      System.out.println("Esperando cliente...");
      
      while(true) {
        Socket cs = ss.accept();
        System.out.println("Cliente conectado desde " + cs.getInetAddress());

        ClientHandler clientHandler = new ClientHandler(cs);
        new Thread(clientHandler).start();
      }
    } catch (IOException ex) {
      System.out.println("Error en el servidor: " + ex.getMessage());
    }
  }
}

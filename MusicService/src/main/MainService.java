package main;

import java.io.IOException;
import server.Server;
import utils.IsoCodeLookupService;

public class MainService {
  public static void main(String[] args) throws IOException {
    Server server = new Server();
    
    System.out.println("Iniciando servidor...");
    server.startServer();
    IsoCodeLookupService.initialize();
    System.out.println("MusicService starting...");
  }
}
package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.dao.CountryDAO;
import com.microservices.dao.GenreDAO;
import com.microservices.dao.SongDAO;
import com.microservices.interfaces.IGenericController;
import com.microservices.request.Request;
import com.microservices.response.ApiResponse;
import com.microservices.utils.ControllerEntry;
import com.microservices.utils.LocalDateAdapter;
import controller.AlbumController;
import controller.SongController;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import controller.ArtistController;
import controller.CountryController;
import controller.GenreController;
import java.time.LocalDate;

public class ClientHandler implements Runnable {
  private final Socket cs;
  private final Gson gson;
  private final Map<String, ControllerEntry<?>> controllers = new HashMap<>();
  
  public ClientHandler(Socket socket) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    
    this.gson = gsonBuilder.create();
    this.cs = socket;
    registerControllers();
  }
  
  @Override
  public void run() {
    try(
      DataOutputStream clientOutput = new DataOutputStream(cs.getOutputStream());
      DataInputStream clientInput = new DataInputStream(cs.getInputStream());
    ) {
      String requestJson;
      while((requestJson = clientInput.readUTF()) != null) {
        System.out.println("Request: " + requestJson);
        Request<?> tempRequest = gson.fromJson(requestJson, new TypeToken<Request<?>>(){}.getType());
        String className = tempRequest.getClassName();
        if(className == null || className.isBlank()) {
          clientOutput.writeUTF("Error: 'className' es requerido para la deserialización dinámica.");
          clientOutput.flush();
          continue;
        }
        
        try {
          Class<?> bodyClass = Class.forName(className);
          Type requestType = TypeToken.getParameterized(Request.class, bodyClass).getType();
          Request<?> request = gson.fromJson(requestJson, requestType);
          String responseJson = handleRequest(request);
          clientOutput.writeUTF(responseJson);
          clientOutput.flush();
        } catch (ClassNotFoundException ex) {
          clientOutput.writeUTF("Error: clase no encontrada - " + className);
          clientOutput.flush();
        } catch(JsonSyntaxException | IOException ex) {
          clientOutput.writeUTF("Error al procesar la solicitud: " + ex.getMessage());
          clientOutput.flush();
        }
      }
    } catch (IOException ex) {
      System.out.println("Cliente desconectado: " + ex.getMessage());
    }
  }
  
  private void registerControllers() {
    controllers.put("/artist", new ControllerEntry<>(new ArtistController(), ArtistDAO.class));
    controllers.put("/album", new ControllerEntry<>(new AlbumController(), AlbumDAO.class));
    controllers.put("/country", new ControllerEntry<>(new CountryController(), CountryDAO.class));
    controllers.put("/genre", new ControllerEntry<>(new GenreController(), GenreDAO.class));
    controllers.put("/song", new ControllerEntry<>(new SongController(), SongDAO.class));
  }
  
  private String handleRequest(Request request) {
    ControllerEntry<?> entry  = controllers.get(request.getPath());
    if(entry  == null)
      return gson.toJson(new ApiResponse<>(false, "Ruta no soportada"));
    
    return switch(request.getMethod()) {
      case "GET" -> {
        Map<String, String> params = request.getParams();
        if(params == null || params.isEmpty())
          yield gson.toJson(entry.getController().getAll());
        else if (params.containsKey("id"))
          yield gson.toJson(entry.getController().getById(params.get("id")));
        else
          yield gson.toJson(entry.getController().getByParams(params));
      }
      case "POST" -> handleBodyRequest(request, entry, "create");
      case "PATCH" -> handleBodyRequest(request, entry, "update");
      case "DELETE" -> {
        String id = request.getParams() != null
          ? (String)request.getParams().get("id") : null;
                
        if(id == null) yield gson.toJson(new ApiResponse(false, "Falta el parametro 'id'"));
        yield gson.toJson(entry .getController().delete(id));
      }
      default -> gson.toJson(new ApiResponse(false, "Metodo no soportado"));
    };
  }
  
  @SuppressWarnings("unchecked")
  private <T> String handleBodyRequest(Request<Object> request, ControllerEntry<T> entry, String action) {
    T body = gson.fromJson(gson.toJson(request.getBody()), entry.getType());
    IGenericController<T> controller = (IGenericController<T>) entry.getController();

    return switch (action) {
      case "create" -> gson.toJson(controller.create(body));
      case "update" -> gson.toJson(controller.update(body));
      default -> gson.toJson(new ApiResponse<>(false, "Acción no soportada"));
    };
  }
}

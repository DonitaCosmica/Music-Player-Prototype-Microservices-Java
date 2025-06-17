package main.java.com.musicplayer.ui.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.dao.CountryDAO;
import com.microservices.dao.GenreDAO;
import com.microservices.dao.SongDAO;
import com.microservices.request.Request;
import com.microservices.response.ApiResponse;
import com.microservices.utils.LocalDateAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ApiClient {
  private final int PORT = 5239;
  private final String HOST = "localhost";
  private final Gson gson;
  
  public ApiClient() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    this.gson = gsonBuilder.create();
  }
  
  private <T, R> ApiResponse<R> sendRequest(String method, String path, Map<String, String> params, T body, Class<T> bodyType, Type responseDataType) throws IOException {
    try(
      Socket cs = new Socket(HOST, PORT);
      DataOutputStream serverOutput = new DataOutputStream(cs.getOutputStream());
      DataInputStream serverInput = new DataInputStream(cs.getInputStream());
    ) {
      String bodyClassName = bodyType != null ? bodyType.getName() : Void.class.getName();
      Request<T> request = new Request<>(method, path, params, body, bodyClassName);
      String requestJson = gson.toJson(request);
      serverOutput.writeUTF(requestJson);
      serverOutput.flush();
      
      String responseJson = serverInput.readUTF();
      return gson.fromJson(responseJson, responseDataType);
    }
  }
  
  public ApiResponse<List<AlbumDAO>> getAllAlbums() throws IOException {
    Type responseType = new TypeToken<ApiResponse<List<AlbumDAO>>>(){}.getType();
    return sendRequest("GET", "/album", null, null, Void.class, responseType);
  }
  
  public ApiResponse<AlbumDAO> getAlbumById(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<AlbumDAO>>(){}.getType();
    return sendRequest("GET", "/album", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<Void> createAlbum(AlbumDAO album) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("POST", "/album", null, album, AlbumDAO.class, responseType);
  }
  
  public ApiResponse<Void> updateAlbum(AlbumDAO album) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("PATCH", "/album", null, album, AlbumDAO.class, responseType);
  }
  
  public ApiResponse<Void> deleteAlbum(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("DELETE", "/album", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<List<SongDAO>> getAllSongs() throws IOException {
    Type responseType = new TypeToken<ApiResponse<List<SongDAO>>>(){}.getType();
    return sendRequest("GET", "/song", null, null, Void.class, responseType);
  }
  
  public ApiResponse<SongDAO> getSongById(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<SongDAO>>(){}.getType();
    return sendRequest("GET", "/song", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<Void> createSong(SongDAO song) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("POST", "/song", null, song, SongDAO.class, responseType);
  }
  
  public ApiResponse<Void> updateSong(SongDAO song) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("PATCH", "/song", null, song, SongDAO.class, responseType);
  }
  
  public ApiResponse<Void> deleteSong(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("DELETE", "/song", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<List<ArtistDAO>> getAllArtists() throws IOException {
    Type responseType = new TypeToken<ApiResponse<List<ArtistDAO>>>(){}.getType();
    return sendRequest("GET", "/artist", null, null, Void.class, responseType);
  }
  
  public ApiResponse<ArtistDAO> getArtistById(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<ArtistDAO>>(){}.getType();
    return sendRequest("GET", "/artist", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<Void> createArtist(ArtistDAO artist) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("POST", "/artist", null, artist, ArtistDAO.class, responseType);
  }
  
  public ApiResponse<Void> updateArtist(ArtistDAO artist) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("PATCH", "/artist", null, artist, ArtistDAO.class, responseType);
  }
  
  public ApiResponse<Void> deleteArtist(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("DELETE", "/artist", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<List<GenreDAO>> getAllGenres() throws IOException {
    Type responseType = new TypeToken<ApiResponse<List<GenreDAO>>>(){}.getType();
    return sendRequest("GET", "/genre", null, null, Void.class, responseType);
  }
  
  public ApiResponse<GenreDAO> getGenreById(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<GenreDAO>>(){}.getType();
    return sendRequest("GET", "/genre", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<Void> createGenre(GenreDAO genre) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("POST", "/genre", null, genre, GenreDAO.class, responseType);
  }
  
  public ApiResponse<Void> updateGenre(GenreDAO genre) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("PATCH", "/genre", null, genre, GenreDAO.class, responseType);
  }
  
  public ApiResponse<Void> deleteGenre(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("DELETE", "/genre", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<List<CountryDAO>> getAllCountries() throws IOException {
    Type responseType = new TypeToken<ApiResponse<List<CountryDAO>>>(){}.getType();
    return sendRequest("GET", "/country", null, null, Void.class, responseType);
  }
  
  public ApiResponse<CountryDAO> getCountryById(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<CountryDAO>>(){}.getType();
    return sendRequest("GET", "/country", Map.of("id", id), null, Void.class, responseType);
  }
  
  public ApiResponse<Void> createCountry(CountryDAO country) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("POST", "/country", null, country, CountryDAO.class, responseType);
  }
  
  public ApiResponse<Void> updateCountry(CountryDAO country) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("PATCH", "/country", null, country, CountryDAO.class, responseType);
  }
  
  public ApiResponse<Void> deleteCountry(String id) throws IOException {
    Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
    return sendRequest("DELETE", "/country", Map.of("id", id), null, Void.class, responseType);
  }
}

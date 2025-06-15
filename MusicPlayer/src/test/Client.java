package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microservices.dao.CountryDAO;
import com.microservices.dao.SongDAO;
import com.microservices.request.Request;
import com.microservices.response.ApiResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.dao.GenreDAO;
import com.microservices.utils.LocalDateAdapter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

public class Client {
  private final int PORT = 5239;
  private final String HOST = "localhost";
  private final Gson gson;
  
  public Client() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
    this.gson = gsonBuilder.create();
  }
  
  public void startClient() {
    try (
      Socket socket = new Socket(HOST, PORT);
      DataOutputStream serverOutput = new DataOutputStream(socket.getOutputStream());
      DataInputStream serverInput = new DataInputStream(socket.getInputStream())
    ) {
      System.out.println("--- Connected to Music Service Server ---");

      // --- Step 1: Create and retrieve Country ---
      // We need the Country's actual ID from the DB to link to Artist
      CountryDAO canada = createAndGetCountry(serverOutput, serverInput, "Canada", "CA");
      if (canada == null) {
        System.err.println("Failed to get Canada country ID. Aborting test.");
        return;
      }

      // --- Step 2: Create and retrieve Genres ---
      // We need their IDs for linking to Songs
      GenreDAO rockGenre = createAndGetGenre(serverOutput, serverInput, "Rock", "Music genre characterized by a strong beat.");
      if (rockGenre == null) {
        System.err.println("Failed to get Rock Genre ID. Aborting test.");
        return;
      }

      GenreDAO popGenre = createAndGetGenre(serverOutput, serverInput, "Pop", "Popular music genre.");
      if (popGenre == null) {
        System.err.println("Failed to get Pop Genre ID. Aborting test.");
        return;
      }

      // --- Step 3: Create and retrieve Artist ---
      // We need the Artist's actual ID from the DB to link to Album and Songs
      ArtistDAO artist = createAndGetArtist(serverOutput, serverInput, "The Awesome Band", "A legendary band from Canada.", "http://example.com/band_pic.jpg", canada);
      if (artist == null) {
        System.err.println("Failed to get Artist ID. Aborting test.");
        return;
      }

      // --- Step 4: Create and retrieve Album ---
      // We need the Album's actual ID from the DB to link to Songs
      AlbumDAO album = createAndGetAlbum(serverOutput, serverInput, "Greatest Hits", LocalDate.of(2020, 1, 15), "http://example.com/album_cover.jpg", artist, 3, "Indie Records");
      if (album == null) {
        System.err.println("Failed to get Album ID. Aborting test.");
        return;
      }

      // --- Step 5: Create 3 Songs ---
      System.out.println("\n--- Creating Songs ---");

      // Song 1
      SongDAO song1 = new SongDAO();
      song1.setTitle("Epic Journey");
      song1.setDurationSeconds(240);
      song1.setAudioFilePath("/audio/epic_journey.mp3");
      song1.setExplicit(false);
      song1.setPlayCount(0);
      song1.setAlbum(album);
      song1.setArtists(Collections.singletonList(artist)); // Link to the created artist
      song1.setGenres(Collections.singletonList(rockGenre)); // Link to rock genre
            
      sendCreateRequest(serverOutput, serverInput, "/song", song1, SongDAO.class);

      // Song 2
      SongDAO song2 = new SongDAO();
      song2.setTitle("Sunny Melody");
      song2.setDurationSeconds(180);
      song2.setAudioFilePath("/audio/sunny_melody.mp3");
      song2.setExplicit(false);
      song2.setPlayCount(0);
      song2.setAlbum(album);
      song2.setArtists(Collections.singletonList(artist));
      song2.setGenres(Collections.singletonList(popGenre)); // Link to pop genre

      sendCreateRequest(serverOutput, serverInput, "/song", song2, SongDAO.class);

      // Song 3 (another rock song by the same artist)
      SongDAO song3 = new SongDAO();
      song3.setTitle("Rock Anthem");
      song3.setDurationSeconds(300);
      song3.setAudioFilePath("/audio/rock_anthem.mp3");
      song3.setExplicit(true); // Example of explicit song
      song3.setPlayCount(0);
      song3.setAlbum(album);
      song3.setArtists(Collections.singletonList(artist));
      song3.setGenres(Collections.singletonList(rockGenre));

      sendCreateRequest(serverOutput, serverInput, "/song", song3, SongDAO.class);

      // --- Step 6: Get all Songs to verify ---
      System.out.println("\n--- Getting All Songs ---");
      Request<Void> getAllSongsRequest = new Request<>(
        "GET", "/song", null, null, Void.class.getName()
      );
      serverOutput.writeUTF(gson.toJson(getAllSongsRequest));
      serverOutput.flush();

      Type listSongsResponseType = new TypeToken<ApiResponse<List<SongDAO>>>(){}.getType();
      String songsResponseJson = serverInput.readUTF();
      ApiResponse<List<SongDAO>> allSongsResult = gson.fromJson(songsResponseJson, listSongsResponseType);

      if (allSongsResult.isSuccess() && allSongsResult.getData() != null) {
        System.out.println("Successfully retrieved songs:");
        for (SongDAO s : allSongsResult.getData()) {
          System.out.println(s.getTitle() + " (ID: " + s.getSongId() + ")");
          if (s.getAlbum() != null)
            System.out.println("  Album: " + s.getAlbum().getTitle());
          if (s.getArtists() != null && !s.getArtists().isEmpty()) 
            System.out.println("  Artists: " + s.getArtists().stream().map(ArtistDAO::getName).collect(Collectors.joining(", ")));
          if (s.getGenres() != null && !s.getGenres().isEmpty())
            System.out.println("  Genres: " + s.getGenres().stream().map(GenreDAO::getName).collect(Collectors.joining(", ")));
        }
      } else
        System.err.println("Failed to retrieve songs: " + allSongsResult.getMessage());
    } catch (IOException e) {
      System.err.println("Error connecting or communicating with the server: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("An unexpected error occurred: " + e.getMessage());
      e.printStackTrace();
    }
    
    System.out.println("\n--- Test finished ---");
  }
  
  private <T> void sendCreateRequest(DataOutputStream output, DataInputStream input, String path, T dao, Class<T> daoClass) throws IOException {
    System.out.println("\nSending POST request to " + path + " for: " + dao);
    Request<T> request = new Request<>("POST", path, null, dao, daoClass.getName());
    output.writeUTF(gson.toJson(request));
    output.flush();

    String responseJson = input.readUTF();
    ApiResponse<Void> apiResponse = gson.fromJson(responseJson, new TypeToken<ApiResponse<Void>>(){}.getType());
    System.out.println("Server response: " + apiResponse.getMessage() + " (Success: " + apiResponse.isSuccess() + ")");
    if (!apiResponse.isSuccess())
      System.err.println("Error creating entity at " + path);
  }

  private CountryDAO createAndGetCountry(DataOutputStream output, DataInputStream input, String name, String iso2) throws IOException {
    System.out.println("\n--- Creating Country: " + name + " ---");
    CountryDAO country = new CountryDAO();
    country.setName(name);
    country.setIsoCode2(iso2);
        
    sendCreateRequest(output, input, "/country", country, CountryDAO.class);

    // Retrieve the created country to get its generated ID
    System.out.println("Attempting to retrieve country by ISO2: " + iso2);
    Request<Void> getCountryRequest = new Request<>(
      "GET", "/country", Map.of("isoCode2", iso2), null, Void.class.getName() // Dummy type for GET body
    );
    output.writeUTF(gson.toJson(getCountryRequest));
    output.flush();

    Type listCountryResponseType = new TypeToken<ApiResponse<List<CountryDAO>>>(){}.getType();
    String countryResponseJson = input.readUTF();
    ApiResponse<List<CountryDAO>> getResult = gson.fromJson(countryResponseJson, listCountryResponseType);

    if (getResult.isSuccess() && getResult.getData() != null && !getResult.getData().isEmpty()) {
      System.out.println("Successfully retrieved country: " + getResult.getData().getFirst().getName() + " (ID: " + getResult.getData().getFirst().getCountryId() + ")");
      return getResult.getData().getFirst();
    } else {
      System.err.println("Failed to retrieve country " + name + ": " + getResult.getMessage());
      return null;
    }
  }

  private GenreDAO createAndGetGenre(DataOutputStream output, DataInputStream input, String name, String description) throws IOException {
    System.out.println("\n--- Creating Genre: " + name + " ---");
    GenreDAO genre = new GenreDAO();
    genre.setName(name);
    genre.setDescription(description);

    sendCreateRequest(output, input, "/genre", genre, GenreDAO.class);

    // Retrieve the created genre to get its generated ID
    System.out.println("Attempting to retrieve genre by name: " + name);
    Request<Void> getGenreRequest = new Request<>(
      "GET", "/genre", Map.of("name", name), null, Void.class.getName()
    );
    output.writeUTF(gson.toJson(getGenreRequest));
    output.flush();

    Type listGenreResponseType = new TypeToken<ApiResponse<List<GenreDAO>>>(){}.getType();
    String genreResponseJson = input.readUTF();
    ApiResponse<List<GenreDAO>> getResult = gson.fromJson(genreResponseJson, listGenreResponseType);

    if (getResult.isSuccess() && getResult.getData() != null && !getResult.getData().isEmpty()) {
      System.out.println("Successfully retrieved genre: " + getResult.getData().getFirst().getName() + " (ID: " + getResult.getData().getFirst().getGenreId() + ")");
      return getResult.getData().getFirst();
    } else {
      System.err.println("Failed to retrieve genre " + name + ": " + getResult.getMessage());
      return null;
    }
  }

  private ArtistDAO createAndGetArtist(DataOutputStream output, DataInputStream input, String name, String bio, String imageUrl, CountryDAO originCountry) throws IOException {
    System.out.println("\n--- Creating Artist: " + name + " ---");
    ArtistDAO artist = new ArtistDAO();
    artist.setName(name);
    artist.setBio(bio);
    artist.setImageUrl(imageUrl);
    artist.setOriginCountry(originCountry);

    sendCreateRequest(output, input, "/artist", artist, ArtistDAO.class);

    // Retrieve the created artist to get its generated ID
    System.out.println("Attempting to retrieve artist by name: " + name);
    Request<Void> getArtistRequest = new Request<>(
      "GET", "/artist", Map.of("name", name), null, Void.class.getName()
    );
    output.writeUTF(gson.toJson(getArtistRequest));
    output.flush();

    Type listArtistResponseType = new TypeToken<ApiResponse<List<ArtistDAO>>>(){}.getType();
    String artistResponseJson = input.readUTF();
    ApiResponse<List<ArtistDAO>> getResult = gson.fromJson(artistResponseJson, listArtistResponseType);

    if (getResult.isSuccess() && getResult.getData() != null && !getResult.getData().isEmpty()) {
      System.out.println("Successfully retrieved artist: " + getResult.getData().getFirst().getName() + " (ID: " + getResult.getData().getFirst().getArtistId() + ")");
      return getResult.getData().getFirst();
    } else {
      System.err.println("Failed to retrieve artist " + name + ": " + getResult.getMessage());
      return null;
    }
  }

  private AlbumDAO createAndGetAlbum(DataOutputStream output, DataInputStream input, String title, LocalDate releaseDate, String coverArtUrl, ArtistDAO primaryArtist, int totalTracks, String recordLabel) throws IOException {
    System.out.println("\n--- Creating Album: " + title + " ---");
    AlbumDAO album = new AlbumDAO();
    album.setTitle(title);
    album.setReleaseDate(releaseDate);
    album.setCoverArtUrl(coverArtUrl);
    album.setPrimaryArtist(primaryArtist);
    album.setTotalTracks(totalTracks);
    album.setRecordLabel(recordLabel);

    sendCreateRequest(output, input, "/album", album, AlbumDAO.class);

    // Retrieve the created album to get its generated ID
    System.out.println("Attempting to retrieve album by title: " + title);
    Request<Void> getAlbumRequest = new Request<>(
      "GET", "/album", Map.of("title", title), null, Void.class.getName()
    );
    output.writeUTF(gson.toJson(getAlbumRequest));
    output.flush();

    Type listAlbumResponseType = new TypeToken<ApiResponse<List<AlbumDAO>>>(){}.getType();
    String albumResponseJson = input.readUTF();
    ApiResponse<List<AlbumDAO>> getResult = gson.fromJson(albumResponseJson, listAlbumResponseType);

    if (getResult.isSuccess() && getResult.getData() != null && !getResult.getData().isEmpty()) {
      System.out.println("Successfully retrieved album: " + getResult.getData().getFirst().getTitle() + " (ID: " + getResult.getData().getFirst().getAlbumId() + ")");
      return getResult.getData().getFirst();
    } else {
      System.err.println("Failed to retrieve album " + title + ": " + getResult.getMessage());
      return null;
    }
  }
}

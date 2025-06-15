package interfaces;

import com.microservices.response.ApiResponse;
import java.util.List;
import java.util.Optional;
import model.Song;

public interface ISongRepository {
  List<Song> getSongs();
  Optional<Song> getSongById(String songId);
  List<Song> getSongsByTitle(String title);
  List<Song> getSongsByArtist(String artistId);
  List<Song> getSongsByGenre(String genreId);
  List<Song> getSongsByAlbum(String albumId);
  List<Song> getSongsByArtistAndGenre(String artistId, String genreId);
  ApiResponse<Void> createSong(Song song);
  ApiResponse<Void> updateSong(Song song);
  ApiResponse<Void> deleteSong(String songId);
}

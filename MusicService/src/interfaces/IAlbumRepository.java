package interfaces;

import com.microservices.response.ApiResponse;
import java.util.List;
import java.util.Optional;
import model.Album;

public interface IAlbumRepository {
  List<Album> getAlbums();
  Optional<Album> getAlbumById(String albumId);
  List<Album> getAlbumsByTitle(String title);
  List<Album> getAlbumsByArtist(String artistId);
  ApiResponse<Void> createAlbum(Album album);
  ApiResponse<Void> updateAlbum(Album album);
  ApiResponse<Void> deleteAlbum(String albumId);
}

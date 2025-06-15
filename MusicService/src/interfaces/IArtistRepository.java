package interfaces;

import com.microservices.response.ApiResponse;
import java.util.List;
import java.util.Optional;
import model.Artist;

public interface IArtistRepository {
  List<Artist> getArtists();
  Optional<Artist> getArtistById(String artistId);
  List<Artist> getArtistsByName(String name);
  List<Artist> getArtistsByCountry(String countryId);
  ApiResponse<Void> createArtist(Artist artist);
  ApiResponse<Void> updateArtist(Artist artist);
  ApiResponse<Void> deleteArtist(String artistId);
}

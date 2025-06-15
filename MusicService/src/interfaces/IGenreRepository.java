package interfaces;

import com.microservices.response.ApiResponse;
import java.util.List;
import java.util.Optional;
import model.Genre;

public interface IGenreRepository {
  List<Genre> getGenres();
  Optional<Genre> getGenreById(String genreId);
  List<Genre> getGenresByName(String name);
  ApiResponse<Void> createGenre(Genre genre);
  ApiResponse<Void> updateGenre(Genre genre);
  ApiResponse<Void> deleteGenre(String genreId);
}

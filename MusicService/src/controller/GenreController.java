package controller;

import com.microservices.dao.GenreDAO;
import com.microservices.interfaces.IGenericController;
import com.microservices.response.ApiResponse;
import interfaces.IGenreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Genre;
import repository.GenreRepository;

public class GenreController implements IGenericController<GenreDAO>{
  private final IGenreRepository genreRepository = new GenreRepository();
  
  @Override
  public ApiResponse<List<GenreDAO>> getAll() {
    List<GenreDAO> genres = genreRepository.getGenres()
      .stream().map(this::toDAO)
      .collect(Collectors.toList());

    return new ApiResponse<>(
      true, genres.isEmpty()
        ? "No hay generos registrados"
        : "Lista de generos obtenida exitosamente",
      genres
    );
  }
  
  @Override
  public ApiResponse<GenreDAO> getById(String genreId) {
    return genreRepository.getGenreById(genreId)
      .map(genre -> new ApiResponse<>(true, "Genero encontrado", toDAO(genre)))
      .orElse(new ApiResponse<>(false, "Genero no encontrado"));
  }
  
  @Override
  public ApiResponse<List<GenreDAO>> getByParams(Map<String, String> params) {
    List<Genre> genres;
    
    if(params.containsKey("name") && params.size() == 1)
      genres = genreRepository.getGenresByName(params.get("name"));
    else
      return new ApiResponse<>(false, "Parametros de busqueda no soportados o incompletos", new ArrayList<>());
    
    List<GenreDAO> result = genres.stream().map(this::toDAO).collect(Collectors.toList());
    return new ApiResponse<>(true, "Filtrado exitoso", result);
  }
  
  @Override
  public ApiResponse create(GenreDAO genreDAO) {
    Genre genre = toModel(genreDAO);
    return genreRepository.createGenre(genre);
  }
  
  @Override
  public ApiResponse update(GenreDAO genreDAO) {
    return genreRepository.getGenreById(genreDAO.getGenreId())
      .map(existingGenre -> {
        existingGenre.setName(genreDAO.getName());
        existingGenre.setDescription(genreDAO.getDescription());
        return genreRepository.updateGenre(existingGenre);
      }).orElse(new ApiResponse<>(false, "Genero inexistente"));
  }
  
  @Override
  public ApiResponse delete(String genreId) {
    return genreRepository.getGenreById(genreId)
      .map(song -> genreRepository.deleteGenre(genreId))
      .orElse(new ApiResponse<>(false, "Genero inexistente"));
  }
  
  private GenreDAO toDAO(Genre genre) {
    GenreDAO genreDAO = new GenreDAO();
    genreDAO.setGenreId(genre.getGenreId());
    genreDAO.setName(genre.getName());
    genreDAO.setDescription(genre.getDescription());
    return genreDAO;
  }
  
  private Genre toModel(GenreDAO genreDAO) {
    Genre genre = new Genre();
    if(genreDAO.getGenreId() != null)
      genre.setGenreId(genreDAO.getGenreId());
    
    genre.setName(genreDAO.getName());
    genre.setDescription(genreDAO.getDescription());
    return genre;
  }
}

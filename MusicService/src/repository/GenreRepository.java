package repository;

import com.microservices.response.ApiResponse;
import context.DataContext;
import interfaces.IGenreRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Genre;

public class GenreRepository implements IGenreRepository{
  private static final String SELECT_ALL = "SELECT * FROM Genre";
  private static final String SELECT_BY_ID = "SELECT * FROM Genre WHERE genre_id = CAST(? AS UUID)";
  private static final String SELECT_BY_NAME = "SELECT * FROM Genre WHERE LOWER(name) LIKE LOWER(?)";
  private static final String INSERT = "INSERT INTO Genre (name, description) VALUES (?, ?)";
  private static final String UPDATE = "UPDATE Genre SET name = ?, description = ? WHERE genre_id = CAST(? AS UUID)";
  private static final String DELETE = "DELETE FROM Genre WHERE genre_id = CAST(? AS UUID)";

  private Genre mapRow(ResultSet rs) throws SQLException {
    Genre genre = new Genre();
    genre.setGenreId(rs.getString("genre_id"));
    genre.setName(rs.getString("name"));
    genre.setDescription(rs.getString("description"));
    return genre;
  }
  
  @Override
  public List<Genre> getGenres() {
    List<Genre> genres = new ArrayList<>();    
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_ALL);
    ) {
      ResultSet rs = ps.executeQuery();
      while(rs.next()) genres.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar generos: " + ex.getMessage());
    }
    
    return genres;
  }
  
  @Override
  public Optional<Genre> getGenreById(String genreId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ID);
    ) {
      ps.setString(1, genreId);
      ResultSet rs = ps.executeQuery();
      if(rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar genero: " + ex.getMessage());
    }
    
    return Optional.empty();
  }
  
  @Override
  public List<Genre> getGenresByName(String name) {
    List<Genre> genres = new ArrayList<>(); 
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_NAME);
    ) {
      ps.setString(1, "%" + name + "%");
      ResultSet rs = ps.executeQuery();
      while(rs.next()) genres.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar genero: " + ex.getMessage());
    }
    
    return genres;
  }

  @Override
  public ApiResponse<Void> createGenre(Genre genre) {    
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(INSERT);
    ) {  
      ps.setString(1, genre.getName());
      ps.setString(2, genre.getDescription());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Genero insertado exitosamente"
          : "No se inserto ningun genero"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al insertar genero: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> updateGenre(Genre genre) {    
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(UPDATE); 
    ) {
      ps.setString(1, genre.getName());
      ps.setString(2, genre.getDescription());
      ps.setString(3, genre.getGenreId());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Genero actualizado exitosamente"
          : "No se actualizo ningun genero"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al actualizar genero: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> deleteGenre(String genreId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(DELETE);  
    ) {
      ps.setString(1, genreId);
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Genero eliminado exitosamente"
          : "No se elimino ningun genero"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al eliminar genero: " + ex.getMessage());
    }
  }
}

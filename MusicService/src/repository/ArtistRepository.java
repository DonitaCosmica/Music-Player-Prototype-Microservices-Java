package repository;

import com.microservices.response.ApiResponse;
import context.DataContext;
import model.Artist;
import interfaces.IArtistRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Country;

public class ArtistRepository implements IArtistRepository{
  private static final String SELECT_BASE =
    "SELECT a.artist_id, a.name, a.bio, a.image_url, " +
    "c.country_id, c.name AS country_name, c.iso_code2 AS country_iso_code2, c.iso_code3 AS country_iso_code3 " +
    "FROM Artist a LEFT JOIN Country c ON a.country_id = c.country_id";

  private static final String SELECT_ALL = SELECT_BASE;
  private static final String SELECT_BY_ID = SELECT_BASE + " WHERE a.artist_id = CAST(? AS UUID)";
  private static final String SELECT_BY_NAME = SELECT_BASE + " WHERE LOWER(a.name) LIKE LOWER(?)";
  private static final String SELECT_BY_COUNTRY_ID = SELECT_BASE + " WHERE a.country_id = CAST(? AS UUID)";
  private static final String INSERT = "INSERT INTO Artist (name, bio, country_id, image_url) VALUES (?, ?, CAST(? AS UUID), ?)";
  private static final String UPDATE = "UPDATE Artist SET name = ?, bio = ?, country_id = CAST(? AS UUID), image_url = ? WHERE artist_id = CAST(? AS UUID)";
  private static final String DELETE = "DELETE FROM Artist WHERE artist_id = CAST(? AS UUID)";
  
  private Artist mapRow(ResultSet rs) throws SQLException {
    Artist artist = new Artist();
    artist.setArtistId(rs.getString("artist_id"));
    artist.setName(rs.getString("name"));
    artist.setBio(rs.getString("bio"));
    artist.setImageUrl(rs.getString("image_url"));
    
    String countryId = rs.getString("country_id");
    if (countryId != null) {
      Country country = new Country();
      country.setCountryId(countryId);
      country.setName(rs.getString("country_name"));
      country.setIsoCode2(rs.getString("country_iso_code2"));
      country.setIsoCode3(rs.getString("country_iso_code3"));
      artist.setOriginCountry(country);
    }
    return artist;
  }
  
  @Override
  public List<Artist> getArtists() {
    List<Artist> artists = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_ALL);
    ) {
      ResultSet rs = ps.executeQuery();
      while(rs.next()) artists.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar artistas: " + ex.getMessage());
    }
    
    return artists;
  }
  
  @Override
  public Optional<Artist> getArtistById(String artistId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ID);
    ) {
      ps.setString(1, artistId);
      ResultSet rs = ps.executeQuery();
      if(rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar artista: " + ex.getMessage());
    }
    
    return Optional.empty();
  }
  
  @Override
  public List<Artist> getArtistsByName(String name) {
    List<Artist> artists = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_NAME);
    ) {
      ps.setString(1, "%" + name + "%");
      ResultSet rs = ps.executeQuery();
      while(rs.next()) artists.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar artista: " + ex.getMessage());
    }
    
    return artists;
  }
  
  @Override
  public List<Artist> getArtistsByCountry(String countryId) {
    List<Artist> artists = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_COUNTRY_ID);
    ) {
      ps.setString(1, countryId);
      ResultSet rs = ps.executeQuery();
      while(rs.next()) artists.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar artista: " + ex.getMessage());
    }
    
    return artists;
  }
  
  @Override
  public ApiResponse<Void> createArtist(Artist artist) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(INSERT);
    ) {
      ps.setString(1, artist.getName());
      ps.setString(2, artist.getBio());
      ps.setString(3, artist.getOriginCountry() != null
        ? artist.getOriginCountry().getCountryId() : null);
      ps.setString(4, artist.getImageUrl());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Artista insertado exitosamente"
          : "No se inserto ningun artista"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al insertar artista: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> updateArtist(Artist artist) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(UPDATE); 
    ) {
      ps.setString(1, artist.getName());
      ps.setString(2, artist.getBio());
      ps.setString(3, artist.getOriginCountry() != null
        ? artist.getOriginCountry().getCountryId() : null);
      ps.setString(4, artist.getImageUrl());
      ps.setString(5, artist.getArtistId());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Artista actualizado exitosamente"
          : "No se actualizo ningun artista"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al actualizar artista: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> deleteArtist(String artistId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(DELETE); 
    ) {
      ps.setString(1, artistId);      
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Artista eliminado exitosamente"
          : "No se elimino ningun artista"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al eliminar artista: " + ex.getMessage());
    }
  }
}

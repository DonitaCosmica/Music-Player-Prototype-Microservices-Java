package repository;

import com.microservices.response.ApiResponse;
import context.DataContext;
import interfaces.IAlbumRepository;
import interfaces.ISongRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Album;
import model.Artist;
import model.Song;

public class AlbumRepository implements IAlbumRepository{
  private static final String SELECT_BASE =
    "SELECT al.album_id, al.title, al.release_date, al.cover_art_url, al.total_tracks, al.record_label, " +
    "a.artist_id AS primary_artist_id, a.name AS primary_artist_name, a.bio AS primary_artist_bio, a.image_url AS primary_artist_image_url " +
    "FROM Album al LEFT JOIN Artist a ON al.artist_id = a.artist_id";
  
  private static final String SELECT_ALL = SELECT_BASE;
  private static final String SELECT_BY_ID = SELECT_BASE + " WHERE al.album_id = CAST(? AS UUID)";
  private static final String SELECT_BY_TITLE = SELECT_BASE + " WHERE LOWER(al.title) LIKE LOWER(?)";
  private static final String SELECT_BY_ARTIST_ID = SELECT_BASE + " WHERE al.artist_id = CAST(? AS UUID)";
  private static final String INSERT = "INSERT INTO Album (title, release_date, cover_art_url, artist_id, total_tracks, record_label) VALUES(?, ?, ?, CAST(? AS UUID), ?, ?)";
  private static final String UPDATE = "UPDATE Album SET title = ?, release_date = ?, cover_art_url = ?, artist_id = (? AS UUID), total_tracks = ?, record_label = ? WHERE album_id = CAST(? AS UUID)";
  private static final String DELETE = "DELETE FROM Album WHERE album_id = CAST(? AS UUID)";
  
  private final ISongRepository songRepository = new SongRepository();
  
  private Album mapRow(ResultSet rs) throws SQLException {
    Album album = new Album();
    album.setAlbumId(rs.getString("album_id"));
    album.setTitle(rs.getString("title"));
    album.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);
    album.setCoverArtUrl(rs.getString("cover_art_url"));
    album.setTotalTracks(rs.getInt("total_tracks"));
    album.setRecordLabel(rs.getString("record_label"));
    
    String primaryArtistId = rs.getString("primary_artist_id");
    if(primaryArtistId != null) {
      Artist primaryArtist = new Artist();
      primaryArtist.setArtistId(primaryArtistId);
      primaryArtist.setName(rs.getString("primary_artist_name"));
      primaryArtist.setBio(rs.getString("primary_artist_bio"));
      primaryArtist.setImageUrl(rs.getString("primary_artist_image_url"));
      album.setPrimaryArtist(primaryArtist);
    }
    
    List<Song> songsInAlbum = new ArrayList<>();
    try {
      songsInAlbum = songRepository.getSongsByAlbum(album.getAlbumId());
    } catch (Exception ex) {
      System.err.println("Warning: Could not retrieve songs for album " + album.getAlbumId() + ": " + ex.getMessage());
    }
    
    album.setSongs(songsInAlbum);
    return album;
  }
  
  @Override
  public List<Album> getAlbums() {
    List<Album> albums = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_ALL);
    ) {
      ResultSet rs = ps.executeQuery();
      while(rs.next()) albums.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar albumes: " + ex.getMessage());
      ex.printStackTrace();
    }
    
    return albums;
  }
  
  @Override
  public Optional<Album> getAlbumById(String albumId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ID);
    ) {
      ps.setString(1, albumId);
      ResultSet rs = ps.executeQuery();
      if(rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar album: " + ex.getMessage());
      ex.printStackTrace();
    }
    
    return Optional.empty();
  }
  
  @Override
  public List<Album> getAlbumsByTitle(String title) {
    List<Album> albums = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_TITLE);
    ) {
      ps.setString(1, "%" + title + "%");
      ResultSet rs = ps.executeQuery();
      while(rs.next()) albums.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar album: " + ex.getMessage());
      ex.printStackTrace();
    }
    
    return albums;
  }
  
  @Override
  public List<Album> getAlbumsByArtist(String artistId) {
    List<Album> albums = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ARTIST_ID);
    ) {
      ps.setString(1, artistId);
      ResultSet rs = ps.executeQuery();
      while(rs.next()) albums.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar albumes por artista: " + ex.getMessage());
      ex.printStackTrace();
    }
    
    return albums;
  }
  
  @Override
  public ApiResponse<Void> createAlbum(Album album) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(INSERT);
    ) {
      ps.setString(1, album.getTitle());
      ps.setDate(2, album.getReleaseDate() != null
        ? java.sql.Date.valueOf(album.getReleaseDate()) : null);
      ps.setString(3, album.getCoverArtUrl());
      ps.setString(4, album.getPrimaryArtist() != null
        ? album.getPrimaryArtist().getArtistId() : null);
      ps.setInt(5, album.getTotalTracks());
      ps.setString(6, album.getRecordLabel());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Album insertado exitosamente"
          : "No se inserto ningun album"
      );
    } catch (SQLException ex) {
      System.out.println("Error al insertar album: " + ex.getMessage());
      ex.printStackTrace();
      return new ApiResponse(false, "Error al insertar album: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> updateAlbum(Album album) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(UPDATE);
    ) {
      ps.setString(1, album.getTitle());
      ps.setDate(2, album.getReleaseDate() != null
        ? java.sql.Date.valueOf(album.getReleaseDate()) : null);
      ps.setString(3, album.getCoverArtUrl());
      ps.setString(4, album.getPrimaryArtist() != null
        ? album.getPrimaryArtist().getArtistId() : null);
      ps.setInt(5, album.getTotalTracks());
      ps.setString(6, album.getRecordLabel());
      ps.setString(7, album.getAlbumId());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Album actualizado exitosamente"
          : "No se actualizo ningun album"
      );
    } catch (SQLException ex) {
      System.out.println("Error al actualizar album: " + ex.getMessage());
      ex.printStackTrace();
      return new ApiResponse(false, "Error al actualizar album: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> deleteAlbum(String albumId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(DELETE); 
    ) {
      ps.setString(1, albumId);      
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Album eliminado exitosamente"
          : "No se elimino ningun album"
      );
    } catch (SQLException ex) {
      System.out.println("Error al eliminar album: " + ex.getMessage());
      ex.printStackTrace();
      return new ApiResponse(false, "Error al eliminar album: " + ex.getMessage());
    }
  }
}

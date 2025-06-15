package repository;

import com.microservices.response.ApiResponse;
import model.Song;
import context.DataContext;
import interfaces.ISongRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import model.Album;
import model.Artist;
import model.Genre;

public class SongRepository implements ISongRepository {
  private static final String SELECT_BASE = 
    "SELECT s.song_id, s.title, s.duration_seconds, s.audio_file_path, s.is_explicit, s.play_count, " +
    "al.album_id, al.title AS album_title, al.release_date AS album_release_date, al.cover_art_url AS album_cover_art_url, " +
    "al.total_tracks, al.record_label, al_artist.artist_id AS album_artist_id, al_artist.name AS album_artist_name, " +
    "a.artist_id AS song_artist_id, a.name AS song_artist_name, a.bio AS song_artist_bio, a.image_url AS song_artist_image_url, " +
    "g.genre_id AS song_genre_id, g.name AS song_genre_name, g.description AS song_genre_description " +
    "FROM Song s LEFT JOIN Album al ON s.album_id = al.album_id " +
    "LEFT JOIN Artist al_artist ON al.artist_id = al_artist.artist_id " +
    "LEFT JOIN Song_Artist sa ON s.song_id = sa.song_id " +
    "LEFT JOIN Artist a ON sa.artist_id = a.artist_id " +
    "LEFT JOIN Song_Genre sg ON s.song_id = sg.song_id " +
    "LEFT JOIN Genre g ON sg.genre_id = g.genre_id";
  
  private static final String SELECT_ALL = SELECT_BASE;
  private static final String SELECT_BY_ID = SELECT_BASE + " WHERE s.song_id = CAST(? AS UUID)";
  private static final String SELECT_BY_TITLE = SELECT_BASE + " WHERE LOWER(s.title) LIKE LOWER(?)";
  private static final String SELECT_BY_ARTIST = SELECT_BASE + " WHERE a.artist_id = CAST(? AS UUID)";
  private static final String SELECT_BY_GENRE = SELECT_BASE + " WHERE g.genre_id = CAST(? AS UUID)";
  private static final String SELECT_BY_ALBUM = SELECT_BASE + " WHERE al.album_id = CAST(? AS UUID)";
  private static final String SELECT_BY_ARTIST_AND_GENRE = SELECT_BASE + " WHERE a.artist_id = CAST(? AS UUID) AND g.genre_id = CAST(? AS UUID)";
  
  private static final String INSERT_SONG = "INSERT INTO Song (title, duration_seconds, audio_file_path, is_explicit, play_count, album_id) VALUES (?, ?, ?, ?, ?, CAST(? AS UUID)) RETURNING song_id";
  private static final String UPDATE_SONG = "UPDATE Song SET title = ?, duration_seconds = ?, audio_file_path = ?, is_explicit = ?, play_count = ?, album_id = CAST(? AS UUID) WHERE song_id = CAST(? AS UUID)";
  private static final String DELETE_SONG = "DELETE FROM Song WHERE song_id = CAST(? AS UUID)";
  
  private static final String INSERT_SONG_ARTIST = "INSERT INTO Song_Artist (song_id, artist_id, is_primary) VALUES (CAST(? AS UUID), CAST(? AS UUID), ?)";
  private static final String DELETE_SONG_ARTISTS_BY_SONG = "DELETE FROM Song_Artist WHERE song_id = CAST(? AS UUID)";
  
  private static final String INSERT_SONG_GENRE = "INSERT INTO Song_Genre (song_id, genre_id) VALUES (CAST(? AS UUID), CAST(? AS UUID))";
  private static final String DELETE_SONG_GENRES_BY_SONG = "DELETE FROM Song_Genre WHERE song_id = CAST(? AS UUID)";
  
  private List<Song> mapRowToSongs(ResultSet rs) throws SQLException {
    Map<String, Song> songMap = new HashMap<>();
    while(rs.next()) {
      String songId = rs.getString("song_id");
      Song song = songMap.get("songId");
      
      if(song == null) {
        song = new Song();
        song.setSongId(songId);
        song.setTitle(rs.getString("title"));
        song.setDurationSeconds(rs.getInt("duration_seconds"));
        song.setAudioFilePath(rs.getString("audio_file_path"));
        song.setExplicit(rs.getBoolean("is_explicit"));
        song.setPlayCount(rs.getLong("play_count"));
        
        String albumId = rs.getString("album_id");
        if(albumId != null) {
          Album album = new Album();
          album.setAlbumId(albumId);
          album.setTitle(rs.getString("album_title"));
          album.setReleaseDate(rs.getDate("album_release_date") != null ? rs.getDate("album_release_date").toLocalDate() : null);
          album.setCoverArtUrl(rs.getString("album_cover_art_url"));
          album.setTotalTracks(rs.getInt("total_tracks"));
          album.setRecordLabel(rs.getString("record_label"));
          
          String albumArtistId = rs.getString("album_artist_id");
          if(albumArtistId != null) {
            Artist albumArtist = new Artist();
            albumArtist.setArtistId(albumArtistId);
            albumArtist.setName(rs.getString("album_artist_name"));
            album.setPrimaryArtist(albumArtist);
          }
          
          song.setAlbum(album);
        }
        
        song.setArtists(new ArrayList<>());
        song.setGenres(new ArrayList<>());
        songMap.put(songId, song);
      }
      
      String songArtistId = rs.getString("song_artist_id");
      if(songArtistId != null) {
        Set<String> addedArtistIds = new HashSet<>();
        if(song.getArtists() != null)
          song.getArtists().forEach(artist -> addedArtistIds.add(artist.getArtistId()));
      
        if(!addedArtistIds.contains(songArtistId)) {
          Artist artist = new Artist();
          artist.setArtistId(songArtistId);
          artist.setName(rs.getString("song_artist_name"));
          song.getArtists().add(artist);
        }
      }
      
      String songGenreId = rs.getString("song_genre_id");
      if(songGenreId != null) {
        Set<String> addedGenreIds = new HashSet<>();
        if(song.getGenres() != null)
          song.getGenres().forEach(genre -> addedGenreIds.add(genre.getGenreId()));
        
        if(!addedGenreIds.contains(songGenreId)) {
          Genre genre = new Genre();
          genre.setGenreId(songGenreId);
          genre.setName(rs.getString("song_genre_name"));
          song.getGenres().add(genre);
        }
      }
    }
    
    return new ArrayList<>(songMap.values());
  }
  
  @Override
  public List<Song> getSongs() {
    List<Song> songs = new ArrayList<>();    
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_ALL);
    ) {
      ResultSet rs = ps.executeQuery();
      songs = mapRowToSongs(rs);
    } catch (SQLException ex) {
      System.out.println("Error al consultar canciones: " + ex.getMessage());
    }
    
    return songs;
  }
  
  @Override
  public Optional<Song> getSongById(String songId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ID);
    ) {
      ps.setString(1, songId);
      ResultSet rs = ps.executeQuery();
      List<Song> songs = mapRowToSongs(rs);
      if(!songs.isEmpty())
        return Optional.of(songs.get(0));
    } catch (SQLException ex) {
      System.out.println("Error al consultar cancion: " + ex.getMessage());
    }
    
    return Optional.empty();
  }
  
  @Override
  public List<Song> getSongsByTitle(String title) {
    List<Song> songs = new ArrayList<>();  
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_TITLE);
    ) {
      ps.setString(1, "%" + title +"%");
      ResultSet rs = ps.executeQuery();
      songs = mapRowToSongs(rs);
    } catch (SQLException ex) {
      System.out.println("Error al consultar cancion: " + ex.getMessage());
    }
    
    return songs;
  }
  
  @Override
  public List<Song> getSongsByArtist(String artistId) {
    List<Song> songs = new ArrayList<>();  
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ARTIST);
    ) {
      ps.setString(1, artistId);
      ResultSet rs = ps.executeQuery();
      songs = mapRowToSongs(rs);
    } catch (SQLException ex) {
      System.out.println("Error al consultar cancion: " + ex.getMessage());
    }
    
    return songs;
  }
  
  @Override
  public List<Song> getSongsByGenre(String genreId) {
    List<Song> songs = new ArrayList<>();  
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_GENRE);
    ) {
      ps.setString(1, genreId);
      ResultSet rs = ps.executeQuery();
      songs = mapRowToSongs(rs);
    } catch (SQLException ex) {
      System.out.println("Error al consultar cancion: " + ex.getMessage());
    }
    
    return songs;
  }
  
  @Override
  public List<Song> getSongsByAlbum(String albumId) {
    List<Song> songs = new ArrayList<>();  
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ALBUM);
    ) {
      ps.setString(1, albumId);
      ResultSet rs = ps.executeQuery();
      songs = mapRowToSongs(rs);
    } catch (SQLException ex) {
      System.out.println("Error al consultar cancion: " + ex.getMessage());
    }
    
    return songs;
  }
  
  @Override
  public List<Song> getSongsByArtistAndGenre(String artistId, String genreId) {
    List<Song> songs = new ArrayList<>();  
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ARTIST_AND_GENRE);
    ) {
      ps.setString(1, artistId);
      ps.setString(2, genreId);
      ResultSet rs = ps.executeQuery();
      songs = mapRowToSongs(rs);
    } catch (SQLException ex) {
      System.out.println("Error al consultar cancion: " + ex.getMessage());
    }
    
    return songs;
  }

  @Override
  public ApiResponse<Void> createSong(Song song) {    
    Connection con = null;
    try {
      con = DataContext.getConnection();
      con.setAutoCommit(false);
      
      try(PreparedStatement ps = con.prepareStatement(INSERT_SONG)) {
        ps.setString(1, song.getTitle());
        ps.setInt(2, song.getDurationSeconds());
        ps.setString(3, song.getAudioFilePath());
        ps.setBoolean(4, song.isExplicit());
        ps.setLong(5, song.getPlayCount());
        ps.setString(6, song.getAlbum() != null ? song.getAlbum().getAlbumId() : null);
        
        try(ResultSet rs = ps.executeQuery()) {
          if(rs.next()) song.setSongId(rs.getString("song_id"));
          else throw new SQLException("Failed to retrieve generated song_id.");
        }
      }
      
      if(song.getArtists() != null && !song.getArtists().isEmpty())
        for(Artist artist : song.getArtists())
          addArtistToSong(con, song.getSongId(), artist.getArtistId(), false);
      
      if(song.getGenres() != null && !song.getGenres().isEmpty())
        for(Genre genre : song.getGenres())
          addGenreToSong(con, song.getSongId(), genre.getGenreId());
      
      con.commit();
      return new ApiResponse<>(true, "Cancion insertada exitosamente");
    } catch (SQLException ex) {
      if(con != null)
        try {
          con.rollback();
        } catch (SQLException rbEx) {
          System.out.println("Error en rollback: " + rbEx.getMessage());
        }
      
      return new ApiResponse<>(false, "Error al insertar cancion: " + ex.getMessage());
    } finally {
      if(con != null)
        try {
          con.setAutoCommit(true);
          con.close();
        } catch (SQLException closeEx) {
          System.out.println("Error al cerrar conexion: " + closeEx.getMessage());
        }
    }
  }
  
  @Override
  public ApiResponse<Void> updateSong(Song song) {    
    Connection con = null;
    try {
      con = DataContext.getConnection();
      con.setAutoCommit(false);
      
      try(PreparedStatement ps = con.prepareStatement(UPDATE_SONG)) {
        ps.setString(1, song.getTitle());
        ps.setInt(2, song.getDurationSeconds());
        ps.setString(3, song.getAudioFilePath());
        ps.setBoolean(4, song.isExplicit());
        ps.setLong(5, song.getPlayCount());
        ps.setString(6, song.getAlbum() != null ? song.getAlbum().getAlbumId() : null);
        ps.setString(7, song.getSongId());
        ps.executeUpdate();
      }
      
      clearArtistsFromSong(con, song.getSongId());
      if(song.getArtists() != null && !song.getArtists().isEmpty())
        for(Artist artist : song.getArtists())
          addArtistToSong(con, song.getSongId(), artist.getArtistId(), false);
      
      clearGenresFromSong(con, song.getSongId());
      if(song.getGenres() != null && !song.getGenres().isEmpty())
        for(Genre genre : song.getGenres())
          addGenreToSong(con, song.getSongId(), genre.getGenreId());
      
      con.commit();
      return new ApiResponse<>(true, "Cancion actualizada exitosamente");
    } catch (SQLException ex) {
      if(con != null)
        try {
          con.rollback();
        } catch (SQLException rbEx) {
          System.out.println("Error en rollback: " + rbEx.getMessage());
        }

      return new ApiResponse<>(false, "Error al actualizar cancion: " + ex.getMessage());
    } finally {
      if(con != null)
        try {
          con.setAutoCommit(true);
          con.close();
        } catch (SQLException closeEx) {
          System.out.println("Error al cerrar conexion: " + closeEx.getMessage());
        }
    }
  }
  
  @Override
  public ApiResponse<Void> deleteSong(String songId) {
    Connection con = null;
    try {
      con = DataContext.getConnection();
      con.setAutoCommit(false);
      
      try(PreparedStatement ps = con.prepareStatement(DELETE_SONG)) {
        ps.setString(1, songId);
        int rows = ps.executeUpdate();
        con.commit();
        return new ApiResponse<>(
          rows > 0, rows > 0
            ? "Cancion eliminada exitosamente"
            : "No se encontro la cancion para eliminar"
        );
      }
    } catch (SQLException ex) {
      if(con != null)
        try {
          con.rollback();
        } catch (SQLException rbEx) {
          System.out.println("Error en rollback: " + rbEx.getMessage());
        }

      return new ApiResponse<>(false, "Error al actualizar cancion: " + ex.getMessage());
    } finally {
      if(con != null)
        try {
          con.setAutoCommit(true);
          con.close();
        } catch (SQLException closeEx) {
          System.out.println("Error al cerrar conexion: " + closeEx.getMessage());
        }
    }
  }
    
  private void addArtistToSong(Connection con, String songId, String artistId, boolean isPrimary) throws SQLException {
    try(PreparedStatement ps = con.prepareStatement(INSERT_SONG_ARTIST)) {
      ps.setString(1, songId);
      ps.setString(2, artistId);
      ps.setBoolean(3, isPrimary);
      ps.executeUpdate();
    }
  }
    
  private void clearArtistsFromSong(Connection con, String songId) throws SQLException {
    try(PreparedStatement ps = con.prepareStatement(DELETE_SONG_ARTISTS_BY_SONG)) {
      ps.setString(1, songId);
      ps.executeUpdate();
    }
  }
    
  private void addGenreToSong(Connection con, String songId, String genreId) throws SQLException {
    try(PreparedStatement ps = con.prepareStatement(INSERT_SONG_GENRE)) {
      ps.setString(1, songId);
      ps.setString(2, genreId);
      ps.executeUpdate();
    }
  }
    
  private void clearGenresFromSong(Connection con, String songId) throws SQLException {
    try(PreparedStatement ps = con.prepareStatement(DELETE_SONG_GENRES_BY_SONG)) {
      ps.setString(1, songId);
      ps.executeUpdate();
    }
  }
}

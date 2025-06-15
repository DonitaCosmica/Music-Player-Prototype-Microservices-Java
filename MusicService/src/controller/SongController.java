package controller;

import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.dao.CountryDAO;
import com.microservices.dao.GenreDAO;
import com.microservices.dao.SongDAO;
import com.microservices.interfaces.IGenericController;
import com.microservices.response.ApiResponse;
import interfaces.IAlbumRepository;
import interfaces.IArtistRepository;
import interfaces.IGenreRepository;
import model.Song;
import interfaces.ISongRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import model.Album;
import model.Artist;
import model.Country;
import model.Genre;
import repository.AlbumRepository;
import repository.ArtistRepository;
import repository.GenreRepository;
import repository.SongRepository;

public class SongController implements IGenericController<SongDAO> {
  private final ISongRepository songRepository = new SongRepository();
  private final IAlbumRepository albumRepository = new AlbumRepository();
  private final IArtistRepository artistRepository = new ArtistRepository();
  private final IGenreRepository genreRepository = new GenreRepository();
  
  @Override
  public ApiResponse<List<SongDAO>> getAll() {
    List<SongDAO> songs = songRepository.getSongs()
      .stream().map(this::toDAO)
      .collect(Collectors.toList());

    return new ApiResponse<>(
      true, songs.isEmpty()
        ? "No hay canciones registradas"
        : "Lista de canciones obtenida exitosamente",
      songs
    );
  }
  
  @Override
  public ApiResponse<SongDAO> getById(String songId) {
    return songRepository.getSongById(songId)
      .map(song -> new ApiResponse<>(true, "Cancion encontrada", toDAO(song)))
      .orElse(new ApiResponse<>(false, "Cancion no encontrada"));
  }
  
  @Override
  public ApiResponse<List<SongDAO>> getByParams(Map<String, String> params) {
    List<Song> songs;
    
    if(params.containsKey("title") && params.size() == 1)
      songs = songRepository.getSongsByTitle(params.get("name"));
    else if(params.containsKey("artistId") && params.size() == 1)
      songs = songRepository.getSongsByArtist(params.get("artistId"));
    else if(params.containsKey("genreId") && params.size() == 1)
      songs = songRepository.getSongsByGenre(params.get("genreId"));
    else if(params.containsKey("name") && params.size() == 1)
      songs = songRepository.getSongsByAlbum(params.get("albumId"));
    else if(params.containsKey("genreId") && params.containsKey("artistId") && params.size() == 2)
      songs = songRepository.getSongsByArtistAndGenre(params.get("genre"), params.get("artist"));
    else
      return new ApiResponse<>(false, "Parametros de busqueda no soportados o incompletos", new ArrayList<>());
    
    List<SongDAO> result = songs.stream().map(this::toDAO).collect(Collectors.toList());
    return new ApiResponse<>(true, "Filtrado exitoso", result);
  }
  
  @Override
  public ApiResponse create(SongDAO songDAO) {
    Optional<Album> albumOpt = validateAlbum(songDAO.getAlbum());
    if(songDAO.getAlbum() != null && albumOpt.isEmpty())
      return new ApiResponse<>(false, "El album especificado no existe");
    
    List<Artist> artists = new ArrayList<>();
    if(songDAO.getArtists() != null)
      for(ArtistDAO artistDAO : songDAO.getArtists()) {
        Optional<Artist> artistOpt = artistRepository.getArtistById(artistDAO.getArtistId());
        if(artistOpt.isEmpty())
          return new ApiResponse<>(false, "Uno o mas artistas especificados no existen");
      
        artists.add(artistOpt.get());
      }
    
    List<Genre> genres = new ArrayList<>();
    if(songDAO.getGenres()!= null)
      for(GenreDAO genreDAO : songDAO.getGenres()) {
        Optional<Genre> genreOpt = genreRepository.getGenreById(genreDAO.getGenreId());
        if(genreOpt.isEmpty())
          return new ApiResponse<>(false, "Uno o mas artistas especificados no existen");
      
        genres.add(genreOpt.get());
      }
    
    Song song = toModel(songDAO, albumOpt.orElse(null), artists, genres);
    return songRepository.createSong(song);
  }
  
  @Override
  public ApiResponse update(SongDAO songDAO) {
    return songRepository.getSongById(songDAO.getSongId())
      .map(existingSong -> {
        Optional<Album> albumOpt = validateAlbum(songDAO.getAlbum());
        if(songDAO.getAlbum() != null && albumOpt.isEmpty())
          return new ApiResponse<>(false, "El album especificado no existe para la actualizacion");
        
        existingSong.setAlbum(albumOpt.orElse(null));
        List<Artist> artists = new ArrayList<>();
        if(songDAO.getArtists() != null)
          for(ArtistDAO artistDAO : songDAO.getArtists()) {
            Optional<Artist> artistOpt = artistRepository.getArtistById(artistDAO.getArtistId());
            if(artistOpt.isEmpty())
              return new ApiResponse<>(false, "Uno o mas artistas especificados no existen");

            artists.add(artistOpt.get());
          }

        existingSong.setArtists(artists);
        List<Genre> genres = new ArrayList<>();
        if(songDAO.getGenres()!= null)
          for(GenreDAO genreDAO : songDAO.getGenres()) {
            Optional<Genre> genreOpt = genreRepository.getGenreById(genreDAO.getGenreId());
            if(genreOpt.isEmpty())
              return new ApiResponse<>(false, "Uno o mas artistas especificados no existen");

            genres.add(genreOpt.get());
          }
        
        existingSong.setGenres(genres);
        existingSong.setTitle(songDAO.getTitle());
        existingSong.setDurationSeconds(songDAO.getDurationSeconds());
        existingSong.setAudioFilePath(songDAO.getAudioFilePath());
        existingSong.setExplicit(songDAO.isExplicit());
        existingSong.setPlayCount(songDAO.getPlayCount());
        
        return songRepository.updateSong(existingSong);
      }).orElse(new ApiResponse<>(false, "Cancion inexistente"));
  }
  
  @Override
  public ApiResponse delete(String songId) {
    return songRepository.getSongById(songId)
      .map(song -> songRepository.deleteSong(songId))
      .orElse(new ApiResponse<>(false, "Cancion inexistente"));
  }
  
  private SongDAO toDAO(Song song) {
    SongDAO songDAO = new SongDAO();
    songDAO.setSongId(song.getSongId());
    songDAO.setTitle(song.getTitle());
    songDAO.setDurationSeconds(song.getDurationSeconds());
    songDAO.setAudioFilePath(song.getAudioFilePath());
    songDAO.setExplicit(song.isExplicit());
    songDAO.setPlayCount(song.getPlayCount());

    if (song.getAlbum() != null)
      songDAO.setAlbum(toAlbumDAO(song.getAlbum()));

    if (song.getArtists() != null)
      songDAO.setArtists(song.getArtists().stream()
        .map(this::toArtistDAO)
        .collect(Collectors.toList()));

    if (song.getGenres() != null)
      songDAO.setGenres(song.getGenres().stream()
        .map(this::toGenreDAO)
        .collect(Collectors.toList()));
    
    return songDAO;
  }
  
  private AlbumDAO toAlbumDAO(Album album) {
    if(album == null) return null;
    AlbumDAO albumDAO = new AlbumDAO();
    albumDAO.setAlbumId(album.getAlbumId());
    albumDAO.setTitle(album.getTitle());
    albumDAO.setReleaseDate(album.getReleaseDate());
    albumDAO.setCoverArtUrl(album.getCoverArtUrl());
    if (album.getPrimaryArtist() != null)
      albumDAO.setPrimaryArtist(toArtistDAO(album.getPrimaryArtist()));

    albumDAO.setTotalTracks(album.getTotalTracks());
    albumDAO.setRecordLabel(album.getRecordLabel());
    return albumDAO;
  }
  
  private ArtistDAO toArtistDAO(Artist artist) {
    if(artist == null) return null;
    ArtistDAO artistDAO = new ArtistDAO();
    artistDAO.setArtistId(artist.getArtistId());
    artistDAO.setName(artist.getName());
    artistDAO.setBio(artist.getBio());
    artistDAO.setImageUrl(artist.getImageUrl());
    if(artist.getOriginCountry() != null)
      artistDAO.setOriginCountry(toCountryDAO(artist.getOriginCountry()));
  
    return artistDAO;
  }
  
  private GenreDAO toGenreDAO(Genre genre) {
    if (genre == null) return null;
    GenreDAO genreDAO = new GenreDAO();
    genreDAO.setGenreId(genre.getGenreId());
    genreDAO.setName(genre.getName());
    genreDAO.setDescription(genre.getDescription());
    return genreDAO;
  }
  
  private CountryDAO toCountryDAO(Country country) {
    if (country == null) return null;
    CountryDAO countryDAO = new CountryDAO();
    countryDAO.setCountryId(country.getCountryId());
    countryDAO.setName(country.getName());
    countryDAO.setIsoCode2(country.getIsoCode2());
    return countryDAO;
  }
  
  private Song toModel(SongDAO songDAO, Album album, List<Artist> artists, List<Genre> genres) {
    Song song = new Song();
    if (songDAO.getSongId() != null)
      song.setSongId(songDAO.getSongId());

    song.setTitle(songDAO.getTitle());
    song.setDurationSeconds(songDAO.getDurationSeconds());
    song.setAudioFilePath(songDAO.getAudioFilePath());
    song.setExplicit(songDAO.isExplicit());
    song.setPlayCount(songDAO.getPlayCount());
    song.setAlbum(album);
    song.setArtists(artists);
    song.setGenres(genres);

    return song;
  }
  
  private Optional<Album> validateAlbum(AlbumDAO albumDAO) {
    if (albumDAO == null || albumDAO.getAlbumId() == null)
      return Optional.empty();

    return albumRepository.getAlbumById(albumDAO.getAlbumId());
  }
}

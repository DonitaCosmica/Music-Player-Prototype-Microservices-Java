package controller;

import com.microservices.dao.AlbumDAO;
import com.microservices.dao.ArtistDAO;
import com.microservices.dao.SongDAO;
import com.microservices.interfaces.IGenericController;
import com.microservices.response.ApiResponse;
import interfaces.IAlbumRepository;
import interfaces.IArtistRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import model.Album;
import model.Artist;
import model.Song;
import repository.AlbumRepository;
import repository.ArtistRepository;

public class AlbumController implements IGenericController<AlbumDAO> {
  private final IAlbumRepository albumRepository = new AlbumRepository();
  private final IArtistRepository artistRepository = new ArtistRepository();
  
  @Override
  public ApiResponse<List<AlbumDAO>> getAll() {
    List<AlbumDAO> albums = albumRepository.getAlbums()
      .stream().map(this::toDAO)
      .collect(Collectors.toList());
    
    return new ApiResponse<>(
      true, albums.isEmpty()
        ? "No hay albumes registrados"
        : "Lista de albumes obtenidos exitosamente",
      albums
    );
  }
  
  @Override
  public ApiResponse<AlbumDAO> getById(String albumId) {
    return albumRepository.getAlbumById(albumId)
      .map(album -> new ApiResponse<>(true, "Album encontrado", toDAO(album)))
      .orElse(new ApiResponse<>(false, "Album no encontrado"));
  }
  
  @Override
  public ApiResponse<List<AlbumDAO>> getByParams(Map<String, String> params) {
    List<Album> albums;
    
    if(params.containsKey("title") && params.size() == 1)
      albums = albumRepository.getAlbumsByTitle(params.get("title"));
    else if(params.containsKey("artistId") && params.size() == 1)
      albums = albumRepository.getAlbumsByArtist(params.get("artistId"));
    else
      return new ApiResponse<>(false, "Parametros de busqueda no soportados o incompletos", new ArrayList<>());
    
    List<AlbumDAO> result = albums.stream().map(this::toDAO).collect(Collectors.toList());
    return new ApiResponse<>(true, "Filtrado exitoso", result);
  }
  
  @Override
  public ApiResponse create(AlbumDAO albumDAO) {
    Optional<Artist> primaryArtistOpt = Optional.empty();
    if(albumDAO.getPrimaryArtist() != null && albumDAO.getPrimaryArtist().getArtistId() != null) {
      primaryArtistOpt = artistRepository.getArtistById(albumDAO.getPrimaryArtist().getArtistId());
      if(primaryArtistOpt.isEmpty())
        return new ApiResponse<>(false, "El artista principal especificado no existe");
    }
    
    Album album = toModel(albumDAO, primaryArtistOpt.orElse(null));
    return albumRepository.createAlbum(album);
  }
  
  @Override
  public ApiResponse update(AlbumDAO albumDAO) {
    return albumRepository.getAlbumById(albumDAO.getAlbumId())
      .map(existinAlbum -> {
        Optional<Artist> primaryArtistOpt;
        if(albumDAO.getPrimaryArtist() != null && albumDAO.getPrimaryArtist().getArtistId() != null) {
          primaryArtistOpt = artistRepository.getArtistById(albumDAO.getPrimaryArtist().getArtistId());
          if(primaryArtistOpt.isEmpty())
            return new ApiResponse<>(false, "El artista principal especificado para la actualizacion no existe");
          
          existinAlbum.setPrimaryArtist(primaryArtistOpt.get());
        } else
          existinAlbum.setPrimaryArtist(null);
        
        existinAlbum.setTitle(albumDAO.getTitle());
        existinAlbum.setReleaseDate(albumDAO.getReleaseDate());
        existinAlbum.setTotalTracks(albumDAO.getTotalTracks());
        existinAlbum.setRecordLabel(albumDAO.getRecordLabel());        
        return albumRepository.updateAlbum(existinAlbum);
      }).orElse(new ApiResponse<>(false, "Album inexistente"));
  }
  
  @Override
  public ApiResponse delete(String albumId) {
    return albumRepository.getAlbumById(albumId)
      .map(album -> albumRepository.deleteAlbum(albumId))
      .orElse(new ApiResponse<>(false, "Album inexistente"));
  }
  
  private AlbumDAO toDAO(Album album) {
    AlbumDAO albumDAO = new AlbumDAO();
    albumDAO.setAlbumId(album.getAlbumId());
    albumDAO.setTitle(album.getTitle());
    albumDAO.setReleaseDate(album.getReleaseDate());
    albumDAO.setTotalTracks(album.getTotalTracks());
    albumDAO.setRecordLabel(album.getRecordLabel());    
    if(album.getPrimaryArtist() != null)
      albumDAO.setPrimaryArtist(toArtistDAO(album.getPrimaryArtist()));
    
    if(album.getSongs() != null && !album.getSongs().isEmpty())
      albumDAO.setSongs(album.getSongs().stream()
        .map(this::toSongDAO).collect(Collectors.toList()));
    
    return albumDAO;
  }
  
  private ArtistDAO toArtistDAO(Artist artist) {
    if(artist == null) return null;
    ArtistDAO artistDAO = new ArtistDAO();
    artistDAO.setArtistId(artist.getArtistId());
    artistDAO.setName(artist.getName());
    artistDAO.setBio(artist.getBio());
    artistDAO.setImageUrl(artist.getImageUrl());
    return artistDAO;
  }
  
  private SongDAO toSongDAO(Song song) {
    if (song == null) return null;
    SongDAO songDAO = new SongDAO();
    songDAO.setSongId(song.getSongId());
    songDAO.setTitle(song.getTitle());
    songDAO.setDurationSeconds(song.getDurationSeconds());
    songDAO.setAudioFilePath(song.getAudioFilePath());
    songDAO.setExplicit(song.isExplicit());
    songDAO.setPlayCount(song.getPlayCount());
    return songDAO;
  }
  
  private Album toModel(AlbumDAO albumDAO, Artist primaryArtist) {
    Album album = new Album();
    if(albumDAO.getAlbumId() != null)
      album.setAlbumId(albumDAO.getAlbumId());
    
    album.setTitle(albumDAO.getTitle());
    album.setReleaseDate(albumDAO.getReleaseDate());
    album.setTotalTracks(albumDAO.getTotalTracks());
    album.setRecordLabel(albumDAO.getRecordLabel());
    album.setPrimaryArtist(primaryArtist);
    return album;
  }
}

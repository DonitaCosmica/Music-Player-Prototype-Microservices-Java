package controller;

import com.microservices.dao.ArtistDAO;
import com.microservices.dao.CountryDAO;
import com.microservices.interfaces.IGenericController;
import com.microservices.response.ApiResponse;
import interfaces.IArtistRepository;
import interfaces.ICountryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Artist;
import repository.ArtistRepository;
import java.util.stream.Collectors;
import model.Country;
import repository.CountryRepository;

public class ArtistController implements IGenericController<ArtistDAO> {
  private final IArtistRepository artistRepository = new ArtistRepository();
  private final ICountryRepository countryRepository = new CountryRepository();
  
  @Override
  public ApiResponse<List<ArtistDAO>> getAll() {
    List<ArtistDAO> artists = artistRepository.getArtists()
      .stream().map(this::toDAO)
      .collect(Collectors.toList());
    
    return new ApiResponse<>(
      true, artists.isEmpty()
        ? "No hay artistas registrados"
        : "Lista de artistas obtenidas exitosamente",
      artists
    );
  }
  
  @Override
  public ApiResponse<ArtistDAO> getById(String artistId) {
    return artistRepository.getArtistById(artistId)
      .map(artist -> new ApiResponse<>(true, "Artista encontrado", toDAO(artist)))
      .orElse(new ApiResponse<>(false, "Artista no encontrado"));
  }
  
  @Override
  public ApiResponse<List<ArtistDAO>> getByParams(Map<String, String> params) {
    List<Artist> artists;
    
    if(params.containsKey("name") && params.size() == 1)
      artists = artistRepository.getArtistsByName(params.get("name"));
    else if(params.containsKey("countryId") && params.size() == 1)
      artists = artistRepository.getArtistsByCountry(params.get("countryId"));
    else
      return new ApiResponse<>(false, "Parametros de busqueda no soportados o incompletos", new ArrayList<>());
    
    List<ArtistDAO> result = artists.stream().map(this::toDAO).collect(Collectors.toList());
    return new ApiResponse<>(true, "Filtrado exitoso", result);
  }
  
  @Override
  public ApiResponse create(ArtistDAO artistDAO) {
    Optional<Country> originCountryOpt = Optional.empty();
    if(artistDAO.getOriginCountry() != null && artistDAO.getOriginCountry().getCountryId() != null) {
      originCountryOpt = countryRepository.getCountryById(artistDAO.getOriginCountry().getCountryId());
      if(originCountryOpt.isEmpty())
        return new ApiResponse<>(false, "El país de origen especificado no existe.");
    }
  
    Artist artist = toModel(artistDAO, originCountryOpt.orElse(null));
    return artistRepository.createArtist(artist);
  }
  
  @Override
  public ApiResponse update(ArtistDAO artistDAO) {
    return artistRepository.getArtistById(artistDAO.getArtistId())
      .map(existingArtist -> {
        existingArtist.setName(artistDAO.getName());
        existingArtist.setBio(artistDAO.getBio());
        existingArtist.setImageUrl(artistDAO.getImageUrl());
        if(artistDAO.getOriginCountry() != null && artistDAO.getOriginCountry().getCountryId() != null) {
          Optional<Country> countryOpt = countryRepository.getCountryById(artistDAO.getOriginCountry().getCountryId());
          if (countryOpt.isPresent()) existingArtist.setOriginCountry(countryOpt.get());
          else return new ApiResponse<>(false, "El país de origen especificado para la actualización no existe.");
        } else
          existingArtist.setOriginCountry(null);
        
        return artistRepository.updateArtist(existingArtist);
      }).orElse(new ApiResponse<>(false, "Artista inexistente"));
  }
  
  @Override
  public ApiResponse delete(String artistId) {
    return artistRepository.getArtistById(artistId)
      .map(artist -> artistRepository.deleteArtist(artistId))
      .orElse(new ApiResponse<>(false, "Artista inexistente"));
  }
  
  private ArtistDAO toDAO(Artist artist) {
    ArtistDAO artistDAO = new ArtistDAO();
    artistDAO.setArtistId(artist.getArtistId());
    artistDAO.setName(artist.getName());
    artistDAO.setBio(artist.getBio());
    artistDAO.setImageUrl(artist.getImageUrl());
    if(artist.getOriginCountry() != null)
      artistDAO.setOriginCountry(toCountryDAO(artist.getOriginCountry()));
    
    return artistDAO;
  }
  
  private CountryDAO toCountryDAO(Country country) {
    if(country == null) return null;
    CountryDAO countryDAO = new CountryDAO();
    countryDAO.setCountryId(country.getCountryId());
    countryDAO.setName(country.getName());
    countryDAO.setIsoCode2(country.getIsoCode2());
    return countryDAO;
  }
  
  private Artist toModel(ArtistDAO artistDAO, Country originCountry) {
    Artist artist = new Artist();
    if(artistDAO.getArtistId() != null)
      artist.setArtistId(artistDAO.getArtistId());
    
    artist.setName(artistDAO.getName());
    artist.setBio(artistDAO.getBio());
    artist.setImageUrl(artistDAO.getImageUrl());
    artist.setOriginCountry(originCountry);
    return artist;
  }
}

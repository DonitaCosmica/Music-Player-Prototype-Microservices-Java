package controller;

import com.microservices.dao.CountryDAO;
import com.microservices.interfaces.IGenericController;
import com.microservices.response.ApiResponse;
import interfaces.ICountryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.Country;
import repository.CountryRepository;
import utils.IsoCodeLookupService;

public class CountryController implements IGenericController<CountryDAO> {
  private final ICountryRepository countryRepository = new CountryRepository();
  
  public CountryController() {
    IsoCodeLookupService.initialize();
  }
  
  @Override
  public ApiResponse<List<CountryDAO>> getAll() {
    List<CountryDAO> countries = countryRepository.getCountries()
      .stream().map(this::toDAO)
      .collect(Collectors.toList());
    
    return new ApiResponse<>(
      true, countries.isEmpty()
        ? "No hay paises registrados"
        : "Lista de paises obtenida exitosamente",
      countries
    );
  }
  
  @Override
  public ApiResponse<CountryDAO> getById(String countryId) {
    return countryRepository.getCountryById(countryId)
      .map(country -> new ApiResponse<>(true, "Pais encontrado", toDAO(country)))
      .orElse(new ApiResponse<>(false, "Pais no encontrado"));
  }
    
  @Override
  public ApiResponse<List<CountryDAO>> getByParams(Map<String, String> params) {
    List<Country> countries = new ArrayList<>();
      
    if(params.containsKey("name") && params.size() == 1)
      countryRepository.getCountriesByName(params.get("name")).ifPresent(countries::add);
    else if(params.containsKey("isoCode2") && params.size() == 1)
      countries = countryRepository.getCountriesByIsoCode2(params.get("isoCode2"));
    else
      return new ApiResponse<>(false, "Parametros de busqueda no soportados o incompletos", new ArrayList<>());
    
    List<CountryDAO> result = countries.stream().map(this::toDAO).collect(Collectors.toList());
    return new ApiResponse<>(true, "Filtrado exitoso", result);
  }
  
  @Override
  public ApiResponse<Void> create(CountryDAO countryDAO) {
    Country country = toModel(countryDAO);
    return countryRepository.createCountry(country);
  }
  
  @Override
  public ApiResponse<Void> update(CountryDAO countryDAO) {
    return countryRepository.getCountryById(countryDAO.getCountryId())
      .map(existingCountry -> {
        existingCountry.setName(countryDAO.getName());
        existingCountry.setIsoCode2(countryDAO.getIsoCode2());
        return countryRepository.updateCountry(existingCountry);
      }).orElse(new ApiResponse<>(false, "Pais inexistente"));
  }
  
  @Override
  public ApiResponse<Void> delete(String countryId) {
    return countryRepository.getCountryById(countryId)
      .map(country -> countryRepository.deleteCountry(countryId))
      .orElse(new ApiResponse<>(false, "Pais inexistente"));
  }
  
  private CountryDAO toDAO(Country country) {
    CountryDAO countryDAO = new CountryDAO();
    countryDAO.setCountryId(country.getCountryId());
    countryDAO.setName(country.getName());
    countryDAO.setIsoCode2(country.getIsoCode2());
    return countryDAO;
  }
  
  private Country toModel(CountryDAO countryDAO) {
    Country country = new Country();
    if(countryDAO.getCountryId() != null)
      country.setCountryId(countryDAO.getCountryId());
    
    String isoCode3FromLookup = IsoCodeLookupService.getIsoCode3FromIsoCode2(countryDAO.getIsoCode2());
    country.setName(countryDAO.getName());
    country.setIsoCode2(countryDAO.getIsoCode2());
    country.setIsoCode3(isoCode3FromLookup);
    return country;
  }
}

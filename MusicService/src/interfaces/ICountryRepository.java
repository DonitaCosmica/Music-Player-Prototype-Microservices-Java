package interfaces;

import com.microservices.response.ApiResponse;
import java.util.List;
import java.util.Optional;
import model.Country;

public interface ICountryRepository {
  List<Country> getCountries();
  Optional<Country> getCountryById(String countryId);
  Optional<Country> getCountriesByName(String name);
  List<Country> getCountriesByIsoCode2(String isoCode2);
  ApiResponse<Void> createCountry(Country country);
  ApiResponse<Void> updateCountry(Country country);
  ApiResponse<Void> deleteCountry(String countryId);
}

package repository;

import com.microservices.response.ApiResponse;
import context.DataContext;
import interfaces.ICountryRepository;
import model.Country;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryRepository implements ICountryRepository {
  private static final String SELECT_ALL = "SELECT * FROM Country";
  private static final String SELECT_BY_ID = "SELECT * FROM Country WHERE country_id = CAST(? AS UUID)";
  private static final String SELECT_BY_NAME = "SELECT * FROM Country WHERE LOWER(name) LIKE LOWER(?)";
  private static final String SELECT_BY_ISO_CODE2 = "SELECT * FROM Country WHERE iso_code2 = ?";
  private static final String INSERT = "INSERT INTO Country (name, iso_code2, iso_code3) VALUES (?, ?, ?)";
  private static final String UPDATE = "UPDATE Country SET name = ?, iso_code2 = ?, iso_code3 = ? WHERE country_id = CAST(? AS UUID)";
  private static final String DELETE = "DELETE FROM Country WHERE country_id = CAST(? AS UUID)";
  
  private Country mapRow(ResultSet rs) throws SQLException {
    Country country = new Country();
    country.setCountryId(rs.getString("country_id"));
    country.setName(rs.getString("name"));
    country.setIsoCode2(rs.getString("iso_code2"));
    country.setIsoCode3(rs.getString("iso_code3"));
    return country;
  }
  
  @Override
  public List<Country> getCountries() {
    List<Country> countries = new ArrayList<>();
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_ALL);
    ) {
      ResultSet rs = ps.executeQuery();
      while(rs.next()) countries.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar paises: " + ex.getMessage());
    }
    
    return countries;
  }
  
  @Override
  public Optional<Country> getCountryById(String countryId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ID);
    ) {
      ps.setString(1, countryId);
      ResultSet rs = ps.executeQuery();
      if(rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar pais: " + ex.getMessage());
    }
    
    return Optional.empty();
  }
  
  @Override
  public Optional<Country> getCountriesByName(String name) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_NAME);
    ) {
      ps.setString(1, "%" + name + "%");
      ResultSet rs = ps.executeQuery();
      if(rs.next()) return Optional.of(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar pais: " + ex.getMessage());
    }
    
    return Optional.empty();
  }
  
  @Override
  public List<Country> getCountriesByIsoCode2(String isoCode2) {
    List<Country> countries = new ArrayList<>();
    try (
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(SELECT_BY_ISO_CODE2);
    ) {
      ps.setString(1, isoCode2);
      ResultSet rs = ps.executeQuery();
      while(rs.next()) countries.add(mapRow(rs));
    } catch (SQLException ex) {
      System.out.println("Error al consultar pais por ISO Code 2: " + ex.getMessage());
    }
    
    return countries;
  }
  
  @Override
  public ApiResponse<Void> createCountry(Country country) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(INSERT);
    ) {
      ps.setString(1, country.getName());
      ps.setString(2, country.getIsoCode2());
      ps.setString(3, country.getIsoCode3());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Pais insertado exitosamente"
          : "No se inserto ningun pais"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al insertar pais: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> updateCountry(Country country) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(UPDATE); 
    ) {
      ps.setString(1, country.getName());
      ps.setString(2, country.getIsoCode2());
      ps.setString(3, country.getIsoCode3());
      ps.setString(4, country.getCountryId());
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Pais actualizado exitosamente"
          : "No se actualizo ningun pais"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al actualizar pais: " + ex.getMessage());
    }
  }
  
  @Override
  public ApiResponse<Void> deleteCountry(String countryId) {
    try(
      Connection con = DataContext.getConnection();
      PreparedStatement ps = con.prepareStatement(DELETE);  
    ) {
      ps.setString(1, countryId);
      int rows = ps.executeUpdate();
      return new ApiResponse<>(
        rows > 0, rows > 0
          ? "Pais eliminado exitosamente"
          : "No se elimino ningun pais"
      );
    } catch (SQLException ex) {
      return new ApiResponse(false, "Error al eliminar pais: " + ex.getMessage());
    }
  }
}

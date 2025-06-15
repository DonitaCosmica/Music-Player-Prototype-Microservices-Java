package model;

import java.util.List;

public class Country {
  private String countryId;
  private String name;
  private String isoCode2;
  private String isoCode3;
  private List<Artist> artists;

  public String getCountryId() { return countryId; }
  public void setCountryId(String countryId) { this.countryId = countryId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getIsoCode2() { return isoCode2; }
  public void setIsoCode2(String isoCode2) { this.isoCode2 = isoCode2; }

  public String getIsoCode3() { return isoCode3; }
  public void setIsoCode3(String isoCode3) { this.isoCode3 = isoCode3; }

  public List<Artist> getArtists() { return artists; }
  public void setArtists(List<Artist> artists) { this.artists = artists; }
  
  @Override
  public String toString() {
    return "Country{" +
      "countryId='" + countryId + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}

package com.microservices.dao;

public class CountryDAO {
  private String countryId;
  private String name;
  private String isoCode2;

  public String getCountryId() { return countryId; }
  public void setCountryId(String genreId) { this.countryId = genreId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getIsoCode2() { return isoCode2; }
  public void setIsoCode2(String isoCode2) { this.isoCode2 = isoCode2; }
}

package com.microservices.dao;

public class ArtistDAO {
  private String artistId;
  private String name;
  private String bio;
  private String imageUrl;
  private CountryDAO originCountry;

  public String getArtistId() { return artistId; }
  public void setArtistId(String artistId) { this.artistId = artistId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getBio() { return bio; }
  public void setBio(String bio) { this.bio = bio; }

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  
  public CountryDAO getOriginCountry() { return originCountry; }
  public void setOriginCountry(CountryDAO originCountry) { this.originCountry = originCountry; }
  
  @Override
  public String toString() {
    return name;
  }
}

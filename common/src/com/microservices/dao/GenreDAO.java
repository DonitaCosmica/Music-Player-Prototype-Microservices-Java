package com.microservices.dao;

public class GenreDAO {
  private String genreId;
  private String name;
  private String description;
  
  public GenreDAO() {}

  public String getGenreId() { return genreId; }
  public void setGenreId(String genreId) { this.genreId = genreId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  @Override
  public String toString() {
    return name;
  }
}

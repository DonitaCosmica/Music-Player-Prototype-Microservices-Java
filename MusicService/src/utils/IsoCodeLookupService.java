package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IsoCodeLookupService {
  private static final Map<String, String> ISO2_TO_ISO3_MAP = new HashMap<>();
  private static final Map<String, String> NAME_TO_ISO3_MAP = new HashMap<>();
  private static final Gson gson = new Gson(); // Objeto Gson

  static {
    try {
      InputStream is = IsoCodeLookupService.class.getClassLoader().getResourceAsStream("countries.json");
      if (is == null)
        throw new IOException("Archivo countries.json no encontrado en src/main/resources");

      try (Reader reader = new InputStreamReader(is)) {
        Type countryListType = new TypeToken<List<Map<String, String>>>() {}.getType();
        List<Map<String, String>> countryDataList = gson.fromJson(reader, countryListType);

        for (Map<String, String> countryData : countryDataList) {
          String iso2 = countryData.get("isoCode2");
          String iso3 = countryData.get("isoCode3");
          String name = countryData.get("name");

          if (iso2 != null && iso3 != null)
            ISO2_TO_ISO3_MAP.put(iso2.toUpperCase(), iso3.toUpperCase());

          if (name != null && iso3 != null)
            NAME_TO_ISO3_MAP.put(name.toLowerCase(), iso3.toUpperCase());
        }
        
        System.out.println("Countries data loaded successfully using Gson. Total entries: " + ISO2_TO_ISO3_MAP.size());
      }
    } catch (IOException e) {
      System.err.println("Error loading countries.json: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Failed to load country ISO codes.", e);
    }
  }

  public static String getIsoCode3FromIsoCode2(String isoCode2) {
    if (isoCode2 == null) return null;
    return ISO2_TO_ISO3_MAP.get(isoCode2.toUpperCase());
  }

  public static String getIsoCode3FromCountryName(String countryName) {
    if (countryName == null) return null;
    return NAME_TO_ISO3_MAP.get(countryName.toLowerCase());
  }

  public static void initialize() {
    System.out.println("IsoCodeLookupService initialized with Gson.");
  }
}
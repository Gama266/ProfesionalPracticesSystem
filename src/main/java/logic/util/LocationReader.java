/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import logic.businessObject.Country;

/**
 * @author Gamal
 */
public class LocationReader {
    
    public static List<Country> loadLocations() {
        
        try (Reader jsonFileReader = new InputStreamReader(
                LocationReader.class.getResourceAsStream("/countries+states_es.json"), 
                StandardCharsets.UTF_8)) {
            
            Gson gsonParser = new Gson();
            Type listOfCountriesType = new TypeToken<ArrayList<Country>>(){}.getType();
            
            return gsonParser.fromJson(jsonFileReader, listOfCountriesType);
            
        } catch (Exception fileReadException) {
            //agregar excepcion despuecito
            return new ArrayList<>();
            
        }
    }
}
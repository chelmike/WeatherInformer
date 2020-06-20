package ru.chelmike.weatherinformer.entities;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * This is a basic implementation of cities list for supporting city selection for weather request
 *
 * @author Michael Ostrovsky
 */
@Component
public class CityList {
    private static List<City> cities = Arrays.asList(
            new City("Chelyabinsk", "Челябинск", "Челябинске", 55.1540, 61.4291),
            new City("Moscow", "Москва", "Москве", 37.62, 55.75),
            new City("Saint Petersburg", "Санкт-Петербург", "Санкт-Петербурге", 30.26, 59.89)
    );

    /**
     * Returns city by initial letters of city name (english).
     * In not found, the first one is returned
     *
     * @param nameStartsWithEn Initial part of city name
     * @return {@code City} object
     */
    public static City getCity(String nameStartsWithEn) {
        for (City c : cities) {
            if (c.getNameEn().startsWith(nameStartsWithEn))
                return c;
        }
        return cities.get(0);
    }

    public List<City> getCities() {
        return cities;
    }
}

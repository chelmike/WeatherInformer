package ru.chelmike.weatherinformer.meteo;

import ru.chelmike.weatherinformer.entities.City;

/**
 * Weather service common interface
 *
 * @author Michael Ostrovsky
 */
public interface MeteoInformer {

    // Date and time of request
    String getDatetimeQueried();

    String getDatetimeQueriedHuman();

    String getLastError();
    String getLastErrorDetails();

    /**
     * Get weather data. Request if not already done
     * @return
     */
    MeteoData getData();

    /**
     * Requests weather data. Results are cached using a combination of city, timestamp (whose accuracy
     * defines a period of caching) and service name.
     *
     * @param city      {@code City} for which weather data is requested
     * @param timestamp string in format i.g. {@code "YYYYMMDDyy"} for caching the received results for given
     *                  city and informer for a time period that is equal to this timestamp accuracy
     *                  (for {@code "YYYYMMDDyy"} - one minute, for {@code "YYYYMMDD"} - one day etc.)
     * @return
     */
    MeteoData requestWeatherData(City city, String timestamp, String informerName);

    void setCity(City city);

    String getName();

    String getHomeURL();

    City getCity();
}

package ru.chelmike.weatherinformer.meteo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.chelmike.weatherinformer.entities.City;

/**
 * Implements a weather request to GridForecast service (gridforecast.com)
 * API: https://gridforecast.com/api
 *
 * @author Michael Ostrovsky
 */
@Component("GFInformer")
public class GFInformer extends MeteoInformerHTTP {

    @Autowired
    public GFInformer(@Value("${GFInformer.URLTemplate}") String URLTemplate, @Value("${GFInformer.APIToken}") String APIToken) {
        super(URLTemplate, GFJsonStruct.class);
        setParam("APIToken", APIToken);
        setParam("timestamp", getUTCTimeStamp());
    }

    /**
     * A POJO-class for converting data from JSON to an object
     * Corresponds to a structure of OpenWeatherMap JSON-response
     * (only basic parameters are taken for example)
     */
    static class GFJsonStruct extends JsonStruct {
        double t;
        double aptmp;
        double sp;
        int r;

        @Override
        public double getCurrentTemperature() { return t; }

        @Override
        public double getCurrentFeelsLike() { return aptmp; }

        @Override
        public double getCurrentPressure() { return sp; }

        @Override
        public int getCurrentHumidity() { return r; }

        @Override
        public String getTemperatureUnits() {
            return "°C";
        }

        @Override
        public String getPressureUnits() {
            return "Pa";
        }

        @Override
        public String getHumidityUnits() {
            return "%";
        }
    }

    @Override
    public String getName() {
        return "GridForecast";
    }

    @Override
    public String getHomeURL() {
        return "gridforecast.com";
    }

    @Override
    public void setCity(City city) {
        super.setCity(city);
        setParam("lat", Double.toString(city.getLatitude()));
        setParam("lon", Double.toString(city.getLongitude()));
    }
}

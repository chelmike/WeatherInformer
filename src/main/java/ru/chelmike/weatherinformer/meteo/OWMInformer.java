package ru.chelmike.weatherinformer.meteo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.chelmike.weatherinformer.entities.City;

/**
 * Implements a weather request to OpenWeatherMap service (openweathermap.org)
 * API: https://openweathermap.org/current#geo
 *
 * @author Michael Ostrovsky
 */
@Component("OWMInformer")
public class OWMInformer extends MeteoInformerHTTP {

    @Autowired
    public OWMInformer(@Value("${OWMInformer.URLTemplate}") String URLTemplate, @Value("${OWMInformer.appID}") String appID) {
        super(URLTemplate, OWMJsonStruct.class);
        setParam("appID", appID);
    }

    /**
     * A POJO-class for converting data from JSON to an object
     * Corresponds to a structure of OpenWeatherMap JSON-response
     * (only basic parameters are taken for example)
     */
    static class OWMJsonStruct extends JsonStruct {
        OWMDataMain main = new OWMDataMain();

        static class OWMDataMain {
            double temp;
            double feels_like;
            int pressure;
            int humidity;
        }

        @Override
        public double getCurrentTemperature() {
            return main.temp;
        }

        @Override
        public double getCurrentFeelsLike() {
            return main.feels_like;
        }

        @Override
        public double getCurrentPressure() {
            return main.pressure;
        }

        @Override
        public int getCurrentHumidity() {
            return main.humidity;
        }

        @Override
        public String getTemperatureUnits() {
            return "°C";
        }

        @Override
        public String getPressureUnits() {
            return "hPa";
        }

        @Override
        public String getHumidityUnits() {
            return "%";
        }
    }

    @Override
    public String getName() { return "OpenWeatherMap"; }

    @Override
    public String getHomeURL() {
        return "openweathermap.org";
    }

    @Override
    public void setCity(City city) {
        super.setCity(city);
        setParam("cityName", city.getNameEn());
        setParam("timestamp", getUTCTimeStamp());
    }

}

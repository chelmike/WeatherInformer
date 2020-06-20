package ru.chelmike.weatherinformer.meteo;

/**
 * Интерфейс предоставляющий показатели метеоданных
 * @author Michael Ostrovsky
 */
public interface MeteoData {
    double getCurrentTemperature();
    double getCurrentFeelsLike();
    double getCurrentPressure();
    int getCurrentHumidity();

    // Measure units provided by the service
    String getTemperatureUnits();
    String getPressureUnits();
    String getHumidityUnits();

    default String getPressureDesc() { return getCurrentPressure() + " " + getPressureUnits(); }

    default String getTemperatureDesc() { return getCurrentTemperature() + getTemperatureUnits(); }

    default String getFeelsLikeDesc() { return getCurrentFeelsLike() + getTemperatureUnits(); }

    default String getHumidityDesc() { return getCurrentHumidity() + " " + getHumidityUnits(); }
}
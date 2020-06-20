package ru.chelmike.weatherinformer.tests;

import ru.chelmike.weatherinformer.meteo.GFInformer;
import ru.chelmike.weatherinformer.meteo.MeteoData;

/**
 * @author Michael Ostrovsky
 */
public class TestGFInformer {
    public static void main(String[] args) {
        GFInformer informer2 = new GFInformer("https://gridforecast.com/api/v1/forecast/55.1540;61.4291/202003091800?api_token=fLcgy0ZcULm4ARxJ", "fLcgy0ZcULm4ARxJ");

        //401
//        informer2.setURLTemplate("https://gridforecast.com/api/v1/forecast/55.1540;61.4291/202003091800?api_token=fLcgy0ZcULm4ARxJ");

        //404
        //informer2.setURLTemplate("https://gridforecast.com/api2/v1/forecast/55.1540;61.4291/202003091800?api_token=fLcgy0ZcULm4ARxJ");

        informer2.setParam("cityName", "Chelyabinsk");
//        informer2.setParam("APIToken", "fLcgy0ZcULm4ARxJ");

        MeteoData data2 = informer2.getData();
        System.out.println(data2.getCurrentTemperature());
        System.out.println(data2.getCurrentFeelsLike());
        System.out.println(data2.getCurrentPressure());
        System.out.println(data2.getCurrentHumidity());
        System.out.println(informer2.getDatetimeQueried());
    }
}

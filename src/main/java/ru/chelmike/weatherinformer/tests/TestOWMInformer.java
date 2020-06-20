package ru.chelmike.weatherinformer.tests;

import ru.chelmike.weatherinformer.entities.CityList;
import ru.chelmike.weatherinformer.meteo.GFInformer;
import ru.chelmike.weatherinformer.meteo.MeteoData;
import ru.chelmike.weatherinformer.meteo.MeteoInformerHTTP;
import ru.chelmike.weatherinformer.meteo.OWMInformer;

/**
 * @author Michael Ostrovsky
 */
public class TestOWMInformer {
    public static int y;

    public static int bar(int z) {
        System.out.println("Bar ");
        return y = z;
    }

    public static void main(String[] args) {

        OWMInformer informer = new OWMInformer("http://82.196.7.246/data/2.5/weather?q={cityName}&appid={appID}&units=metric", "32fe380f57e9b22b13e89afc26b52395");
//        informer.setParam("cityName", "Chelyabinsk");
        informer.setParam("cityName", "Chelydfdfabinsk"); //incorrect
//        informer.setParam("appID", "12332fe380f57e9b22b13e89afc26b52395"); //incorrect

        MeteoData data = informer.getData();
        System.out.println(data.getCurrentTemperature());
        System.out.println(data.getCurrentFeelsLike());
        System.out.println(data.getCurrentPressure());
        System.out.println(data.getCurrentHumidity());
        System.out.println(informer.getDatetimeQueried());


/*
        GFInformer informer2 = new GFInformer();

        informer2.setURLTemplate("https://gridforecast.com/api2/v1/forecast/55.1540;61.4291/202003091800?api_token=fLcgy0ZcULm4ARxJ");
        informer2.setParam("cityName", "Chelyabinsk");
        informer2.setParam("APIToken", "fLcgy0ZcULm4ARxJ");

        MeteoData data2 = informer2.requestData();

        System.out.println(data2.getCurrentTemperature());
        System.out.println(data2.getCurrentFeelsLike());
        System.out.println(data2.getCurrentPressure());
        System.out.println(data2.getCurrentHumidity());
        System.out.println(informer2.getDatetimeQueried());
*/
    }
}

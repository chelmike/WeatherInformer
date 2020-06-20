package ru.chelmike.weatherinformer.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.chelmike.weatherinformer.entities.City;
import ru.chelmike.weatherinformer.entities.CityList;
import ru.chelmike.weatherinformer.meteo.InformerList;
import ru.chelmike.weatherinformer.meteo.MeteoData;
import ru.chelmike.weatherinformer.meteo.MeteoInformer;
import ru.chelmike.weatherinformer.meteo.MeteoInformerHTTP;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

/**
 * @author Michael Ostrovsky
 */
@Controller
public class HomeController {
    @Autowired
    private InformerList informerList;

    @Autowired
    private CityList cities;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/weather")
    public String showWeather(@RequestParam("city") Optional<String> city,
                              @RequestParam("informer") Optional<String> informer,
                              ModelMap model, HttpServletRequest request, HttpServletResponse response) {

        String informerNameFromCookie = "";
        String cityNameFromCookie = "";

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("informer")) {
                    informerNameFromCookie = cookie.getValue();
                }
                if (cookie.getName().equals("city")) {
                    cityNameFromCookie = cookie.getValue();
                }
            }
        }
        logger.debug("informerNameFromCookie: {}", informerNameFromCookie);
        logger.debug("cityNameFromCookie: {}", cityNameFromCookie);

        String informerName = informer.map(String::trim).orElse(informerNameFromCookie);
        String cityName = city.map(s -> s.trim().replace('_', ' ')).orElse(cityNameFromCookie);

        logger.debug("informerName: {}", informerName);
        logger.debug("cityName: {}", cityName);

        MeteoInformer meteoInformer = informerList.get(informerName);
        meteoInformer.setCity(cities.getCity(cityName));

        logger.debug(meteoInformer.getName() + " / " + meteoInformer.getHomeURL());

        Cookie informerCookie = new Cookie("informer", meteoInformer.getName());
        informerCookie.setMaxAge(24 * 60 * 60);
        Cookie cityCookie = new Cookie("city", cityName.replace(' ', '_'));
        cityCookie.setMaxAge(24 * 60 * 60);

        response.addCookie(informerCookie);
        response.addCookie(cityCookie);

        City cityObj = CityList.getCity(cityName);
        String timestamp = MeteoInformerHTTP.getUTCTimeStamp();

        logger.debug("Requesting weather for {}, timestamp: {}", cityObj.getNameEn(), timestamp);
        MeteoData meteoData = meteoInformer.requestWeatherData(cityObj, timestamp, informerName);

        if (!meteoInformer.getLastError().isBlank()) {
            model.addAttribute("errorMessage", meteoInformer.getLastErrorDetails());
        }

        setResponseHeaders(response);

        model.addAttribute("cities", cities.getCities());
        model.addAttribute("cityName", meteoInformer.getCity().getNameEn());
        model.addAttribute("informers", informerList.getInformerList());
        model.addAttribute("meteoInformer", meteoInformer);
        model.addAttribute("meteoData", meteoData);

        return "cityWeather";
    }

    /**
     * Устанавливает заголовки ответа
     *
     * @param response Объект {@code HttpServletResponse}
     */
    protected void setResponseHeaders(@NonNull HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}

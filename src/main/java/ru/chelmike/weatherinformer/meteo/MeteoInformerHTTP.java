package ru.chelmike.weatherinformer.meteo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import ru.chelmike.weatherinformer.entities.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A base class for derived weather informers. Implements GET-request to a given weather service and receives response
 * considered as JSON and stores it as {@code InformerData} object
 *
 * @author Michael Ostrovsky
 */
abstract public class MeteoInformerHTTP implements MeteoInformer {
    private String URLTemplate;
    private final Map<String, String> params = new HashMap<>();
    private Map<String, String> requestHeaders = new HashMap<>();

    private String responseJSON = "";
    private City city;
    private LocalDateTime datetimeQueried;

    private String lastError = "";
    private boolean isLastError = false;
    private int HTTPResponseCode;
    private String HTTPResponse; // in case of an error when receiving a response, this field may contain detailed info
    protected static final Logger logger = LoggerFactory.getLogger(MeteoInformerHTTP.class);

    private final Class<? extends JsonStruct> dataClass;

    public MeteoInformerHTTP(String URLTemplate, Class<? extends JsonStruct> jsonStructClass) {
        this.URLTemplate = URLTemplate;
        this.dataClass = jsonStructClass;
    }

    @Override
    public void setCity(City city) {
        if (this.city != null && !this.city.equals(city))
            responseJSON = "";
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String getLastError() {
        if (!"".equals(lastError)) {
            return lastError;
        }
        if (HTTPResponseCode < 400) {
            return "";
        }
        return "" + HTTPResponseCode;
    }

    public int getHTTPResponseCode() {
        return HTTPResponseCode;
    }

    public String getHTTPResponse() {
        return HTTPResponse;
    }

    @Override
    public String getLastErrorDetails() {
        logger.debug("Last error is: {}", lastError);
        return (!lastError.isBlank()) ? HTTPResponse : "";
    }

    @Override
    public String getDatetimeQueried() {
        if (datetimeQueried == null)
            return "---";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YMMddHHmmss");
        return datetimeQueried.format(formatter);
    }

    @Override
    public String getDatetimeQueriedHuman() {
        if (datetimeQueried == null)
            return "---";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.Y HH:mm:ss");
        return datetimeQueried.format(formatter);
    }

    public void setURLTemplate(String URLTemplate) {
        this.URLTemplate = URLTemplate;
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    /**
     * Time stamp in format YYYYMMDDHHmm - with accuracy of one minute. Used to cache invocations
     * of data requests - the result is cached for a combination of arguments [city + timestamp], i.e. for
     * one minute there is no repeated request to the weather service about the same city.
     *
     * @return A string containing the timestamp
     */
    public static String getUTCTimeStamp() {
        LocalDateTime dateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YMMddHH00");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YMMddHHmm");
        return dateTime.format(formatter);
    }

    /**
     * Processes a given URL's template and substitutes parameter keys placed in braces
     * with the corresponding values from {@code params}
     *
     * @return Prepared URL for requesting weather data
     */
    public String getParsedURL() {
        String parsedURL = URLTemplate;
        for (var entry : params.entrySet()) {
            parsedURL = parsedURL.replaceFirst("\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        return parsedURL;
    }

    public String getResponseJSON() {
        if (!responseJSON.isBlank()) {
            logger.debug("Getting previously loaded JSON");
            return responseJSON;
        }
        try {
            logger.debug("JSON-response is blank, will request service for fresh JSON-data if there was no errors in prev request..");

            // [HTTPResponseCode = 0] indicates that weather data is not requested yet,
            // otherwise empty responseJSON says about error(s) while previous weather query
            if (HTTPResponseCode == 0 && requestWeatherData(getParsedURL())) {
                return responseJSON;
            }
        } catch (Exception e) {
            lastError = e.getMessage();
        }
        logger.error("Last error while requesting weather data ({}): {}", this.getName(), this.getLastError());
        return "";
    }

    @Override
    @Cacheable("weatherData")
    public MeteoData requestWeatherData(City city, String timestamp, String informerName) {
        logger.debug("requestWeatherData: requesting weather for {}, timestamp [{}], informer [{}]", city.getNameEn(), timestamp, informerName);
        setCity(city);
        try {
            requestWeatherData(getParsedURL());
        } catch (IOException e) {
            logger.error(e.getMessage());
            lastError = e.getMessage();
        }
        setDataFromJSON(responseJSON);
        return getData();
    }

    /**
     * Requests weather data (JSON)
     *
     * @param url is parsed URL containing all the necessary parameters with its values and ready for request
     * @return {@code true} if data successfully received, otherwise {@code false}
     * @throws IOException
     */
    @Cacheable("jsonData")
    public boolean requestWeatherData(String url) throws IOException {
        Map<String, List<String>> responseHeaders;

        logger.debug("URL for request: {}", url);
        URL urlObj = new URL(url);

        logger.debug("Opening connection...");
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        HTTPResponseCode = connection.getResponseCode();
        logger.debug("HTTP-response code: {}", HTTPResponseCode);

        logger.debug("Getting response body...");
        try {
            if (HTTPResponseCode < 400) {
                HTTPResponse = readHTTPResponse(new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)));
            } else {
                HTTPResponse = readHTTPResponse(new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8)));
                lastError = "" + HTTPResponseCode;
                logger.error("Weather service response: {}", HTTPResponse);
                return false;
            }
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
            lastError = e.getMessage();
            return false;
        }

        // Collecting response headers
        try {
            responseHeaders = connection.getHeaderFields();
        } catch (Exception e) {
            logger.error("Error while getting response headers: {}", e.getMessage());
            lastError = "Error while getting response headers: " + e.getMessage();
            return false;
        }

        // 200/OK && Content-Type == application/json|text/html
        if (HTTPResponseCode == 200 &&
                (
                        responseHeaders.get("Content-Type").get(0).startsWith("application/json") ||
                                responseHeaders.get("Content-Type").get(0).startsWith("text/html")
                )
        ) {
            logger.debug("Response succeed");
            datetimeQueried = LocalDateTime.now();

            if (!"".equals(HTTPResponse)) {
                logger.debug("JSON: {}", HTTPResponse);
                responseJSON = HTTPResponse;
                return true;
            }
        }
        lastError = "There is no JSON in response";
        return false;
    }

    protected String readHTTPResponse(BufferedReader br) {
        return br.lines().collect(Collectors.joining("\n"));
    }

    /**
     * Returns {@code MeteoData} which contains early received weather parameters in case there was no error,
     * otherwise returns empty data object.
     * @return {@code MeteoData} object
     */
    public MeteoData getData() {
        if (data == null) {
            if (lastError.isBlank()) {
                logger.debug("Data is not loaded, loading from JSON if not empty");
                setDataFromJSON(getResponseJSON());
            }
            if (data == null) {
                try {
                    logger.debug("Instantiating an empty meteodata object");
                    data = dataClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * A POJO-class representing a structure of JSON that is supposed to receive from weather service.
     * A derived class corresponds to a concrete field structure of a particular informer
     * according to its API and needs to be passed as an argument to the constructor
     */
    abstract static class JsonStruct implements MeteoData {
    }

    private JsonStruct data;

    /**
     * Converts JSON to a POJO-object ({@code data}) that has identical fields.
     * A reference to its concrete class is passed in constructor and stored in
     * {@code dataClass} field
     *
     * @param json a string containing JSON
     */
    public void setDataFromJSON(String json) {
        if (json.isBlank()) {
            return;
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        data = gson.fromJson(json, dataClass);
    }
}
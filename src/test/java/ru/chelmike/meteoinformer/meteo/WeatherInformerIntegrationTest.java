package ru.chelmike.meteoinformer.meteo;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import ru.chelmike.weatherinformer.meteo.MeteoData;
import ru.chelmike.weatherinformer.meteo.OWMInformer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * @author Michael Ostrovsky
 */

public class WeatherInformerIntegrationTest extends AbstractTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8090);

    String url = "http://localhost:8090/weather?q=Moscow&appid=123456789&units=metric";
    OWMInformer informer = new OWMInformer(url, "123456789"); // appID passed to constructor will be ignored

    @Test
    public void shouldCallWeatherServiceAndReceiveData() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/weather"))
                .willReturn(aResponse()
                        .withBody(readFile("classpath:weatherServiceResponse.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)));

        MeteoData data = informer.getData();

        assertEquals(17, data.getCurrentTemperature(), 0.1);
        assertEquals(27, data.getCurrentHumidity(), 0.1);
        assertEquals(9.82, data.getCurrentFeelsLike(), 0.1);
        assertEquals(1015, data.getCurrentPressure(), 0.1);
    }

    @Test
    public void shouldCallWeatherServiceAndReceivePageNotFoundErrorAndErrorDetails() throws Exception {
        String errorDetails = readFile("classpath:pageNotFoundResponse.json");
        wireMockRule.stubFor(get(urlPathEqualTo("/weather"))
                .willReturn(aResponse()
                        .withBody(errorDetails)
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)));

        MeteoData data = informer.getData();

        assertEquals(404, informer.getHTTPResponseCode());
        assertEquals(errorDetails, informer.getHTTPResponse());
    }
}
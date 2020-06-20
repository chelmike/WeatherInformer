package ru.chelmike.meteoinformer.meteo;

import org.junit.Before;
import org.junit.Test;
import ru.chelmike.weatherinformer.meteo.MeteoData;
import ru.chelmike.weatherinformer.meteo.OWMInformer;

import static org.junit.Assert.assertEquals;

public class OWMInformerTest extends AbstractTest {

    private OWMInformer subject;

    @Before
    public void setUp() {
        String url = "http://11.22.33.44/path/to/service/api?somefixedparam=value&param3={param3value}&param2={param2value}&param1={param1value}&appID={appID}";
        String key = "abcdefghijklmnopqrstuvwxyz123456";
        subject = new OWMInformer(url, key);
    }

    @Test
    public void shouldReturnName() {
        assertEquals("OpenWeatherMap", subject.getName());
    }

    @Test
    public void shouldReturnParsedURL() {
        subject.setParam("param1value", "value1");
        subject.setParam("param2value", "value2");
        subject.setParam("param3value", "value3");

        String url = subject.getParsedURL();
        String expectedURL = "http://11.22.33.44/path/to/service/api?somefixedparam=value&param3=value3&param2=value2&param1=value1&appID=abcdefghijklmnopqrstuvwxyz123456";

        assertEquals(expectedURL, url);
    }

    @Test
    public void shouldConvertJSONtoMeteoData() throws Exception {
        String json = readFile("classpath:weatherServiceResponse.json");
        subject.setDataFromJSON(json);
        MeteoData data = subject.getData();

        assertEquals(17, data.getCurrentTemperature(), 0.1);
        assertEquals(27, data.getCurrentHumidity(), 0.1);
        assertEquals(9.82, data.getCurrentFeelsLike(), 0.1);
        assertEquals(1015, data.getCurrentPressure(), 0.1);
    }
}
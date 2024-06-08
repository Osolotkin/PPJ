package sem.weather.weather;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;

@SpringBootTest
@AutoConfigureMockMvc
public class APITest {

    @MockBean
	private CityService cityService;

	@MockBean
	private WeatherService weatherService;

	@Autowired
	private API api;



    final Date date = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();

    final String cityName = "London";
    final String countryName = "United Kingdom";
            
    final DCountry country = new DCountry(countryName);
    final DCity city = new DCity(country, cityName);



    @Test
    public void download() throws Exception {

        when(cityService.insert(cityName, countryName)).thenReturn(city);
        when(weatherService.insertRecords(any(city.getClass()), any(JSONArray.class))).thenReturn(24);

        JSONObject response = new JSONObject(api.download("London", "2024-01-01").getBody());
        JSONObject expectedResponse = new JSONObject(Files.readString(Path.of("src/test/java/sem/weather/weather/2024-01-01.json")));

        response.getJSONObject("location").put("localtime", "");
        response.getJSONObject("location").put("localtime_epoch", "");

        expectedResponse.getJSONObject("location").put("localtime", "");
        expectedResponse.getJSONObject("location").put("localtime_epoch", "");

        assertEquals(expectedResponse.toString(), response.toString());
    
    }

    @Test
    public void weather() throws Exception {

        List<DWeather> testRecords = new ArrayList<DWeather>();
		testRecords.add(new DWeather(city, date));

        when(cityService.get(cityName)).thenReturn(city);
        when(weatherService.getRecords(city, date)).thenReturn(testRecords);

        JSONObject response = new JSONObject(api.weather("London", "2024-01-01").getBody());

        assertEquals(response.getString("city"), cityName);
        assertEquals(response.getJSONArray("weather").toList().size(), 1);
        
        JSONObject tmp = response.getJSONArray("weather").getJSONObject(0); 
        assertEquals(tmp.getInt("condition"), testRecords.get(0).getCondition()); 
        assertEquals(tmp.getFloat("tempC"), testRecords.get(0).getTempC());
        assertEquals(tmp.getNumber("timestamp"), testRecords.get(0).getTimestamp().getTime());
    
    }

    @Test
    public void prevWeek() throws Exception {

        List<DWeather> testRecords = new ArrayList<DWeather>();
        for (int i = 0; i < 24; i++) {
            testRecords.add(new DWeather(city, new Date(date.getTime() + i * 60 * 60 * 1000)));
            testRecords.get(i).setTemp(i);
        }

        when(cityService.get(cityName)).thenReturn(city);
        when(weatherService.getRecords(matches(cityName), any(Date.class))).thenReturn(testRecords);

        JSONObject response = new JSONObject(api.prevWeek("London").getBody());

        assertEquals(response.getFloat("avg_day_temp"), 13);
        assertEquals(response.getFloat("avg_night_temp"), 9);
        
        assertEquals(response.getJSONArray("temps").toList().size(), 7);
    
    }
    
}

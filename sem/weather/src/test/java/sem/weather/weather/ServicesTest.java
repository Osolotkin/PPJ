package sem.weather.weather;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;

@SpringBootTest
@AutoConfigureMockMvc
class ServicesTest {

	@Autowired
	private CityService cityService;

	@Autowired
	private WeatherService weatherService;

	@MockBean 
	WeatherRepository weatherRepository;

	@MockBean 
	CityRepository cityRepository;

	@MockBean 
	CountryRepository countryRepository;

	@Test
	void insertCity() {

		final String cityName = "London";
		final String countryName = "United Kingdom";
				
		DCountry country = new DCountry(countryName);
		DCity city = new DCity(country, cityName);

		when(countryRepository.save(any(DCountry.class))).thenReturn(country);
		when(cityRepository.save(any(DCity.class))).thenReturn(city);
		
		DCity city1 = cityService.insert(cityName, countryName);
		assertNotNull(city1);

		when(countryRepository.findOne(any(Example.class))).thenReturn(Optional.of(country));
		when(cityRepository.findOne(any(Example.class))).thenReturn(Optional.of(city));

		DCity city2 = cityService.insert(cityName, countryName);
		assertEquals(city1, city2);

	}

	@Test
	void getCity() {

		final String cityName = "London";
		final String countryName = "United Kingdom";
				
		DCountry country = new DCountry(countryName);
		DCity city = new DCity(country, cityName);

		when(cityRepository.findOne(any(Example.class))).thenReturn(Optional.of(city));

		DCity city2 = cityService.get(cityName);
		assertEquals(city, city2);

	}

	@Test
	void insertRecords() throws Exception {

		final String fileString = Files.readString(Path.of("src/test/java/sem/weather/weather/2024-01-01.json"));
		final JSONObject js = new JSONObject(fileString);
		final JSONArray data = js.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour");

		assertEquals(24, weatherService.insertRecords(new DCity(), data));

	}

	@Test
	void getRecordsByCity() {

		final Date date = new Date(2024);
		final String cityName = "London";
		final String countryName = "United Kingdom";
				
		DCountry country = new DCountry(countryName);
		DCity city = new DCity(country, cityName);

		List<DWeather> testRecords = new ArrayList<DWeather>();
		testRecords.add(new DWeather(city, date));

		when(weatherRepository.findRecordsByDate(city, date)).thenReturn(testRecords);

		List<DWeather> records = weatherService.getRecords(city, date);
		assertNotNull(records);
		assertEquals(1, records.size());

	}

	@Test
	void getRecordsByCityName() {

		final String cityName = "London";

		final Date date = new Date(2024);
		DCity city = new DCity(cityName);

		List<DWeather> testRecords = new ArrayList<DWeather>();
		testRecords.add(new DWeather(city, date));

		when(cityRepository.findOne(any(Example.class))).thenReturn(Optional.of(city));
		when(weatherRepository.findRecordsByDate(city, date)).thenReturn(testRecords);

		List<DWeather> records = weatherService.getRecords(cityName, date);
		assertNotNull(records);
		assertEquals(1, records.size());

	}

	@Test
	void removeRecords() {

		final String cityName = "London";

		final Date date = new Date(2024);
		DCity city = new DCity(cityName);

		List<DWeather> testRecords = new ArrayList<DWeather>();
		testRecords.add(new DWeather(city, date));

		when(cityRepository.findOne(any(Example.class))).thenReturn(Optional.of(city));

		assertDoesNotThrow(() -> weatherService.removeRecords(cityName));

	}

}

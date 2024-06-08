package sem.weather.weather;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONObject;

@RestController
public class API {

    private final CityService cityService;
    private final WeatherService weatherService;

    final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

    final int dayStartHour = 6;
    final int dayEndHour = 20;

    public API(CityService cityService, WeatherService weatherService) {
        this.cityService = cityService;
        this.weatherService = weatherService;
    }

    private JSONObject downloadWeatherData(String city, Date date) {

        HttpClient client = HttpClient.newBuilder()
            .proxy(ProxySelector.getDefault())
            .build();

        UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl("http://api.weatherapi.com/v1/history.json?key=051c2d98d022446888a115913240805&q={city}&dt={date}")
                    .buildAndExpand(city, dayFormat.format(date));
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri.toUri())
            .GET()
            .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());    
            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
            
    }

    private int insertWeatherData(JSONObject js) {

        final String cityName = js.getJSONObject("location").getString("name");
        final String country = js.getJSONObject("location").getString("country");
        JSONArray hours = null;

        try {
            hours = js.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        DCity city = cityService.insert(cityName, country);
        return weatherService.insertRecords(city, hours);

    }

    private JSONArray weatherToSortedJSONArray(List<DWeather> weather) {

        weather.sort(Comparator.comparing(DWeather::getTimestamp));

        ObjectMapper mapper = new ObjectMapper();
        List<ObjectNode> objects = new ArrayList<>();

        return new JSONArray(mapper.convertValue(weather, objects.getClass()));

    }

    @GetMapping("/")
    public ModelAndView root() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main.html");
        return modelAndView;
    }

    @GetMapping("/weather_conditions")
    public ModelAndView weatherConditions() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("weather_conditions.json");
        return modelAndView;
    }

    @GetMapping("/download")
    public ResponseEntity<String> download(@RequestParam(value = "city") String cityName, @RequestParam(value = "date") String date) throws ParseException {

        JSONObject js = downloadWeatherData(cityName, dayFormat.parse(date));
        if (js == null) return ResponseEntity.status(503).body(null);

        int cnt = insertWeatherData(js);
        if (cnt < 0) return ResponseEntity.status(503).body(null);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(js.toString());
    
    }

	@GetMapping(path = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> weather(@RequestParam(value = "city") String cityName, @RequestParam(value = "date") String date) throws ParseException {
        
        JSONObject js = new JSONObject();

        DCity city = cityService.get(cityName);
        if (city == null) return ResponseEntity.ok().body(null);

        js.put("city", city.getName());

        List<DWeather> weather = weatherService.getRecords(city, dayFormat.parse(date));
        js.put("weather", weatherToSortedJSONArray(weather));

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(js.toString());
        
    }

    @GetMapping(path = "/prev_week", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> prevWeek(@RequestParam(value = "city") String city) {

        final int daysPerWeek = 7;

        JSONArray jsa = new JSONArray();
        Date today = new Date();

        double avgDayTemp = 0;
        double avgNightTemp = 0;

        for (int i = 0; i < daysPerWeek; i++) {

            Date date = new Date(today.getTime() - i * 24 * 60 * 60 * 1000);

            List<DWeather> data = weatherService.getRecords(city, date);
            if (data == null || data.size() == 0) {
                
                JSONObject js = downloadWeatherData(city, date);
                if (js == null) {
                    return ResponseEntity.status(503).body(null);
                }

                int recordsInserted = insertWeatherData(js);
                if (recordsInserted < 0) {
                    return ResponseEntity.status(503).body(null);    
                }

                data = weatherService.getRecords(city, date);
                if (data == null || data.size() == 0) {
                    return ResponseEntity.status(503).body(null);
                }

            }

            data.sort(Comparator.comparing(DWeather::getTimestamp));

            double dayTmp = 0;
            double nightTmp = 0;
            int dayCnt = 0;
            int nightCnt = 0;

            Calendar calendar = Calendar.getInstance();

            for (int ii = 0; ii < data.size(); ii++) {
                
                DWeather weather = data.get(ii);

                calendar.setTime(weather.getTimestamp());
                final int hours = calendar.get(Calendar.HOUR_OF_DAY);
                if (hours >= dayStartHour && hours <= dayEndHour) {
                    dayCnt++;
                    dayTmp += weather.getTempC();
                } else {
                    nightCnt++;
                    nightTmp += weather.getTempC();
                }

            }
            
            avgDayTemp += dayTmp / dayCnt;
            avgNightTemp += nightTmp / nightCnt;

            jsa.put(weatherToSortedJSONArray(data));

        }

        avgDayTemp /= daysPerWeek;
        avgNightTemp /= daysPerWeek;

        JSONObject js = new JSONObject();
        js.put("avg_day_temp", avgDayTemp);
        js.put("avg_night_temp", avgNightTemp);
        js.put("temps", jsa);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(js.toString());
    
    }

	@GetMapping("/delete")
    public String hello(@RequestParam(value = "city") String city) {
        weatherService.removeRecords(city);
        return String.format("Weather for %s is deleted!", city);
    }

}

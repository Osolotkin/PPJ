package sem.weather.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WeatherService {
    
    private final WeatherRepository weatherRep;
    private final CityService cityService;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    

    public WeatherService(WeatherRepository weatherRep, CityService cityService) {
        this.weatherRep = weatherRep;
        this.cityService = cityService;
    }

    public int insertRecords(DCity city, JSONArray data) {

        int cnt = 0;
        for (int i = 0; i < data.length(); i++) {

            final JSONObject tmp = data.getJSONObject(i);
            
            try {
                
                weatherRep.saveAndFlush(new DWeather(
                    city,
                    tmp.getDouble("temp_c"),
                    dateFormat.parse(tmp.getString("time")),
                    tmp.getJSONObject("condition").getInt("code")
                ));

                cnt++;
            
            } catch(ParseException ex) {

                ex.printStackTrace();

            } catch (Exception ex) {

                ex.printStackTrace();
                
            }

        }

        return cnt;

    }

    
    @Transactional
    public List<DWeather> getRecords(DCity city, Date date) {

        return weatherRep.findRecordsByDate(city, date);
    
    }

    @Transactional
    public List<DWeather> getRecords(String cityName, Date date) {

        DCity city = cityService.get(cityName);
        if (city == null) return null;

        List<DWeather> tmp = weatherRep.findRecordsByDate(city, date);
        return tmp;
    
    }

    @Transactional
    public void removeRecords(String cityName) {

        DCity city = cityService.get(cityName);
        if (city == null) return;

        weatherRep.deleteAll(city.getWeather());

    }

}

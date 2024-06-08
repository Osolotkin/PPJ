package sem.weather.weather;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WeatherRepository extends JpaRepository<DWeather, DWeatherPK> {

    @Query("SELECT w FROM DWeather w WHERE w.city = ?1 AND DATEDIFF(day, CAST(w.timestamp as date), CAST(?2 as date)) = 0")
    List<DWeather> findRecordsByDate(DCity city, Date date);

}

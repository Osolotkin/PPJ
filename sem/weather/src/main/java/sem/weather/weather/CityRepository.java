package sem.weather.weather;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<DCity, Long> {

}
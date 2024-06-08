package sem.weather.weather;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<DCountry, Long> {

}

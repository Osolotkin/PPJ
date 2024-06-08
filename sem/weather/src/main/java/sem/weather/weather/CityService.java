package sem.weather.weather;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class CityService {
    
    private final CityRepository cityRep;
    private final CountryRepository countryRep;

    private final ExampleMatcher countryMatcher = ExampleMatcher.matching().withIgnorePaths("id");
    private final ExampleMatcher cityMatcher = ExampleMatcher.matching().withIgnorePaths("id").withIgnoreCase();
    
    public CityService(CityRepository cityRep, CountryRepository countryRep) {
        this.cityRep = cityRep;
        this.countryRep = countryRep;
    }

    @Transactional
    public DCity insert(String name, String countryName) {

        DCountry refCountry = new DCountry(countryName);
        DCountry ansCountry = countryRep.findOne(Example.of(refCountry, countryMatcher)).orElse(null);

        if (ansCountry == null) {
            ansCountry = countryRep.save(refCountry);
        }

        DCity refCity = new DCity(ansCountry, name);
        DCity ansCity = cityRep.findOne(Example.of(refCity, cityMatcher)).orElse(null);

        if (ansCity == null) {
            ansCity = cityRep.save(new DCity(ansCountry, name));
        }

        return ansCity;

    }

    public DCity get(String name) {
        return cityRep.findOne(Example.of(new DCity(name), cityMatcher)).orElse(null);
    }
    
}

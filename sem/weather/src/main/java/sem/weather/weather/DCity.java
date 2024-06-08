package sem.weather.weather;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "city")
public class DCity implements Serializable{

    public DCity() {
        
    }

    public DCity(DCountry country, String name) {
        this.country = country;
        this.name = name;
    }

    public DCity(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private DCountry country;

    @Column(name = "name")
    private String name;

    @OneToMany
    @JoinColumn(name = "city_id")
    private Set<DWeather> weatherRecords;
    
    public long getId() {
        return id;
    }

    public Set<DWeather> getWeather() {
        return weatherRecords;
    }

    public String getName() {
        return name;
    }

}
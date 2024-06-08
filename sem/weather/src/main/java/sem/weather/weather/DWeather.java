package sem.weather.weather;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "weather")
@IdClass(DWeatherPK.class)
public class DWeather implements Serializable {

    public DWeather() {
        
    }

    public DWeather(DCity city, double tempC, Date timestamp, int condition) {
        this.city = city;
        this.tempC = tempC;
        this.timestamp = timestamp;
        this.condition = condition;
    }

    public DWeather(DCity city, Date timestamp) {
        this.city = city;
        this.timestamp = timestamp;
    }

    public DWeather(DCity city) {
        this.city = city;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "city_id")
    private DCity city;

    @Column(name = "temp_c")
    private double tempC;
 
    @Id
    @Column(name = "time_stamp")
    private Date timestamp;
    
    @Column(name = "cond")
    private int condition;

    @JsonIgnore
    public DCity getCity() {
        return city;
    }

    public int getCondition() {
        return condition;
    }

    public double getTempC() {
        return tempC;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTemp(double val) {
        tempC = val;
    }
    
}

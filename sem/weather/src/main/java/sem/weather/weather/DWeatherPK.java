package sem.weather.weather;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class DWeatherPK implements Serializable {

    private DCity city;
    private Date timestamp;

    public DWeatherPK() {
    }

    public DWeatherPK(DCity city, Date timestamp) {
        this.city = city;
        this.timestamp = timestamp;
    }

    public boolean equals(Object obj) {

        if (this == obj ) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DWeatherPK pk = (DWeatherPK) obj;
        return Objects.equals(city, pk.city) &&
                Objects.equals(timestamp, pk.timestamp);
    }

    public int hashCode() {
        return Objects.hash(city, timestamp);
    }
    
}

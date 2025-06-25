package ai.rpg.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import ai.rpg.core.domain.LocationVisitData;

@Embeddable
public class LocationStateEmbeddable {
    
    @Column(name = "current_location", nullable = false)
    private String current = "starting_village";
    
    @Column(name = "previous_location")
    private String previous;
    
    @Column(name = "visit_count")
    private int visitCount = 1;
    
    @Column(name = "first_visit")
    private Instant firstVisit;
    
    @Column(name = "time_in_location_minutes")
    private int timeInLocationMinutes = 0;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "location_history", columnDefinition = "CLOB")
    private List<LocationVisitData> locationHistory = new ArrayList<>();
    
    public LocationStateEmbeddable() {
        this.locationHistory = new ArrayList<>();
    }
    
    public String getCurrent() { return current; }
    public void setCurrent(String current) { this.current = current; }
    
    public String getPrevious() { return previous; }
    public void setPrevious(String previous) { this.previous = previous; }
    
    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }
    
    public Instant getFirstVisit() { return firstVisit; }
    public void setFirstVisit(Instant firstVisit) { this.firstVisit = firstVisit; }
    
    public int getTimeInLocationMinutes() { return timeInLocationMinutes; }
    public void setTimeInLocationMinutes(int timeInLocationMinutes) { this.timeInLocationMinutes = timeInLocationMinutes; }
    
    public List<LocationVisitData> getLocationHistory() { return locationHistory; }
    public void setLocationHistory(List<LocationVisitData> locationHistory) { 
        this.locationHistory = locationHistory != null ? locationHistory : new ArrayList<>();
    }
} 
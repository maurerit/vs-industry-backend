package io.github.vaporsea.vsindustry.domain;

import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "industry_jobs")
public class IndustryJob implements Timestamped, Identified {
    
    @Id
    private Long jobId;
    
    private Long blueprintTypeId;
    
    private Long productTypeId;
    
    private Long activityId;
    
    private Long licensedRuns;
    
    private Long runs;
    
    private Double probability;
    
    private Double cost;
    
    private String status;
    
    private Long successfulRuns;
    
    private ZonedDateTime startDate;
    
    private ZonedDateTime endDate;
    
    private Long installerId;
    
    public ZonedDateTime getTimestamp() {
        return startDate;
    }
    
    public Long getId() {
        return jobId;
    }
}

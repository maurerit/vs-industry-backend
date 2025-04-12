package io.github.vaporsea.vsindustry.contract;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class IndustryJobDTO implements Serializable {
    
    @JsonProperty("job_id")
    private Long jobId;
    
    @JsonProperty("blueprint_type_id")
    private Long blueprintTypeId;
    
    @JsonProperty("product_type_id")
    private Long productTypeId;
    
    @JsonProperty("activity_id")
    private Integer activityId;
    
    @JsonProperty("licensed_runs")
    private Integer licensedRuns;
    
    @JsonProperty("runs")
    private Long runs;
    
    @JsonProperty("probability")
    private Double probability;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    
    @JsonProperty("end_date")
    private ZonedDateTime endDate;
    
    @JsonProperty("cost")
    private Double cost;
    
    @JsonProperty("successful_runs")
    private Long successfulRuns;
    
    @JsonProperty("installer_id")
    private Long installerId;
}

package io.github.vaporsea.vsindustry.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractHeaderDTO implements Serializable {
    
    @JsonProperty("acceptor_id")
    private Long acceptorId;
    
    @JsonProperty("assignee_id")
    private Long assigneeId;
    
    @JsonProperty("availability")
    private String availability;
    
    @JsonProperty("buyout")
    private Double buyout;
    
    @JsonProperty("collateral")
    private Double collateral;
    
    @JsonProperty("contract_id")
    private Long contractId;
    
    @JsonProperty("date_accepted")
    private ZonedDateTime dateAccepted;
    
    @JsonProperty("date_completed")
    private ZonedDateTime dateCompleted;
    
    @JsonProperty("date_expired")
    private ZonedDateTime dateExpired;
    
    @JsonProperty("date_issued")
    private ZonedDateTime dateIssued;
    
    @JsonProperty("days_to_complete")
    private Long daysToComplete;
    
    @JsonProperty("end_location_id")
    private Long endLocationId;
    
    @JsonProperty("for_corporation")
    private Boolean forCorporation;
    
    @JsonProperty("issuer_corporation_id")
    private Long issuerCorporationId;
    
    @JsonProperty("issuer_id")
    private Long issuerId;
    
    @JsonProperty("price")
    private Double price;
    
    @JsonProperty("reward")
    private Double reward;
    
    @JsonProperty("start_location_id")
    private Long startLocationId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("volume")
    private Double volume;
}

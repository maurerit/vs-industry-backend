package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contract_headers")
public class ContractHeader implements Timestamped, Identified {
    
    @Id
    private Long contractId;
    
    private Long acceptorId;
    
    private Long assigneeId;
    
    private String availability;
    
    private Double buyout;
    
    private Double collateral;
    
    private ZonedDateTime dateAccepted;
    
    private ZonedDateTime dateCompleted;
    
    private ZonedDateTime dateExpired;
    
    private ZonedDateTime dateIssued;
    
    private Long daysToComplete;
    
    private Long endLocationId;
    
    private Boolean forCorporation;
    
    private Long issuerCorporationId;
    
    private Long issuerId;
    
    private Double price;
    
    private Double reward;
    
    private Long startLocationId;
    
    private String status;
    
    private String title;
    
    private String type;
    
    private Double volume;
    
    public ZonedDateTime getTimestamp() {
        return dateAccepted;
    }
    
    public Long getId() {
        return contractId;
    }
}
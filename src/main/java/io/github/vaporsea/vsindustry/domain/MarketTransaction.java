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
@Table(name = "market_transactions")
public class MarketTransaction implements Timestamped, Identified {
    @Id
    private Long transactionId;

    private Long clientId;

    private ZonedDateTime date;

    private Long journalRefId;

    private Integer quantity;

    private Long typeId;

    private Double unitPrice;

    private Integer divisionId;

    private Boolean isBuy;
    
    public ZonedDateTime getTimestamp() {
        return date;
    }
    
    public Long getId() {
        return transactionId;
    }
}

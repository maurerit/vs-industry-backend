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
public class MarketTransactionDTO implements Serializable {
    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("date")
    private ZonedDateTime date;

    @JsonProperty("journal_ref_id")
    private Long journalRefId;

    @JsonProperty("quantity")
    private Long quantity;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("unit_price")
    private Double unitPrice;

    @JsonProperty("is_buy")
    private Boolean isBuy;
}

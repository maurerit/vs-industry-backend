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
public class JournalEntryDTO implements Serializable {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("date")
    private ZonedDateTime date;

    @JsonProperty("description")
    private String description;

    @JsonProperty("first_party_id")
    private Long firstPartyId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("ref_type")
    private String refType;

    @JsonProperty("second_party_id")
    private Long secondPartyId;

    @JsonProperty("tax")
    private Double tax;

    @JsonProperty("tax_receiver_id")
    private Long taxReceiverId;
}

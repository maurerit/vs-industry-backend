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
@Table(name = "journal_entries")
public class JournalEntry {
    @Id
    private Long id;

    private Double amount;

    private Double balance;

    private ZonedDateTime date;

    private String description;

    private Long firstPartyId;

    private Long secondPartyId;

    private String reason;

    private String refType;

    private Integer divisionId;
}

package io.github.vaporsea.vsindustry.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO implements Serializable {
    @JsonProperty("division")
    private Integer division;

    @JsonProperty("balance")
    private Double balance;
}

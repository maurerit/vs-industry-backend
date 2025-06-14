/*
 * MIT License
 *
 * Copyright (c) 2025 VaporSea
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
public class MarketOrderDTO implements Serializable {
    
    @JsonProperty("order_id")
    private Long orderId;
    
    @JsonProperty("duration")
    private Integer duration;
    
    @JsonProperty("issued")
    private ZonedDateTime issued;
    
    @JsonProperty("price")
    private Double price;
    
    @JsonProperty("type_id")
    private Long typeId;
    
    @JsonProperty("type_name")
    private String typeName;
    
    @JsonProperty("volume_remain")
    private Integer volumeRemain;
    
    @JsonProperty("volume_total")
    private Integer volumeTotal;
    
    @JsonProperty("wallet_division")
    private Integer divisionId;
    
    @JsonProperty("is_buy_order")
    private boolean buyOrder;
    
    @JsonProperty("location_id")
    private Long locationId;
    
    @JsonProperty("system_id")
    private Long systemId;
}

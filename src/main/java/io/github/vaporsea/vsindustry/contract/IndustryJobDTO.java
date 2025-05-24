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
    
    //Does not come from CCP
    //It's driving me nuts seeing the two naming standards
    @JsonProperty("item_name")
    private String itemName;
    
    @JsonProperty("installer_name")
    private String installerName;
}

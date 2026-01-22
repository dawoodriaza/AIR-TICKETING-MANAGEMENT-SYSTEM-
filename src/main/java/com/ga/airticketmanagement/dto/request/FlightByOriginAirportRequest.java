package com.ga.airticketmanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlightByOriginAirportRequest {

    @NotBlank
    private Long destinationAirportId;

    @NotBlank
    private LocalDateTime departureTime;

    @NotBlank
    private LocalDateTime arrivalTime;

    @NotBlank @Min(0)
    private BigDecimal price;
}

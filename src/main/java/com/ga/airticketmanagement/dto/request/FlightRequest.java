package com.ga.airticketmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FlightRequest extends CreateFlightByOriginAirportRequest {

    @NotNull
    private Long originAirportId;

}
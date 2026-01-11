package com.ga.airticketmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateFlightRequest extends CreateFlightByOriginAirportRequest {

    private Long originAirportId;

}
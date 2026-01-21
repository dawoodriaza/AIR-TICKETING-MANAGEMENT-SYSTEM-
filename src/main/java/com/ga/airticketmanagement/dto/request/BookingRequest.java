package com.ga.airticketmanagement.dto.request;

import com.ga.airticketmanagement.model.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingRequest {

    @NotNull
    private Long flightId;

    private BookingStatus status;
}
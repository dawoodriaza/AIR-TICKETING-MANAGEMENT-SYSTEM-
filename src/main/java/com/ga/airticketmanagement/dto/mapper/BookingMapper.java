package com.ga.airticketmanagement.dto.mapper;

import com.ga.airticketmanagement.dto.request.BookingRequest;
import com.ga.airticketmanagement.dto.response.BookingResponse;
import com.ga.airticketmanagement.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {FlightMapper.class, UserMapper.class})
public interface BookingMapper {

    BookingResponse toResponse(Booking booking);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "bookedAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "flight", ignore = true)
    })
    Booking toEntity(BookingRequest bookingRequest);
}
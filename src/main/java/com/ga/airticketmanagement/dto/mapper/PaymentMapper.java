package com.ga.airticketmanagement.dto.mapper;

import com.ga.airticketmanagement.dto.request.PaymentRequest;
import com.ga.airticketmanagement.dto.response.PaymentResponse;
import com.ga.airticketmanagement.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface PaymentMapper {

    @Mappings({
            @Mapping(source = "booking.id", target = "bookingId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "user.emailAddress", target = "userEmail")
    })
    PaymentResponse toResponse(Payment payment);

    @Mappings({
            @Mapping(source = "booking.id", target = "bookingId"),
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "user.emailAddress", target = "userEmail")
    })
    PaymentWithBookingResponse toDetailedResponse(Payment payment);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "paidAt", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "transactionRef", ignore = true),
            @Mapping(target = "booking", ignore = true),
            @Mapping(target = "user", ignore = true)
    })
    Payment toEntity(PaymentRequest request);
}
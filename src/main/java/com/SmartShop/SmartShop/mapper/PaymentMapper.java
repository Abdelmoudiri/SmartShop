package com.SmartShop.SmartShop.mapper;

import com.SmartShop.SmartShop.dto.PaymentDTO;
import com.SmartShop.SmartShop.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "numeroPaiement", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "datePaiement", ignore = true)
    @Mapping(target = "dateEncaissement", ignore = true)
    Payment toEntity(PaymentDTO.CreateRequest request);

    PaymentDTO.Response toResponse(Payment payment);
}
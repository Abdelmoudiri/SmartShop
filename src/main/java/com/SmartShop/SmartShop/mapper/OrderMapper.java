package com.SmartShop.SmartShop.mapper;

import com.SmartShop.SmartShop.dto.OrderDTO;
import com.SmartShop.SmartShop.entities.Order;
import com.SmartShop.SmartShop.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "client.nom", target = "clientNom")
    @Mapping(source = "promoCode.code", target = "codePromoApplique")
    @Mapping(source = "total", target = "totalTTC")
    @Mapping(source = "remisePercentage", target = "remiseFidelitePercentage")
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderDTO.Response toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.nom", target = "nomProduit")
    OrderDTO.OrderItemResponse toItemResponse(OrderItem item);
}
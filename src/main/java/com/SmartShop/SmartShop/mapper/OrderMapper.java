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
    OrderDTO.Response toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.nom", target = "nomProduit")
    OrderDTO.OrderItemResponse toItemResponse(OrderItem item);
}
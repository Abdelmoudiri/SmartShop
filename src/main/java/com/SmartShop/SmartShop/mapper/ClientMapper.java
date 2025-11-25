package com.SmartShop.SmartShop.mapper;

import com.SmartShop.SmartShop.dto.ClientDTO;
import com.SmartShop.SmartShop.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO.Response toResponse(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "fidelite", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntityFromDto(ClientDTO.UpdateRequest request, @MappingTarget Client client);
}
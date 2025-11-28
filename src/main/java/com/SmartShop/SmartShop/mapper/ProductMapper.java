package com.SmartShop.SmartShop.mapper;

import com.SmartShop.SmartShop.dto.ProductDTO;
import com.SmartShop.SmartShop.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Product toEntity(ProductDTO.CreateRequest request);

    ProductDTO.Response toResponse(Product product);
}
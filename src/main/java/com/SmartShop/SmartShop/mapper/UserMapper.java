package com.SmartShop.SmartShop.mapper;


import com.SmartShop.SmartShop.dto.AuthDTO;
import com.SmartShop.SmartShop.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

   User toEntity(AuthDTO.RegisterRequest request);

   AuthDTO.RegisterRequest toResponse(User user);
   AuthDTO.UserInfoResponse toDto(User  user);

}

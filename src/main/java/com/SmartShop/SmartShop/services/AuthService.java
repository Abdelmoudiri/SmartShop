package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.dto.AuthDTO;
import com.SmartShop.SmartShop.entities.User;
import com.SmartShop.SmartShop.entities.enums.UserRole;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import com.SmartShop.SmartShop.exceptions.ResourceNotFoundException;
import com.SmartShop.SmartShop.repositories.UserRepository;
import com.SmartShop.SmartShop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User authenticate(AuthDTO.LoginRequest request){

      User u=userRepository.findByUsername(request.getUsername())
              .orElseThrow(()->new ResourceNotFoundException("user name or mot de pass ne trouve pas"));

      if (!PasswordUtil.checkPassword(request.getPassword(), u.getPassword())) {
          throw new BusinessException("Mot de passe incorrect");
      }

    return  u;
  }

  public User register(AuthDTO.RegisterRequest request){

      if (userRepository.findByUsername(request.getUsername()).isPresent()) {
          throw new BusinessException("Ce nom d'utilisateur existe déjà");
      }

      User user = new User();
      user.setUsername(request.getUsername());
      user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
      
      user.setRole(request.getRole() != null ? request.getRole() : UserRole.CLIENT);

      return userRepository.save(user);
  }
}

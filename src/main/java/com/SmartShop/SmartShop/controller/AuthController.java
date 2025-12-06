package com.SmartShop.SmartShop.controller;

import com.SmartShop.SmartShop.dto.AuthDTO;
import com.SmartShop.SmartShop.entities.Client;
import com.SmartShop.SmartShop.entities.User;
import com.SmartShop.SmartShop.entities.enums.UserRole;
import com.SmartShop.SmartShop.mapper.ClientMapper;
import com.SmartShop.SmartShop.mapper.UserMapper;
import com.SmartShop.SmartShop.repositories.ClientRepository;
import com.SmartShop.SmartShop.services.AuthService;
import com.SmartShop.SmartShop.services.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final ClientMapper clientMapper;
    private final ClientRepository  clientRepository;
    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.UserInfoResponse> login(@RequestBody @Valid AuthDTO.LoginRequest request, HttpServletRequest httpServletRequest)
    {
        User user=authService.authenticate(request);

        HttpSession session=httpServletRequest.getSession(true);
        session.setAttribute("currentUser",user);
        session.setMaxInactiveInterval(3600);

        AuthDTO.UserInfoResponse response=AuthDTO.UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .clientId(user.getClientDetails() != null ? user.getClientDetails().getId() : null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTO.UserInfoResponse> register(@RequestBody @Valid AuthDTO.RegisterRequest request, HttpServletRequest httpServletRequest){
        User user = authService.register(request);

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("currentUser", user);
        session.setMaxInactiveInterval(3600);

        AuthDTO.UserInfoResponse response = AuthDTO.UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .clientId(user.getClientDetails() != null ? user.getClientDetails().getId() : null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User u=(User)session.getAttribute("currentUser");
            if (u.getRole().equals(UserRole.ADMIN)){
                return  ResponseEntity.status(HttpStatus.OK).body(userMapper.toDto(u));
            }
            else {
                Client client = authorizationService.getCurrentClient();
                return  ResponseEntity.status(HttpStatus.OK).body(clientMapper.toResponse(client));
            }
            //return ResponseEntity.ok((User) session.getAttribute("currentUser"));
        }

        Map<String,String> map = Map.of("message" ,"UNAUTHORIZED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }

}

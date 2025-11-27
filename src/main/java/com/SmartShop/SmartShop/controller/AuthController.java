package com.SmartShop.SmartShop.controller;

import com.SmartShop.SmartShop.dto.AuthDTO;
import com.SmartShop.SmartShop.entities.User;
import com.SmartShop.SmartShop.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            return ResponseEntity.ok((User) session.getAttribute("currentUser"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/string")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("  SmartShop is running");
    }
}

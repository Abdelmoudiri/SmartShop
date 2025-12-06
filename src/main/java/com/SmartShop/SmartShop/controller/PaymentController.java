package com.SmartShop.SmartShop.controller;

import com.SmartShop.SmartShop.dto.PaymentDTO;
import com.SmartShop.SmartShop.entities.Payment;
import com.SmartShop.SmartShop.mapper.PaymentMapper;
import com.SmartShop.SmartShop.services.AuthorizationService;
import com.SmartShop.SmartShop.services.PaymentService;
import com.SmartShop.SmartShop.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<PaymentDTO.Response> createPayment(@RequestBody @Valid PaymentDTO.CreateRequest request) {
        SecurityUtil.requireAdmin();
        
        Payment payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponse(payment));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO.Response>> getAllPayments() {
        SecurityUtil.requireAdmin();
        
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentDTO.Response> responses = payments.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO.Response> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        authorizationService.checkOrderAccess(payment.getOrder().getId());
        
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @PostMapping("/{id}/encash")
    public ResponseEntity<PaymentDTO.Response> encashPayment(@PathVariable Long id) {
        SecurityUtil.requireAdmin();
        
        Payment payment = paymentService.encashPayment(id);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PaymentDTO.Response> rejectPayment(@PathVariable Long id) {
        SecurityUtil.requireAdmin();
        
        Payment payment = paymentService.rejectPayment(id, "Rejeté par l'administrateur");
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }
}

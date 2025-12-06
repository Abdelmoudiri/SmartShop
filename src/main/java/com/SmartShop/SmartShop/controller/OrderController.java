package com.SmartShop.SmartShop.controller;

import com.SmartShop.SmartShop.dto.OrderDTO;
import com.SmartShop.SmartShop.dto.PaymentDTO;
import com.SmartShop.SmartShop.entities.Order;
import com.SmartShop.SmartShop.entities.Payment;
import com.SmartShop.SmartShop.mapper.OrderMapper;
import com.SmartShop.SmartShop.mapper.PaymentMapper;
import com.SmartShop.SmartShop.services.AuthorizationService;
import com.SmartShop.SmartShop.services.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final AuthorizationService authorizationService;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    public ResponseEntity<OrderDTO.Response> createOrder(
            @Valid @RequestBody OrderDTO.CreateRequest request) {


        SecurityUtil.requireAdmin();

        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO.Response>> getAllOrders() {

        SecurityUtil.requireAdmin();

        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO.Response> responses = orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO.Response> getOrder(@PathVariable Long id) {
        authorizationService.checkOrderAccess(id);

        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderDTO.Response>> getClientOrders(@PathVariable Long clientId) {
        authorizationService.checkClientAccess(clientId);

        List<Order> orders = orderService.getOrdersByClientId(clientId);
        List<OrderDTO.Response> responses = orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDTO.Response> confirmOrder(@PathVariable Long id) {
        SecurityUtil.requireAdmin();

        Order order = orderService.confirmOrder(id);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO.Response> cancelOrder(@PathVariable Long id) {
        SecurityUtil.requireAdmin();

        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<OrderDTO.Response> rejectOrder(@PathVariable Long id) {
        SecurityUtil.requireAdmin();

        Order order = orderService.rejectOrder(id, "Rejeté par administrateur");
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentDTO.Response>> getOrderPayments(@PathVariable Long id) {
        authorizationService.checkOrderAccess(id);

        List<Payment> payments = paymentService.getPaymentsByOrderId(id);
        List<PaymentDTO.Response> responses = payments.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}

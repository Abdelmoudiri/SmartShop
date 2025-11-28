package com.SmartShop.SmartShop.controller;

import com.SmartShop.SmartShop.dto.ClientDTO;
import com.SmartShop.SmartShop.dto.OrderDTO;
import com.SmartShop.SmartShop.entities.Client;
import com.SmartShop.SmartShop.entities.Order;
import com.SmartShop.SmartShop.mapper.ClientMapper;
import com.SmartShop.SmartShop.mapper.OrderMapper;
import com.SmartShop.SmartShop.services.AuthorizationService;
import com.SmartShop.SmartShop.services.ClientService;
import com.SmartShop.SmartShop.services.OrderService;
import com.SmartShop.SmartShop.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final OrderService orderService;
    private final AuthorizationService authorizationService;
    private final ClientMapper clientMapper;
    private final OrderMapper orderMapper;


    @PostMapping
    public ResponseEntity<ClientDTO.Response> createClient(
            @Valid @RequestBody ClientDTO.CreateRequest request) {
        SecurityUtil.requireAdmin();

        Client client = clientService.createClient(request);
        ClientDTO.Response response = clientMapper.toResponse(client);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<ClientDTO.Response>> getAllClients() {
        SecurityUtil.requireAdmin();

        List<Client> clients = clientService.getAllClients();
        List<ClientDTO.Response> responses = clients.stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO.Response> getClient(@PathVariable Long id) {
        authorizationService.checkClientAccess(id);

        Client client = clientService.getClientById(id);
        ClientDTO.Response response = clientMapper.toResponse(client);
        
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO.Response> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDTO.UpdateRequest request) {
        SecurityUtil.requireAdmin();

        Client client = clientService.updateClient(id, request);
        ClientDTO.Response response = clientMapper.toResponse(client);
        
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        SecurityUtil.requireAdmin();

        clientService.deleteClient(id);
        
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderDTO.Response>> getClientOrders(@PathVariable Long id) {
        authorizationService.checkClientOrdersAccess(id);

        List<Order> orders = orderService.getOrdersByClientId(id);
        List<OrderDTO.Response> responses = orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}/statistics")
    public ResponseEntity<ClientDTO.Response> getClientStatistics(@PathVariable Long id) {
        authorizationService.checkClientAccess(id);

        Client client = clientService.getClientById(id);
        ClientDTO.Response response = clientMapper.toResponse(client);
        
        return ResponseEntity.ok(response);
    }
}

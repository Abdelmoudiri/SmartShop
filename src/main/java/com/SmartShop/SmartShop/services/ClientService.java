package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.dto.ClientDTO;
import com.SmartShop.SmartShop.entities.Client;
import com.SmartShop.SmartShop.entities.enums.CustomerTier;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import com.SmartShop.SmartShop.exceptions.ResourceNotFoundException;
import com.SmartShop.SmartShop.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client createClient(ClientDTO.CreateRequest request) {
        if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Un client avec cet email existe déjà");
        }

        Client client = Client.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .fidelite(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        return clientRepository.save(client);
    }


    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));
    }


    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Transactional
    public Client updateClient(Long id, ClientDTO.UpdateRequest request) {
        Client client = getClientById(id);

        if (request.getNom() != null) {
            client.setNom(request.getNom());
        }

        if (request.getEmail() != null && !request.getEmail().equals(client.getEmail())) {
            if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("Un client avec cet email existe déjà");
            }
            client.setEmail(request.getEmail());
        }

        return clientRepository.save(client);
    }


    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        
        if (client.getOrders() != null && !client.getOrders().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un client ayant des commandes");
        }
        
        clientRepository.delete(client);
    }


    @Transactional
    public void updateLoyaltyTier(Long clientId) {
        Client client = getClientById(clientId);

        CustomerTier newTier = calculateTier(client.getTotalOrders(), client.getTotalSpent());

        if (client.getFidelite() != newTier) {
            client.setFidelite(newTier);
            clientRepository.save(client);
        }
    }


    private CustomerTier calculateTier(int totalOrders, BigDecimal totalSpent) {

        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        }
        
        if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        }
        
        if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }
        
        return CustomerTier.BASIC;
    }


    @Transactional
    public void updateClientStatistics(Long clientId, BigDecimal orderAmount) {
        Client client = getClientById(clientId);

        client.setTotalOrders(client.getTotalOrders() + 1);

        client.setTotalSpent(client.getTotalSpent().add(orderAmount));

        LocalDateTime now = LocalDateTime.now();
        
        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(now);
        }
        
        client.setLastOrderDate(now);

        clientRepository.save(client);

        updateLoyaltyTier(clientId);
    }


    public int getLoyaltyDiscountPercentage(Long clientId, BigDecimal subtotal) {
        Client client = getClientById(clientId);
        CustomerTier tier = client.getFidelite();

        switch (tier) {
            case PLATINUM:
                return subtotal.compareTo(new BigDecimal("1200")) >= 0 ? 15 : 0;
            
            case GOLD:
                return subtotal.compareTo(new BigDecimal("800")) >= 0 ? 10 : 0;
            
            case SILVER:
                return subtotal.compareTo(new BigDecimal("500")) >= 0 ? 5 : 0;
            
            case BASIC:
            default:
                return 0;
        }
    }
}

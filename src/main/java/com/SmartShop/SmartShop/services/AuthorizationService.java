package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.entities.Client;
import com.SmartShop.SmartShop.entities.Order;
import com.SmartShop.SmartShop.entities.User;
import com.SmartShop.SmartShop.entities.enums.UserRole;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import com.SmartShop.SmartShop.repositories.ClientRepository;
import com.SmartShop.SmartShop.repositories.OrderRepository;
import com.SmartShop.SmartShop.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;


    public void checkClientAccess(Long clientId) {
        User currentUser = SecurityUtil.getCurrentUser();
        
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }
        
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new BusinessException("Client non trouvé"));
        
        if (!client.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("Accès refusé. Vous ne pouvez accéder qu'à votre propre profil.");
        }
    }
    public void checkOrderAccess(Long orderId) {
        User currentUser = SecurityUtil.getCurrentUser();
        
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("Commande non trouvée"));
        
        if (!order.getClient().getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException("Accès refusé. Vous ne pouvez accéder qu'à vos propres commandes.");
        }
    }


    public void requireAdmin() {
        SecurityUtil.requireAdmin();
    }

   public void requireAnyRole(UserRole... roles) {
        SecurityUtil.requireAnyRole(roles);
    }

    public Client getCurrentClient() {
        User currentUser = SecurityUtil.getCurrentUser();
        
        if (currentUser.getRole() != UserRole.CLIENT) {
            throw new BusinessException("L'utilisateur actuel n'est pas un client");
        }
        
        return clientRepository.findByUserId(currentUser.getClientDetails().getId())
            .orElseThrow(() -> new BusinessException("Aucun profil client associé à cet utilisateur"));
    }

    public void checkClientOrdersAccess(Long clientId) {
        checkClientAccess(clientId);
    }


    public void checkOrderPaymentsAccess(Long orderId) {
        checkOrderAccess(orderId);
    }


    public void checkOrderModificationAccess() {
        requireAdmin();
    }


    public void checkPaymentOperationAccess() {
        requireAdmin();
    }
}

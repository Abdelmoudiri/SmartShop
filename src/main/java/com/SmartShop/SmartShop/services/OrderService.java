package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.dto.OrderDTO;
import com.SmartShop.SmartShop.entities.*;
import com.SmartShop.SmartShop.entities.enums.OrderStatus;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import com.SmartShop.SmartShop.exceptions.ResourceNotFoundException;
import com.SmartShop.SmartShop.repositories.OrderRepository;
import com.SmartShop.SmartShop.repositories.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final PromoCodeRepository promoCodeRepository;

    @Value("${smartshop.tva.rate:0.20}")
    private BigDecimal tvaRate;

    @Transactional
    public Order createOrder(OrderDTO.CreateRequest request) {
        Client client = clientService.getClientById(request.getClientId());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal sousTotal = BigDecimal.ZERO;

        for (OrderDTO.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());

            productService.validateAndReserveStock(product.getId(), itemRequest.getQuantite());


            BigDecimal prixUnitaire = product.getPrixUnitaire();
            BigDecimal totalLigne = prixUnitaire.multiply(new BigDecimal(itemRequest.getQuantite()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantite(itemRequest.getQuantite())
                    .prixUnitaireSnapshot(prixUnitaire)
                    .totalLigne(totalLigne)
                    .build();

            orderItems.add(orderItem);
            sousTotal = sousTotal.add(totalLigne);
        }

        int loyaltyDiscountPercentage = clientService.getLoyaltyDiscountPercentage(client.getId(), sousTotal);

        int promoDiscountPercentage = 0;
        PromoCode promoCode = null;
        
        if (request.getCodePromo() != null && !request.getCodePromo().isEmpty()) {
            promoCode = validateAndUsePromoCode(request.getCodePromo());
            promoDiscountPercentage = promoCode.getRemisePercentage();
        }

        int totalDiscountPercentage = loyaltyDiscountPercentage + promoDiscountPercentage;
        BigDecimal montantRemise = sousTotal
                .multiply(new BigDecimal(totalDiscountPercentage))
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);


        BigDecimal montantApresRemise = sousTotal.subtract(montantRemise).setScale(2, RoundingMode.HALF_UP);

        BigDecimal tva = montantApresRemise.multiply(tvaRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalTTC = montantApresRemise.add(tva).setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
                .client(client)
                .status(OrderStatus.PENDING)
                .sousTotal(sousTotal)
                .remisePercentage(loyaltyDiscountPercentage)
                .montantRemise(montantRemise)
                .tva(tva)
                .total(totalTTC)
                .montantRestant(totalTTC)
                .promoCode(promoCode)
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        return orderRepository.save(order);
    }

    private PromoCode validateAndUsePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Code promo invalide: " + code));

        if (!promoCode.isActive()) {
            throw new BusinessException("Ce code promo n'est plus actif");
        }

        if (promoCode.getDateExpiration() != null && 
            promoCode.getDateExpiration().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("Ce code promo a expiré");
        }

        if (promoCode.isUtilisationUnique()) {
            promoCode.setActive(false);
            promoCodeRepository.save(promoCode);
        }

        return promoCode;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id));
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    @Transactional
    public Order confirmOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes PENDING peuvent être confirmées");
        }

        if (order.getMontantRestant().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("La commande doit être totalement payée avant confirmation. Reste à payer: " 
                + order.getMontantRestant() + " DH");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        clientService.updateClientStatistics(order.getClient().getId(), order.getTotal());

        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes PENDING peuvent être annulées");
        }

        for (OrderItem item : order.getItems()) {
            productService.restoreStock(item.getProduct().getId(), item.getQuantite());
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order rejectOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes PENDING peuvent être rejetées");
        }

        order.setStatus(OrderStatus.REJECTED);
        return orderRepository.save(order);
    }

    @Transactional
    public void updateRemainingAmount(Long orderId, BigDecimal paymentAmount) {
        Order order = getOrderById(orderId);
        
        BigDecimal newRemaining = order.getMontantRestant().subtract(paymentAmount);
        
        if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Le montant du paiement dépasse le montant restant");
        }
        
        order.setMontantRestant(newRemaining.setScale(2, RoundingMode.HALF_UP));
        orderRepository.save(order);
    }
}

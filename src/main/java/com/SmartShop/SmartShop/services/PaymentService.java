package com.SmartShop.SmartShop.services;

import com.SmartShop.SmartShop.dto.PaymentDTO;
import com.SmartShop.SmartShop.entities.Order;
import com.SmartShop.SmartShop.entities.Payment;
import com.SmartShop.SmartShop.entities.enums.PaymentStatus;
import com.SmartShop.SmartShop.entities.enums.PaymentType;
import com.SmartShop.SmartShop.exceptions.BusinessException;
import com.SmartShop.SmartShop.exceptions.ResourceNotFoundException;
import com.SmartShop.SmartShop.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    private static final BigDecimal ESPECES_LIMIT = new BigDecimal("20000");


    @Transactional
    public Payment createPayment(PaymentDTO.CreateRequest request) {
        Order order = orderService.getOrderById(request.getOrderId());

        if (order.getMontantRestant().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("Cette commande est déjà totalement payée");
        }

        if (request.getMontant().compareTo(order.getMontantRestant()) > 0) {
            throw new BusinessException(
                String.format("Le montant du paiement (%s DH) dépasse le montant restant (%s DH)",
                    request.getMontant(), order.getMontantRestant())
            );
        }

        validatePaymentType(request);

        int nextPaymentNumber = paymentRepository.countByOrderId(order.getId()) + 1;
        String numeroPaiement = String.format("PAY-ORD%d-%02d", order.getId(), nextPaymentNumber);

        PaymentStatus initialStatus = determineInitialStatus(request.getTypePaiement());

        Payment payment = Payment.builder()
                .order(order)
                .numeroPaiement(numeroPaiement)
                .montant(request.getMontant())
                .typePaiement(request.getTypePaiement())
                .statut(initialStatus)
                .datePaiement(LocalDate.now())
                .banque(request.getBanque())
                .reference(request.getReference())
                .dateEcheance(request.getDateEcheance())
                .build();


        if (initialStatus == PaymentStatus.ENCAISSE) {
            payment.setDateEncaissement(LocalDate.now());
        }

        Payment savedPayment = paymentRepository.save(payment);

        if (initialStatus == PaymentStatus.ENCAISSE) {
            orderService.updateRemainingAmount(order.getId(), payment.getMontant());
        }

        return savedPayment;
    }

    private void validatePaymentType(PaymentDTO.CreateRequest request) {
        switch (request.getTypePaiement()) {
            case ESPECE:
                if (request.getMontant().compareTo(ESPECES_LIMIT) > 0) {
                    throw new BusinessException(
                        String.format("Paiement en espèces limité à %s DH (Art. 193 CGI)", ESPECES_LIMIT)
                    );
                }
                break;

            case CHEQUE:
                if (request.getReference() == null || request.getReference().isEmpty()) {
                    throw new BusinessException("Le numéro de chèque est obligatoire");
                }
                if (request.getBanque() == null || request.getBanque().isEmpty()) {
                    throw new BusinessException("La banque est obligatoire pour un paiement par chèque");
                }
                if (request.getDateEcheance() == null) {
                    throw new BusinessException("La date d'échéance est obligatoire pour un chèque");
                }
                break;

            case VIREMENT:
                if (request.getReference() == null || request.getReference().isEmpty()) {
                    throw new BusinessException("La référence du virement est obligatoire");
                }
                if (request.getBanque() == null || request.getBanque().isEmpty()) {
                    throw new BusinessException("La banque est obligatoire pour un virement");
                }
                break;

            default:
                throw new BusinessException("Type de paiement non supporté");
        }
    }

    private PaymentStatus determineInitialStatus(PaymentType type) {
        if (type == PaymentType.ESPECE) {
            return PaymentStatus.ENCAISSE;
        }
        
        return PaymentStatus.EN_ATTENTE;
    }

    @Transactional
    public Payment encashPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatut() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Seuls les paiements EN_ATTENTE peuvent être encaissés");
        }

        payment.setStatut(PaymentStatus.ENCAISSE);
        payment.setDateEncaissement(LocalDate.now());
        
        Payment savedPayment = paymentRepository.save(payment);

        orderService.updateRemainingAmount(payment.getOrder().getId(), payment.getMontant());

        return savedPayment;
    }

    @Transactional
    public Payment rejectPayment(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatut() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Seuls les paiements EN_ATTENTE peuvent être rejetés");
        }

        payment.setStatut(PaymentStatus.REJETE);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'ID: " + id));
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }


    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}

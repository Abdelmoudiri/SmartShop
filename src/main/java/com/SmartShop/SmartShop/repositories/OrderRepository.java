package com.SmartShop.SmartShop.repositories;

import com.SmartShop.SmartShop.entities.Order;
import com.SmartShop.SmartShop.entities.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientId(Long clientId);

    List<Order> findByStatus(OrderStatus status);

    long countByClientIdAndStatus(Long clientId, OrderStatus status);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.client.id = :clientId AND o.status = 'CONFIRMED'")
    BigDecimal sumTotalSpentByClient(@Param("clientId") Long clientId);
}
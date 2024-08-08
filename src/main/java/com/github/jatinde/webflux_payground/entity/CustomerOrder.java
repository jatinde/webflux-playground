package com.github.jatinde.webflux_payground.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("CUSTOMER_ORDER") 
public record CustomerOrder(
    @Id UUID orderId, 
    @Column("CUSTOMER_ID") Integer customerId, 
    @Column("PRODUCT_ID") Integer productId,
    Integer amount,
    Instant orderDate
    ) {
    
        public CustomerOrder(Integer customerId, Integer productId, Integer amount, Instant orderDate) {
            this(null, customerId, productId, amount, orderDate);
        }
}

package com.github.jatinde.webflux_payground.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.github.jatinde.webflux_payground.common.model.Product;
import com.github.jatinde.webflux_payground.dto.OrderDetails;
import com.github.jatinde.webflux_payground.entity.CustomerOrder;

import reactor.core.publisher.Flux;

@Repository
public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {

    @Query("""
                SELECT
                p.*
            FROM
                customer c, customer_order co, product p
            WHERE
                c.name = :name and
                c.id = co.customer_id and
                p.id = co.product_id""")
    Flux<Product> getProductsOrderedbyCustomer(String name);

    @Query("""
             SELECT
                co.order_id,
                c.name AS customer_name,
                p.description AS product_name,
                co.amount,
                co.order_date
            FROM
                customer c
            INNER JOIN customer_order co ON c.id = co.customer_id
            INNER JOIN product p ON p.id = co.product_id
            WHERE
                p.description = :description
            ORDER BY co.amount DESC""")
    Flux<OrderDetails> getOrderDetailsByProduct(String description);
}

package com.claivenant.orderService.Service;

import com.claivenant.orderService.Entity.Order;
import com.claivenant.orderService.Model.OrderRequest;
import com.claivenant.orderService.Repository.OrderRepository;
import com.claivenant.orderService.external.client.PaymentService;
import com.claivenant.orderService.external.client.ProductService;
import com.claivenant.orderService.external.request.PaymentRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        log.info("Placing order:{}",orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());
        log.info("Creating Order with status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("Calling Payment service to complete the payment");
        PaymentRequest paymentRequest
                =PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus = null;
        try{
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successful.Changing the order status to PLACED");
            orderStatus ="PLACED";

        }catch (Exception e){
            log.error("Error occurred during payment.Changing order status to PAYMENT-FAILED");
            orderStatus = "PAYMENT_FAILED";


        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order Placed successful with Id :{}",order.getId());

        return order.getId();
    }
}

package com.claivenant.orderService.Service;

import com.claivenant.orderService.Entity.Order;
import com.claivenant.orderService.Model.OrderRequest;
import com.claivenant.orderService.Model.OrderResponse;
import com.claivenant.orderService.Model.ProductResponse;
import com.claivenant.orderService.Repository.OrderRepository;
import com.claivenant.orderService.exception.CustomException;
import com.claivenant.orderService.external.client.PaymentService;
import com.claivenant.orderService.external.client.ProductService;
import com.claivenant.orderService.external.request.PaymentRequest;
import com.claivenant.orderService.external.response.PaymentResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private RestTemplate restTemplate;
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

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for Order Id : {}",orderId);
        Order order
                =orderRepository.findById(orderId)
                .orElseThrow(()->new CustomException("Order not found for the order : "+orderId,"NOT_FOUND",404));
        log.info("Invoking Product service to fetch the product for id : {}",order.getProductId());

         ProductResponse productResponse
                =restTemplate.getForObject(
                        "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                 ProductResponse.class
         );
         log.info("Getting information from the PaymentService");
        PaymentResponse paymentResponse
                =restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/payment/"+order.getId(),
                PaymentResponse.class
        );


        OrderResponse.ProductDetails productDetails
                =OrderResponse.ProductDetails
                .builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .build();
        OrderResponse.PaymentDetails paymentDetails
                =OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();


        OrderResponse orderResponse
                =OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}

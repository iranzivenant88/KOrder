package com.claivenant.orderService.Service;

import com.claivenant.orderService.Entity.Order;
import com.claivenant.orderService.Model.OrderRequest;
import com.claivenant.orderService.Model.OrderResponse;
import com.claivenant.orderService.Model.PaymentMode;
import com.claivenant.orderService.Model.ProductResponse;
import com.claivenant.orderService.Repository.OrderRepository;
import com.claivenant.orderService.exception.CustomException;
import com.claivenant.orderService.external.client.PaymentService;
import com.claivenant.orderService.external.client.ProductService;
import com.claivenant.orderService.external.request.PaymentRequest;
import com.claivenant.orderService.external.response.PaymentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();



    @DisplayName("Get Order -Success Scenario")
    @Test
    void test_When_Order_Success(){
        Order order =getMockOrder();
        //for internal different calls , we use mock
        when(orderRepository.findById(anyLong()))
                        .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/"+order.getId(),
                PaymentResponse.class
        )).thenReturn(getMockPaymentResponse());

        //call actual method
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        //verification
        verify(orderRepository,times(1)).findById(anyLong());

       verify(restTemplate,times(1)).getForObject(
               "http://PRODUCT-SERVICE/product/" + order.getProductId(),
               ProductResponse.class);

        verify(restTemplate,times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/" + order.getId(),
                PaymentResponse.class);

        //Assert
        Assertions.assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getOrderId());
    }
    @DisplayName("Get Orders - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found(){
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception =
                assertThrows(CustomException.class,
                        () -> orderService.getOrderDetails(1));
        assertEquals("NOT_FOUND",exception.getErrorCode());
        assertEquals(404, exception.getStatus());
        verify(orderRepository,times(1))
                .findById(anyLong());

    }
    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2))
                .save(any());
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(),orderId);

    }
    @DisplayName("Place Order - Failed Scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_Then_Order_Placed(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2))
                .save(any());
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(),orderId);

    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }
    private ProductResponse getMockProductResponse() {
      return ProductResponse.builder()
              .productId(2)
              .productName("iPhone")
              .price(100)
              .quantity(200)
              .build();
    }
    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();
    }

}
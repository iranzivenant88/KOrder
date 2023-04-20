package com.claivenant.orderService.Service;

import com.claivenant.orderService.Entity.Order;
import com.claivenant.orderService.Model.OrderResponse;
import com.claivenant.orderService.Model.PaymentMode;
import com.claivenant.orderService.Model.ProductResponse;
import com.claivenant.orderService.Repository.OrderRepository;
import com.claivenant.orderService.external.client.PaymentService;
import com.claivenant.orderService.external.client.ProductService;
import com.claivenant.orderService.external.response.PaymentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {
    @Autowired
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();


    @Test
    @DisplayName("Get Order -Success Scenario")
    void test_When_Order_Success(){
        Order order =getMockOrder();
        //for internal different calls , we use mock
        when(orderRepository.findById(anyLong()))
                        .thenReturn(Optional.of(order));
//        Mockito.when(orderRepository.findById(anyLong()))
//                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class)).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/"+order.getId(),
                PaymentResponse.class)).thenReturn(getMockPaymentResponse());
        //call actual method
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        //verification
        verify(orderRepository,times(1)).findById(anyLong());

       verify(restTemplate,times(1)).getForObject(
                "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                PaymentResponse.class);

        verify(restTemplate,times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/"+order.getId(),
                PaymentResponse.class);

        //Assert
        Assertions.assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getOrderId());
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
              .price(1000)
              .quantity(20)
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
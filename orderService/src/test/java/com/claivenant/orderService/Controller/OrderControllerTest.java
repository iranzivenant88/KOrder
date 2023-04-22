package com.claivenant.orderService.Controller;

import com.claivenant.orderService.Entity.Order;
import com.claivenant.orderService.Model.OrderRequest;
import com.claivenant.orderService.Model.PaymentMode;
import com.claivenant.orderService.OrderServiceConfig;
import com.claivenant.orderService.Repository.OrderRepository;
import com.claivenant.orderService.Service.OrderService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.Charset.defaultCharset;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port = 0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})

public class OrderControllerTest {
    @Autowired
   private OrderService orderService ;

    @Autowired
    private OrderRepository orderRepository ;

    @Autowired
    private MockMvc mockMvc ;

    @RegisterExtension
    static WireMockExtension wireMockServer
            =WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8080))

            .build();
    private ObjectMapper objectMapper
            =new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
     @BeforeEach
    void setup() throws IOException {
      getProductDetailsResponse();
      doPayment();
      getPaymentDetails();
      reduceQuantity();

     }

 private void reduceQuantity() {
         wireMockServer.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                         .willReturn(aResponse()
                                 .withStatus(HttpStatus.OK.value())
                                 .withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE)));

 }

 private void getPaymentDetails() throws IOException {
         wireMockServer.stubFor(get(urlMatching("/payment/.*"))
                 .willReturn(aResponse()
                         .withStatus(HttpStatus.OK.value())
                         .withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE)
                         .withBody(
                                 copyToString(
                                         OrderControllerTest.class
                                                 .getClassLoader()
                                                 .getResourceAsStream("mock/GetPaymentDetails.json"),
                                         defaultCharset()))));

 }

 private void doPayment() {
         wireMockServer.stubFor(post(urlEqualTo("/payment"))
                 .willReturn(aResponse()
                         .withStatus(HttpStatus.OK.value())
                         .withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE)));
 }

 private void getProductDetailsResponse() throws IOException {
      //GET/product/1
  wireMockServer.stubFor((WireMock.get("/product/1")
          .willReturn((aResponse()
                  .withStatus(HttpStatus.OK.value())
                  .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                  .withBody(copyToString(
                          OrderControllerTest.class
                                  .getClassLoader()
                                  .getResourceAsStream("mock/GetProduct.json"),
                          defaultCharset()
                  ))
          ))));

 }

 @Test
    public void test_WhenPlaceOrder_DoPayment_Success() throws Exception{
        //First Place Order
       //Get Order By Order Id from Db and Check
        //Check Output
     OrderRequest orderRequest = getMockOrderRequest();
     MvcResult mvcResult
             =mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
             .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("Customer")))
             .content(MediaType.APPLICATION_JSON_VALUE)
             .content(objectMapper.writeValueAsString(orderRequest))
             ).andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
     String orderId = mvcResult.getResponse().getContentAsString();
     Optional<Order>order = orderRepository.findById(Long.valueOf(orderId));
     assertTrue(order.isPresent());

     assertEquals(Long.parseLong(orderId),o.getId());
     Order o = order.get();
     assertEquals("PLACED",o.getOrderStatus());
     assertEquals(orderRequest.getTotalAmount(),o.getAmount());
     assertEquals(orderRequest.getQuantity(),o.getQuantity());

    }

    private OrderRequest getMockOrderRequest() {
         return OrderRequest.builder()
                 .productId(1)
                 .paymentMode(PaymentMode.CASH)
                 .quantity(10)
                 .totalAmount(200)
                 .build();
    }

}
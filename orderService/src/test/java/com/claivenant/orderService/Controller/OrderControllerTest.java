package com.claivenant.orderService.Controller;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
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
 }

 private void getPaymentDetails() {

 }

 private void doPayment() {

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
                          Charset.defaultCharset()
                  ))
          ))));

 }

 @Test
    public void test_WhenPlaceOrder_DoPayment_Success(){
        //First Place Order
       //Get Order By Order Id from Db and Check
        //
    }

}
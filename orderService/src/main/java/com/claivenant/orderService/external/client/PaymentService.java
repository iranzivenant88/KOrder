package com.claivenant.orderService.external.client;

import com.claivenant.orderService.external.request.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentService {
    @PostMapping
   ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);
}

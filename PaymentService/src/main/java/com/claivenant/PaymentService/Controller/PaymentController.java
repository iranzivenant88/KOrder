package com.claivenant.PaymentService.Controller;

import com.claivenant.PaymentService.Model.PaymentRequest;
import com.claivenant.PaymentService.Model.PaymentResponse;
import com.claivenant.PaymentService.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long>doPayment(@RequestBody PaymentRequest paymentRequest){
        return  new ResponseEntity<>(
                paymentService.doPayment(paymentRequest), HttpStatus.OK);

    }
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse>getPaymentDetailsByOrderId(@PathVariable String orderId){
        return new ResponseEntity<>(
                paymentService.getPaymentDetailsByOrderId(orderId),HttpStatus.OK
        );

  }
}

package com.claivenant.PaymentService.Service;

import com.claivenant.PaymentService.Model.PaymentRequest;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}

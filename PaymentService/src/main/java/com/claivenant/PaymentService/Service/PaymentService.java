package com.claivenant.PaymentService.Service;

import com.claivenant.PaymentService.Model.PaymentRequest;
import com.claivenant.PaymentService.Model.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    PaymentResponse getPaymentDetailsByOrderId(String orderId);

    long doPayment(PaymentRequest paymentRequest);
}

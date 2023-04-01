package com.claivenant.orderService.Service;

import com.claivenant.orderService.Model.OrderRequest;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);
}

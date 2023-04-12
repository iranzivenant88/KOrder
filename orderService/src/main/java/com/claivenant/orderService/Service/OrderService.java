package com.claivenant.orderService.Service;

import com.claivenant.orderService.Model.OrderRequest;
import com.claivenant.orderService.Model.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}

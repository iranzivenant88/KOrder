package com.claivenant.ProductService.Service;

import com.claivenant.ProductService.Model.ProductRequest;
import com.claivenant.ProductService.Model.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}

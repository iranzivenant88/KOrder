package com.claivenant.ProductService.Service;

import com.claivenant.ProductService.Entity.Product;
import com.claivenant.ProductService.Exception.ProductServiceCustomException;
import com.claivenant.ProductService.Model.ProductRequest;
import com.claivenant.ProductService.Model.ProductResponse;
import com.claivenant.ProductService.Repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product..");
        Product product
                = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product created!");

        return product.getProductId();

    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for product Id");
        Product product
                =productRepository.findById(productId)
                .orElseThrow(
                        ()->new ProductServiceCustomException("Product with " +productId+ "is not found ","PRODUCT_NOT_FOUND")
                );
        ProductResponse productResponse
                =new ProductResponse();
        copyProperties(product,productResponse);

        return productResponse;
    }
}

package com.claivenant.ProductService.Controller;

import com.claivenant.ProductService.Model.ProductRequest;
import com.claivenant.ProductService.Model.ProductResponse;
import com.claivenant.ProductService.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;
    @RequestMapping
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest){
        long productId = productService.addProduct(productRequest);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);

    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse>getProductById(@PathVariable("id") long productId){
        ProductResponse productResponse
                =productService.getProductById(productId);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);

    }
}
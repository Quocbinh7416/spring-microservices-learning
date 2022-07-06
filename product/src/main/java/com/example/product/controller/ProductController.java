package com.example.product.controller;

import com.example.product.dto.Product;
import com.example.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/addProduct")
    ResponseEntity<Product> addProduct(@Valid @RequestBody Product product){
        String status = productService.addProduct(product);
        log.info("Product add status - {}", status);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/productList")
    List<Product> productList(){
        log.info("Listing product");
        List<Product> productList = productService.listAllProducts();
        log.info("All product return - {}", productList);
        return productList;
    }

    @GetMapping("/productList/{category}")
    List<Product> productList(@PathVariable String category){
        return productService.productCategoryList(category);
    }

    @GetMapping("/product/{id}")
    Product productById(@PathVariable String id){
        return productService.productById(id);
    }

        @PutMapping("/productUpdate")
    String updateProduct(@RequestBody Product product){

        return productService.updateProduct(product);
    }

    @DeleteMapping("/product/{id}")
    String deleteProductById(@PathVariable String id){
        return productService.deleteProductById(id);
    }
}

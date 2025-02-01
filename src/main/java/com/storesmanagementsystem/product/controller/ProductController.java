package com.storesmanagementsystem.product.controller;

import com.storesmanagementsystem.product.contracts.CommonResponse;
import com.storesmanagementsystem.product.contracts.DealerProductInfoBean;
import com.storesmanagementsystem.product.contracts.ProductInfoBean;
import com.storesmanagementsystem.product.contracts.UserInfoBean;
import com.storesmanagementsystem.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping(path = "/Product")
    public ResponseEntity<CommonResponse> addProduct(@RequestPart("user") UserInfoBean userInfoBean, @RequestPart("file") MultipartFile file) {
        System.out.println(file);
        System.out.println(userInfoBean);
        CommonResponse respStructure = getRespStructure();
//
        if (null != userInfoBean.getId()) {
            boolean isAdded = productService.addProduct(userInfoBean, file);
            
            if (isAdded) {
                respStructure.setStatus("SUCCESS");
                return ResponseEntity.status(HttpStatus.CREATED).body(respStructure);
            } else {
                respStructure.setStatus("FAILED");
                return ResponseEntity.badRequest().body(respStructure);
            }
        } else {
            throw new IllegalArgumentException("Provide User Details");
        }
//        return  ResponseEntity.status(HttpStatus.CREATED).body(respStructure);
    }
    
    @PostMapping(path = "/Product/Order")
    public ResponseEntity<CommonResponse> addDealerProduct(@RequestBody UserInfoBean userInfoBean) {
        System.out.println(userInfoBean);
        CommonResponse respStructure = getRespStructure();
//
        if (null != userInfoBean.getId()) {
            boolean isAdded = productService.addDealerProduct(userInfoBean);
            if (isAdded) {
                respStructure.setStatus("SUCCESS");
                return ResponseEntity.status(HttpStatus.CREATED).body(respStructure);
            } else {
                respStructure.setStatus("FAILED");
                return ResponseEntity.badRequest().body(respStructure);
            }
        } else {
            throw new IllegalArgumentException("Provide User Details");
        }
//        return  ResponseEntity.status(HttpStatus.CREATED).body(respStructure);
    }
    

//    @PostMapping(path = "/Product", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<CommonResponse> addProduct(@RequestBody UserInfoBean userInfoBean) {
//        CommonResponse respStructure = getRespStructure();
//        if (null != userInfoBean.getId()) {
//            boolean isAdded = productService.addProduct(userInfoBean, null);
//
//            if (isAdded) {
//                respStructure.setStatus("SUCCESS");
//                return ResponseEntity.status(HttpStatus.CREATED).body(respStructure);
//            } else {
//                respStructure.setStatus("FAILED");
//                return ResponseEntity.badRequest().body(respStructure);
//            }
//        } else {
//            throw new IllegalArgumentException("Provide User Details");
//        }
//    }

    @GetMapping(path = "/Products", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getProducts(@RequestParam("userId") Integer userId) {
        List<ProductInfoBean> products = productService.getAllProducts(userId);
        CommonResponse respStructure = getRespStructure();
        if (null != products && !products.isEmpty()) {
            respStructure.setProducts(products);
            return ResponseEntity.status(HttpStatus.OK).body(respStructure);
        } else {
            return ResponseEntity.badRequest().body(respStructure);
        }
    }

    @GetMapping(path = "/Products/All",  consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllProductsForDealer() {
        List<ProductInfoBean> products = productService.getAllProductsForDealer();
        CommonResponse respStructure = getRespStructure();
        if (null != products && !products.isEmpty()) {
            respStructure.setProducts(products);
            return ResponseEntity.status(HttpStatus.OK).body(respStructure);
        } else {
            return ResponseEntity.badRequest().body(respStructure);
        }
    }

    @GetMapping(path = "/Product/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getProduct(@PathVariable("id") Integer id) {
        ProductInfoBean products = productService.getProduct(id);
        CommonResponse respStructure = getRespStructure();
        if (null != products) {
            respStructure.setProduct(products);
            return ResponseEntity.status(HttpStatus.OK).body(respStructure);
        } else {
            return ResponseEntity.badRequest().body(respStructure);
        }
    }
    
    @GetMapping(path = "/Product/{id}/Dealer/{dealerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getDealerProduct(@PathVariable("id") Integer id, @PathVariable("dealerId") Integer dealerId ) {
    	DealerProductInfoBean products = productService.getDealerProduct(id, dealerId);
        CommonResponse respStructure = getRespStructure();
        if (null != products) {
            respStructure.setDealerProd(products);
            return ResponseEntity.status(HttpStatus.OK).body(respStructure);
        } else {
            return ResponseEntity.badRequest().body(respStructure);
        }
    }

    @PutMapping(path = "/Product", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> updateProduct(@RequestParam(value = "transactionType", required = true) String transactionType, @RequestBody ProductInfoBean productInfoBean) {
        boolean isUpdated = productService.updateProduct(productInfoBean, transactionType);
        CommonResponse respStructure = getRespStructure();
        if (isUpdated) {
            respStructure.setStatus("SUCCESS");
            return ResponseEntity.ok().body(respStructure);
        } else {
            respStructure.setStatus("FAILED");
            return ResponseEntity.badRequest().body(respStructure);
        }
    }

    @DeleteMapping(path = "/Product/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> deleteProduct(@PathVariable("id") Integer id) {
        CommonResponse respStructure = getRespStructure();
        boolean isDeleted = productService.removeProduct(id);
        if (isDeleted) {
            respStructure.setStatus("SUCCESS");
            return ResponseEntity.ok().body(respStructure);
        } else {
            respStructure.setStatus("FAILED");
            return ResponseEntity.badRequest().body(respStructure);
        }
    }

    private CommonResponse getRespStructure() {
        return new CommonResponse();
    }


}

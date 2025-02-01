package com.storesmanagementsystem.product.controller;

import com.storesmanagementsystem.product.contracts.CommonResponse;
import com.storesmanagementsystem.product.contracts.DealerProductInfoBean;
import com.storesmanagementsystem.product.exceptions.DetailsNotFoundException;
import com.storesmanagementsystem.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DealerProductController {

    @Autowired
    ProductService productService;

    @GetMapping(path = "/Product/Dealer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getDealerProduct(@PathVariable(value = "id", required = true) Integer id) {
        CommonResponse respStructure = getRespStructure();
        DealerProductInfoBean dealerProduct = productService.getDealerProduct(id);
        if (null != dealerProduct) {
            respStructure.setDealerProd(dealerProduct);
            return ResponseEntity.ok().body(respStructure);
        } else {
            throw new DetailsNotFoundException();
        }
    }

    @GetMapping(path = "/Product/Dealer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllDealerProducts(@RequestParam(value = "userId", required = true) Integer userId) {
        CommonResponse respStructure = getRespStructure();
        List<DealerProductInfoBean> dealerProducts = productService.getAllDealerProducts(userId);
        if (null != dealerProducts && !dealerProducts.isEmpty()) {
            respStructure.setDealerProds(dealerProducts);
            return ResponseEntity.ok().body(respStructure);
        } else {
            throw new DetailsNotFoundException();
        }
    }

    @GetMapping(path = "/Product/Dealer/All", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllDealerProductsForCustomer() {
        CommonResponse respStructure = getRespStructure();
        List<DealerProductInfoBean> dealerProducts = productService.getAllDealerProductsForCustomer();
        if (null != dealerProducts && !dealerProducts.isEmpty()) {
            respStructure.setDealerProds(dealerProducts);
            return ResponseEntity.ok().body(respStructure);
        } else {
            throw new DetailsNotFoundException();
        }
    }

    @PutMapping(path = "/Product/Dealer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> updateDealerProduct(@RequestBody DealerProductInfoBean bean,
                                                      @RequestParam(value = "transactionType", required = true) String transactionType) {
        CommonResponse respStructure = getRespStructure();
        boolean isUpdated = productService.updateDealerProduct(bean, transactionType);
        if (isUpdated) {
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

package com.storesmanagementsystem.product.util;

import com.storesmanagementsystem.product.contracts.ProductInfoBean;
import com.storesmanagementsystem.product.contracts.UserInfoBean;
import com.storesmanagementsystem.product.domain.ProductDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Component
public class FileUtil {
    public ProductDetails getDbProduct(MultipartFile file, ProductInfoBean cameProd, UserInfoBean user) {
        ProductDetails product = new ProductDetails();
        product.setManufacturerName(user.getName());
        product.setProductName(cameProd.getProductName());
        product.setProductCost(cameProd.getProductCost());
        product.setQuantity(cameProd.getQuantity());
        product.setDescription(cameProd.getDescription());
        product.setUserId(user.getId());
        verifyImage(file,product);
        return product;
    }

    private void verifyImage(MultipartFile file, ProductDetails product){
       String fileName = StringUtils.cleanPath(file.getOriginalFilename());
       if(fileName.contains("..")){
           throw new IllegalArgumentException("not a valid file");
       }
        try {
            product.setImageUrl(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

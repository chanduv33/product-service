package com.storesmanagementsystem.product.client.config;

import com.storesmanagementsystem.product.contracts.CommonResponse;
import com.storesmanagementsystem.product.contracts.UserInfoBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "userServiceClient",url = "http://localhost:8085/User", configuration = UserServiceClientConfig.class)
public interface UserServiceClient {

    @GetMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getUser(@PathVariable("id") int id);

}

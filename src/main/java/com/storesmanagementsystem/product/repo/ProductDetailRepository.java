package com.storesmanagementsystem.product.repo;

import com.storesmanagementsystem.product.domain.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductDetailRepository extends JpaRepository<ProductDetails,Integer> {

    @Query(value = "select * from product_info where user_id=?1",nativeQuery = true)
    List<ProductDetails> findProductDetailsByUserId(int id);
    
    Optional<ProductDetails> findByIdAndUserId(int id, int userId);
}

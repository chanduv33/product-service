package com.storesmanagementsystem.product.repo;

import com.storesmanagementsystem.product.domain.DealerProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealerProductRepository extends JpaRepository<DealerProduct, Integer> {

    DealerProduct findDealerProductByDealerIdAndId(Integer dealerId, Integer productId);

    List<DealerProduct> findDealerProductByDealerId(Integer dealerId);
}

package com.storesmanagementsystem.product.service;

import com.storesmanagementsystem.product.contracts.DealerProductInfoBean;
import com.storesmanagementsystem.product.contracts.ProductInfoBean;
import com.storesmanagementsystem.product.contracts.UserInfoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

//    public boolean addProduct(UserInfoBean bean);

    public boolean addProduct(UserInfoBean bean, MultipartFile file);
    
    public boolean addDealerProduct(UserInfoBean bean);

    public List<ProductInfoBean> getAllProducts(int userId);

    public boolean updateProduct(ProductInfoBean bean, String transactionType);

    public boolean removeProduct(int productId);

    public ProductInfoBean getProduct(int productId);

    public boolean updateDealerProduct(DealerProductInfoBean productInfoBean, String transactionType);

    public List<DealerProductInfoBean> getAllDealerProducts(int userId);

    public List<DealerProductInfoBean> getAllDealerProductsForCustomer();

    public DealerProductInfoBean getDealerProduct(int id);

    public List<ProductInfoBean> getAllProductsForDealer();

	DealerProductInfoBean getDealerProduct(int productId, int dealerId);
}

package com.storesmanagementsystem.product.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storesmanagementsystem.product.client.config.UserServiceClient;
import com.storesmanagementsystem.product.contracts.CommonResponse;
import com.storesmanagementsystem.product.contracts.DealerProductInfoBean;
import com.storesmanagementsystem.product.contracts.ProductInfoBean;
import com.storesmanagementsystem.product.contracts.TransactionType;
import com.storesmanagementsystem.product.contracts.UserInfoBean;
import com.storesmanagementsystem.product.domain.DealerProduct;
import com.storesmanagementsystem.product.domain.ProductDetails;
import com.storesmanagementsystem.product.exceptions.DetailsNotFoundException;
import com.storesmanagementsystem.product.repo.DealerProductRepository;
import com.storesmanagementsystem.product.repo.ProductDetailRepository;
import com.storesmanagementsystem.product.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	@Autowired
	UserServiceClient userServiceClient;

	@Autowired
	ProductDetailRepository productsRepository;

	@Autowired
	DealerProductRepository dealerProductRepository;

	@Autowired
	FileUtil fileUtil;

	@Autowired
	ObjectMapper mapper;

	@Override
	public boolean addProduct(UserInfoBean bean, MultipartFile file) {
		CommonResponse response = userServiceClient.getUser(bean.getId());
		if (null != response && null != response.getUser()) {
			UserInfoBean user = response.getUser();
			if (user.getRole().contains("MANUFACTURER")) {
				if (null != bean.getProducts() && !bean.getProducts().isEmpty()) {
					Iterator<ProductInfoBean> itr = bean.getProducts().iterator();
					if (itr.hasNext()) {
						ProductInfoBean cameProd = itr.next();
						ProductDetails product = fileUtil.getDbProduct(file, cameProd, user);
						productsRepository.save(product);
						return true;
					}
				} else {
					throw new IllegalArgumentException("Please provide product details to add ");
				}
			} else if (user.getRole().contains("DEALER")) {
				return addDealerProduct(bean);
			}
		} else if (null != response && null != response.getErrors() && !response.getErrors().isEmpty()) {
			throw new IllegalArgumentException(response.getErrors().get(0).getErrorMessage());
		} else {
			throw new DetailsNotFoundException();
		}
		return false;
	}

	@Override
	public List<ProductInfoBean> getAllProducts(int userId) {
		List<ProductDetails> byUserId = productsRepository.findProductDetailsByUserId(userId);
		if (!byUserId.isEmpty()) {
			return byUserId.stream().map(p -> {
				ProductInfoBean productInfoBean = mapper.convertValue(p, ProductInfoBean.class);
				return productInfoBean;
			}).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public synchronized boolean updateProduct(ProductInfoBean bean, String transactionType) {
		Optional<ProductDetails> byId = productsRepository.findById(bean.getId());
		if (byId.isPresent()) {
			ProductDetails setProd = byId.get();
			if (transactionType.equals(TransactionType.UPDATE_DETAILS.name())) {
				if (null != bean.getProductName())
					setProd.setProductName(bean.getProductName());
				if (null != bean.getQuantity())
					setProd.setQuantity(bean.getQuantity());
				if (null != bean.getProductCost())
					setProd.setProductCost(bean.getProductCost());
			} else if (transactionType.equals(TransactionType.UPDATE_PRICE.name())) {
				if (null != bean.getProductCost())
					setProd.setProductCost(bean.getProductCost());
				else
					throw new IllegalArgumentException("productCost cannot be null");
			} else if (transactionType.equals(TransactionType.UPDATE_QUANTITY.name())) {
				if (null != bean.getQuantity())
					setProd.setQuantity(bean.getQuantity());
				else
					throw new IllegalArgumentException("productCost quantity be null");
			} else {
				throw new IllegalArgumentException("Invalid Transaction Type");
			}
			productsRepository.save(setProd);
			return true;
		} else {
			throw new DetailsNotFoundException();
		}
	}

	@Override
	public synchronized boolean removeProduct(int productId) {
		Optional<ProductDetails> byId = productsRepository.findById(productId);
		if (byId.isPresent()) {
			try {
				productsRepository.deleteById(productId);
				return true;
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
				return false;
			}
		} else {
			throw new DetailsNotFoundException();
		}
	}

	@Override
	public ProductInfoBean getProduct(int productId) {
		Optional<ProductDetails> byId = productsRepository.findById(productId);
		if (byId.isPresent()) {
			ProductDetails setProd = byId.get();
			ProductInfoBean productInfoBean = mapper.convertValue(setProd, ProductInfoBean.class);
			return productInfoBean;
		} else {
			throw new DetailsNotFoundException();
		}
	}

	@Override
	public DealerProductInfoBean getDealerProduct(int productId, int dealerId) {
		DealerProduct byId = dealerProductRepository.findDealerProductByDealerIdAndId(dealerId, productId);
		if (byId != null) {

			DealerProductInfoBean productInfoBean = mapper.convertValue(byId, DealerProductInfoBean.class);
			//ProductInfoBean productInfoBean = mapper.convertValue(byId, ProductInfoBean.class);
			return productInfoBean;
		} else {
			throw new DetailsNotFoundException();
		}
	}
	
	@Override
	public boolean updateDealerProduct(DealerProductInfoBean productInfoBean, String transactionType) {
		Optional<DealerProduct> byId = dealerProductRepository.findById(productInfoBean.getId());
		if (byId.isPresent()) {
			DealerProduct dealerProduct = byId.get();
			if (transactionType.equals(TransactionType.UPDATE_SALES_DETAILS.name())) {
				if (null != productInfoBean.getProductName())
					dealerProduct.setProductName(productInfoBean.getProductName());
				if (null != productInfoBean.getImageUrl())
					dealerProduct.setImageUrl(productInfoBean.getImageUrl());
			} else if (transactionType.equals(TransactionType.UPDATE_SALES_PRICE.name())) {
				if (null != productInfoBean.getSellingPrice())
					dealerProduct.setSellingPrice(productInfoBean.getSellingPrice());
				else
					throw new IllegalArgumentException("Selling Price cannot be null");
			} else if (transactionType.equals(TransactionType.UPDATE_SALES_QUANTITY.name())) {
				if (null != productInfoBean.getQuantity()) {
					dealerProduct.setQuantity(dealerProduct.getQuantity() - productInfoBean.getQuantity());
				} else
					throw new IllegalArgumentException("Quantity cannot be null");

			} else if (transactionType.equals(TransactionType.ORDER_UPDATE_SALES_QUANTITY.name())) {
				if (null != productInfoBean.getQuantity()) {
					if (productInfoBean.getQuantity() < dealerProduct.getQuantity()) {
						dealerProduct.setQuantity(dealerProduct.getQuantity() - productInfoBean.getQuantity());
					} else {
						throw new IllegalArgumentException("Specified quantity is more than available quantity");
					}
				} else
					throw new IllegalArgumentException("Quantity cannot be null");

			} else {
				throw new IllegalArgumentException("Invalid Transaction Type");
			}
			dealerProductRepository.save(dealerProduct);
			return true;
		} else {
			throw new DetailsNotFoundException();
		}
	}

	@Override
	public List<DealerProductInfoBean> getAllDealerProducts(int userId) {
		List<DealerProduct> byDealerId = dealerProductRepository.findDealerProductByDealerId(userId);
		if (!byDealerId.isEmpty()) {
			return byDealerId.stream().map(p -> {
				return mapper.convertValue(p, DealerProductInfoBean.class);
			}).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("No Products found");
		}
	}

	@Override
	public List<DealerProductInfoBean> getAllDealerProductsForCustomer() {
		List<DealerProduct> all = dealerProductRepository.findAll();
		if (all != null) {
			return all.stream().map(p -> {
				return mapper.convertValue(p, DealerProductInfoBean.class);
			}).collect(Collectors.toList());
		} else {
			throw new IllegalArgumentException("No Products found");
		}
	}

	@Override
	public DealerProductInfoBean getDealerProduct(int id) {
		Optional<DealerProduct> byId = dealerProductRepository.findById(id);
		if (byId.isPresent()) {
			DealerProduct dealerProduct = byId.get();
			return mapper.convertValue(dealerProduct, DealerProductInfoBean.class);
		} else {
			throw new DetailsNotFoundException();
		}
	}

	@Override
	public List<ProductInfoBean> getAllProductsForDealer() {
		List<ProductDetails> byUserId = productsRepository.findAll();
		if (!byUserId.isEmpty()) {
			return byUserId.stream().map(p -> {
				ProductInfoBean productInfoBean = mapper.convertValue(p, ProductInfoBean.class);
				return productInfoBean;
			}).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public boolean addDealerProduct(UserInfoBean bean) {

		if (null != bean.getProducts() && !bean.getDealersProds().isEmpty()) {
			Iterator<DealerProductInfoBean> iterator = bean.getDealersProds().iterator();
			if (iterator.hasNext()) {
				boolean isExistingProd = false;
				DealerProductInfoBean prod = iterator.next();
				DealerProduct dealerProd = dealerProductRepository
						.findDealerProductByDealerIdAndId(bean.getId(), prod.getProductId());
				Optional<ProductDetails> byId = productsRepository.findById(prod.getProductId());
				if (byId.isPresent()) {
					ProductDetails productDetails = byId.get();
					if (prod.getQuantity() < productDetails.getQuantity()) {
						productDetails.setQuantity(productDetails.getQuantity() - prod.getQuantity());
						productsRepository.save(productDetails);
					} else {
						throw new IllegalArgumentException("Specified quantity is more than available quantity");
					}
					if (null != dealerProd) {
						dealerProd.setQuantity(dealerProd.getQuantity() + prod.getQuantity());
						if (null != prod.getSellingPrice())
							dealerProd.setSellingPrice(prod.getSellingPrice());
						isExistingProd = true;
						dealerProductRepository.save(dealerProd);
					}
					if (!isExistingProd) {
						DealerProduct dealerProds = new DealerProduct();
						dealerProds.setManufacturerName(productDetails.getManufacturerName());
						dealerProds.setProductId(prod.getProductId());
						dealerProds.setDealerId(bean.getId());
						dealerProds.setQuantity(prod.getQuantity());
						if (null != prod.getSellingPrice())
							dealerProds.setSellingPrice(prod.getSellingPrice());
						else
							dealerProds.setSellingPrice(productDetails.getProductCost());
						if (null != prod.getProductName()) {
							dealerProds.setProductName(prod.getProductName());
						} else {
							dealerProds.setProductName(productDetails.getProductName());
						}

						if (null != prod.getImageUrl())
							dealerProds.setImageUrl(prod.getImageUrl());
						else
							dealerProds.setImageUrl(productDetails.getImageUrl());
						dealerProductRepository.save(dealerProds);
					}
				} else {
					throw new DetailsNotFoundException();
				}
				return true;
			}
		} else {
			throw new IllegalArgumentException("Please provide dealer product details to add ");
		}
		return false;
	}
}

package com.storesmanagementsystem.product.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString.Exclude;

import javax.persistence.*;

@Data
@Entity
@Table(name = "product_info")
public class ProductDetails {
	
	@Id
	@GeneratedValue
	@Column
	private int id;
	
	@Column(nullable = false)
	private String productName;
	
	@Column(nullable = false)
	private double productCost;
	
	@Column 
	private String description;

	@Lob @Basic(fetch = FetchType.EAGER)
	@Column(name = "imageUrl")
	private String imageUrl;
	
	@Column
	private int quantity;
	
	
	@Column
	private String manufacturerName;

	@Column
	private Integer userId;
}

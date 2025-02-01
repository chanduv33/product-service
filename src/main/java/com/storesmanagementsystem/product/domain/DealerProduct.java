package com.storesmanagementsystem.product.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString.Exclude;

import javax.persistence.*;

@Entity
@Data
@Table(name = "dealer_prods")
public class DealerProduct {
	
	@Id
	@GeneratedValue
	@Column
	private int id;
	
	@Column
	private double sellingPrice;
	
	@Column
	private int quantity;

	@Column
	private int productId;
	
	@Column
	private int minimumQuantity;

	@Lob @Basic(fetch = FetchType.EAGER)
	@Column(name = "imageUrl", columnDefinition = "MEDIUMBLOB")
	private String imageUrl;
	
	@Column
	private String productName;
	
	@Column
	private String manufacturerName;
	
	@Column
	private Integer dealerId ;

}

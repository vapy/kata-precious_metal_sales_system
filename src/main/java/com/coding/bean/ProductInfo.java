package com.coding.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品信息实体类
 * @author wangpengfei
 *
 */
public class ProductInfo {
	//产品编号
	private String productNo;
	//产品名称
	private String productName;
	//单位
	private String unit;
	//单价
	private BigDecimal price;
	//优惠券
	private String discountCoupon;
	//打折券列表
	private List<String> discountList;
	
	public ProductInfo(String productNo, String productName, BigDecimal price, String discountCoupon,
			List<String> discountList) {
		super();
		this.productNo = productNo;
		this.productName = productName;
		this.price = price;
		this.discountCoupon = discountCoupon;
		this.discountList = discountList;
	}
	public String getProductNo() {
		return productNo;
	}
	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public List<String> getDiscountList() {
		return discountList;
	}
	public void setDiscountList(List<String> discountList) {
		this.discountList = discountList;
	}
	public String getDiscountCoupon() {
		return discountCoupon;
	}
	public void setDiscountCoupon(String discountCoupon) {
		this.discountCoupon = discountCoupon;
	}
}
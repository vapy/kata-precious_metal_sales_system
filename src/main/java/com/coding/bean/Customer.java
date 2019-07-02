package com.coding.bean;

import java.math.BigDecimal;

/**
 * 客户信息实体类
 * @author wangpengfei
 *
 */
public class Customer {
	//客户姓名
	private String name;
	//等级
	private String MemberType;
	//会员卡号
	private String memberNo;
	//会员积分
	private BigDecimal memberPoints;

	public Customer(String name, String memberType, String memberNo, BigDecimal memberPoints) {
		super();
		this.name = name;
		MemberType = memberType;
		this.memberNo = memberNo;
		this.memberPoints = memberPoints;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMemberType() {
		return MemberType;
	}
	public void setMemberType(String memberType) {
		MemberType = memberType;
	}
	public String getMemberNo() {
		return memberNo;
	}
	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}
	public BigDecimal getMemberPoints() {
		return memberPoints;
	}
	public void setMemberPoints(BigDecimal memberPoints) {
		this.memberPoints = memberPoints;
	}
	
}
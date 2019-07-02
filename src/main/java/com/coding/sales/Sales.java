package com.coding.sales;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.coding.Constant.Constant;
import com.coding.bean.Customer;
import com.coding.bean.ProductInfo;
import com.coding.sales.input.OrderCommand;
import com.coding.sales.input.OrderItemCommand;
import com.coding.sales.input.PaymentCommand;
import com.coding.sales.output.DiscountItemRepresentation;
import com.coding.sales.output.OrderItemRepresentation;
import com.coding.sales.output.OrderRepresentation;
import com.coding.sales.output.PaymentRepresentation;

public class Sales {
	private static Map<String, Customer> customerMap = new HashMap<String, Customer>();
	private static Map<String, ProductInfo> productInfoMap = new HashMap<String, ProductInfo>();
	//订单总金额
	private static BigDecimal totalPrice = new BigDecimal(0);
	//优惠总金额
	private static BigDecimal totalDiscountPrice = new BigDecimal(0);
	//应收金额
	private static BigDecimal receivables = new BigDecimal(0);
	//付款使用的打折券
	private static List<String> discountCards = new ArrayList<String>();
	//本次消费会员新增的积分
	private static BigDecimal memberPointsIncreased = new BigDecimal(0);
	//当前用户
	private static Customer customer = null;
	//会员最新的积分
	private static BigDecimal memberPoints= new BigDecimal(0);
	//原会员等级
	private static String oldMemberType = "";
	//新会员等级
	private static String newMemberType = "";
	static {
		customerMap.put("6236609999",new Customer("马丁","普卡","6236609999",new BigDecimal(9860)));
		customerMap.put("6630009999",new Customer("王立","金卡","6630009999",new BigDecimal(48860)));
		customerMap.put("8230009999",new Customer("李想","白金卡","8230009999",new BigDecimal(98860)));
		customerMap.put("9230009999",new Customer("张三","钻石卡","9230009999",new BigDecimal(198860)));
		
		productInfoMap.put("001001", new ProductInfo("001001", "世园会五十国钱币册", new BigDecimal(998.00), "", new ArrayList<String>() {}));
		productInfoMap.put("001002", new ProductInfo("001002", "2019北京世园会纪念银章大全40g", new BigDecimal(1380.00), Constant.DISCOUNT_COUPON_DESC_90, new ArrayList<String>() {}));
		productInfoMap.put("003001", new ProductInfo("003001", "招财进宝", new BigDecimal(1580.00), Constant.DISCOUNT_COUPON_DESC_95, new ArrayList<String>() {}));
		productInfoMap.put("003002", new ProductInfo("003002", "水晶之恋", new BigDecimal(980.00), "", new ArrayList<String>() {{add(Constant.DISCOUNT_LIST_DESC_01);add(Constant.DISCOUNT_LIST_DESC_02);}}));
		productInfoMap.put("002002", new ProductInfo("002002", "中国经典钱币套装", new BigDecimal(998.00), "", new ArrayList<String>() {{add(Constant.DISCOUNT_LIST_DESC_2000);add(Constant.DISCOUNT_LIST_DESC_1000);}}));
		productInfoMap.put("002001", new ProductInfo("002001", "守扩之羽比翼双飞4.8g", new BigDecimal(1080.00), Constant.DISCOUNT_COUPON_DESC_95, new ArrayList<String>() {{add(Constant.DISCOUNT_LIST_DESC_01);add(Constant.DISCOUNT_LIST_DESC_02);}}));
		productInfoMap.put("002003", new ProductInfo("002003", "中国银象棋12g", new BigDecimal(698.00), Constant.DISCOUNT_COUPON_DESC_90, new ArrayList<String>() {{add(Constant.DISCOUNT_LIST_DESC_3000);add(Constant.DISCOUNT_LIST_DESC_2000);add(Constant.DISCOUNT_LIST_DESC_1000);}}));

		
	}
	public static OrderRepresentation sales(OrderCommand command) {
		//获取用户信息
		String memberId = command.getMemberId();
		String orderId = command.getOrderId();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//格式化订单时间
		String createTime = command.getCreateTime();
		Date date = null;
		if(ObjectUtils.isNotEmpty(createTime)){
			try {
				date = dateFormat.parse(createTime);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("日期格式化错误。");
			}
		}
		customer = customerMap.get(memberId);
		oldMemberType = customer.getMemberType();
		List<PaymentCommand> paymentCommandList = command.getPayments();
		BigDecimal amount = paymentCommandList.get(0).getAmount();
		if(amount.compareTo(BigDecimal.ZERO) < 1){
			throw new IllegalArgumentException("余额不足！");
		}
		List<OrderItemCommand> orderItemCommandList = command.getItems();
		List<OrderItemRepresentation> orderItemRepresentationList = getOrderItemRepresentation(orderItemCommandList);
		List<String> discountsList = command.getDiscounts();
		List<DiscountItemRepresentation> discountItemRepresentationList = getDiscountItemRepresentation(orderItemCommandList,discountsList);
		receivables = totalPrice.subtract(totalDiscountPrice);
		if(amount.compareTo(receivables) == -1){
			 throw new IllegalArgumentException("余额不足！");
		}
		List<PaymentRepresentation> paymentRepresentationList = getPaymentRepresentations(paymentCommandList);
		getMemberPointsIncreased();
		OrderRepresentation orderRepresentation = new OrderRepresentation(orderId,date,memberId,customer.getName(),oldMemberType,newMemberType,memberPointsIncreased.intValue(),memberPoints.intValue(),orderItemRepresentationList,totalPrice,discountItemRepresentationList,totalDiscountPrice,receivables,paymentRepresentationList,discountCards);
		return orderRepresentation;
		
	}
	/**
	 * 获取销售凭证中的订单行列表
	 * @param orderItemCommandList
	 * @return
	 */
	private static List<OrderItemRepresentation> getOrderItemRepresentation(List<OrderItemCommand> orderItemCommandList){
		List<OrderItemRepresentation> list = new ArrayList<OrderItemRepresentation>();
		for(Iterator<OrderItemCommand> itr = orderItemCommandList.iterator();itr.hasNext();){
			OrderItemCommand orderItemCommand = itr.next();
			//获取商品编号
			String productNo = orderItemCommand.getProduct();
			ProductInfo productInfo = productInfoMap.get(productNo);
			//产品数量
			BigDecimal amount = orderItemCommand.getAmount();
			//产品小计
			BigDecimal subTotal = amount.multiply(productInfo.getPrice());
			totalPrice = totalPrice.add(subTotal);
			OrderItemRepresentation orderItemRepresentation = new OrderItemRepresentation(productNo,productInfo.getProductName(),productInfo.getPrice(),amount,subTotal);
			list.add(orderItemRepresentation);
		}
		return list;
	}
	/**
	 * 获取优惠信息
	 * @param orderItemCommandList
	 * @return
	 */
	private static List<DiscountItemRepresentation> getDiscountItemRepresentation(List<OrderItemCommand> orderItemCommandList,List<String> discounts){
		List<DiscountItemRepresentation> list = new ArrayList<DiscountItemRepresentation>();
		for(OrderItemCommand orderItemCommand : orderItemCommandList){
			//获取商品编号
			String productNo = orderItemCommand.getProduct();
			ProductInfo productInfo = productInfoMap.get(productNo);
			//产品数量
			BigDecimal amount = orderItemCommand.getAmount();
			//产品优惠金额
			BigDecimal discountAmount;
			//产品小计
			BigDecimal subTotal = amount.multiply(productInfo.getPrice());
			//产品优惠券
			String discountCoupon = productInfo.getDiscountCoupon();
			//计算优惠券优惠后金额
			BigDecimal discountCouponAmount = new BigDecimal(0);
			String discountCouponDESC = "";
			if(null != discountCoupon && !"".equals(discountCoupon)){
				if(Constant.DISCOUNT_COUPON_DESC_90.equals(discountCoupon)){
					if(discounts.contains(discountCoupon)){
						//计算9折优惠后金额
						discountCouponAmount = subTotal.multiply(new BigDecimal("0.1"));
						discountCouponDESC = Constant.DISCOUNT_COUPON_DESC_90;
					}
				}else if(Constant.DISCOUNT_COUPON_DESC_95.equals(discountCoupon)){
					if(discounts.contains(discountCoupon)){
						//计算95折优惠后金额
						discountCouponAmount = subTotal.multiply(new BigDecimal("0.05"));
						discountCouponDESC = Constant.DISCOUNT_COUPON_DESC_95;
					}
				}
			}
			//产品满减券
			List<String> discountList = productInfo.getDiscountList();
			//计算满减券优惠后金额
			BigDecimal discountListAmount = new BigDecimal(0);
			if (ObjectUtils.isNotEmpty(discountList)) {
				BigDecimal discountAmount1 = new BigDecimal(0);
				BigDecimal discountAmount2 = new BigDecimal(0);
				if (discountList.contains(Constant.DISCOUNT_LIST_DESC_3000)) {
					discountAmount1 = new BigDecimal(350)
							.multiply(subTotal.divideToIntegralValue(new BigDecimal(3000)));
				} else if (discountList.contains(Constant.DISCOUNT_LIST_DESC_2000)) {
					discountAmount1 = new BigDecimal(30)
							.multiply(subTotal.divideToIntegralValue(new BigDecimal(2000)));
				} else if (discountList.contains(Constant.DISCOUNT_LIST_DESC_1000)) {
					discountAmount1 = new BigDecimal(10)
							.multiply(subTotal.divideToIntegralValue(new BigDecimal(1000)));
				}
				if (amount.compareTo(new BigDecimal(3)) > -1) {
					if (discountList.contains(Constant.DISCOUNT_LIST_DESC_01)) {
						discountAmount2 = discountAmount2.subtract(productInfo.getPrice().divide(new BigDecimal(2)));
					}
					if (amount.compareTo(new BigDecimal(3)) == 1
							&& discountList.contains(Constant.DISCOUNT_LIST_DESC_02)) {
						discountAmount2 = discountAmount2.subtract(productInfo.getPrice());
					}
				}
				discountListAmount = discountAmount1.compareTo(discountAmount2) == 1 ? discountAmount1
						: discountAmount2;
			}
			if(discountCouponAmount.compareTo(BigDecimal.ZERO) ==0 && discountListAmount.compareTo(BigDecimal.ZERO) == 0){
				continue;
			}
			if(discountCouponAmount.compareTo(discountListAmount) > -1){
				discountAmount = discountCouponAmount;
				if(!discountCards.contains(discountCouponDESC)){
					discountCards.add(discountCouponDESC);
				}
			}else {
				discountAmount = discountListAmount;
			}
			totalDiscountPrice = totalDiscountPrice.add(discountAmount);
			DiscountItemRepresentation discountItemRepresentation = new DiscountItemRepresentation(productNo,productInfo.getProductName(),discountAmount);
			list.add(discountItemRepresentation);
		}
		return list;
	}
	/**
	 * 获取支付信息
	 * @param paymentCommandList
	 * @return
	 */
	private static List<PaymentRepresentation> getPaymentRepresentations(List<PaymentCommand> paymentCommandList){
		List<PaymentRepresentation> list = new ArrayList<PaymentRepresentation>();
		for(PaymentCommand paymentCommand : paymentCommandList){
			PaymentRepresentation paymentRepresentation = new PaymentRepresentation(paymentCommand.getType(),paymentCommand.getAmount());
			list.add(paymentRepresentation);
		}
		return list;
	}
	/**
	 * 计算用户新增积分
	 */
	private static void getMemberPointsIncreased(){
		String memberType = customer.getMemberType();
		switch (memberType) {
		case Constant.MEMBER_TYPE_01:
			memberPointsIncreased = receivables.divideToIntegralValue(new BigDecimal(1));
			memberPoints = memberPointsIncreased.add(customer.getMemberPoints());
			break;
		case Constant.MEMBER_TYPE_02:
			memberPointsIncreased = receivables.divideToIntegralValue(new BigDecimal(1));
			memberPoints = memberPointsIncreased.multiply(new BigDecimal("1.5")).add(customer.getMemberPoints());
			break;
		case Constant.MEMBER_TYPE_03:
			memberPointsIncreased = receivables.divideToIntegralValue(new BigDecimal(1));
			memberPoints = memberPointsIncreased.multiply(new BigDecimal("1.8")).add(customer.getMemberPoints());
			break;
		default:
			memberPointsIncreased = receivables.divideToIntegralValue(new BigDecimal(1));
			memberPoints = memberPointsIncreased.multiply(new BigDecimal(2)).add(customer.getMemberPoints());
			break;
		}
		if(memberPoints.compareTo(new BigDecimal(10000)) == -1){
			return;
		}else if(memberPoints.compareTo(new BigDecimal(50000)) == -1){
			newMemberType = Constant.MEMBER_TYPE_02;
		}else if(memberPoints.compareTo(new BigDecimal(100000)) == -1){
			newMemberType = Constant.MEMBER_TYPE_03;
		}else{
			newMemberType = Constant.MEMBER_TYPE_04;
		}
	}
}
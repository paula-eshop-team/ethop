package com.eshop.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.eshop.common.Const;
import com.eshop.common.ServerResponse;
import com.eshop.dao.CartMapper;
import com.eshop.dao.OrderItemMapper;
import com.eshop.dao.OrderMapper;
import com.eshop.dao.PayInfoMapper;
import com.eshop.dao.ProductMapper;
import com.eshop.dao.ShippingMapper;
import com.eshop.pojo.Cart;
import com.eshop.pojo.Order;
import com.eshop.pojo.OrderItem;
import com.eshop.pojo.PayInfo;
import com.eshop.pojo.Product;
import com.eshop.pojo.Shipping;
import com.eshop.service.IOrderService;
import com.eshop.utilities.BigDecimalUtil;
import com.eshop.utilities.DateTimeUtil;
import com.eshop.utilities.FTPUtil;
import com.eshop.utilities.PropertiesUtil;
import com.eshop.vo.OrderItemVo;
import com.eshop.vo.OrderProductVo;
import com.eshop.vo.OrderVo;
import com.eshop.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author Paula Lin
 *
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	private static AlipayTradeService tradeService;
	static {
		Configs.init("zfbinfo.properties");
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
	}

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private PayInfoMapper payInfoMapper;
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ShippingMapper shippingMapper;

	/**
     * @param userId
	 * @param shippingId
	 * @return
	 * 
	 */
	public ServerResponse createOrder(Integer userId, Integer shippingId) {
		List<Cart> cartList = cartMapper.selectAllCheckedCartByUserId(userId);
		ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
		if (!serverResponse.isSuccess()) {
			return serverResponse;
		}
		List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
		BigDecimal payment = this.getOrderTotalPrice(orderItemList);
		Order order = this.assembleOrder(userId, shippingId, payment);
		if (order == null) {
			return ServerResponse.createByErrorMessage("生成订单错误");
		}
		if (CollectionUtils.isEmpty(orderItemList)) {
			return ServerResponse.createByErrorMessage("购物车为空");
		}
		for (OrderItem orderItem : orderItemList) {
			orderItem.setOrderNo(order.getOrderNo());
		}
		orderItemMapper.batchInsertOrderItems(orderItemList);
		this.reduceProductStock(orderItemList);
		this.cleanCart(cartList);

		OrderVo orderVo = assembleOrderVo(order, orderItemList);
		return ServerResponse.createBySuccess(orderVo);
	}

	/**
     * @param order
	 * @param orderItemList
	 * @return
	 * 
	 */
	private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
		OrderVo orderVo = new OrderVo();
		orderVo.setOrderNo(order.getOrderNo());
		orderVo.setPayment(order.getPayment());
		orderVo.setPaymentType(order.getPaymentType());
		orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

		orderVo.setPostage(order.getPostage());
		orderVo.setStatus(order.getStatus());
		orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

		orderVo.setShippingId(order.getShippingId());
		Shipping shipping = shippingMapper.selectShippingByPrimaryKey(order.getShippingId());
		if (shipping != null) {
			orderVo.setReceiverName(shipping.getReceiverName());
			orderVo.setShippingVo(assembleShippingVo(shipping));
		}

		orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
		orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
		orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
		orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
		orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

		orderVo.setImageHost(PropertiesUtil.getStringProperty("ftp.server.http.prefix"));

		List<OrderItemVo> orderItemVoList = Lists.newArrayList();

		for (OrderItem orderItem : orderItemList) {
			OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
			orderItemVoList.add(orderItemVo);
		}
		orderVo.setOrderItemVoList(orderItemVoList);
		return orderVo;
	}

	/**
     * @param orderItem
	 * @return
	 * 
	 */
	private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
		OrderItemVo orderItemVo = new OrderItemVo();
		orderItemVo.setOrderNo(orderItem.getOrderNo());
		orderItemVo.setProductId(orderItem.getProductId());
		orderItemVo.setProductName(orderItem.getProductName());
		orderItemVo.setProductImage(orderItem.getProductImage());
		orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
		orderItemVo.setQuantity(orderItem.getQuantity());
		orderItemVo.setTotalPrice(orderItem.getTotalPrice());

		orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
		return orderItemVo;
	}

	/**
     * @param shipping
	 * @return
	 * 
	 */
	private ShippingVo assembleShippingVo(Shipping shipping) {
		ShippingVo shippingVo = new ShippingVo();
		shippingVo.setReceiverName(shipping.getReceiverName());
		shippingVo.setReceiverAddress(shipping.getReceiverAddress());
		shippingVo.setReceiverProvince(shipping.getReceiverProvince());
		shippingVo.setReceiverCity(shipping.getReceiverCity());
		shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVo.setReceiverMobile(shipping.getReceiverMobile());
		shippingVo.setReceiverZip(shipping.getReceiverZip());
		shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
		return shippingVo;
	}

	/**
     * @param cartList
	 * 
	 */
	private void cleanCart(List<Cart> cartList) {
		for (Cart cart : cartList) {
			cartMapper.deleteCartByPrimaryKey(cart.getId());
		}
	}

	/**
     * @param orderItemList
	 * 
	 */
	private void reduceProductStock(List<OrderItem> orderItemList) {
		for (OrderItem orderItem : orderItemList) {
			Product product = productMapper.selectProductMapperByPrimaryKey(orderItem.getProductId());
			product.setStock(product.getStock() - orderItem.getQuantity());
			productMapper.updateProductMapperByPrimaryKeySelective(product);
		}
	}

	/**
     * @param userId
     * @param shippingId
     * @param payment
	 * @return
	 */
	private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
		Order order = new Order();
		long orderNo = this.generateOrderNo();
		order.setOrderNo(orderNo);
		order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
		order.setPostage(0);
		order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
		order.setPayment(payment);

		order.setUserId(userId);
		order.setShippingId(shippingId);
		int rowCount = orderMapper.insertOrderMapper(order);
		if (rowCount > 0) {
			return order;
		}
		return null;
	}
	
	/**
	 * @return
	 */
	private long generateOrderNo() {
		long currentTime = System.currentTimeMillis();
		return currentTime + new Random().nextInt(100);
	}

	/**
     * @param orderItemList
	 * @return
	 */
	private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
		BigDecimal payment = new BigDecimal("0");
		for (OrderItem orderItem : orderItemList) {
			payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
		return payment;
	}

	/**
	 * @param userId
     * @param cartList
	 * @return
	 */
	private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
		List<OrderItem> orderItemList = Lists.newArrayList();
		if (CollectionUtils.isEmpty(cartList)) {
			return ServerResponse.createByErrorMessage("购物车为空");
		}
		for (Cart cartItem : cartList) {
			OrderItem orderItem = new OrderItem();
			Product product = productMapper.selectProductMapperByPrimaryKey(cartItem.getProductId());
			if (product != null && Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
				return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在线售卖状态");
			}
			if (product != null && cartItem.getQuantity() > product.getStock()) {
				return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
			}

			orderItem.setUserId(userId);
			if (product != null) {
				orderItem.setProductId(product.getId());
				orderItem.setProductName(product.getName());
				orderItem.setProductImage(product.getMainImage());
				orderItem.setCurrentUnitPrice(product.getPrice());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
				orderItemList.add(orderItem);
			} 
		}
		return ServerResponse.createBySuccess(orderItemList);
	}

	/**
	 * @param userId
     * @param orderNo
	 * @return
	 */
	public ServerResponse<String> cancelOrder(Integer userId, Long orderNo) {
		Order order = orderMapper.selectOrderMapperByUserIdAndOrderNo(userId, orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("该用户此订单不存在");
		}
		if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
			return ServerResponse.createByErrorMessage("已付款,无法取消订单");
		}
		Order updateOrder = new Order();
		updateOrder.setId(order.getId());
		updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

		int row = orderMapper.updateOrderMapperByPrimaryKeySelective(updateOrder);
		if (row > 0) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}

	/**
	 * @param userId
	 * @return
	 */
	public ServerResponse getOrderByCartProduct(Integer userId) {
		OrderProductVo orderProductVo = new OrderProductVo();
		List<Cart> cartList = cartMapper.selectAllCheckedCartByUserId(userId);
		ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
		if (!serverResponse.isSuccess()) {
			return serverResponse;
		}
		List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
		List<OrderItemVo> orderItemVoList = Lists.newArrayList();

		BigDecimal payment = new BigDecimal("0");
		for (OrderItem orderItem : orderItemList) {
			payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
			orderItemVoList.add(assembleOrderItemVo(orderItem));
		}
		orderProductVo.setProductTotalPrice(payment);
		orderProductVo.setOrderItemVoList(orderItemVoList);
		orderProductVo.setImageHost(PropertiesUtil.getStringProperty("ftp.server.http.prefix"));
		return ServerResponse.createBySuccess(orderProductVo);
	}

	/**
	 * @param userId
     * @param orderNo
	 * @return
	 */
	public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
		Order order = orderMapper.selectOrderMapperByUserIdAndOrderNo(userId, orderNo);
		if (order != null) {
			List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNoUserId(orderNo, userId);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);
			return ServerResponse.createBySuccess(orderVo);
		}
		return ServerResponse.createByErrorMessage("没有找到该订单");
	}

	/**
	 * @param userId
     * @param pageNum
     * @param pageSize
	 * @return
	 */
	public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Order> orderList = orderMapper.selectOrderMapperByUserId(userId);
		List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
		PageInfo pageResult = new PageInfo(orderList);
		pageResult.setList(orderVoList);
		return ServerResponse.createBySuccess(pageResult);
	}

	/**
	 * @param orderList
     * @param userId
	 * @return
	 */
	private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
		List<OrderVo> orderVoList = Lists.newArrayList();
		for (Order order : orderList) {
			List<OrderItem> orderItemList = Lists.newArrayList();
			if (userId == null) {
				orderItemList = orderItemMapper.getOrderItemByOrderNo(order.getOrderNo());
			} else {
				orderItemList = orderItemMapper.getOrderItemByOrderNoUserId(order.getOrderNo(), userId);
			}
			OrderVo orderVo = assembleOrderVo(order, orderItemList);
			orderVoList.add(orderVo);
		}
		return orderVoList;
	}

	/**
	 * @param orderNo
     * @param userId
     * @param path
	 * @return
	 */
	public ServerResponse getQrCodeAddressForPayment(Long orderNo, Integer userId, String path) {
		Map<String, String> resultMap = Maps.newHashMap();
		Order order = orderMapper.selectOrderMapperByUserIdAndOrderNo(userId, orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("用户没有该订单");
		}
		resultMap.put("orderNo", String.valueOf(order.getOrderNo()));
		String outTradeNo = order.getOrderNo().toString();
		String subject = new StringBuilder().append("eshop扫码支付,订单号:").append(outTradeNo).toString();
		String totalAmount = order.getPayment().toString();
		String undiscountableAmount = "0";
		String sellerId = "";
		String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount)
				.append("元").toString();
		String operatorId = "test_operator_id";
		String storeId = "test_store_id";
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");
		String timeoutExpress = "120m";

		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNoUserId(orderNo, userId);
		for (OrderItem orderItem : orderItemList) {
			GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
					BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue())
							.longValue(),
					orderItem.getQuantity());
			goodsDetailList.add(goods);
		}

		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder().setSubject(subject)
				.setTotalAmount(totalAmount).setOutTradeNo(outTradeNo).setUndiscountableAmount(undiscountableAmount)
				.setSellerId(sellerId).setBody(body).setOperatorId(operatorId).setStoreId(storeId)
				.setExtendParams(extendParams).setTimeoutExpress(timeoutExpress)
				.setNotifyUrl(PropertiesUtil.getStringProperty("alipay.callback.url"))// 支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			logger.info("支付宝预下单成功: )");
			AlipayTradePrecreateResponse response = result.getResponse();
			dumpResponse(response);

			File folder = new File(path);
			if (!folder.exists()) {
				folder.setWritable(true);
				folder.mkdirs();
			}

			String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
			String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
			ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

			File targetFile = new File(path, qrFileName);
			try {
				FTPUtil.uploadFile(Lists.newArrayList(targetFile));
			} catch (IOException e) {
				logger.error("上传二维码异常", e);
			}
			logger.info("qrPath:" + qrPath);
			String qrUrl = PropertiesUtil.getStringProperty("ftp.server.http.prefix") + targetFile.getName();
			resultMap.put("qrUrl", qrUrl);
			return ServerResponse.createBySuccess(resultMap);
		case FAILED:
			logger.error("支付宝预下单失败!!!");
			return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

		case UNKNOWN:
			logger.error("系统异常，预下单状态未知!!!");
			return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

		default:
			logger.error("不支持的交易状态，交易返回异常!!!");
			return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
		}
	}

	/**
	 * @param response
	 */
	private void dumpResponse(AlipayResponse response) {
		if (response != null) {
			logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(), response.getSubMsg()));
			}
			logger.info("body:" + response.getBody());
		}
	}

	/**
	 * @param params
	 * @return
	 */
	public ServerResponse aliCallback(Map<String, String> params) {
		Long orderNo = Long.parseLong(params.get("out_trade_no"));
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		Order order = orderMapper.selectoOrderMapperByOrderNo(orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("非快乐慕商城的订单,回调忽略");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
			return ServerResponse.createBySuccess("支付宝重复调用");
		}
		if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
			order.setStatus(Const.OrderStatusEnum.PAID.getCode());
			orderMapper.updateOrderMapperByPrimaryKeySelective(order);
		}

		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);

		payInfoMapper.insertPayInfo(payInfo);
		return ServerResponse.createBySuccess();
	}

	/**
	 * @param userId
	 * @param orderNo
	 * @return
	 */
	public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
		Order order = orderMapper.selectOrderMapperByUserIdAndOrderNo(userId, orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("用户没有该订单");
		}
		if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}


	/**
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public ServerResponse<PageInfo> manageList(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Order> orderList = orderMapper.selectAllOrderMapper();
		List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
		PageInfo pageResult = new PageInfo(orderList);
		pageResult.setList(orderVoList);
		return ServerResponse.createBySuccess(pageResult);
	}

	/**
	 * @param orderNo
	 * @return
	 */
	public ServerResponse<OrderVo> manageDetail(Long orderNo) {
		Order order = orderMapper.selectoOrderMapperByOrderNo(orderNo);
		if (order != null) {
			List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNo(orderNo);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);
			return ServerResponse.createBySuccess(orderVo);
		}
		return ServerResponse.createByErrorMessage("订单不存在");
	}

	/**
	 * @param orderNo
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Order order = orderMapper.selectoOrderMapperByOrderNo(orderNo);
		if (order != null) {
			List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNo(orderNo);
			OrderVo orderVo = assembleOrderVo(order, orderItemList);

			PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
			pageResult.setList(Lists.newArrayList(orderVo));
			return ServerResponse.createBySuccess(pageResult);
		}
		return ServerResponse.createByErrorMessage("订单不存在");
	}

	/**
	 * @param orderNo
	 * @return
	 */
	public ServerResponse<String> manageSendGoods(Long orderNo) {
		Order order = orderMapper.selectoOrderMapperByOrderNo(orderNo);
		if (order != null) {
			if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
				order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
				order.setSendTime(new Date());
				orderMapper.updateOrderMapperByPrimaryKeySelective(order);
				return ServerResponse.createBySuccess("发货成功");
			}
		}
		return ServerResponse.createByErrorMessage("订单不存在");
	}

}

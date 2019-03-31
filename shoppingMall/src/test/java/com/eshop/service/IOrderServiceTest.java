package com.eshop.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Order;
import com.eshop.pojo.Shipping;
import com.eshop.pojo.User;

@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class IOrderServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private IOrderService iOrderService;
	
	@Autowired
	private IShippingService iShippingService;
	
	@Autowired
	private IUserService iUserService;
	
	private List<Order> orderList = new ArrayList<Order>();
	
	
	private Order order = null;
	private Shipping shipping = null;
	private User user = null;
	
	@Before
	public void setUp() {
		List<User> userList = iUserService.findUserListInfo();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
		
		List<Shipping> shippingList = iShippingService.findAllShippingListInfo();
		if (!shippingList.isEmpty()) {
			shipping = shippingList.get(0);
		}
		//orderList = iOrderService.getOrderList(userId, pageNum, pageSize);
		if (!orderList.isEmpty()) {
			order = orderList.get(0);
		}
	}
	
	@After
	public void clear() {
		orderList.clear();
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testCreateOrder() {
		if (user != null && shipping != null) {
			ServerResponse<?> resp = iOrderService.createOrder(user.getId(), shipping.getId());
			Assert.assertNotNull(resp);
		} else {
			Assert.fail();
		}
	}

}

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
import com.eshop.pojo.Cart;
import com.eshop.pojo.Product;
import com.eshop.pojo.User;
import com.eshop.service.ICartService;
import com.eshop.service.IProductService;
import com.eshop.service.IUserService;
import com.eshop.vo.CartVo;

/**
 * Created by Paula
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ICartServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private ICartService iCartService;
	
	@Autowired
	private IProductService iProductService;
	
	@Autowired
	private IUserService iUserService;
	
	private List<Cart> cartList = new ArrayList<Cart>();
	
	private Cart cart = null;
	private Product product = null;
	private User user = null;
	
	@Before
	public void setUp() {
		cartList = iCartService.findCartListInfo();
		List<Product> productList = iProductService.getAllProductList();
		List<User> userList = iUserService.findUserListInfo();
		if (!cartList.isEmpty()) {
			cart = cartList.get(0);
		}
		if (!productList.isEmpty()) {
			product = productList.get(0);
		}
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
	}
	
	@After
	public void clear() {
		cartList.clear();
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testAddProduct() {
		if (product != null && user != null) {
			ServerResponse<CartVo> cart = iCartService.addProduct(user.getId(), product.getId(), 1);
			Assert.assertNotNull(cart);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProduct() {
		if (product != null && user != null) {
			ServerResponse<CartVo> cart = iCartService.updateProduct(user.getId(), product.getId(), 1);
			Assert.assertNotNull(cart);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteProduct() {
		if (product != null && user != null) {
			String prodctIds = String.valueOf(product.getId());
			ServerResponse<CartVo> cart = iCartService.deleteProduct(user.getId(), prodctIds);
			Assert.assertNotNull(cart);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testListCart() {
		if (user != null) {
			ServerResponse<CartVo> cart = iCartService.listCart(user.getId());
			Assert.assertNotNull(cart);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testSelectOrUnSelectProduct() {
		if (user != null && cart != null && product != null) {
			ServerResponse<CartVo> resp = iCartService.selectOrUnSelectProduct(user.getId(), product.getId(), cart.getChecked());
			Assert.assertNotNull(resp);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testGetCartProductCount() {
		if (user != null) {
			ServerResponse<Integer> resp = iCartService.getCartProductCount(user.getId());
			Assert.assertNotNull(resp);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testFindCartListInfo() {
		if (user != null) {
			List<Cart> carts = iCartService.findCartListInfo();
			Assert.assertNotNull(carts);
		} else {
			Assert.fail();
		}
	}

}

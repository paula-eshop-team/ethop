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

import com.eshop.common.EshopConstant;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.User;
import com.eshop.service.IUserService;
import com.eshop.utilities.RedisPoolUtil;

/**
 * Created by Paula
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) 
public class IUserServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private IUserService iUserService;
	
	private List<User> userList = new ArrayList<User>();
	
	private User user = null;
	
	@Before
	public void setUp() {
		userList = iUserService.findUserListInfo();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
	}
	
	@After
	public void clear() {
		userList.clear();
	}
	
	@Test
	public void testCheckAnswer() {
		if (user != null) {
			ServerResponse<String> result = iUserService.checkAnswer(user.getUsername(), user.getQuestion(), user.getAnswer());
			Assert.assertNotNull(result);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testLogin() {
		if (user != null) {
			ServerResponse<User> result = iUserService.login(user.getUsername(), user.getPassword());
			Assert.assertNotNull(result);
		} else {
			Assert.fail();
		}
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testRegister() {
		if (user != null) {
			ServerResponse<String> result = iUserService.register(user);
			Assert.assertEquals(EshopConstant.USER_EXIST.toString(), result.getMsg());
		} else {
			Assert.fail();
		}
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testFindUserListInfo() {
		List<User> userList = iUserService.findUserListInfo();
		Assert.assertNotNull(userList);
	}
	
	@Test
	public void testGetInfomation() {
		if (user != null) {
			ServerResponse<User> resp = iUserService.getInfomation(user.getId());
			User user = resp.getData();
			Assert.assertNotNull(user);
		} else {
			Assert.fail();
		}
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testForgetResetPassword() {
		if (user != null) {
			ServerResponse<String> resp = iUserService.forgetResetPassword(user.getUsername(), user.getPassword(), "token");
			Assert.assertEquals(EshopConstant.TOKEN_INVALIDATE.toString().toString(), resp.getMsg());
		}
	}
	
	@Test
	@Transactional
    @Rollback(true)
	public void testUpdateInfomation() {
		user.setUsername("newuserName");
		ServerResponse<User> resp = iUserService.updateInfomation(user);
		Assert.assertEquals(EshopConstant.UPDATE_USER_INFO_SUCCESS.toString(), resp.getMsg());
	}

}

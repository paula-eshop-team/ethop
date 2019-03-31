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
import com.eshop.pojo.Category;

@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ICategoryServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private ICategoryService iCategoryService;
	
	private List<Category> categoryList = new ArrayList<Category>();
	
	private Category category = null;
	
	@Before
	public void setUp() {
		categoryList = iCategoryService.findCategoryListInfo();
		if (!categoryList.isEmpty()) {
			category = categoryList.get(0);
		}
	}
	
	@After
	public void clear() {
		categoryList.clear();
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testAddCategory() {
		if (category != null) {
			ServerResponse resp = iCategoryService.addCategory("test", category.getParentId());
			Assert.assertEquals(EshopConstant.ADD_CATEGORY_SUCCESS.toString(), resp.getData());
		} else {
			Assert.fail();
		}
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testUpdateCategoryName() {
		if (category != null) {
			ServerResponse resp = iCategoryService.updateCategoryName(category.getId(), "name");
			Assert.assertEquals(EshopConstant.UPDATE_CATEGORY_SUCCESS.toString(), resp.getData());
		} else {
			Assert.fail();
		}
	} 
	
	@Test
	public void testGetChildrenParallelCategory() {
		if (category != null) {
			ServerResponse<List<Category>> list  = iCategoryService.getChildrenParallelCategory(category.getId());
			Assert.assertNotNull(list);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testSelectCategoryAndChildrenById() {
		if (category != null) {
			ServerResponse<List<Integer>> list = iCategoryService.selectCategoryAndChildrenById(category.getId());
			Assert.assertNotNull(list);
		} else {
			Assert.fail();
		}
	}
	
	@Test
	public void testfindChildCategory() {
		if (category != null) {
			List<Category> list = iCategoryService.findCategoryListInfo();
			Assert.assertNotNull(list);
		} else {
			Assert.fail();
		}
	}

}

package com.eshop.dao.test;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.eshop.common.ServerResponse;
import com.eshop.dao.CategoryMapper;
import com.eshop.pojo.Category;
import com.eshop.service.impl.CategoryServiceImpl;

import junit.framework.Assert;

/**
 * Created by Paula
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) 
public class CategoryDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
    private CategoryMapper categoryMapper;
	
	@Autowired
    private CategoryServiceImpl iCategoryService;

    @Test
    public void testSelectCategoryByPrimaryKey(){
        Category d  = categoryMapper.selectCategoryByPrimaryKey(100001);
        Assert.assertNotNull(d);
    }
    
    @Test
    public void testSelectCategoryAndChildrenById(){
    	ServerResponse<List<Integer>> list = iCategoryService.selectCategoryAndChildrenById(100006);
    	Assert.assertNotNull(list);
    }

}

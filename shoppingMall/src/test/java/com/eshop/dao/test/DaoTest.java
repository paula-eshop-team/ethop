package com.eshop.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.eshop.dao.UserMapper;
import com.eshop.pojo.User;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Paula
 */

@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) 
public class DaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
    private UserMapper userMapper;

    @Test
    @Transactional
    @Rollback(true)
    public void testDao(){
        User a = new User();
        a.setPassword("111");
        a.setUsername("PauaTes1t");
        a.setRole(0);
        a.setCreateTime(new Date());	
        a.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        System.out.println(userMapper.insertUser(a));
    }


}

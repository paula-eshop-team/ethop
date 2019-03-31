package com.eshop.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.eshop.pojo.User;

/**
 * 
 * @author Paula Lin
 *
 */
public interface UserMapper {
    /**
     * @param id
     * @return
     */
    int deleteUserByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertUser(User record);

    /**
     * @param record
     * @return
     */
    int insertUserSelective(User record);

    /**
     * @param id
     * @return
     */
    User selectUserByPrimaryKey(Integer id);

    //哪个属性不为空就更新哪个->选择性更新
    /**
     * @param record
     * @return
     */
    int updateUserByPrimaryKeySelective(User record);

    //全部更新
    /**
     * @param record
     * @return
     */
    int updateUserByPrimaryKey(User record);
    
    /**
     * @param username
     * @return
     */
    int checkUsesUserName(String username);
    
    /**
     * @param email
     * @return
     */
    int checkUsersInfoByEamil(String email);
    
    //@Param注解指定了sql中的参数名字
    /**
     * @param username
     * @param password
     * @return
     */
    User selectUserLoginInfo(@Param("username")String username, @Param("password")String password);
    
    /**
     * @param username
     * @return
     */
    String selectFaftyQuestionByUsername(String username);
    
    /**
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkSaftyAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);
    
    /**
     * @param username
     * @param passwordNew
     * @return
     */
    int updateUsersPasswordByUsername(@Param("username")String username, @Param("passwordNew")String passwordNew);
    
    /**
     * @param password
     * @param userId
     * @return
     */
    int checkUsersPassword(@Param("password")String password, @Param("userId")Integer userId);
    
    /**
     * @param email
     * @param userId
     * @return
     */
    int checkUsersEmailInfoByUserId(@Param("email")String email, @Param("userId")Integer userId);
    
    /**
     * @return
     */
    List<User> findUserListInfo();
    
}
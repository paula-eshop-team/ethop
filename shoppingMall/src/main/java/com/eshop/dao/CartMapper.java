package com.eshop.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.eshop.pojo.Cart;

/**
 * 
 * @author Paula Lin
 *
 */
public interface CartMapper {
	/**
	 * @param id
	 * @return
	 */
	int deleteCartByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertCart(Cart record);

    /**
     * @param record
     * @return
     */
    int insertCartSelective(Cart record);

    /**
     * @param id
     * @return
     */
    Cart selectCartByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updateCartByPrimaryKeySelective(Cart record);

    /**
     * @param record
     * @return
     */
    int updateCartByPrimaryKey(Cart record);

    /**
     * @param userId
     * @param productId
     * @return
     */
    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId")Integer productId);

    /**
     * @param userId
     * @return
     */
    List<Cart> selectOneCartByUserId(Integer userId);

    /**
     * @param userId
     * @return
     */
    int selectDetailsCartProductCheckedStatusByUserId(Integer userId);

    /**
     * @param userId
     * @param productIdList
     * @return
     */
    int deleteByUserIdAndProductIds(@Param("userId") Integer userId,@Param("productIdList")List<String> productIdList);


    /**
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    int checkedCartOrUncheckedCartProduct(@Param("userId") Integer userId,@Param("productId")Integer productId,@Param("checked") Integer checked);

    /**
     * @param userId
     * @return
     */
    int selectCartTotalProductCount(@Param("userId") Integer userId);


    /**
     * @param userId
     * @return
     */
    List<Cart> selectAllCheckedCartByUserId(Integer userId);
    
    /**
     * @return
     */
    List<Cart> findCartListInfo();
}
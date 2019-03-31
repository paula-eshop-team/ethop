package com.eshop.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.eshop.pojo.OrderItem;

/**
 * 
 * @author Paula Lin
 *
 */
public interface OrderItemMapper {
    /**
     * @param id
     * @return
     */
    int deleteOrderItemByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertOrderItem(OrderItem record);

    /**
     * @param record
     * @return
     */
    int insertOrderItemSelective(OrderItem record);

    /**
     * @param id
     * @return
     */
    OrderItem selectOrderItemByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updateOrderItemByPrimaryKeySelective(OrderItem record);

    /**
     * @param record
     * @return
     */
    int updateOrderItemByPrimaryKey(OrderItem record);
    
    /**
     * @param orderNo
     * @param userId
     * @return
     */
    List<OrderItem> getOrderItemByOrderNoUserId(@Param("orderNo")Long orderNo, @Param("userId")Integer userId);

    /**
     * @param orderNo
     * @return
     */
    List<OrderItem> getOrderItemByOrderNo(@Param("orderNo")Long orderNo);

    /**
     * @param orderItemList
     */
    void batchInsertOrderItems(@Param("orderItemList") List<OrderItem> orderItemList);
}
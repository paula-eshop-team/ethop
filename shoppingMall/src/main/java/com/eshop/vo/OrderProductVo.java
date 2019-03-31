package com.eshop.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author Paula Lin
 *
 */
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;
    
    /**
     * 
     * @return
     */
    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    /**
     * 
     * @param orderItemVoList
     */
    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    /**
     * 
     * @param productTotalPrice
     */
    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    /**
     * 
     * @return
     */
    public String getImageHost() {
        return imageHost;
    }

    /**
     * 
     * @param imageHost
     */
    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}

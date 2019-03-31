package com.eshop.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author Paula Lin
 *
 */
public class OrderItemVo {

	
    private Long orderNo;

    private Integer productId;

    private String productName;
    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;

    /**
     * 
     * @return
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * 
     * @param orderNo
     */
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 
     * @return
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * 
     * @param productId
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 
     * @return
     */
    public String getProductImage() {
        return productImage;
    }

    /**
     * 
     * @param productImage
     */
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getCurrentUnitPrice() {
        return currentUnitPrice;
    }

    /**
     * 
     * @param currentUnitPrice
     */
    public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
        this.currentUnitPrice = currentUnitPrice;
    }

    /**
     * 
     * @return
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 
     * @param quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * 
     * @param totalPrice
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 
     * @return
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 
     * @param createTime
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

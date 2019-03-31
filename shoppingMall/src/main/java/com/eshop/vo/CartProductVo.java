package com.eshop.vo;

import java.math.BigDecimal;

/**
 * 
 * @author Paula Lin
 *
 */
public class CartProductVo {

//结合了产品和购物车的一个抽象对象

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;//购物车中此商品的数量
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;//某商品的总价格:商品单价*商品数量
    private Integer productStock;
    private Integer productChecked;//此商品是否勾选

    private String limitQuantity;//限制数量的一个返回结果

    /**
     * 
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
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
    public String getProductSubtitle() {
        return productSubtitle;
    }

    /**
     * 
     * @param productSubtitle
     */
    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    /**
     * 
     * @return
     */
    public String getProductMainImage() {
        return productMainImage;
    }

    /**
     * 
     * @param productMainImage
     */
    public void setProductMainImage(String productMainImage) {
        this.productMainImage = productMainImage;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * 
     * @param productPrice
     */
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * 
     * @return
     */
    public Integer getProductStatus() {
        return productStatus;
    }

    /**
     * 
     * @param productStatus
     */
    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
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
    public Integer getProductStock() {
        return productStock;
    }

    /**
     * 
     * @param productStock
     */
    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    /**
     * 
     * @return
     */
    public Integer getProductChecked() {
        return productChecked;
    }

    /**
     * 
     * @param productChecked
     */
    public void setProductChecked(Integer productChecked) {
        this.productChecked = productChecked;
    }

    /**
     * 
     * @return
     */
    public String getLimitQuantity() {
        return limitQuantity;
    }

    /**
     * 
     * @param limitQuantity
     */
    public void setLimitQuantity(String limitQuantity) {
        this.limitQuantity = limitQuantity;
    }
}

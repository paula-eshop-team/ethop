package com.eshop.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author Paula Lin
 *
 */
public class CartVo {

    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;//购物车中所有商品的总价格:商品单价*商品数量之和
    private Boolean allChecked;//是否已经都勾选
    private String imageHost;

    /**
     * 
     * @return
     */
    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    /**
     * 
     * @param cartProductVoList
     */
    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    /**
     * 
     * @param cartTotalPrice
     */
    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    /**
     * 
     * @return
     */
    public Boolean getAllChecked() {
        return allChecked;
    }
    
    /**
     * 
     * @param allChecked
     */
    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
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

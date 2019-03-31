package com.eshop.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Paula Lin
 *
 */
public class OrderVo {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc;
    private Integer postage;

    private Integer status;


    private String statusDesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    //订单的明细
    private List<OrderItemVo> orderItemVoList;

    private String imageHost;
    private Integer shippingId;
    private String receiverName;

    private ShippingVo shippingVo;

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
    public BigDecimal getPayment() {
        return payment;
    }

    /**
     * 
     * @param payment
     */
    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    /**
     * 
     * @return
     */
    public Integer getPaymentType() {
        return paymentType;
    }

    /**
     * 
     * @param paymentType
     */
    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * 
     * @return
     */
    public String getPaymentTypeDesc() {
        return paymentTypeDesc;
    }

    /**
     * 
     * @param paymentTypeDesc
     */
    public void setPaymentTypeDesc(String paymentTypeDesc) {
        this.paymentTypeDesc = paymentTypeDesc;
    }

    /**
     * 
     * @return
     */
    public Integer getPostage() {
        return postage;
    }

    /**
     * 
     * @param postage
     */
    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    /**
     * 
     * @return
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 
     * @return
     */
    public String getStatusDesc() {
        return statusDesc;
    }

    /**
     * 
     * @param statusDesc
     */
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    /**
     * 
     * @return
     */
    public String getPaymentTime() {
        return paymentTime;
    }

    /**
     * 
     * @param paymentTime
     */
    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    /**
     * 
     * @return
     */
    public String getSendTime() {
        return sendTime;
    }

    /**
     * 
     * @param sendTime
     */
    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * 
     * @return
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * 
     * @param endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * 
     * @return
     */
    public String getCloseTime() {
        return closeTime;
    }

    /**
     * 
     * @param closeTime
     */
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
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

    /**
     * 
     * @return
     */
    public Integer getShippingId() {
        return shippingId;
    }

    /**
     * 
     * @param shippingId
     */
    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    /**
     * 
     * @return
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * 
     * @param receiverName
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * 
     * @return
     */
    public ShippingVo getShippingVo() {
        return shippingVo;
    }

    /**
     * 
     * @param shippingVo
     */
    public void setShippingVo(ShippingVo shippingVo) {
        this.shippingVo = shippingVo;
    }
}

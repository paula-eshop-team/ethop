package com.eshop.vo;

import java.math.BigDecimal;

/**
 * 
 * @author Paula Lin
 *
 */
public class ProductDetailVo {

    private Integer  id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;//富文本信息
    private BigDecimal price;
    private Integer stock;//库存
    private Integer status;
    private String createTime;
    private String updateTime;


    private String imageHost;//图片服务url的前缀,后面可以拼接具体图片名字就可以实时打开图片
    private Integer parentCategoryId;

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
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * 
     * @param categoryId
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @return
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * 
     * @param subtitle
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    /**
     * 
     * @return
     */
    public String getMainImage() {
        return mainImage;
    }
    
    /**
     * 
     * @param mainImage
     */
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    /**
     * 
     * @return
     */
    public String getSubImages() {
        return subImages;
    }

    /**
     * 
     * @param subImages
     */
    public void setSubImages(String subImages) {
        this.subImages = subImages;
    }

    /**
     * 
     * @return
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 
     * @param detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 
     * @param price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    /**
     * 
     * @return
     */
    public Integer getStock() {
        return stock;
    }
    
    /**
     * 
     * @param stock
     */
    public void setStock(Integer stock) {
        this.stock = stock;
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
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * 
     * @param updateTime
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    /**
     * 
     * @param parentCategoryId
     */
    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}

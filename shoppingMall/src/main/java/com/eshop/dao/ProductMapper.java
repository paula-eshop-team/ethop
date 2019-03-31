package com.eshop.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.eshop.pojo.Product;

/**
 * 
 * @author Paula Lin
 *
 */
public interface ProductMapper {
    /**
     * @param id
     * @return
     */
    int deleteProductMapperByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertProductMapper(Product record);

    /**
     * @param record
     * @return
     */
    int insertProductMapperSelective(Product record);

    /**
     * @param id
     * @return
     */
    Product selectProductMapperByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updateProductMapperByPrimaryKeySelective(Product record);

    /**
     * @param record
     * @return
     */
    int updateProdctMapperByPrimaryKey(Product record);
    
    /**
     * @return
     */
    List<Product> selectProductMapperList();

    /**
     * @param productName
     * @param productId
     * @return
     */
    List<Product> selectProductByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);

    /**
     * @param productName
     * @param categoryIdList
     * @return
     */
    List<Product> selectProductByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList")List<Integer> categoryIdList);

}
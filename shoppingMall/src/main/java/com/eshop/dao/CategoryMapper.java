package com.eshop.dao;

import java.util.List;

import com.eshop.pojo.Category;

/**
 * 
 * @author Paula Lin
 *
 */
public interface CategoryMapper {
    /**
     * @param id
     * @return
     */
    int deleteCategoryByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertCategory(Category record);

    /**
     * @param record
     * @return
     */
    int insertCategorySelective(Category record);

    /**
     * @param id
     * @return
     */
    Category selectCategoryByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updateCategoryByPrimaryKeySelective(Category record);

    /**
     * @param record
     * @return
     */
    int updateCategoryByPrimaryKey(Category record);
    
    /**
     * @param parentId
     * @return
     */
    List<Category> selectCategoryChildrensByParentId(Integer parentId);
    
    /**
     * @return
     */
    List<Category> findCategoryListInfo();
}
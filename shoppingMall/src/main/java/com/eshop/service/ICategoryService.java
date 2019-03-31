package com.eshop.service;

import java.util.List;

import com.eshop.common.ServerResponse;
import com.eshop.pojo.Category;

/**
 * 
 * @author Paula Lin
 *
 */
public interface ICategoryService {
	
	/**
	 * 
	 * @param categoryName
	 * @param parentId
	 * @return
	 */
    ServerResponse addCategory(String categoryName, Integer parentId);
    
    /**
     * 
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);
    
    /**
     * 
     * @param categoryId
     * @return
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    
    /**
     * 
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

    /**
     * @return
     */
    List<Category> findCategoryListInfo();
}
